package org.hyeonqz.springtcplab.domain;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.springframework.stereotype.Repository;

import io.netty.channel.Channel;

@Repository
public class ChannelRepository {
	/** 공유자원을 사용하게 하는 -> Repository Role */
	private ConcurrentMap<String, Channel> channelCahce = new ConcurrentHashMap<>();

	public void put(String key, Channel value){
		channelCahce.put(key, value);
	}

	public Channel get(String key){
		return channelCahce.get(key);
	}

	public void remove(String key){
		this.channelCahce.remove(key);
	}

	public int size() {
		return this.channelCahce.size();
	}
}
