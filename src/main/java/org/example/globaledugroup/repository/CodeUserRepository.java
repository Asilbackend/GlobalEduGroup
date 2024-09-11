package org.example.globaledugroup.repository;

import org.example.globaledugroup.entity.CodeUser;
import org.example.globaledugroup.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CodeUserRepository extends JpaRepository<CodeUser, Long> {
    Optional<CodeUser> findByCode(String data);
}