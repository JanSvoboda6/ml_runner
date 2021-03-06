package com.jan.web.project.label;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for obtaining the {@link ClassificationLabel}.
 */
@Repository
public interface ClassificationLabelRepository extends JpaRepository<ClassificationLabel, Long>
{
}
