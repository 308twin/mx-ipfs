package fdu.mit.ipfs.schedule;

import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import fdu.mit.ipfs.cache.ProcessCache;
import fdu.mit.ipfs.dto.AddResDto;
import fdu.mit.ipfs.dto.GetResDto;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class RemoveHashSchedule {
    @Autowired
    ProcessCache addFileCache;

    /**
     * 延迟清理Cache
     */
    @Scheduled(fixedRate = 30000)
    public void removeAddedFileHash() {        
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

        for (Map.Entry<String, AddResDto> entry : addFileCache.getAddProgressMap().entrySet()) {            
            if (entry.getValue().getAddProgress().contains("100")) {    //通过add进度判断
                log.info("开始清理getAddProgressMap");
                scheduler.schedule(() -> {
                    // if (addFileCache.getAddProgressMap().get(entry.getKey()) != null
                    //         && addFileCache.getAddProgressMap().get(entry.getKey()).getProcess() == "100.00%") 
                            {
                        addFileCache.getAddProgressMap().remove(entry.getKey());
                        log.info("Removed file from Add Hashmap: " + entry.getKey());
                    }
                }, 30, TimeUnit.SECONDS);
            }
        }

        for (Map.Entry<String, GetResDto> entry : addFileCache.getGetProgressMap().entrySet()) {            
            if (entry.getValue().getDencryptProgress().contains("100")) {   //通过解密进度判断
                log.info("开始清理getGetProgressMap()");
                scheduler.schedule(() -> {
                            {
                        addFileCache.getGetProgressMap().remove(entry.getKey());
                        log.info("Removed file from Get Hashmap: " + entry.getKey());
                    }
                }, 30, TimeUnit.SECONDS);
            }
        }
    }
}
