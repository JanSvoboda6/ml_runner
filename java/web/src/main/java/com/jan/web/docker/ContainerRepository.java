package com.jan.web.docker;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for obtaining {@link ContainerEntity}.
 */
@Repository
public interface ContainerRepository extends JpaRepository<ContainerEntity, Long>
{
    boolean existsByUserId(Long userId);
    Optional<ContainerEntity> findByUserId(Long userId);;
}
