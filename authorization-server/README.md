# OAuth Server

### Generate Self signed certificate
keytool -genkey -alias <alias> -keyalg RSA -keystore <jks name> -keysize 2048

### Generate Public Key
keytool -export -keystore <jks name> -alias <key store alias> -file <output file name>

### Get Public Key
http://<replace with Authorization-server-host>/oauth/token_key <br/>
e.g. http://localhost:8080/oauth/token_key