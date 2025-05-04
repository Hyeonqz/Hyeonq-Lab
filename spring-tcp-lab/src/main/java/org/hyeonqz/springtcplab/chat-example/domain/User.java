package org.hyeonqz.springtcplab.domain;

import io.netty.channel.Channel;
import io.netty.util.AttributeKey;
import lombok.Getter;
import lombok.NonNull;

public class User {
	public static final AttributeKey<User> USER_ATTRIBUTE_KEY = AttributeKey.newInstance("USER");

	@Getter
	private final String username;
	private final Channel channel;

	private User(String username, Channel channel){
		this.username = username;
		this.channel = channel;
	}

	public static User of(@NonNull String loginCommand, @NonNull Channel channel) {
		if (loginCommand.startsWith("login ")) {
			return new User(loginCommand.trim().substring("login ".length()),channel );
		}

		throw new IllegalArgumentException("loginCommand ["+loginCommand+"] can not be accepted");
	}

	public void login(org.hyeonqz.springtcplab.domain.ChannelRepository channelRepository, Channel channel) {
		channel.attr(USER_ATTRIBUTE_KEY).set(this);
		channelRepository.put(this.username, channel);
	}

	public void logout(org.hyeonqz.springtcplab.domain.ChannelRepository channelRepository, Channel channel) {
		channel.attr(USER_ATTRIBUTE_KEY).getAndSet(null);
		channelRepository.remove(this.username);
	}

	public static User current(Channel channel) {
		User user = channel.attr(USER_ATTRIBUTE_KEY).get();
		if ( user == null ){
			throw new RuntimeException("User is null");
		}
		return user;
	}

	public void tell(Channel targetChannel, @NonNull String username, @NonNull String message) {
		if (targetChannel != null) {
			targetChannel.write(this.username);
			targetChannel.write(">");
			targetChannel.writeAndFlush(message + "\n\r");
			this.channel.writeAndFlush("The message was sent to ["+username+"] successfully.\r\n");
		}else{
			this.channel.writeAndFlush("No user named with ["+ username +"].\r\n");
		}
	}
}
