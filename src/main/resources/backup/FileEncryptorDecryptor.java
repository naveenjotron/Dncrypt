package baseCodes;

import javax.crypto.*;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.ArrayList;
import java.util.List;

public class FileEncryptorDecryptor {

    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES";
    private static final int ITERATIONS = 65536; // Adjust as needed
    private static final int KEY_LENGTH = 256;

    public static int encryptFiles(File file, String password) throws InvalidKeyException {
        return processFiles(file, password, Cipher.ENCRYPT_MODE);
    }

    public static int decryptFiles(File file, String password) throws InvalidKeyException {
        return processFiles(file, password, Cipher.DECRYPT_MODE);
    }

    private static int processFiles(File file, String password, int cipherMode) throws InvalidKeyException {
        try {
            List<File> filesToProcess = getFilesToProcess(file);

            SecretKey secretKey = generateKey(password);

            int fileCount = 0;

            for (File currentFile : filesToProcess) {
                byte[] buffer = new byte[8192];
                Path filePath = Paths.get(currentFile.getAbsolutePath());

                try (FileInputStream fileInputStream = new FileInputStream(currentFile);
                     ByteArrayOutputStream output = new ByteArrayOutputStream()) {

                    Cipher cipher = Cipher.getInstance(TRANSFORMATION);
                    cipher.init(cipherMode, secretKey);

                    int bytesRead;

                    while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                        byte[] cipherBytes = cipher.doFinal(buffer, 0, bytesRead);
                        output.write(cipherBytes);
                    }

                    byte[] resultBytes = output.toByteArray();
                    try (FileOutputStream fileOutputStream = new FileOutputStream(currentFile)) {
                        fileOutputStream.write(resultBytes);
                    }

                    fileCount++;
                }
            }

            return fileCount;
        } catch (Exception e) {
            // Catch and rethrow specific exceptions
            if (e instanceof BadPaddingException || e instanceof IllegalBlockSizeException) {
                throw new InvalidKeyException("Incorrect password");
            } else {
                throw new RuntimeException("Error processing files", e);
            }
        }
    }

    private static List<File> getFilesToProcess(File file) {
        List<File> files = new ArrayList<>();

        if (file.isDirectory()) {
            File[] fileList = file.listFiles();
            if (fileList != null) {
                for (File f : fileList) {
                    if (f.isFile()) {
                        files.add(f);
                    }
                }
            }
        } else {
            files.add(file);
        }

        return files;
    }

    private static SecretKey generateKey(String password) throws NoSuchAlgorithmException, InvalidKeySpecException {
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        KeySpec spec = new PBEKeySpec(password.toCharArray(), "salt".getBytes(), ITERATIONS, KEY_LENGTH);
        SecretKey tmp = factory.generateSecret(spec);
        return new SecretKeySpec(tmp.getEncoded(), ALGORITHM);
    }
}



