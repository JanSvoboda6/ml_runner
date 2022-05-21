package com.jan.web.runner.result;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository class for obtaining the {@link Result};
 */
@Repository
public interface ResultRepository extends JpaRepository<Result, Long>
{
    Optional<Result> findByRunnerId(Long runnerId);
}
