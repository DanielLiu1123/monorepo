# Todo service

```shell
docker run -id \
 --name postgres \
 -e POSTGRES_DB=todo \
 -e POSTGRES_USER=root \
 -e POSTGRES_PASSWORD=root \
 -p 5432:5432 \
 postgres:latest
```