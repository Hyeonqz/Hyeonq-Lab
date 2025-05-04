package org.hyeonqz.springtcplab.config;

import java.net.InetSocketAddress;

import org.hyeonqz.springtcplab.netty.handler.SimpleChannelInitializer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@EnableConfigurationProperties(org.hyeonqz.springtcplab.config.NettyProperties.class)
@Configuration
public class NettyConfiguration {
	private final org.hyeonqz.springtcplab.config.NettyProperties nettyProperties;

	@Bean(name = "serverBootstrap")
	public ServerBootstrap bootstrap(SimpleChannelInitializer simpleChannelInitializer) {
		ServerBootstrap b = new ServerBootstrap();
		b.group(bossGroup(), workerGroup())
			.channel(NioServerSocketChannel.class)
			.handler(new LoggingHandler(LogLevel.DEBUG))
			.childHandler(simpleChannelInitializer);
		b.option(ChannelOption.SO_BACKLOG, nettyProperties.getBacklog());
		return b;
	}

	@Bean(destroyMethod = "shutdownGracefully")
	public NioEventLoopGroup bossGroup() {
		return new NioEventLoopGroup(nettyProperties.getBossCount());
	}

	@Bean(destroyMethod = "shutdownGracefully")
	public NioEventLoopGroup workerGroup() {
		return new NioEventLoopGroup(nettyProperties.getWorkerCount());
	}

	@Bean
	public InetSocketAddress tcpSocketAddress() {
		return new InetSocketAddress(nettyProperties.getTcpPort());
	}

}
