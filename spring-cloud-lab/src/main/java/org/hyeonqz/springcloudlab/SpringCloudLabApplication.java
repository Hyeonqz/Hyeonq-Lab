package org.hyeonqz.springcloudlab;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@EnableEurekaServer // eureka server 활성화
@SpringBootApplication
public class SpringCloudLabApplication {

	public static void main (String[] args) {
		SpringApplication.run(SpringCloudLabApplication.class, args);
	}

}
