package com.MOA.zupzup.mailbox;

import com.google.cloud.firestore.QueryDocumentSnapshot;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/mailboxes")
@Tag(name= "우편함 API", description = "우편함을 관리하는 API")
public class MailboxController {

    private final MailboxService mailboxService;

    public MailboxController(MailboxService mailboxService) {
        this.mailboxService = mailboxService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "우편함 생성", description = "원하는 ID로 새로운 우편함을 생성합니다.")
    public String createMailbox(@RequestBody Mailbox mailbox, @RequestParam String mailboxId) {
        // 우편함 생성 후, 생성된 ID를 반환
        return mailboxService.createMailbox(mailbox, mailboxId);
    }

    @GetMapping("/{id}")
    @Operation(summary = "우편함 조회", description = "우편함 ID로 우편함을 조회합니다.")
    public Mailbox getMailbox(@PathVariable String id) {
        // 우편함 ID로 찾기, 없으면 null 반환
        Mailbox mailbox = mailboxService.findMailboxById(id);
        if (mailbox != null) {
            return mailbox;
        } else {
            return null; // 우편함을 못 찾은 경우 null 반환
        }
    }

    @GetMapping
    @Operation(summary = "모든 우편함 조회", description = "모든 우편함을 조회합니다.")
    public List<QueryDocumentSnapshot> getAllMailboxes() {
        // 모든 우편함 리스트 반환, 없으면 null 반환
        List<QueryDocumentSnapshot> mailboxes = mailboxService.findAllMailboxes();
        return mailboxes != null ? mailboxes : null; // 없으면 null 반환
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "우편함 업데이트", description = "우편함 ID로 우편함 정보를 업데이트합니다.")
    public void updateMailbox(@PathVariable String id, @RequestBody Mailbox mailbox) {
        // 우편함 ID로 찾아서 업데이트
        mailbox.setId(id);
        mailboxService.updateMailbox(mailbox);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "우편함 삭제", description = "우편함을 삭제합니다.")
    public void deleteMailbox(@PathVariable String id) {
        // 우편함 ID로 삭제
        mailboxService.deleteMailboxById(id);
    }
}
