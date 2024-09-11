package org.example.globaledugroup.repository;

import org.example.globaledugroup.entity.view.Jobs;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobsRepository extends JpaRepository<Jobs, Long> {
}