package org.example.globaledugroup.repository;

import org.example.globaledugroup.entity.DeletingUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface DeletingUserRepository extends JpaRepository<DeletingUser, Long> {
    List<DeletingUser> findByFromUserId(Long id );

    List<DeletingUser> findByDeleteUserId(Long id);
}
