package fdu.mit.ipfs;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class IpfsApplication {

    public static void main(String[] args) {
        SpringApplication.run(IpfsApplication.class, args);
    }

}
