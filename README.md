# ssl-protocol-java
## Running the Project
This project contains the code for both the client and the server components. Command line arguments must be set to run as either the client or the server.  
### Running The Program
In the terminal window, cd into the dist directory: /ssl-project/dist
#### Running as the Server
Run the command `java -jar ssl-project.jar client`
#### Running as the Client
Run the command `java -jar ssl-project.jar server`  

## Certificate Generation
To generate the public certificates the `keytool` utility is used.  
This followed the guide from the [oracle docs](https://docs.oracle.com/cd/E19830-01/819-4712/ablqw/index.html)  

Step 1: Generate the certificate in the keystore  
`keytool -genkey -alias serverKey -keyalg RSA -keypass keyPasswordServer -storepass storePasswordSecret -keystore keystore.jks`

first and last name: Project Server  
name of org unit: coe817-server  
name of org: project  
city: toronto  
state: ontario  
country code: ca  

Step 2: Export the certificate to a .cer file  
`keytool -export -alias serverKey -storepass storePasswordSecret -file server.cer -keystore keystore.jks`  

Step 3: Follow the same steps for the client certificate  
`keytool -genkey -alias clientKey -keyalg RSA -keypass keyPasswordClient -storepass storePasswordSecret -keystore keystore.jks`  

first and last name: Project Client  
name of org unit: coe817-client  
name of org: project  
city: toronto  
state: ontario  
country code: ca  