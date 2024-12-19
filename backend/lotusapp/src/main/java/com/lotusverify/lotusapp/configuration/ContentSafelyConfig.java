package com.lotusverify.lotusapp.configuration;

import com.azure.ai.contentsafety.ContentSafetyClient;
import com.azure.ai.contentsafety.ContentSafetyClientBuilder;
import com.azure.core.credential.AzureKeyCredential;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ContentSafelyConfig {
    private final Dotenv dotenv = Dotenv.load();
    private final String ENDPOINT = dotenv.get("CONTENT_SAFELY_ENDPOINT");
    private final String KEY = dotenv.get("CONTENT_SAFELY_KEY");

    @Bean
    public ContentSafetyClient contentSafetyClient() {
        if (KEY == null || ENDPOINT == null) {
            throw new IllegalArgumentException("CONTENT_SAFELY_KEY o CONTENT_SAFELY_ENDPOINT no están configurados");
        }
        return new ContentSafetyClientBuilder()
                .credential(new AzureKeyCredential(KEY))
                .endpoint(ENDPOINT)
                .buildClient();
    }
}
