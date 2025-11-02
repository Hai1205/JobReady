import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.io.FileWriter;
import java.io.IOException;

public class KeyGenerator {
    public static void main(String[] args) throws Exception {
        // Generate RSA key pair
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        
        PrivateKey privateKey = keyPair.getPrivate();
        PublicKey publicKey = keyPair.getPublic();
        
        // Write private key
        String privateKeyPEM = "-----BEGIN PRIVATE KEY-----\n" +
                Base64.getMimeEncoder(64, "\n".getBytes()).encodeToString(privateKey.getEncoded()) +
                "\n-----END PRIVATE KEY-----\n";
        
        try (FileWriter writer = new FileWriter("private_key.pem")) {
            writer.write(privateKeyPEM);
        }
        
        // Write public key
        String publicKeyPEM = "-----BEGIN PUBLIC KEY-----\n" +
                Base64.getMimeEncoder(64, "\n".getBytes()).encodeToString(publicKey.getEncoded()) +
                "\n-----END PUBLIC KEY-----\n";
        
        try (FileWriter writer = new FileWriter("public_key.pem")) {
            writer.write(publicKeyPEM);
        }
        
        System.out.println("Keys generated successfully!");
        System.out.println("Private key saved to private_key.pem");
        System.out.println("Public key saved to public_key.pem");
    }
}