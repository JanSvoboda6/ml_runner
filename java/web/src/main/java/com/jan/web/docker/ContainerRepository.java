package com.jan.web.docker;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ContainerRepository extends JpaRepository<ContainerEntity, Long>
{
    boolean existsByUserId(Long userId);
    ContainerEntity findByUserId(Long userId);
    Long findIdByUserId(Long userId);
}
