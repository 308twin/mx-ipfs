package fdu.mit.ipfs.util;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Autowired;

import fdu.mit.ipfs.cache.ProcessCache;
import fdu.mit.ipfs.enums.OperationType;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

@Slf4j
public class FileEncryptionUtil {
    @Autowired
    ProcessCache processCache;

    public static final String AES_ALGORITHM = "AES";

    //转换密钥为String
    public static String convertSecretKeyToString(SecretKey secretKey) {
        byte[] keyBytes = secretKey.getEncoded();
        return Base64.getEncoder().encodeToString(keyBytes);
    }

    //转换String为密钥
    public static SecretKey convertStringToSecretKey(String keyString, String algorithm) {
        byte[] keyBytes = Base64.getDecoder().decode(keyString);
        return new SecretKeySpec(keyBytes, algorithm);
    }

    public static void encryptFile(File sourceFile, File encryptedFile, SecretKey secretKey,ProgressTracker progressTracker) throws IOException {
        try {
            Cipher cipher = Cipher.getInstance(AES_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);

            try (FileInputStream inputStream = new FileInputStream(sourceFile);
                 FileOutputStream outputStream = new FileOutputStream(encryptedFile)) {

                CipherOutputStream cipherOutputStream = new CipherOutputStream(outputStream, cipher);

                byte[] buffer = new byte[8192];
                int bytesRead;
                long totalBytesRead = 0;
                long fileSize = sourceFile.length();

                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    cipherOutputStream.write(buffer, 0, bytesRead);
                    totalBytesRead += bytesRead;

                    // 更新进度
                    double progress = (double) totalBytesRead / fileSize * 100;
                    log.debug(String.format("\rEncrypting: %.2f%%", progress));
                    if(progressTracker!=null){
                        progressTracker.updateProgress(progress, sourceFile.getPath(),  FileEncryptionUtil.convertSecretKeyToString(secretKey), OperationType.ADD);
                    }
                    //updateAddEncryptProgress(progress,sourceFile.getPath());
                }
                
                cipherOutputStream.close();
            }
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException e) {
            e.printStackTrace();
        }
    }

    public static void decryptFile(File encryptedFile, File decryptedFile, SecretKey secretKey,ProgressTracker progressTracker) throws IOException {
        try {
            Cipher cipher = Cipher.getInstance(AES_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, secretKey);

            try (FileInputStream inputStream = new FileInputStream(encryptedFile);
                 FileOutputStream outputStream = new FileOutputStream(decryptedFile)) {

                CipherInputStream cipherInputStream = new CipherInputStream(inputStream, cipher);

                byte[] buffer = new byte[8192];
                int bytesRead;
                long totalBytesRead = 0;
                long fileSize = encryptedFile.length();

                while ((bytesRead = cipherInputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                    totalBytesRead += bytesRead;

                    // 更新进度
                    double progress = (double) totalBytesRead / fileSize * 100;
                    log.debug(String.format("\rDecrypting: %.2f%%", progress));
                    if(progressTracker!=null){
                        progressTracker.updateProgress(progress, decryptedFile.getPath(), null, OperationType.GET);
                    }
                    //updateGetDecryptProgress(progress, decryptedFile.getPath());  
                }
                
                log.debug(String.format("\rDecrypting: %.2f%%", (double) totalBytesRead / fileSize * 100));
                cipherInputStream.close();
            }
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException e) {
            e.printStackTrace();
        }
    }

    public static SecretKey generateSecretKey() throws NoSuchAlgorithmException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance(AES_ALGORITHM);
        keyGenerator.init(256);
        return keyGenerator.generateKey();
    }

    public static SecretKey loadSecretKey(byte[] keyBytes) {
        return new SecretKeySpec(keyBytes, AES_ALGORITHM);
    }

    public interface ProgressTracker {
        void updateProgress(double progress,String filePath,String key,OperationType type);
    }

    //对IPFS add操作的加密进度
    public  void updateAddEncryptProgress(double progress,String filePath) {
    }

    //对IPFS get操作的解密进度
    public  void updateGetDecryptProgress(double progress,String filePath){

    }

    

    // private static final String AES_ALGORITHM = "AES";

    // public static void encryptFile(File sourceFile, File encryptedFile, SecretKey secretKey) throws IOException {
    //     try {
    //         Cipher cipher = Cipher.getInstance(AES_ALGORITHM);
    //         cipher.init(Cipher.ENCRYPT_MODE, secretKey);

    //         try (FileInputStream inputStream = new FileInputStream(sourceFile);
    //                 FileOutputStream outputStream = new FileOutputStream(encryptedFile)) {

    //             byte[] inputBuffer = new byte[1024];
    //             int bytesRead;

    //             while ((bytesRead = inputStream.read(inputBuffer)) != -1) {
    //                 byte[] outputBuffer = cipher.update(inputBuffer, 0, bytesRead);
    //                 if (outputBuffer != null) {
    //                     outputStream.write(outputBuffer);
    //                 }
    //             }

    //             byte[] outputBuffer = cipher.doFinal();
    //             if (outputBuffer != null) {
    //                 outputStream.write(outputBuffer);
    //             }
    //         }
    //     } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException
    //             | BadPaddingException e) {
    //         e.printStackTrace();
    //     }
    // }

    // public static void decryptFile(File encryptedFile, File decryptedFile, SecretKey secretKey) throws IOException {
    //     try {
    //         Cipher cipher = Cipher.getInstance(AES_ALGORITHM);
    //         cipher.init(Cipher.DECRYPT_MODE, secretKey);

    //         try (FileInputStream inputStream = new FileInputStream(encryptedFile);
    //                 FileOutputStream outputStream = new FileOutputStream(decryptedFile)) {

    //             byte[] inputBuffer = new byte[1024];
    //             int bytesRead;

    //             while ((bytesRead = inputStream.read(inputBuffer)) != -1) {
    //                 byte[] outputBuffer = cipher.update(inputBuffer, 0, bytesRead);
    //                 if (outputBuffer != null) {
    //                     outputStream.write(outputBuffer);
    //                 }
    //             }

    //             byte[] outputBuffer = cipher.doFinal();
    //             if (outputBuffer != null) {
    //                 outputStream.write(outputBuffer);
    //             }
    //         }
    //     } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException
    //             | BadPaddingException e) {
    //         e.printStackTrace();
    //     }
    // }

    // public static SecretKey generateSecretKey() throws NoSuchAlgorithmException {
    //     KeyGenerator keyGenerator = KeyGenerator.getInstance(AES_ALGORITHM);
    //     keyGenerator.init(256); // Set key size (128, 192, or 256)
    //     return keyGenerator.generateKey();
    // }

    // public static SecretKey loadSecretKey(byte[] keyBytes) {
    //     return new SecretKeySpec(keyBytes, AES_ALGORITHM);
    // }
}
