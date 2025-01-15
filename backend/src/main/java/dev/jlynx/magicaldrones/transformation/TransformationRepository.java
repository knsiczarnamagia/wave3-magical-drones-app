package dev.jlynx.magicaldrones.transformation;

import dev.jlynx.magicaldrones.dto.TransformationView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransformationRepository extends JpaRepository<Transformation, Long> {

    @Query("select new dev.jlynx.magicaldrones.dto.TransformationView(" +
            "t.id, t.startedAt, t.completedAt, t.sourceImageUuid, t.transformedImageUuid, t.title, t.description" +
            ") from Transformation t where t.account.id = ?1")
    List<TransformationView> findByAccount(Long id);
}
