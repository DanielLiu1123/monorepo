package monorepo.lib.common.cds;

import java.util.Objects;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;

/**
 * <p> {@code java -Dtraining=1 -jar app.jar} </p>
 *
 * @author Freeman
 * @since 2025/5/19
 */
public class CDSTrainingRunHandler implements ApplicationListener<ApplicationReadyEvent> {

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        var ctx = event.getApplicationContext();
        var training = ctx.getEnvironment().getProperty("training");
        if (Objects.equals(training, "1") || Objects.equals(training, "true")) {
            // see org.springframework.context.support.DefaultLifecycleProcessor.onRefresh
            Runtime.getRuntime().halt(0);
        }
    }
}
