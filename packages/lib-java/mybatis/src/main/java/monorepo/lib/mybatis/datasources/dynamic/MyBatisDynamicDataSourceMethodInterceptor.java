package monorepo.lib.mybatis.datasources.dynamic;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;
import javax.sql.DataSource;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.boot.autoconfigure.MybatisAutoConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.ProxyMethodInvocation;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.util.ReflectionUtils;

/**
 * @author Freeman
 * @since 2026/1/10
 */
final class MyBatisDynamicDataSourceMethodInterceptor implements MethodInterceptor {
    private static final Logger log = LoggerFactory.getLogger(MyBatisDynamicDataSourceMethodInterceptor.class);

    private final Object originMapper;
    private final ConfigurableApplicationContext ctx;
    private final Class<?> mapperInterface;

    MyBatisDynamicDataSourceMethodInterceptor(Object originMapper, ApplicationContext ctx) {
        this.originMapper = originMapper;
        this.mapperInterface = AopProxyUtils.proxiedUserInterfaces(originMapper)[0];
        this.ctx = (ConfigurableApplicationContext) ctx;
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Method method = invocation.getMethod();
        if (Objects.equals(method, DynamicDataSource.useDataSourceMethod)
                && invocation instanceof ProxyMethodInvocation pmi) {
            return getOrRegisterMapper(pmi);
        }

        ReflectionUtils.makeAccessible(method);

        try {
            return method.invoke(originMapper, invocation.getArguments());
        } catch (InvocationTargetException e) {
            throw e.getTargetException();
        }
    }

    private Object getOrRegisterMapper(ProxyMethodInvocation invocation) throws Exception {
        var datasourceName = Objects.requireNonNull((String) invocation.getArguments()[0]);

        var beanName = mapperInterface.getName() + "#" + datasourceName;
        if (ctx.containsBean(beanName)) {
            log.debug("Found existing mapper {}", beanName);
            return ctx.getBean(beanName);
        }

        DataSource dataSource;
        try {
            dataSource = ctx.getBean(datasourceName, DataSource.class);
        } catch (BeansException e) {
            log.error(
                    "No such datasource: {}, available datasource(s): {}",
                    datasourceName,
                    ctx.getBeanNamesForType(DataSource.class));
            return invocation.getProxy();
        }

        var sqlSessionTemplate = getOrRegisterSqlSessionTemplate(datasourceName, dataSource);

        synchronized (sqlSessionTemplate) {
            if (ctx.containsBean(beanName)) {
                log.debug("Found existing mapper {}", beanName);
                return ctx.getBean(beanName);
            } else {
                var mapper = registerMapper(sqlSessionTemplate, beanName);
                log.debug("Registered mapper {}", beanName);
                return mapper;
            }
        }
    }

    private Object registerMapper(SqlSessionTemplate sqlSessionTemplate, String beanName) {
        Object mapper = sqlSessionTemplate.getMapper(mapperInterface);
        var proxy = createProxy(mapper, ctx);

        registerSingleton(beanName, proxy);

        return proxy;
    }

    private SqlSessionTemplate getOrRegisterSqlSessionTemplate(String datasourceName, DataSource dataSource)
            throws Exception {
        var sstBeanName = "sqlSessionTemplate#" + datasourceName;
        SqlSessionTemplate sst;
        if (ctx.containsBean(sstBeanName)) {
            log.debug("Found existing SqlSessionTemplate {}", sstBeanName);
            sst = ctx.getBean(sstBeanName, SqlSessionTemplate.class);
        } else {
            synchronized (dataSource) {
                if (ctx.containsBean(sstBeanName)) {
                    log.debug("Found existing SqlSessionTemplate {}", sstBeanName);
                    sst = ctx.getBean(sstBeanName, SqlSessionTemplate.class);
                } else {
                    sst = registerSqlSessionTemplate(dataSource, sstBeanName);
                    log.debug("Registered SqlSessionTemplate {}", sstBeanName);
                }
            }
        }
        var configuration = sst.getConfiguration();
        if (!configuration.hasMapper(mapperInterface)) {
            synchronized (configuration) {
                if (!configuration.hasMapper(mapperInterface)) {
                    configuration.addMapper(mapperInterface);
                }
            }
        }
        return sst;
    }

    private SqlSessionTemplate registerSqlSessionTemplate(DataSource dataSource, String sstBeanName) throws Exception {
        var mybatisAutoConfiguration = ctx.getAutowireCapableBeanFactory().createBean(MybatisAutoConfiguration.class);
        var sqlSessionFactory = mybatisAutoConfiguration.sqlSessionFactory(dataSource);
        var sqlSessionTemplate = mybatisAutoConfiguration.sqlSessionTemplate(sqlSessionFactory);

        registerSingleton(sstBeanName, sqlSessionTemplate);

        return sqlSessionTemplate;
    }

    private void registerSingleton(String beanName, Object bean) {
        ctx.getBeanFactory().registerSingleton(beanName, bean);
    }

    static Object createProxy(Object originMapper, ApplicationContext ctx) {
        var interfaces = AopProxyUtils.proxiedUserInterfaces(originMapper);
        var proxyFactory = new ProxyFactory(interfaces);
        proxyFactory.addAdvice(new MyBatisDynamicDataSourceMethodInterceptor(originMapper, ctx));
        return proxyFactory.getProxy();
    }
}
