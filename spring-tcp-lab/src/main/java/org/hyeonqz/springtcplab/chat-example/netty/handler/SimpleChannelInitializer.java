package org.hyeonqz.springtcplab.netty.handler;

import org.springframework.stereotype.Component;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class SimpleChannelInitializer extends ChannelInitializer<SocketChannel> {


	@Override
	protected void initChannel (SocketChannel socketChannel) throws Exception {

	}

}
