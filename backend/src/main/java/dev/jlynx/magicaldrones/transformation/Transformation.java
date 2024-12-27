package dev.jlynx.magicaldrones.transformation;

import dev.jlynx.magicaldrones.auth.Account;
import dev.jlynx.magicaldrones.dto.TransformationView;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Objects;

@Getter @Setter
@NoArgsConstructor
@Entity
@Table(
        name = "transformation",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "source_image_id", name = "source_image_id_unique"),
                @UniqueConstraint(columnNames = "transformed_image_id", name = "transformed_image_id_unique"),
        }
)
public class Transformation {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "transformationId")
    @SequenceGenerator(name = "transformationId", sequenceName = "transformation_id_seq", allocationSize = 50)
    private Long id;

    /**
     * Datetime in UTC when the transformation request was submitted.
     */
    @Column(name = "started_at")
    private LocalDateTime startedAt;

    /**
     * Datetime in UTC when the transformation process was finished.
     */
    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    /**
     * A UUID of the source image file.
     */
    @Column(name = "source_image_id", nullable = false)
    private String sourceImageUuid;

    /**
     * A UUID of the transformed image file.
     */
    @Column(name = "transformed_image_id")
    private String transformedImageUuid;

    @Column(name = "title", nullable = false, length = 50)
    private String title;

    @Column(name = "description", length = 3000)
    private String description;

    @ManyToOne
    @JoinColumn(name = "account_id", foreignKey = @ForeignKey(name = "fk_transformation_account"))
    private Account account;

    public Transformation(
            LocalDateTime startedAt,
            LocalDateTime completedAt,
            String sourceImageUuid,
            String transformedImageUuid,
            String title,
            String description
    ) {
        this.startedAt = startedAt;
        this.completedAt = completedAt;
        this.sourceImageUuid = sourceImageUuid;
        this.transformedImageUuid = transformedImageUuid;
        this.title = title;
        this.description = description;
    }

    public TransformationView toDto() {
        return new TransformationView(
                getId(),
                getStartedAt(),
                getCompletedAt(),
                getSourceImageUuid(),
                getTransformedImageUuid(),
                getTitle(),
                getDescription()
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transformation that = (Transformation) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Transformation{" +
                "id=" + id +
                ", startedAt=" + startedAt +
                ", completedAt=" + completedAt +
                ", sourceImageUuid='" + sourceImageUuid + '\'' +
                ", transformedImageUuid='" + transformedImageUuid + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", account=" + account.getId() +
                '}';
    }
}
