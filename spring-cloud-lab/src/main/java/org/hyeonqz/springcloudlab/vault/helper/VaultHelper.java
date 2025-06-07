package org.hyeonqz.springcloudlab.vault.helper;

import java.util.Base64;
import java.util.Objects;

import org.springframework.stereotype.Component;
import org.springframework.vault.core.VaultKeyValueOperations;
import org.springframework.vault.core.VaultKeyValueOperationsSupport;
import org.springframework.vault.core.VaultTemplate;
import org.springframework.vault.core.VaultTransitOperations;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@Component
public class VaultHelper {

	private final VaultTransitOperations transitOperations;
	private final byte[] dek;

	public VaultHelper(VaultTemplate vaultTemplate) {
		this.transitOperations = vaultTemplate.opsForTransit("kek");

		VaultKeyValueOperations keyValueOperations  = vaultTemplate.opsForKeyValue("/dek", VaultKeyValueOperationsSupport.KeyValueBackend.KV_2);
		String encryptedDek = Objects.requireNonNull(keyValueOperations.get("vault").getRequiredData().get("1").toString());

		this.dek = loadDekFromVault(encryptedDek);
	}

	// DEK 복호화
	private byte[] loadDekFromVault(String encryptedDek) {
		return Base64.getDecoder().decode(transitOperations.decrypt("vault", encryptedDek));
	}

}