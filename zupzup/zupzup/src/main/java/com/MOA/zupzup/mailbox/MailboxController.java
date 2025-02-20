package com.MOA.zupzup.mailbox;

import com.google.cloud.firestore.QueryDocumentSnapshot;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/mailboxes")
public class MailboxController {

    private final MailboxService mailboxService;

    public MailboxController(MailboxService mailboxService) {
        this.mailboxService = mailboxService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public String createMailbox(@RequestBody Mailbox mailbox) {
        // 우편함 생성 후, 생성된 ID를 반환
        return mailboxService.createMailbox(mailbox);
    }

    @GetMapping("/{id}")
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
    public List<QueryDocumentSnapshot> getAllMailboxes() {
        // 모든 우편함 리스트 반환, 없으면 null 반환
        List<QueryDocumentSnapshot> mailboxes = mailboxService.findAllMailboxes();
        return mailboxes != null ? mailboxes : null; // 없으면 null 반환
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateMailbox(@PathVariable String id, @RequestBody Mailbox mailbox) {
        // 우편함 ID로 찾아서 업데이트
        mailbox.setId(id);
        mailboxService.updateMailbox(mailbox);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMailbox(@PathVariable String id) {
        // 우편함 ID로 삭제
        mailboxService.deleteMailboxById(id);
    }
}
