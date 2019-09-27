# Sample Spring Boot Config Server

## config-server

**application.properties**

```
spring.cloud.config.server.git.uri={HOME}/config 
```
 
 Go to ```**{HOME}/config** ``` and run following commands:

```
 git init
 echo message: Default Hello World > application.yml
 echo message: Hello World from Dev > config-client-dev.yml
 echo message: Hello World from Prod > config-client-prod.yml
```

**Run**

```
curl http://localhost:8888
```

## config-client

**bootstrap.properties**

```
spring.application.name=config-client
spring.profiles.active=dev
spring.cloud.config.uri=http://localhost:8888

server.port=9999
```

**Run**

```
curl http://localhost:9999/message
```



 
 
