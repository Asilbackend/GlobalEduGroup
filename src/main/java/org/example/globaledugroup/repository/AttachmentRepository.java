package org.example.globaledugroup.repository;

import org.example.globaledugroup.entity.view.Attachment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AttachmentRepository extends JpaRepository<Attachment, Long> {
}