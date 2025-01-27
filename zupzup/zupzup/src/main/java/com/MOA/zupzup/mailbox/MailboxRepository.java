package com.MOA.zupzup.mailbox;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MailboxRepository extends JpaRepository<Mailbox, Long>{
    List<Mailbox> findByCenterLatitudeBetweenAndCenterLongitudeBetween(double minLat, double maxLat, double minLong, double maxLong);
}