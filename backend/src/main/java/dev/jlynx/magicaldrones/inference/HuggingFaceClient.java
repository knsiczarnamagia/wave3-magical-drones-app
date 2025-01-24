package dev.jlynx.magicaldrones.inference;

import dev.jlynx.magicaldrones.dto.HuggingFaceRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.PostExchange;

public interface HuggingFaceClient {

    @PostExchange("/models/{modelId}")
    ResponseEntity<byte[]> sendImage(@PathVariable String modelId, @RequestBody HuggingFaceRequest request);
}
