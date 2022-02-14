package com.jan.web.project;

import com.jan.web.security.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long>
{
    List<Project> findAllByUser(User user);
}