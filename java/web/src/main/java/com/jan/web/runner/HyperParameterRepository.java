package com.jan.web.runner;

import org.springframework.data.jpa.repository.JpaRepository;

public interface HyperParameterRepository extends JpaRepository<HyperParameter, Long>
{
}
