package org.hyeonqz.springtcplab;

import org.hyeonqz.springtcplab.netty.TCPServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@SpringBootApplication
public class SpringTcpLabApplication {

	public static void main (String[] args) {
		SpringApplication.run(SpringTcpLabApplication.class, args);
	}

	private final TCPServer tcpServer;

	public ApplicationListener<ApplicationReadyEvent> readyEventApplicationListener() {
		return new ApplicationListener<ApplicationReadyEvent>() {
			@Override
			public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {
				tcpServer.start();
			}
		};
	}

}
