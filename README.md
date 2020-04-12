# ssl-protocol-java
## Running the Project
This project contains the code for both the client and the server components. Command line arguments must be set to run as either the client or the server.  
### Running The Program
In the terminal window, cd into the ssl-protocol-java directory
#### Running as the Server
Run the command `java -jar ssl-project/dist/ssl-project.jar server`
#### Running as the Client
Run the command `java -jar ssl-project/dist/ssl-project.jar client`  

## Certificate Generation
To generate the public certificates the `keytool` utility is used.  
This followed the guide from the [oracle docs](https://docs.oracle.com/cd/E19830-01/819-4712/ablqw/index.html)  

Step 1: Generate the certificate in the keystore  
`keytool -genkey -alias serverKey -keyalg RSA -keypass keyPasswordServer -storepass storePasswordSecret -keystore keystore.jks -validity 360 -keysize 2048 -dname "CN=Project Server,OU=coe817-server,O=coe817,L=toronto,S=ontario,C=ca"`

Step 2: Export the certificate to a .cer file   
`keytool -export -alias serverKey -storepass storePasswordSecret -file server.cer -keystore keystore.jks`  

Step 3: Repeat Steps 1 & 2 for the client  
`keytool -genkey -alias clientKey -keyalg RSA -keypass keyPasswordClient -storepass storePasswordSecret -keystore keystore.jks -validity 360 -keysize 2048 -dname "CN=Project Client,OU=coe817-client,O=coe817,L=toronto,S=ontario,C=ca"`  

`keytool -export -alias clientKey -storepass storePasswordSecret -file client.cer -keystore keystore.jks`  

Step 4: Getting private keys; convert JKS to PKCS12 format  
`keytool -importkeystore -srckeystore keystore.jks -srcstorepass storePasswordSecret -srckeypass keyPasswordServer -srcalias serverKey -destalias serverKey -destkeystore keystoreServer.p12 -deststoretype PKCS12 -deststorepass password -destkeypass password`

Step 5: Export the private key; winpty is only required on WINDOWS 10 machines  
`winpty openssl pkcs12 -in keystoreServer.p12 -nodes -nocerts -out server_private_key.pem`  
Enter Password: password  

Step 6: Repeat Steps 4 & 5 for the client  
`keytool -importkeystore -srckeystore keystore.jks -srcstorepass storePasswordSecret -srckeypass keyPasswordClient -srcalias clientKey -destalias clientKey -destkeystore keystoreClient.p12 -deststoretype PKCS12 -deststorepass password -destkeypass password`

`winpty openssl pkcs12 -in keystoreClient.p12 -nodes -nocerts -out client_private_key.pem`  
Enter Password: password  

Step 7: Convert to der format  
`winpty openssl pkcs8 -topk8 -inform PEM -outform DER -in server_private_key.pem -out server_private_key.der -nocrypt`  
`winpty openssl pkcs8 -topk8 -inform PEM -outform DER -in client_private_key.pem -out client_private_key.der -nocrypt`  


## Key Exchange
There are currently 2 KeyExchange objects that can be used in our encryption package, DHEncrypt and RSAEncrypt. Usage of these objects is outlined as follows:  

DHEncrypt  

Step 1: Create a DHEncrypt object on either the Server or Client side.  

Server: DHEncrypt alice = new DHEncrypt();  

Step 2: Send Alice's key to Bob and create a new DHEncrypt object using this key.  

Server: byte[] aliceKey = alice.getPublicKey();  
Server: aliceKey -> Client  
Client: DHEncrypt bob = new DHEncrypt(aliceKey);  

Step 3: Send Bob's key to Alice and perform phase one of the DH protocol.  

Client: byte[] bobKey = bob.getPublicKey();  
Client: bobKey -> Server  
Server: alice.phaseOne(bobKey);  

Once these steps are performed the DH protocol has been completed, you can get a key for use in symmetric encryption objects on either side as follows:  

Example (using AES)  

Server: AESEncrypt aliceEncrypt = new AESEncrypt(alice.getSecret());  
Client: AESEncrypt bobEncrypt = new AESEncrypt(bob.getSecret());  

RSAEncrypt

Step 1: Create a RSAEncrypt object on both the Client and Server side, and get their public keys.  

Server: RSAEncrypt alice = new RSAEncrypt();  
Server: byte[] aliceKey = alice.getPublicKey();  
Client: RSAEncrypt bob = new RSAEncrypt();  
Client: byte[] bobKey = bob.getPublicKey();  

Step 2: Client and Server send their keys to each other  

Server: aliceKey -> Client  
Client: bobKey -> Server  

Step 3: Client and Server set the public keys of eachother.  

Server: alice.setReceiverPublicKey(bobKey);  
Client: bob.setReceiverPublicKey(aliceKey);  

Step 4: Client or Server creates a symmetric encryption object and sends the key of that to the other encrypted using the RSA object.  

Server: AESEncrypt serverAES = new AESEncrypt();  
Server: byte[] serverKey = alice.encrypt(serverAES.getKey());  
Server: serverKey -> Client  
 
Step 5: Use the received key to instantiate a new symmetric encryption object  

Client: AESEncrypt clientAES = new AESEncrypt(serverKey);  

The RSA key exchange has now been performed.  

## Encryption
There are currently 3 symmetric encryption objects in our encryption package, each with the option to be instantiated using a byte key as follows:  

DES  

DESEncrypt des = new DESEncrypt();  
DESEncrypt des = new DESEncrypt(key);  

AES  

AESEncrypt aes = new AESEncrypt();  
AESEncrypt aes = new AESEncrypt(key);  

Triple DES  

TripleDESEncrypt tripdes = new TripleDESEncrypt();  
TripleDESEncrypt tripdes = new TripleDESEncrypt(key);  