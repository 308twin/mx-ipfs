package fdu.mit.ipfs.cache;

import lombok.Data;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import fdu.mit.ipfs.dto.AddResDto;
import fdu.mit.ipfs.dto.GetResDto;

import javax.annotation.PostConstruct;

@Data
@Component
public class ProcessCache {
    @PostConstruct
    public void init() {
        addProgressMap = new HashMap<String, AddResDto>();
        getProgressMap = new HashMap<String, GetResDto>();
    }

    private Map<String, AddResDto> addProgressMap = new HashMap<>();
    private Map<String, GetResDto> getProgressMap = new HashMap<>();
}
