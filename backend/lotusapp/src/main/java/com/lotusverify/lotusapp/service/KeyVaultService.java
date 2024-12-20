package com.lotusverify.lotusapp.service;

import com.azure.security.keyvault.secrets.SecretClient;
import org.springframework.stereotype.Service;

@Service
public class KeyVaultService {
    private final SecretClient secretClient;

     public KeyVaultService(SecretClient secretClient) {
         this.secretClient = secretClient;
     }

     public String getSecret(String secretName) {
         return secretClient.getSecret(secretName).getValue();
     }
}
