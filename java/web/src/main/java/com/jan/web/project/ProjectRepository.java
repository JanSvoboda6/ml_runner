package com.jan.web.project;

import com.jan.web.security.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for obtaining the {@link Project}.
 */
@Repository
public interface ProjectRepository extends JpaRepository<Project, Long>
{
    List<Project> findAllByUser(User user);
    Optional<Project> findByUserAndId(User user, Long id);
}