package com.lotusverify.lotusapp.configuration;

import com.azure.ai.textanalytics.TextAnalyticsClient;
import com.azure.ai.textanalytics.TextAnalyticsClientBuilder;
import com.azure.core.credential.AzureKeyCredential;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TextAnalyticsConfig {

    private static final Dotenv dotenv = Dotenv.load();
    private final String ENDPOINT = dotenv.get("TEXT_ANALYTICS_CLIENT");
    private final String API_KEY = dotenv.get("TEXT_ANALYTICS_KEY");

    @Bean
    public TextAnalyticsClient textAnalyticsClient() {
        assert API_KEY != null;
        assert ENDPOINT != null;

        return new TextAnalyticsClientBuilder()
                .credential(new AzureKeyCredential(API_KEY))
                .endpoint(ENDPOINT)
                .buildClient();
    }
}
