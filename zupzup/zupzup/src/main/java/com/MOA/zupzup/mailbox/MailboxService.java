package com.MOA.zupzup.mailbox;

import com.google.cloud.firestore.GeoPoint;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
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

        // Firestore에 데이터 추가
        Map<String, Object> data = firestoreService.convertMailboxToFirestoreData(mailbox);
        firestoreService.addDocument("mailboxes", mailboxId.toString(), data);
    }

    public List<Mailbox> getMailboxes() {
        // Firestore에서 데이터를 조회하는 방법
        List<QueryDocumentSnapshot> documents = firestoreService.getDocuments("mailboxes");
        return documents.stream()
                .map(doc -> {
                    // Firestore 문서 ID를 가져와서 Mailbox 객체에 설정
                    Mailbox mailbox = doc.toObject(Mailbox.class);
                    mailbox.setFirestoreId(doc.getId()); // firestoreId에 문서 ID 설정
                    // Firestore 문서 ID를 Long으로 변환하여 id 필드에 설정
                    mailbox.setId(Long.valueOf(doc.getId()));
                    return mailbox;
                })
                .collect(Collectors.toList());
    }
}
