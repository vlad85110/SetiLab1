package app.secure;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;

import java.io.*;
import java.security.*;

public class MessageSecure {
    private final Key key;
    private final Cipher cipher;
    public MessageSecure(String keyPath) {
        try (FileInputStream inputStream = new FileInputStream(keyPath)){
            var byteKey = inputStream.readAllBytes();
            key = new SecretKeySpec(byteKey, "AES");
            String transformation = "AES/ECB/PKCS5Padding";
            cipher = Cipher.getInstance(transformation);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public byte[] encrypt(String message) {
        try {
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return cipher.doFinal(message.getBytes());
        } catch (InvalidKeyException | IllegalBlockSizeException |
                 BadPaddingException e) {
            throw new RuntimeException(e);
        }
    }

    public String decipher(byte[] data) {
        try {
            cipher.init(Cipher.DECRYPT_MODE, key);
            return new String(cipher.doFinal(data));
        } catch (InvalidKeyException | IllegalBlockSizeException |
                 BadPaddingException e) {
            return null;
        }
    }
}
