package com.jan.web.runner.parameter;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository class for obtaining the {@link HyperParameter}.
 */
public interface HyperParameterRepository extends JpaRepository<HyperParameter, Long>
{
}
