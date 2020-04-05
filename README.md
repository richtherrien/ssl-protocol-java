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