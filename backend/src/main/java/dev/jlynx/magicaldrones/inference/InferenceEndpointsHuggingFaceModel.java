package dev.jlynx.magicaldrones.inference;

import dev.jlynx.magicaldrones.dto.HuggingFaceRequest;
import dev.jlynx.magicaldrones.exception.InternalServerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Base64;

@Service
public class InferenceEndpointsHuggingFaceModel implements ImageToImageService {

    private static final Logger log = LoggerFactory.getLogger(InferenceEndpointsHuggingFaceModel.class);

    private final InferenceEndpointsHuggingFaceClient client;

    @Autowired
    public InferenceEndpointsHuggingFaceModel(InferenceEndpointsHuggingFaceClient client) {
        this.client = client;
    }

    @Override
    public byte[] transform(byte[] image) {
        String encodedImage = Base64.getEncoder().encodeToString(image);
        var response = client.sendImage(new HuggingFaceRequest(encodedImage));
        if (response.getStatusCode().is4xxClientError() || response.getStatusCode().is5xxServerError()) {
            log.debug("Hugging Face Inference Endpoints client returned error status: {}.", response.getStatusCode().value());
            throw new InternalServerException("Hugging Face Inference Endpoints client failed to process image (status: %d)."
                    .formatted(response.getStatusCode().value()));
        }
        log.trace("Hugging Face Serverless Endpoints client successfully returned generated image (status: {}).", response.getStatusCode().value());
        String base64Output = response.getBody().output();
        log.trace("Base64 output decoded");
        return Base64.getDecoder().decode(base64Output);
    }
}
