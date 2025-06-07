package org.hyeonqz.springcloudlab.vault.config;

import jakarta.annotation.Nullable;
import lombok.NonNull;
import lombok.Setter;
import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.ssl.DefaultClientTlsStrategy;
import org.apache.hc.core5.ssl.SSLContexts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.vault.authentication.ClientAuthentication;
import org.springframework.vault.authentication.SessionManager;
import org.springframework.vault.authentication.TokenAuthentication;
import org.springframework.vault.client.RestTemplateBuilder;
import org.springframework.vault.client.RestTemplateFactory;
import org.springframework.vault.client.VaultEndpoint;
import org.springframework.vault.config.AbstractVaultConfiguration;
import org.springframework.vault.core.VaultTemplate;
import org.springframework.web.client.RestTemplate;

import java.util.function.Consumer;
import java.util.function.Function;


@Setter
@Configuration
public class VaultConfig extends AbstractVaultConfiguration {

	@Value("${spring.cloud.vault.host}")
	private String host;
	@Value("${spring.cloud.vault.port}")
	private int port;
	@Value("${spring.cloud.vault.token}")
	private String token;

	@Override
	@NonNull
	public VaultEndpoint vaultEndpoint() {
		// Vault 서버 연결 정보 생성
		return VaultEndpoint.create(host, port);
	}

	@Override
	@NonNull
	public ClientAuthentication clientAuthentication() {
		// Vault 인증에 사용할 토큰 설정
		return new TokenAuthentication(token);
	}

	@Bean
	@NonNull
	public RestTemplateFactory restTemplateFactory(ClientFactoryWrapper requestFactoryWrapper) {
		// RestTemplateFactory 생성 및 커스터마이징
		return new DefaultRestTemplateFactory(requestFactoryWrapper.getClientHttpRequestFactory(),
			it -> restTemplateBuilder(vaultEndpointProvider(), it));
	}

	@Bean
	@NonNull
	public VaultTemplate vaultTemplate() {
		// VaultTemplate Bean 생성
		return new VaultTemplate(
			restTemplateBuilder(vaultEndpointProvider(), clientHttpRequestFactoryWrapper().getClientHttpRequestFactory()),
			getBeanFactory().getBean("sessionManager", SessionManager.class)
		);
	}

	@Bean
	@NonNull
	public ClientFactoryWrapper clientHttpRequestFactoryWrapper() {
		// HttpComponentsClientHttpRequestFactory를 Wrapping한 Bean 생성
		return new ClientFactoryWrapper(createHttpClientWithDisabledSslVerification());
	}

	private HttpComponentsClientHttpRequestFactory createHttpClientWithDisabledSslVerification() {
		try {
			// SSL 검증을 비활성화한 HttpClient 생성
			HttpClient client = HttpClients.custom()
				.setConnectionManager(
					PoolingHttpClientConnectionManagerBuilder.create()
						.setTlsSocketStrategy(
							new DefaultClientTlsStrategy(
								SSLContexts.custom()
									.loadTrustMaterial(null, (chain, authType) -> true) // 모든 인증서를 신뢰
									.build(),
								(host, session) -> true // 모든 호스트를 신뢰
							)
						)
						.build()
				)
				.build();
			return new HttpComponentsClientHttpRequestFactory(client);
		} catch (Exception e) {
			// 예외 발생 시 IllegalStateException으로 래핑
			throw new IllegalStateException("Failed to create unsecured HTTP client", e);
		}
	}

	// RestTemplateFactory의 기본 구현체 (RestTemplate 커스터마이징 가능)
	static class DefaultRestTemplateFactory implements RestTemplateFactory {
		private final ClientHttpRequestFactory requestFactory;
		private final Function<ClientHttpRequestFactory, RestTemplateBuilder> builderFunction;

		public DefaultRestTemplateFactory(ClientHttpRequestFactory requestFactory,
			Function<ClientHttpRequestFactory, RestTemplateBuilder> builderFunction) {
			this.requestFactory = requestFactory;
			this.builderFunction = builderFunction;
		}

		@Override
		@NonNull
		public RestTemplate create(@Nullable Consumer<RestTemplateBuilder> customizer) {
			RestTemplateBuilder builder = this.builderFunction.apply(this.requestFactory);

			if (customizer != null) {
				customizer.accept(builder); // RestTemplate 커스터마이징
			}

			return builder.build();
		}
	}

}