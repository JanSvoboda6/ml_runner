package com.jan.web.docker;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ContainerRepository extends JpaRepository<ContainerEntity, Long>
{
    boolean existsByUserId(Long userId);
    Optional<ContainerEntity> findByUserId(Long userId);;
}
