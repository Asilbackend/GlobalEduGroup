package org.example.globaledugroup.repository;

import org.example.globaledugroup.entity.Operator;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OperatorRepository extends JpaRepository<Operator, Long> {
    Optional<Operator> findByUserId(Long id);


    //Optional<Operator> findByTelegramUserId(Long id);
}