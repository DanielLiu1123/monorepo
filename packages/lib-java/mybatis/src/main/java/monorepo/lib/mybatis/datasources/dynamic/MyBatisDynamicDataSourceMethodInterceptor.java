package monorepo.lib.mybatis.datasources.dynamic;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;
import java.util.function.Supplier;
import javax.sql.DataSource;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.mybatis.spring.boot.autoconfigure.MybatisAutoConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.ProxyMethodInvocation;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.function.SingletonSupplier;

/**
 * @author Freeman
 * @since 2024/12/13
 */
final class MyBatisDynamicDataSourceMethodInterceptor implements MethodInterceptor {
    private static final Logger log = LoggerFactory.getLogger(MyBatisDynamicDataSourceMethodInterceptor.class);

    private final Object originMapper;
    private final ApplicationContext ctx;
    private final Class<?> mapperInterface;
    private final Supplier<MybatisAutoConfiguration> mybatisAutoConfigurationSupplier;

    MyBatisDynamicDataSourceMethodInterceptor(Object originMapper, ApplicationContext ctx) {
        this.originMapper = originMapper;
        this.mapperInterface = AopProxyUtils.proxiedUserInterfaces(originMapper)[0];
        this.ctx = ctx;
        this.mybatisAutoConfigurationSupplier = SingletonSupplier.of(
                () -> ctx.getAutowireCapableBeanFactory().createBean(MybatisAutoConfiguration.class));
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Method method = invocation.getMethod();
        if (Objects.equals(method, DynamicDataSource.useDataSourceMethod)
                && invocation instanceof ProxyMethodInvocation pmi) {
            return getCachedMapper(pmi);
        }

        ReflectionUtils.makeAccessible(method);

        try {
            return method.invoke(originMapper, invocation.getArguments());
        } catch (InvocationTargetException e) {
            throw e.getTargetException();
        }
    }

    private Object getCachedMapper(ProxyMethodInvocation invocation) throws Exception {
        var datasourceName = Objects.requireNonNull((String) invocation.getArguments()[0]);

        var beanName = beanName(mapperInterface, datasourceName);
        if (ctx.containsBean(beanName)) {
            return ctx.getBean(beanName, mapperInterface);
        }

        DataSource dataSource;
        try {
            dataSource = ctx.getBean(datasourceName, DataSource.class);
        } catch (BeansException e) {
            log.error(
                    "No such datasource: {}, available datasource(s): {}",
                    datasourceName,
                    ctx.getBeanNamesForType(DataSource.class));
            // no such datasource, return current proxy, means do nothing
            return invocation.getProxy();
        }

        var mybatisAutoConfiguration = mybatisAutoConfigurationSupplier.get();
        var sqlSessionFactory = mybatisAutoConfiguration.sqlSessionFactory(dataSource);
        var sqlSessionTemplate = mybatisAutoConfiguration.sqlSessionTemplate(sqlSessionFactory);
        var configuration = sqlSessionTemplate.getConfiguration();
        if (!configuration.hasMapper(mapperInterface)) {
            configuration.addMapper(mapperInterface);
        }
        Object newMapper = sqlSessionTemplate.getMapper(mapperInterface);
        return createProxy(newMapper, ctx);
    }

    static Object createProxy(Object originMapper, ApplicationContext ctx) {
        var interfaces = AopProxyUtils.proxiedUserInterfaces(originMapper);
        var proxyFactory = new ProxyFactory(interfaces);
        proxyFactory.addAdvice(new MyBatisDynamicDataSourceMethodInterceptor(originMapper, ctx));
        return proxyFactory.getProxy();
    }

    private static String beanName(Class<?> mapperInterface, String datasourceName) {
        return mapperInterface.getName() + "#" + datasourceName;
    }
}
