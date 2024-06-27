package baseCodes;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.security.InvalidKeyException;
import java.security.Key;
import java.util.Base64;

public class FileEncryptorDecryptor {
    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES";

    private Key generateKey(String key) throws Exception {
        byte[] keyBytes = key.getBytes("UTF-8");
        byte[] keyBytesPadded = new byte[16]; // AES key size is 16 bytes
        System.arraycopy(keyBytes, 0, keyBytesPadded, 0, Math.min(keyBytes.length, keyBytesPadded.length));
        return new SecretKeySpec(keyBytesPadded, ALGORITHM);
    }

    public void encryptFile(File inputFile, String key, boolean encryptFileName) throws Exception {
        encryptSingleFile(inputFile, key, encryptFileName);
    }

    public void decryptFile(File inputFile, String key, boolean encryptFileName) throws Exception {
        decryptSingleFile(inputFile, key, encryptFileName);
    }

    private void encryptSingleFile(File inputFile, String key, boolean encryptFileName) throws Exception {
        Key secretKey = generateKey(key);
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);

        String newFileName;
        if (encryptFileName) {
            newFileName = Base64.getEncoder().encodeToString(inputFile.getName().getBytes()) + ".ncrypt";
        } else {
            newFileName = inputFile.getName() + ".crypt";
        }

        File outputFile = new File(inputFile.getParent(), newFileName);
        try (FileInputStream inputStream = new FileInputStream(inputFile);
             FileOutputStream outputStream = new FileOutputStream(outputFile);
             CipherOutputStream cipherOutputStream = new CipherOutputStream(outputStream, cipher)) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                cipherOutputStream.write(buffer, 0, bytesRead);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Error during encryption: " + e.getMessage());
        }

        // Delete the original file after processing
        if (!inputFile.delete()) {
            throw new Exception("Failed to delete the original file: " + inputFile.getAbsolutePath());
        }
    }

    private void decryptSingleFile(File inputFile, String key, boolean encryptFileName) throws Exception {
        Key secretKey = generateKey(key);
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.DECRYPT_MODE, secretKey);

        String newFileName;
        String inputPath = inputFile.getAbsolutePath();
        if (inputPath.endsWith(".crypt")) {
            newFileName = inputFile.getName().replaceFirst(".crypt$", "");
        } else if (inputPath.endsWith(".ncrypt")) {
            newFileName = new String(Base64.getDecoder().decode(inputFile.getName().replaceFirst(".ncrypt$", "")));
        } else {
            throw new IllegalArgumentException("File does not have a valid encrypted extension: " + inputPath);
        }

        File outputFile = new File(inputFile.getParent(), newFileName);
        try (FileInputStream inputStream = new FileInputStream(inputFile);
             FileOutputStream outputStream = new FileOutputStream(outputFile);
             CipherInputStream cipherInputStream = new CipherInputStream(inputStream, cipher)) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = cipherInputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Error during decryption: " + e.getMessage());
        }

        // Delete the original file after processing
        if (!inputFile.delete()) {
            throw new Exception("Failed to delete the original file: " + inputFile.getAbsolutePath());
        }
    }
}
