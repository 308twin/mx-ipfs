package fdu.mit.ipfs.dto.request;

import lombok.Data;

@Data
public class GetFileRequest {
    private String hash;
    private String storePath;    
    private String key;
}
