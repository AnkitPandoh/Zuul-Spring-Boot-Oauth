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

--INSERT INTO oauth_client_details
--    (client_id, client_secret, scope, authorized_grant_types,
--    web_server_redirect_uri, authorities, access_token_validity,
--    refresh_token_validity, additional_information, autoapprove)
--VALUES ("testclient", "{bcrypt}$2a$10$R6sO1mzyxwEEln4SOAxHYOxQgj7wxz/8QQiEVWClqeVdxRMgKEkPa", "all","password,refresh_token", null, null, 36000, 36000, null, true);
--    
--INSERT INTO users (id, username, password, enabled) VALUES (1, 'test_user', '{bcrypt}$2a$10$6OsA9RTimHFW6ZpMkxWab.WvymOGzGM7j5KK2C1lE22CVnaUN0puW', 1);
--
--INSERT INTO authorities (username, authority) VALUES ('test_user', 'ADMIN');