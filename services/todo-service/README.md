# Todo service

```shell
docker run -id --name postgres \
  -e POSTGRES_DB=todo \
  -e POSTGRES_USER=root \
  -e POSTGRES_PASSWORD=root \
  -p 5432:5432 \
  postgres:latest
 
docker run -id --name jaeger \
  -p 16686:16686 \
  -p 4317:4317 \
  -p 4318:4318 \
  -p 5778:5778 \
  -p 9411:9411 \
  jaegertracing/jaeger:latest
```

```shell
./gradlew :services:todo-service:bootRun
open http://localhost:16686
```