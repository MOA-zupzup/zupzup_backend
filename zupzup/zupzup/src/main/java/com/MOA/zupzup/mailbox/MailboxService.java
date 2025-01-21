package com.MOA.zupzup.mailbox;

import com.MOA.zupzup.mailbox.Mailbox;
import com.MOA.zupzup.mailbox.MailboxRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MailboxService {

    private final MailboxRepository mailboxRepository;

    public MailboxService(MailboxRepository mailboxRepository) {
        this.mailboxRepository = mailboxRepository;
    }

    public List<Mailbox> getMailboxesNearby(double latitude, double longitude) {
        List<Mailbox> allMailboxes = mailboxRepository.findAll();

        return allMailboxes.stream()
                .filter(mailbox -> calculateDistance(latitude, longitude, mailbox.getCenterLatitude(), mailbox.getCenterLongitude()) <= mailbox.getRadius())
                .collect(Collectors.toList());
    }

    public double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // 지구의 반경 (단위: km)
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c; // 결과 거리 (단위: km)
    }

    public void addLetterToMailbox(Long mailboxId, Long letterId) {
        Mailbox mailbox = mailboxRepository.findById(mailboxId).orElseThrow(() -> new IllegalArgumentException("Mailbox not found"));
        mailbox.getLetterIds().add(letterId);
        mailboxRepository.save(mailbox);
    }

    public List<Long> getLettersInMailbox(Long mailboxId) {
        Mailbox mailbox = mailboxRepository.findById(mailboxId).orElseThrow(() -> new IllegalArgumentException("Mailbox not found"));
        return mailbox.getLetterIds();
    }
}
