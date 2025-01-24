package dev.jlynx.magicaldrones.inference;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
public class HuggingFaceClientConfiguration {

    @Value("${hugging-face.api.token}")
    private String HUGGING_FACE_API_KEY;

    @Value("${hugging-face.api.base-url}")
    private String HUGGING_FACE_BASE_URL;

    @Bean
    public HuggingFaceClient huggingFaceClient() {
        RestClient restClient = RestClient.builder()
                .baseUrl(HUGGING_FACE_BASE_URL)
                .defaultHeader("Authorization", "Bearer " + HUGGING_FACE_API_KEY)
                .build();

        RestClientAdapter adapter = RestClientAdapter.create(restClient);
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(adapter).build();
        return factory.createClient(HuggingFaceClient.class);
    }
}
