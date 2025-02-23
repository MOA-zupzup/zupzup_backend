package com.MOA.zupzup.mailbox;

import com.MOA.zupzup.global.exception.MailboxException;
import com.MOA.zupzup.global.exception.ErrorCode;
import com.google.cloud.firestore.GeoPoint;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/mailboxes")
@Tag(name = "우편함 API", description = "우편함을 관리하는 API")
public class MailboxController {

    private final MailboxService mailboxService;

    public MailboxController(MailboxService mailboxService) {
        this.mailboxService = mailboxService;
    }

    @PostMapping
    @Operation(summary = "우편함 생성", description = "원하는 ID로 새로운 우편함을 생성합니다.")
    public ResponseEntity<Mailbox> createMailbox(
            @RequestBody Mailbox mailbox,
            @RequestParam String mailboxId) {
        Mailbox createdMailbox = mailboxService.createMailbox(mailbox, mailboxId);
        URI location = URI.create("/api/mailboxes/" + createdMailbox.getId());  // 새로 생성된 우편함 URI
        return ResponseEntity.created(location).body(createdMailbox);  // 201 Created 상태와 함께 URI 반환
    }

    @GetMapping("/{id}/checkLocation")
    @Operation(summary = "우편함 위치 확인", description = "사용자가 우편함 반경에 있는지 확인합니다.")
    public ResponseEntity<Boolean> checkMailboxLocation(
            @PathVariable String id,
            @RequestParam @Parameter(description = "사용자 위치") GeoPoint userLocation) {
        Mailbox mailbox = mailboxService.getMailboxById(id);
        boolean isWithinRadius = mailboxService.isWithinRadius(userLocation, mailbox);
        return ResponseEntity.ok(isWithinRadius);
    }

    @GetMapping("/{id}")
    @Operation(summary = "우편함 조회", description = "우편함 ID로 우편함을 조회합니다.")
    public ResponseEntity<Mailbox> getMailbox(@PathVariable String id) {
        Mailbox mailbox = mailboxService.getMailboxById(id);
        if (mailbox == null) {
            throw new MailboxException(ErrorCode.MAILBOX_NOT_FOUND);  // 예외 처리
        }
        return ResponseEntity.ok(mailbox);
    }

    @GetMapping
    @Operation(summary = "모든 우편함 조회", description = "모든 우편함을 조회합니다.")
    public ResponseEntity<List<Mailbox>> getAllMailboxes() {
        List<QueryDocumentSnapshot> queryDocumentSnapshots = mailboxService.findAllMailboxes();

        // QueryDocumentSnapshot을 Mailbox로 변환
        List<Mailbox> mailboxes = queryDocumentSnapshots.stream()
                .map(doc -> doc.toObject(Mailbox.class)) // Firestore 문서를 Mailbox 객체로 변환
                .collect(Collectors.toList());

        if (mailboxes.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(mailboxes);
    }

    @PutMapping("/{id}")
    @Operation(summary = "우편함 업데이트", description = "우편함 ID로 우편함 정보를 업데이트합니다.")
    public ResponseEntity<Void> updateMailbox(
            @PathVariable String id,
            @RequestBody Mailbox mailbox) {
        if (!id.equals(mailbox.getId())) {
            throw new MailboxException(ErrorCode.MAILBOX_ID_MISMATCH);  // ID 불일치 예외 처리
        }
        mailboxService.updateMailbox(mailbox);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "우편함 삭제", description = "우편함을 삭제합니다.")
    public ResponseEntity<Void> deleteMailbox(@PathVariable String id) {
        mailboxService.deleteMailboxById(id);
        return ResponseEntity.noContent().build();
    }
}
