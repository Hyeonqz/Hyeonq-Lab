package org.hyeonqz.springcloudlab.vault.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.hyeonqz.springcloudlab.vault.helper.VaultHelper;
import org.springframework.stereotype.Component;
import org.springframework.vault.core.*;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.*;


@Slf4j
@RequiredArgsConstructor
@Component
public class VaultUtil {
	private final VaultHelper vaultHelper;
	private final VaultTemplate vaultTemplate;
	private final VaultOperations vaultOperations;

	/**
	 * 사용자 입력을 DEK로 직접 암호화 (Base64로 인코딩된 문자열 반환)
	 */
	public String encryptWithDekByPlainText(String plaintext) {
		try {
			log.info("PlainText: {}", plaintext);
			log.info("DEK : {}" ,vaultHelper.getDek());
			SecretKey secretKey = new SecretKeySpec(vaultHelper.getDek(), "AES");
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			cipher.init(Cipher.ENCRYPT_MODE, secretKey);

			// 평문 암호화
			byte[] encryptedData = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));
			return Base64.getEncoder().encodeToString(encryptedData);
		} catch (Exception e) {
			throw new RuntimeException("AES ECB 256-bit 암호화 오류", e);
		}
	}

	/**
	 * 암호화된 데이터를 DEK로 복호화 (Base64로 인코딩된 데이터를 원본 문자열로 복원)
	 */
	public String decryptWithDekByEncryptedText(String encryptedText) {
		try {
			byte[] cipherData = Base64.getDecoder().decode(encryptedText);
			SecretKey secretKey = new SecretKeySpec(vaultHelper.getDek(), "AES");
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			cipher.init(Cipher.DECRYPT_MODE, secretKey);

			byte[] decryptedData = cipher.doFinal(cipherData);
			return new String(decryptedData);
		} catch (Exception e) {
			throw new RuntimeException("AES 복호화 오류", e);
		}
	}

	public Map<String, Object> getSecretData(String path) {
		return Optional.ofNullable(vaultOperations.read(path))
			.map(response -> Objects.requireNonNull(response.getData()).get("data"))
			.filter(data -> data instanceof Map)
			.map(data -> (Map<String, Object>) data)
			.orElse(Collections.emptyMap());
	}

	// DEK 를 새로 생성한다.
	public void createDek() {
		VaultTransitOperations transit = vaultTemplate.opsForTransit("kek");

		// 1. 32바이트(256비트) 랜덤 키 생성
		byte[] newKey = new byte[32];
		new SecureRandom().nextBytes(newKey);

		// 2. Base64로 인코딩
		String base64Key = Base64.getEncoder().encodeToString(newKey);

		// 3. Transit 엔진으로 암호화
		String encryptedKey = transit.encrypt("vault", base64Key);

		// 4. KV 엔진에 저장
		VaultKeyValueOperations keyValueOps = vaultTemplate.opsForKeyValue("/dek",
			VaultKeyValueOperationsSupport.KeyValueBackend.KV_2);

		Map<String, Object> data = new HashMap<>();
		data.put("1", encryptedKey);
		keyValueOps.put("vault", data);

		log.info("New DEK generated and stored in Vault");
	}

}