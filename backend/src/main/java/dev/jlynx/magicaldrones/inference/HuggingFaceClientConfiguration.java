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

    @Value("${hugging-face.api.inference-endpoints-url}")
    private String INFERENCE_ENDPOINTS_HF_URL;

    @Value("${hugging-face.api.serverless-url}")
    private String SERVERLESS_HF_URL;

    @Bean
    public ServerlessHuggingFaceClient serverlessHuggingFaceClient() {
        RestClient restClient = RestClient.builder()
                .baseUrl(SERVERLESS_HF_URL)
                .defaultHeader("Authorization", "Bearer " + HUGGING_FACE_API_KEY)
                .build();

        RestClientAdapter adapter = RestClientAdapter.create(restClient);
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(adapter).build();
        return factory.createClient(ServerlessHuggingFaceClient.class);
    }

    @Bean
    public InferenceEndpointsHuggingFaceClient inferenceEndpointsHuggingFaceClient() {
        RestClient restClient = RestClient.builder()
                .baseUrl(INFERENCE_ENDPOINTS_HF_URL)
                .defaultHeader("Authorization", "Bearer " + HUGGING_FACE_API_KEY)
                .build();
        RestClientAdapter adapter = RestClientAdapter.create(restClient);
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(adapter).build();
        return factory.createClient(InferenceEndpointsHuggingFaceClient.class);
    }
}
