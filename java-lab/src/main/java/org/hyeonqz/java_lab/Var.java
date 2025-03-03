package org.hyeonqz.java_lab;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.Charset;
import java.util.concurrent.CompletableFuture;

public class Var {
	public static void main (String[] args) throws URISyntaxException, IOException, InterruptedException {
		var client = HttpClient.newBuilder().build();
		var uri = new URI("https://naver.com");
		var request = HttpRequest.newBuilder(uri).build();

		// http/1.1  동기
		var response = client.send(
			request,
			HttpResponse.BodyHandlers.ofString(
				Charset.defaultCharset()
			)
		);

		System.out.println(response);
		System.out.println(response.body());
		System.out.println(response.uri());

		// http/2 비동기
		var handler = HttpResponse.BodyHandlers.ofString();
		CompletableFuture.allOf( // 모든 요청이 완료될 때 까지 대기
			client.sendAsync(request, handler)
				.thenAccept((res) -> System.out.println(res.body())),
			client.sendAsync(request, handler)
				.thenAccept((res) -> System.out.println(res.body())),
			client.sendAsync(request, handler)
				.thenAccept((res) -> System.out.println(res.body()))
		).join();


	}
}
