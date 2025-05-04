package org.hyeonqz.springtcplab.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ConfigurationProperties(prefix = "netty")
public class NettyProperties {

	@NotNull
	@Size(min = 1000, max = 65535)
	private int tcpPort;

	@NotNull
	@Min(1)
	private int bossCount;

	@NotNull
	@Min(2)
	private int workerCount;

	@NotNull
	private boolean KeepAlive;

	@NotNull
	private int backlog;

}
