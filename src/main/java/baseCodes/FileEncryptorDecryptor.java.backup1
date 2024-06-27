package baseCodes;

import javax.crypto.*;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class FileEncryptorDecryptor {

	private static final String ALGORITHM = "AES";
	private static final String TRANSFORMATION_CONTENT = "AES";
	private static final String TRANSFORMATION_FILENAME = "AES/ECB/PKCS5Padding";
	private static final int ITERATIONS = 65536; // Adjust as needed
	private static final int KEY_LENGTH = 256;

	public static int encryptFiles(File file, String password, boolean encryptFileName) throws InvalidKeyException {
		int cipherMode = Cipher.ENCRYPT_MODE;

		// Check if the file doesn't have the ".crypt" extension
		if (file.isDirectory()) {
			// If it's a directory, process it
			return enProcessFiles(file, password, cipherMode, encryptFileName);
		} else if (!file.getName().endsWith(".crypt")) {
			return enProcessFiles(file, password, cipherMode, encryptFileName);
		} else {
            System.out.println("*Skipping encryption for file with \".crypt\" extension: " + file.getName());
			return 0; // Return 0 files processed
		}
	}

	public static int decryptFiles(File file, String password, boolean encryptFileName) throws InvalidKeyException {
		int cipherMode = Cipher.DECRYPT_MODE;

		if (file.isDirectory()) {
			// If it's a directory, process it
			return deProcessFiles(file, password, cipherMode, encryptFileName);
		} else if (file.getName().endsWith(".crypt")) {
			// If it's a file with the ".crypt" extension, process it
			return deProcessFiles(file, password, cipherMode, encryptFileName);
		} else {
            System.out.println("*Skipping decryption for file without \".crypt\" extension: " + file.getName());
			return 0; // Return 0 files processed
		}
	}

	private static int enProcessFiles(File file, String password, int cipherMode, boolean encryptFileName)
			throws InvalidKeyException {
		try {
			List<File> filesToProcess = getFilesToProcess(file);

			SecretKey secretKey = generateKey(password);

			int fileCount = 0;

			for (File currentFile : filesToProcess) {
				Path filePath = Paths.get(currentFile.getAbsolutePath());
				String fileName = currentFile.getName();
				File processedFile = null;
				if (!fileName.endsWith(".crypt") && !fileName.endsWith(".ncrypt")) {
				processedFile = enGetProcessedFile(currentFile, encryptFileName, true);
				
				try (CipherOutputStream cipherOutputStream = new CipherOutputStream(new BufferedOutputStream(new FileOutputStream(processedFile)),
						getCipher(secretKey, cipherMode));
						CipherInputStream cipherInputStream = new CipherInputStream(new BufferedInputStream(new FileInputStream(currentFile)),
								getCipher(secretKey, cipherMode))) {

					byte[] buffer = new byte[8192];
					int bytesRead;

					while ((bytesRead = cipherInputStream.read(buffer)) != -1) {
						cipherOutputStream.write(buffer, 0, bytesRead);
					}

					fileCount++;
					
				} catch (IOException e) {
					e.printStackTrace(); // Handle or log IOException
				}
				try {
                    Files.delete(filePath);
                } catch (IOException e) {
                    e.printStackTrace(); // Handle or log IOException
                }
				}else {
					System.out.println("Skipping encryption for file without \".crypt\" extension: " + fileName);
				}
			}

//            if (!encryptFileName) {
//                // Delete original files after decryption
//                for (File currentFile : filesToProcess) {
//                    try {
//                        Files.delete(currentFile.toPath());
//                    } catch (IOException e) {
//                        e.printStackTrace(); // Handle or log IOException
//                    }
//                }
//            }

			return fileCount;
		} catch (Exception e) {
			if (e instanceof BadPaddingException || e instanceof IllegalBlockSizeException) {
				throw new InvalidKeyException("Incorrect password");
			} else {
				throw new RuntimeException("Error processing files", e);
			}
		}
	}

	private static int deProcessFiles(File file, String password, int cipherMode, boolean encryptFileName)
			throws InvalidKeyException {
		try {
			List<File> filesToProcess = getFilesToProcess(file);

			SecretKey secretKey = generateKey(password);

			int fileCount = 0;

			for (File currentFile : filesToProcess) {
				Path filePath = Paths.get(currentFile.getAbsolutePath());
				String fileName = currentFile.getName();
				File processedFile = null;

				if (fileName.endsWith(".crypt") || fileName.endsWith(".ncrypt")) {
					processedFile = deGetProcessedFile(currentFile, encryptFileName, false);
					System.out.println("file has \".crypt\" extension: " + processedFile.getName());
					
				try (CipherOutputStream cipherOutputStream = new CipherOutputStream(new BufferedOutputStream(new FileOutputStream(processedFile)),
						getCipher(secretKey, cipherMode));
						CipherInputStream cipherInputStream = new CipherInputStream(new BufferedInputStream(new FileInputStream(currentFile)),
								getCipher(secretKey, cipherMode))) {

					byte[] buffer = new byte[8192];
					int bytesRead;

					while ((bytesRead = cipherInputStream.read(buffer)) != -1) {
						cipherOutputStream.write(buffer, 0, bytesRead);
					}

					fileCount++;
				} catch (IOException e) {
					e.printStackTrace(); // Handle or log IOException
				}
				try {
                    Files.delete(filePath);
                } catch (IOException e) {
                    e.printStackTrace(); // Handle or log IOException
                }
				}else {
					System.out.println("Skipping decryption for file without \".crypt\" extension: " + fileName);
				}
			}

//            if (!encryptFileName) {
//                // Delete original files after decryption
//                for (File currentFile : filesToProcess) {
//                    try {
//                        Files.delete(currentFile.toPath());
//                    } catch (IOException e) {
//                        e.printStackTrace(); // Handle or log IOException
//                    }
//                }
//            }

			return fileCount;
		} catch (Exception e) {
			if (e instanceof BadPaddingException || e instanceof IllegalBlockSizeException) {
				throw new InvalidKeyException("Incorrect password");
			} else {
				throw new RuntimeException("Error processing files", e);
			}
		}
	}

	private static Cipher getCipher(SecretKey secretKey, int cipherMode)
			throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException {
		Cipher cipher = Cipher.getInstance(TRANSFORMATION_CONTENT);
		cipher.init(cipherMode, secretKey);
		return cipher;
	}

	private static File enGetProcessedFile(File file, boolean encryptFileName, boolean enDecrypt) {
		String fileName = file.getName();
		if (!fileName.endsWith(".crypt")) {
			String encodedFileName = file.getName();
			// Encoding file name
			if (encryptFileName) {
				encodedFileName = encodeFileName(fileName);
				System.out.println("Encoded File Name: " + encodedFileName);
				return new File(file.getParent(), encodedFileName + ".ncrypt");
			}else {
//				try {
//					Files.delete(file.toPath());
//				} catch (IOException e) {
//					e.printStackTrace(); // Handle or log IOException
//				}
//			
			return new File(file.getParent(), encodedFileName + ".crypt");
			}
		}
		return file;
	}

	private static File deGetProcessedFile(File file, boolean encryptFileName, boolean enDecrypt) {
//            String fileName = file.getName();
		String fileName = file.getName();
		if (fileName.endsWith(".crypt") || fileName.endsWith(".ncrypt")) {
//			String decodedFileName = fileName.substring(0, fileName.length() - 6);
			// Decoding file name
			if (fileName.endsWith(".ncrypt")) {
				String decodedFileName = fileName.substring(0, fileName.length() - 7);
				decodedFileName = decodeFileName(decodedFileName);
				System.out.println("Decoded File Name: " + decodedFileName);
				return new File(file.getParent(), decodedFileName);
			}else {
				String dFileName = fileName.substring(0, fileName.length() - 6);
				return new File(file.getParent(), dFileName);
			}
		}
			return file; // No change to file name	
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

	public static String encodeFileName(String fileName) {
		try {
			byte[] fileNameBytes = fileName.getBytes("UTF-8");
			String encodedFileName = Base64.getEncoder().encodeToString(fileNameBytes);
			return encodedFileName;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			// Handle encoding exception
			return null;
		}
	}

	public static String decodeFileName(String encodedFileName) {
		try {
			byte[] decodedFileNameBytes = Base64.getDecoder().decode(encodedFileName);
			String decodedFileName = new String(decodedFileNameBytes, "UTF-8");
			return decodedFileName;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			// Handle decoding exception
			return null;
		}
	}
}
