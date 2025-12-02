package monorepo.lib.common.cds;

import java.lang.management.ManagementFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;

/**
 * Treat as training run when -XX:AOTCacheOutput is specified.
 *
 * <p> {@code java -XX:AOTCacheOutput=app.aot -jar app.jar}
 *
 * @author Freeman
 * @since 2025/5/19
 */
public final class CDSTrainingRunHandler implements ApplicationRunner {
    private static final Logger log = LoggerFactory.getLogger(CDSTrainingRunHandler.class);

    @Override
    public void run(ApplicationArguments args) {
        var runtimeMxBean = ManagementFactory.getRuntimeMXBean();
        var jvmArgs = runtimeMxBean.getInputArguments();
        for (var arg : jvmArgs) {
            if (arg.startsWith("-XX:AOTCacheOutput")) {
                // In CDS training mode, we need to exit the application.
                // see https://openjdk.org/jeps/514
                // see org.springframework.context.support.DefaultLifecycleProcessor.onRefresh
                log.info("CDS training run detected ({}), exiting application...", arg);
                Runtime.getRuntime().halt(0);
            }
        }
    }
}
