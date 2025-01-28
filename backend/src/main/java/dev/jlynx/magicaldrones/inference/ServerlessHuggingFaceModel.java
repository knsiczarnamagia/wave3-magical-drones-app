package dev.jlynx.magicaldrones.inference;

import dev.jlynx.magicaldrones.dto.HuggingFaceRequest;
import dev.jlynx.magicaldrones.exception.InternalServerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Base64;

@Service
public class ServerlessHuggingFaceModel implements ImageToImageService {

    private static final Logger log = LoggerFactory.getLogger(ServerlessHuggingFaceModel.class);
    private static final String MODEL_ID = "stabilityai/stable-diffusion-xl-refiner-1.0";

    private final ServerlessHuggingFaceClient client;

    @Autowired
    public ServerlessHuggingFaceModel(ServerlessHuggingFaceClient client) {
        this.client = client;
    }

    @Override
    public byte[] transform(byte[] image) {
        String encodedImage = Base64.getEncoder().encodeToString(image);
        ResponseEntity<byte[]> response = client.sendImage(MODEL_ID, new HuggingFaceRequest(encodedImage));
        if (response.getStatusCode().is4xxClientError() || response.getStatusCode().is5xxServerError()) {
            log.debug("Hugging Face Serverless Endpoints client returned error status: {}.", response.getStatusCode().value());
            throw new InternalServerException("Hugging Face Serverless Endpoints client failed to process image (status: %d)."
                    .formatted(response.getStatusCode().value()));
        }
        log.trace("Hugging Face Serverless Endpoints client successfully returned generated image (status: {}).", response.getStatusCode().value());
        return response.getBody();
    }
}
