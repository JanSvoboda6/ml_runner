package com.jan.web.docker;

import com.jan.web.runner.Runner;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContainerRepository extends JpaRepository<ContainerEntity, Long>
{
    boolean existsByUserId(Long userId);
}
