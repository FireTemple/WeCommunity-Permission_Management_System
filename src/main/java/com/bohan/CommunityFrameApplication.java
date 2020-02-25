package com.bohan;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.bohan.mapper")
public class CommunityFrameApplication {

    public static void main(String[] args) {
        SpringApplication.run(CommunityFrameApplication.class, args);
    }

}
