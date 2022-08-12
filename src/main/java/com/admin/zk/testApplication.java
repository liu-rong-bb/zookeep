package com.admin.zk;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
// @EnableFeignClients
// @EnableDiscoveryClient
public class testApplication extends SpringBootServletInitializer {


    public static void main(String[] args) {
        new SpringApplicationBuilder(testApplication.class).web(WebApplicationType.SERVLET).run(args);
    }

}
