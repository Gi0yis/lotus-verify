package com.lotusverify.lotusapp.configuration;

import com.azure.ai.textanalytics.TextAnalyticsClient;
import com.azure.ai.textanalytics.TextAnalyticsClientBuilder;
import com.azure.core.credential.AzureKeyCredential;
import com.lotusverify.lotusapp.service.KeyVaultService;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TextAnalyticsConfig {

    final KeyVaultService keyVaultService;
    public TextAnalyticsConfig(KeyVaultService keyVaultService) {
        this.keyVaultService = keyVaultService;
    }

    @Bean
    public TextAnalyticsClient textAnalyticsClient() {

        return new TextAnalyticsClientBuilder()
                .credential(new AzureKeyCredential(keyVaultService.getSecret("TEXT-ANALYTICS-KEY")))
                .endpoint(keyVaultService.getSecret("TEXT-ANALYTICS-CLIENT"))
                .buildClient();
    }
}
