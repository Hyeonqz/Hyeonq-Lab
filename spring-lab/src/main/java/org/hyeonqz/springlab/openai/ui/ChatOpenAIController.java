package org.hyeonqz.springlab.openai.ui;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api")
@RestController
public class ChatOpenAIController {
	private final ChatClient chatClient;
	private final ChatModel chatModel;

	public ChatOpenAIController (ChatClient chatClient, OpenAiChatModel chatModel) {
		this.chatClient = chatClient;
		this.chatModel = chatModel;
	}

	@PostMapping("/v1/call")
	public String call() {
		return chatModel.call("경기도 오산 날씨 알려줘");
	}

	@PostMapping("/v2/call")
	public String callV2 () {
		return chatClient.prompt()
			.user("경기도 오산 날씨 알려줘")
			.call()
			.content();
	}

}
