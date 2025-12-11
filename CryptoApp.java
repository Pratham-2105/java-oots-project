
// CryptoApp.java
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.SwingUtilities;
import java.security.SecureRandom;
import java.util.Base64;

public class CryptoApp {

    // ----- main -----
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new GUI().createAndShowGUI();
        });
    }

    // ----- Caesar (demo) -----
    public static String caesarEncrypt(String plain, int key) {
        if (plain == null)
            return "";
        StringBuilder sb = new StringBuilder();
        for (char c : plain.toCharArray()) {
            sb.append((char) (c + key));
        }
        return sb.toString();
    }

    public static String caesarDecrypt(String cipher, int key) {
        if (cipher == null)
            return "";
        StringBuilder sb = new StringBuilder();
        for (char c : cipher.toCharArray()) {
            sb.append((char) (c - key));
        }
        return sb.toString();
    }

    // ----- AES (secure demo) -----
    // Returns cipherBase64 (IV + ciphertext) and keyBase64 as a two-line string:
    // cipherBase64\nkeyBase64
    public static String aesEncryptBundle(String plain) throws Exception {
        if (plain == null)
            plain = "";

        // generate random 128-bit AES key
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(128);
        SecretKey secretKey = keyGen.generateKey();
        byte[] keyBytes = secretKey.getEncoded();

        // init cipher AES/CBC/PKCS5Padding
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        SecureRandom rnd = new SecureRandom();
        byte[] iv = new byte[16];
        rnd.nextBytes(iv);
        IvParameterSpec ivSpec = new IvParameterSpec(iv);

        SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
        byte[] cipherBytes = cipher.doFinal(plain.getBytes("UTF-8"));

        // store IV || ciphertext together
        byte[] ivAndCipher = new byte[iv.length + cipherBytes.length];
        System.arraycopy(iv, 0, ivAndCipher, 0, iv.length);
        System.arraycopy(cipherBytes, 0, ivAndCipher, iv.length, cipherBytes.length);

        String cipherB64 = Base64.getEncoder().encodeToString(ivAndCipher);
        String keyB64 = Base64.getEncoder().encodeToString(keyBytes);

        return cipherB64 + "\n" + keyB64;
    }

    // Accepts cipherBase64 (IV||cipher) and key bytes (raw) as Base64.
    public static String aesDecryptFromBundle(String cipherB64, String keyB64) throws Exception {
        if (cipherB64 == null || keyB64 == null)
            return "";

        byte[] ivAndCipher = Base64.getDecoder().decode(cipherB64);
        if (ivAndCipher.length < 16)
            throw new IllegalArgumentException("cipher too short (no IV found)");

        byte[] iv = new byte[16];
        System.arraycopy(ivAndCipher, 0, iv, 0, 16);
        byte[] cipherBytes = new byte[ivAndCipher.length - 16];
        System.arraycopy(ivAndCipher, 16, cipherBytes, 0, cipherBytes.length);

        byte[] keyBytes = Base64.getDecoder().decode(keyB64);
        SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");
        IvParameterSpec ivSpec = new IvParameterSpec(iv);

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);

        byte[] plainBytes = cipher.doFinal(cipherBytes);
        return new String(plainBytes, "UTF-8");
    }
}
