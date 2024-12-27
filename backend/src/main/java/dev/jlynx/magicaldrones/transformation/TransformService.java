package dev.jlynx.magicaldrones.transformation;

import dev.jlynx.magicaldrones.auth.Account;
import dev.jlynx.magicaldrones.dto.CreateTransformation;
import dev.jlynx.magicaldrones.dto.TransformationView;
import dev.jlynx.magicaldrones.dto.UpdateTransformation;

import java.util.List;

public interface TransformService {

    /**
     * Persists a new {@link Transformation} object to database and returns its id.
     *
     * @param request a DTO object with request data
     * @param accountId id of the currently authenticated {@link Account}
     * @return the id of the persisted {@link Transformation}
     */
    long createTransformation(CreateTransformation request, long accountId);

    String passToModel(long transformationId, long accountId);

    List<TransformationView> getAll(long accountId);

    TransformationView getTransformation(long transformationId, long accountId);

    TransformationView updateTransformation(long transformationId, UpdateTransformation update, long accountId);

    void deleteTransformation(long transformationId, long accountId);
}
