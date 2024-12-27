package dev.jlynx.magicaldrones.inference;

import dev.jlynx.magicaldrones.exception.InternalServerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

//@Profile("dev")
@Service
public class NoopModel implements ImageToImageService {

    private static final Logger log = LoggerFactory.getLogger(NoopModel.class);

    @Override
    public byte[] transform(byte[] sourceFile) {
        log.debug("No-op model inference invoked.");
        try {
            log.debug("Artificial thread sleep began.");
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            log.debug("Error occurred during artificial thread sleep. InterruptedException was thrown.");
            throw new InternalServerException("Error during artificial thread sleep", e);
        }
        log.debug("Returning bytes successfully.");
        return sourceFile;
    }
}
