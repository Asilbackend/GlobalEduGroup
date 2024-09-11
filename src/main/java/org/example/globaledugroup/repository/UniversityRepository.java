package org.example.globaledugroup.repository;

import org.example.globaledugroup.entity.view.University;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UniversityRepository extends JpaRepository<University, Long> {
}