package org.example.globaledugroup.repository;

import org.example.globaledugroup.entity.Administrator;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AdministratorRepository extends JpaRepository<Administrator, Long> {
    Optional<Administrator> findByUserId(Long id);

    //Optional<Administrator> findByTelegramUserId(Long chatId);
}
