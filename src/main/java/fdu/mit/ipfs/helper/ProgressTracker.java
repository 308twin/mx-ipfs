package fdu.mit.ipfs.helper;

import org.springframework.beans.factory.annotation.Autowired;

import fdu.mit.ipfs.cache.ProcessCache;
import fdu.mit.ipfs.dto.AddResDto;
import fdu.mit.ipfs.dto.GetResDto;
import fdu.mit.ipfs.enums.OperationType;
import fdu.mit.ipfs.util.FileEncryptionUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ProgressTracker implements FileEncryptionUtil.ProgressTracker {

    private ProcessCache processCache;

    public ProgressTracker(ProcessCache processCache) {
        this.processCache = processCache;
    }

    @Override
    public void updateProgress(double progress, String filePath, String key, OperationType type) {
        if (type.equals(OperationType.ADD)) {
            if (processCache != null && processCache.getAddProgressMap() != null) {
                log.debug("\r" + String.format("Decrypting: %.2f%%", progress));
                processCache.getAddProgressMap().put(filePath,
                        new AddResDto("0.00%", "", key, String.format("%.2f%%", progress)));
            }

        }
        if (type.equals(OperationType.GET)) {
            if (processCache != null && processCache.getAddProgressMap() != null) {
                processCache.getGetProgressMap().put(filePath,
                        new GetResDto("100.00%", String.format("%.2f%%", progress)));
            }

        }
    }
}
