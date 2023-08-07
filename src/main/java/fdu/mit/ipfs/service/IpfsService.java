package fdu.mit.ipfs.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import javax.crypto.SecretKey;
import javax.management.RuntimeErrorException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import fdu.mit.ipfs.cache.ProcessCache;
import fdu.mit.ipfs.dto.AddResDto;
import fdu.mit.ipfs.dto.GetResDto;
import fdu.mit.ipfs.helper.ProgressTracker;
import fdu.mit.ipfs.schedule.RemoveHashSchedule;
import fdu.mit.ipfs.util.FileEncryptionUtil;
import fdu.mit.ipfs.util.StringUtil;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class IpfsService {

    @Autowired
    ProcessCache processCache;

    public void addFile(String account, String filePath) throws Exception {
        try {
            log.info("Start add file,account:" + account + ", file name:" + filePath);
            // 构建命令

            Thread outputThread = new Thread(() -> {
                try {
                    processCache.getAddProgressMap().put(filePath, null);
                    log.info("Start encrypt file");
                    // 加密
                    SecretKey secretKey = FileEncryptionUtil.generateSecretKey();
                    byte[] keyBytes = secretKey.getEncoded();
                    String keyString = Base64.getEncoder().encodeToString(keyBytes);
                    String encryptedFilePath = filePath + ".encrypted";

                    File sourceFile = new File(filePath);
                    File encryptedFile = new File(filePath + ".encrypted");
                    FileEncryptionUtil.encryptFile(sourceFile, encryptedFile, secretKey,
                            new ProgressTracker(processCache));

                    log.info("Start ipfs add");
                    // 执行ipfs add
                    ProcessBuilder processBuilder = new ProcessBuilder("ipfs", "add", encryptedFilePath);
                    processBuilder.redirectErrorStream(true); // 合并流
                    Process process = processBuilder.start();
                    InputStream stdout = process.getInputStream();
                    BufferedReader stdoutReader = new BufferedReader(new InputStreamReader(stdout));
                    String hash = "";
                    String line;
                    while ((line = stdoutReader.readLine()) != null) {
                        if (line.contains("%")) {
                            processCache.getAddProgressMap().put(filePath,
                                    new AddResDto(StringUtil.abstractProgress(line), null, keyString, "100.00%"));
                            log.debug(StringUtil.abstractProgress(line));
                        }
                        if (line.contains("add")) {
                            hash = StringUtil.abstractIPFSHash(line);
                            log.debug(StringUtil.abstractIPFSHash(line));
                        }
                    }
                    processCache.getAddProgressMap().put(filePath,
                            new AddResDto("100.00%", hash, keyString, "100.00%"));
                    stdoutReader.close();
                    int exitCode = process.waitFor();

                    // Pin IPFS file to network
                    log.info("Start pin: " + hash);
                    ProcessBuilder pinBuilder = new ProcessBuilder("ipfs", "pin", "add", hash);
                    pinBuilder.start();

                    encryptedFile.delete(); // 删除加密后的本地文件
                    if (exitCode == 0) {
                        System.out.println("Command executed successfully");
                    } else {
                        System.out.println("Command execution failed");
                    }

                } catch (Exception e) {
                    log.info("Exception ,delete hash map;");
                    processCache.getAddProgressMap().remove(filePath);
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }
            });

            outputThread.start();

        } catch (Exception e) {
            processCache.getAddProgressMap().remove(filePath);
            throw new RuntimeException(e);
            // 处理 IOException 异常
        }
    }

    public void getFile(String hash, String filePath, String key) throws Exception {
        try {
            log.info("Start get file,hash:" + hash + ",file path:" + filePath);
            ProcessBuilder processBuilder = new ProcessBuilder("ipfs", "get", hash,
                    "--output=" + filePath + ".encrypted");
            processBuilder.redirectErrorStream(true); // 合并流
            Process process = processBuilder.start();
            Thread outputThread = new Thread(() -> {
                try {
                    InputStream stdout = process.getInputStream();
                    BufferedReader stdoutReader = new BufferedReader(new InputStreamReader(stdout));
                    String line;
                    while ((line = stdoutReader.readLine()) != null) {
                        if (line.contains("%")) {// 先开始下载，此时解密进度位0
                            processCache.getGetProgressMap().put(filePath,
                                    new GetResDto(StringUtil.abstractProgress(line), "0.00%"));
                            log.debug("Get progress:" + StringUtil.abstractProgress(line));
                        }
                    }
                    stdoutReader.close();
                    int exitCode = process.waitFor();

                    if (exitCode == 0) {
                        System.out.println("Command executed successfully");
                    } else {
                        System.out.println("Command execution failed");
                    }

                    // 解密
                    log.info("Start decrypt file");
                    File encryptedFile = new File(filePath + ".encrypted");
                    FileEncryptionUtil.decryptFile(encryptedFile, new File(filePath),
                            FileEncryptionUtil.convertStringToSecretKey(key, FileEncryptionUtil.AES_ALGORITHM),
                            new ProgressTracker(processCache));
                    processCache.getGetProgressMap().put(filePath,
                            new GetResDto("100.00%", "100.00%"));
                    log.info("Delete encrypted file");
                    // 删除加密的暂存文件
                    encryptedFile.delete();

                } catch (Exception e) {
                    log.info("Exception ,delete hash map;");
                    processCache.getGetProgressMap().remove(filePath);
                    throw new RuntimeException(e);
                }
            });

            outputThread.start();

        } catch (IOException e) {
            processCache.getGetProgressMap().remove(filePath);
            throw new RuntimeException(e);
            // 处理 IOException 异常
        }

    }

    public void myMethod() {
        // 执行过程中发生异常
        throw new RuntimeException("An error occurred during execution");
    }
}
