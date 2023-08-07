package fdu.mit.ipfs.dto.request;

import lombok.Data;

@Data
public class SaveFileRequest {
    private String account;
    private String filePath;
}
