package fdu.mit.ipfs.controller;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fdu.mit.ipfs.cache.ProcessCache;
import fdu.mit.ipfs.dto.BaseResponse;
import fdu.mit.ipfs.dto.request.GetFileRequest;
import fdu.mit.ipfs.dto.request.SaveFileRequest;
import fdu.mit.ipfs.helper.ProgressTracker;
import fdu.mit.ipfs.service.IpfsService;
import fdu.mit.ipfs.util.FileEncryptionUtil;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping(value = "/ipfs")
@Slf4j
public class IPFSController {
    @Autowired
    ProcessCache processCache;

    @Autowired
    IpfsService ipfsService;

    @PostMapping(value = "/saveFile")
    public BaseResponse<Object> saveFile(@RequestBody SaveFileRequest request) {
        try {
            if (processCache.getAddProgressMap().get(request.getFilePath()) != null)
                return new BaseResponse<>("任务已经被添加");

            ipfsService.addFile(request.getAccount(), request.getFilePath());
            return new BaseResponse<>("任务添加成功");
        } catch (Exception e) {
            return new BaseResponse<>(e.toString());
        }

    }

    @PostMapping(value = "/getSaveProgress")
    public BaseResponse<Object> getSaveProgress(@RequestBody SaveFileRequest request) {
        if (processCache.getAddProgressMap().get(request.getFilePath()) == null)        
            return new BaseResponse<>(500,"","不存在对应任务");

        return new BaseResponse<>(processCache.getAddProgressMap().get(request.getFilePath()));
    }

    @PostMapping(value = "/getFile")
    public BaseResponse<Object> getFile(@RequestBody GetFileRequest request) {
        try {
            if (processCache.getGetProgressMap().get(request.getStorePath()) != null)
                return new BaseResponse<>("任务已经被添加");
            ipfsService.getFile(request.getHash(), request.getStorePath(), request.getKey());
            return new BaseResponse<>("任务添加成功");
        } catch (Exception e) {
            return new BaseResponse<>(e.toString());
        }

    }

    @PostMapping(value = "/getGetProgress")
    public BaseResponse<Object> getGetProgress(@RequestBody GetFileRequest request) {

        if (processCache.getGetProgressMap().get(request.getStorePath()) == null)
            return new BaseResponse<>(500,"","不存在对应任务");

        return new BaseResponse<>(processCache.getGetProgressMap().get(request.getStorePath()));
    }

    @GetMapping(value = "/testApi")
    public BaseResponse<Object> testApi() {
        try {
            ipfsService.myMethod();
            return new BaseResponse<>(null);
        } catch (Exception e) {
            return new BaseResponse<>(e.toString());
        }
        
    }

}
