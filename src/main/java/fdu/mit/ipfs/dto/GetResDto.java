package fdu.mit.ipfs.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetResDto {
    private String getProgress;
    private String dencryptProgress;
}
