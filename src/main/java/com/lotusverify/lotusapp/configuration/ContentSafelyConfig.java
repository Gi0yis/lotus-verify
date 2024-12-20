package com.lotusverify.lotusapp.configuration;

import com.azure.ai.contentsafety.ContentSafetyClient;
import com.azure.ai.contentsafety.ContentSafetyClientBuilder;
import com.azure.core.credential.AzureKeyCredential;
import com.lotusverify.lotusapp.service.KeyVaultService;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ContentSafelyConfig {
    private final KeyVaultService keyVaultService;

    public ContentSafelyConfig(KeyVaultService keyVaultService) {
        this.keyVaultService = keyVaultService;
    }

    @Bean
    public ContentSafetyClient contentSafetyClient() {

        return new ContentSafetyClientBuilder()
                .credential(new AzureKeyCredential(keyVaultService.getSecret("CONTENT-SAFELY-ENDPOINT")))
                .endpoint(keyVaultService.getSecret("CONTENT-SAFELY-KEY"))
                .buildClient();
    }
}
