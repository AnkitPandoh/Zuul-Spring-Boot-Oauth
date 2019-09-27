# OAuth Server

Brief about components:

* **authorization-server**

Responsible for Issuing the Jwt token which can be used to access the sample microservice. Uses Spring Boot **@EnableAuthorizationServer** annotation which enables the **/oauth/authorize** and **/oauth/token** endpoints. For more details, have a look at https://projects.spring.io/spring-security-oauth/docs/oauth2.html. For this sample, we are using **password** Grant Type. But this code, can be extended for other grant types as well. We are using Private/Public keys to sign and verify the token at authorizaton-server and edge-server respectively. Commands mentioned below to generate the private and public key using java keytool.


* **edge-server**

Acting as a Resource Server and responsible for proxying any request to Authorization Server and sample microservice. Since we have used **@EnableResourceServer** annotation which adds **OAuth2AuthenticationProcessingFilter** to the security filter chain which is responsible to load the OAuth2Authentication Object to Spring Context, it checks wheather request has valid JWT. If not, it would return the Unauthorized 401 response back to client. For proxying the request, we have used Spring boot Zuul. Currently we have registered our end points with Zuul but we can also use Ribbon and Eureka to register our services dynamically with Zuul proxy server.


* **sample-microservice**

Sample Rest API which we want to protect. We can add more granular security checks at microservice level such as authorizing the user based on roles to access the API. This would require to validate the token again at microservice and extract the roles out of JWT and authorize users to access the resources. But for the sake of simplicity, we are just checking the JWT at edge-server only.


### Generate Self signed certificate

keytool -genkey -alias <alias> -keyalg RSA -keystore <jks name> -keysize 2048


### Generate Public Key

keytool -export -keystore <jks name> -alias <key store alias> -file <output file name>


### Get Public Key

http://localhost:8081/oauth/token_key


### Get Auth Token

http://localhost:8081/oauth/token


### Setup Database

```
CREATE TABLE oauth_client_details (
  client_id VARCHAR(256) PRIMARY KEY,
  resource_ids VARCHAR(256),
  client_secret VARCHAR(256),
  scope VARCHAR(256),
  authorized_grant_types VARCHAR(256),
  web_server_redirect_uri VARCHAR(256),
  authorities VARCHAR(256),
  access_token_validity INTEGER,
  refresh_token_validity INTEGER,
  additional_information VARCHAR(4096),
  autoapprove VARCHAR(256)
);

CREATE TABLE IF NOT EXISTS users (
  id INT AUTO_INCREMENT PRIMARY KEY,
  username VARCHAR(256) NOT NULL,
  password VARCHAR(256) NOT NULL,
  enabled TINYINT(1),
  UNIQUE KEY unique_username(username)
);

CREATE TABLE IF NOT EXISTS authorities (
  username VARCHAR(256) NOT NULL,
  authority VARCHAR(256) NOT NULL,
  PRIMARY KEY(username, authority)
);

INSERT INTO oauth_client_details
    (client_id, client_secret, scope, authorized_grant_types,
    web_server_redirect_uri, authorities, access_token_validity,
    refresh_token_validity, additional_information, autoapprove)
VALUES ("testclient", "{bcrypt}$2a$10$R6sO1mzyxwEEln4SOAxHYOxQgj7wxz/8QQiEVWClqeVdxRMgKEkPa", "all","password,refresh_token", null, null, 36000, 36000, null, true);
    
INSERT INTO users (id, username, password, enabled) VALUES (1, 'test_user', '{bcrypt}$2a$10$6OsA9RTimHFW6ZpMkxWab.WvymOGzGM7j5KK2C1lE22CVnaUN0puW', 1);

INSERT INTO authorities (username, authority) VALUES ('test_user', 'ADMIN');
```

### Test

Run edge-server: http://localhost:8080/

Run authorization-server: http://localhost:8081/

Run sample-microservice: http://localhost:8082/

**Get token:**

```
curl -X POST \
  http://localhost:8080/oauth/token \
  -H 'Accept: */*' \
  -H 'Accept-Encoding: gzip, deflate' \
  -H 'Authorization: Basic dGVzdGNsaWVudDp0ZXN0c2VjcmV0' \
  -H 'Cache-Control: no-cache' \
  -H 'Connection: keep-alive' \
  -H 'Content-Length: 66' \
  -H 'Content-Type: text/plain' \
  -H 'Cookie: JSESSIONID.09c9e808=node01voxge02l4eli1v8z2crh9h5704.node0; JSESSIONID=06C487858CBEACDCEB06CA5A3DEEDBC1' \
  -H 'Host: localhost:8081' \
  -H 'Postman-Token: 1819fa27-bdf4-41e5-b699-482234e81e5f,d7285afb-97f3-48ac-98da-4228b3dd2ac0' \
  -H 'User-Agent: PostmanRuntime/7.17.1' \
  -H 'cache-control: no-cache' \
  -d 'grant_type=password&username=test_user&password=password&scope=all'
```

**Response:**

```
{
    "access_token": "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJleHAiOjE1Njk0ODEyOTMsInVzZXJfbmFtZSI6InRlc3RfdXNlciIsImF1dGhvcml0aWVzIjpbIlJFQUQiLCJBRE1JTiJdLCJqdGkiOiI5YTYzYmUyNi1jY2YyLTQ4NGEtOThiMy0xNzdkOGMwYmQ4MjIiLCJjbGllbnRfaWQiOiJ0ZXN0Y2xpZW50Iiwic2NvcGUiOlsiYWxsIl19.g2FS89XWCMX3W-zVP7venL3zdlsdW7UpIUyqOlwEHm28ZDp42HkN9suH1YynL8YKWcNQb2N3MbQIHH8hYBv2XHTcZP9YHfJHeGMPx0v1_6VlxEm6MXK4Eym89wTHlZEZ3Ff5mYNJYdmeHpkK-8cYJnhQzXgNom0qKQV5huNghLoX7bczQfh74mYJIBLJ71H0jhV4-dJWCE1RdRKwYkTHul55Q28DF8XcP5RqeydTKAK3XUh64gSDDKIWjFcfIrIU21gFA9AiJpBaYFDzodbsI4vSyDR8rWgPG46d5Rv-c58BalZSfYaPancIFzxXOkuj4iuOG4FRp96FYfTA8jbesQ",
    "token_type": "bearer",
    "refresh_token": "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX25hbWUiOiJ0ZXN0X3VzZXIiLCJzY29wZSI6WyJhbGwiXSwiYXRpIjoiOWE2M2JlMjYtY2NmMi00ODRhLTk4YjMtMTc3ZDhjMGJkODIyIiwiZXhwIjoxNTY5NDgxMjkzLCJhdXRob3JpdGllcyI6WyJSRUFEIiwiQURNSU4iXSwianRpIjoiZDRhY2MwNjYtYjE0MC00ZDUwLTliMTktN2Y3YmVhZDRjZjUyIiwiY2xpZW50X2lkIjoidGVzdGNsaWVudCJ9.iXxsEo70BPqs-I5Wk7a3UIY2TAyjuOdIJuJiXgEnwLr5xTbI70X2TB-9602ouj6xnuYoccuR-gTihIC9W4D4ws5QeHqTO1cWm5p1k5PG4s22dXlL48WYld3sqZFprCe738y8u-e-v1TLQDOHRAie6Pf06GQ76Wg5HKnwS3ulMxgWygS2qtLGdj6ElUa233aMZbWkhNEZAkHXslKeylO3gXtVbXCvVnGoaEz_JI5dYNedaoakPtJbzDHIbfO8RPyjyTEmPU6owoez4d2jcQOY4XzrmAGj-zYwDKVDUB71A87-Edhlo7OShPMrcgV63AvcQw_W1Wh4urtsUQNJnqBuwQ",
    "expires_in": 35999,
    "scope": "all",
    "jti": "9a63be26-ccf2-484a-98b3-177d8c0bd822"
}
```

**Access sample-microservice**

```
curl -X GET \
  http://localhost:8080/sample/api/v1 \
  -H 'Accept: */*' \
  -H 'Accept-Encoding: gzip, deflate' \
  -H 'Authorization: Bearer eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJleHAiOjE1Njk0ODEyOTMsInVzZXJfbmFtZSI6InRlc3RfdXNlciIsImF1dGhvcml0aWVzIjpbIlJFQUQiLCJBRE1JTiJdLCJqdGkiOiI5YTYzYmUyNi1jY2YyLTQ4NGEtOThiMy0xNzdkOGMwYmQ4MjIiLCJjbGllbnRfaWQiOiJ0ZXN0Y2xpZW50Iiwic2NvcGUiOlsiYWxsIl19.g2FS89XWCMX3W-zVP7venL3zdlsdW7UpIUyqOlwEHm28ZDp42HkN9suH1YynL8YKWcNQb2N3MbQIHH8hYBv2XHTcZP9YHfJHeGMPx0v1_6VlxEm6MXK4Eym89wTHlZEZ3Ff5mYNJYdmeHpkK-8cYJnhQzXgNom0qKQV5huNghLoX7bczQfh74mYJIBLJ71H0jhV4-dJWCE1RdRKwYkTHul55Q28DF8XcP5RqeydTKAK3XUh64gSDDKIWjFcfIrIU21gFA9AiJpBaYFDzodbsI4vSyDR8rWgPG46d5Rv-c58BalZSfYaPancIFzxXOkuj4iuOG4FRp96FYfTA8jbesQ' \
  -H 'Cache-Control: no-cache' \
  -H 'Connection: keep-alive' \
  -H 'Cookie: JSESSIONID.09c9e808=node01voxge02l4eli1v8z2crh9h5704.node0; JSESSIONID=06C487858CBEACDCEB06CA5A3DEEDBC1' \
  -H 'Host: localhost:8081' \
  -H 'Postman-Token: 09b8a69e-7cb6-4fae-a5b4-b8f7688e0af8,ff6ef88d-b855-45b7-89f2-bec9eb5408ea' \
  -H 'User-Agent: PostmanRuntime/7.17.1' \
  -H 'cache-control: no-cache'


