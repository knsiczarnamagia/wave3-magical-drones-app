package dev.jlynx.magicaldrones.inference;

import dev.jlynx.magicaldrones.dto.HuggingFaceRequest;
import dev.jlynx.magicaldrones.dto.InferenceEndpointsHuggingFaceResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.PostExchange;

public interface InferenceEndpointsHuggingFaceClient {

    @PostExchange
    ResponseEntity<InferenceEndpointsHuggingFaceResponse> sendImage(@RequestBody HuggingFaceRequest request);
}
