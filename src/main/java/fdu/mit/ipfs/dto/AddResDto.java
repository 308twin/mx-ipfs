package fdu.mit.ipfs.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddResDto {
    private String addProgress;
    private String hash;
    private String key;
    private String encryptProgress;
}
