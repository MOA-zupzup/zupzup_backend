package com.MOA.zupzup.mailbox;

import com.MOA.zupzup.mailbox.Mailbox;
import com.MOA.zupzup.mailbox.MailboxRepository;
import com.MOA.zupzup.mailbox.FirestoreService;
import com.google.cloud.firestore.GeoPoint;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Service
public class MailboxService {

    private final MailboxRepository mailboxRepository;
    private final FirestoreService firestoreService;

    public MailboxService(MailboxRepository mailboxRepository, FirestoreService firestoreService) {
        this.mailboxRepository = mailboxRepository;
        this.firestoreService = firestoreService;
    }

    public List<Mailbox> getMailboxesNearby(double latitude, double longitude, double searchRadius) {
        List<Mailbox> allMailboxes = mailboxRepository.findAll();
        return allMailboxes.stream()
                .filter(mailbox -> calculateDistance(latitude, longitude, mailbox.getCenterLatitude(), mailbox.getCenterLongitude()) <= searchRadius)
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

        // Firestore에 편지 추가
        try {
            Map<String, Object> mailboxData = new HashMap<>();
            mailboxData.put("id", mailbox.getId());
            mailboxData.put("location", new GeoPoint(mailbox.getCenterLatitude(), mailbox.getCenterLongitude()));
            mailboxData.put("radius", mailbox.getRadius());
            mailboxData.put("letterIds", mailbox.getLetterIds());
            firestoreService.addDocument("mailboxes", mailboxId.toString(), mailboxData);
        } catch (Exception e) {
            // 예외 처리
            e.printStackTrace();
        }
    }

    public List<Mailbox> getFirestoredMailboxes() throws ExecutionException, InterruptedException {
        List<QueryDocumentSnapshot> documents = firestoreService.getDocuments("mailboxes");
        return documents.stream()
                .map(doc -> doc.toObject(Mailbox.class))
                .collect(Collectors.toList());
    }

    public List<Long> getLettersInMailbox(Long mailboxId) {
        Mailbox mailbox = mailboxRepository.findById(mailboxId).orElseThrow(() -> new IllegalArgumentException("Mailbox not found"));
        return mailbox.getLetterIds();
    }
}
