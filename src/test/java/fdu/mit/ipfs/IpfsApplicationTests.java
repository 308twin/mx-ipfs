package fdu.mit.ipfs;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import javax.crypto.SecretKey;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import fdu.mit.ipfs.cache.ProcessCache;
import fdu.mit.ipfs.helper.ProgressTracker;
import fdu.mit.ipfs.util.FileEncryptionUtil;
import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@Slf4j
class IpfsApplicationTests {

    @Autowired
    ProcessCache processCache;

    @Test
    void contextLoads() {
    }

    @Test
    void encryptFile() {
        // try {
        //     processCache = new ProcessCache();
        //     processCache.init();
        //     SecretKey secretKey = FileEncryptionUtil.generateSecretKey();
        //     String keyString = FileEncryptionUtil.convertSecretKeyToString(secretKey); // 源文件和加密文件路径
        //     SecretKey convertedSecretKey = FileEncryptionUtil.convertStringToSecretKey(keyString,
        //             FileEncryptionUtil.AES_ALGORITHM);
        //     System.out.println("!!test!!");        
        //     File sourceFile = new File("/home/Test/go1.17.7.linux-amd64.tar.gz");
        //     File encryptedFile = new File("/home/Test/go1.17.7.linux-amd64.tar.gz.encrypted");
        //     File decryptedFile = new File("/home/Test/go1.17.7.linux-amd64.tar.gz_new.xml");
        //     FileEncryptionUtil.encryptFile(sourceFile, encryptedFile, secretKey,new ProgressTracker());
        //     FileEncryptionUtil.decryptFile(encryptedFile, decryptedFile, secretKey,new ProgressTracker());

        //     //log.debug(processCache.getAddProgressMap().get("/home/Test/go1.17.7.linux-amd64.tar.gz").toString());
        // } catch (IOException e) {
        //     e.printStackTrace();
        // } catch (NoSuchAlgorithmException e) {
        //     e.printStackTrace();
        // }

    }

    void decrypteFile() {

    }

}
