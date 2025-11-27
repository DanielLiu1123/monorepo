package monorepo.services.todo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
// @MapperScan("monorepo.services.todo.mapper")
public class TodoApp {
    static void main(String[] args) {
        SpringApplication.run(TodoApp.class, args);
    }

    //    @Bean
    //    public ObservationGrpcServerInterceptor observationGrpcServerInterceptor(ObservationRegistry registry) {
    //        return new ObservationGrpcServerInterceptor(registry);
    //    }
}
