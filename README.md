# MICROSERVICES


```
docker run --name my-postgresdb -e POSTGRES_PASSWORD=123456789 -d -p 9999:5432 postgres
```

```
docker run --name my-zipkin -d -p 9411:9411 --memory=256m  openzipkin/zipkin
```

```
docker run --name my-redis -p 6379:6379 redis
```

