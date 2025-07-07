package com.example.blogbackend.repository;

import com.example.blogbackend.domain.Contact;
import com.example.blogbackend.enums.ContactStatus;
import java.time.Instant;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ContactRepository extends JpaRepository<Contact, Long> {
  @Query("SELECT c FROM Contact c WHERE c.status = :status")
  List<Contact> findByStatus(@Param("status") ContactStatus status);

  @Modifying
  @Query("UPDATE Contact c SET c.status = :status, c.updatedAt = :instant WHERE c.id IN :ids")
  void updateStatusById(
      @Param("ids") List<Long> ids,
      @Param("status") ContactStatus status,
      @Param("instant") Instant instant);
}
