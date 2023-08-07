package fdu.mit.ipfs.util;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class StringUtil {
    public static String abstractProgress(String progressText){
        int percentIndex = progressText.lastIndexOf("%");
        int spaceIndex = progressText.lastIndexOf(" ", percentIndex);
        return progressText.substring(spaceIndex + 1, percentIndex + 1);
    }

    public static String abstractIPFSHash(String progressText){
        int firstSpaceIndex = progressText.indexOf(' ');
        int secondSpaceIndex = progressText.indexOf(' ', firstSpaceIndex + 1);
        try {
            if(progressText.contains("added")){
            log.info(progressText);
             //if (firstSpaceIndex != -1 && secondSpaceIndex != -1) {
            // 提取两个空格之间的内容
            String extractedHash = progressText.substring(firstSpaceIndex + 1, secondSpaceIndex);
            log.info("提取的哈希值: " + extractedHash);
            return extractedHash;
        } else {
            log.info("未找到哈希值");                        
            return null;
        }
        } catch (Exception e) {
           return null;
        }
       

        
    }
}
