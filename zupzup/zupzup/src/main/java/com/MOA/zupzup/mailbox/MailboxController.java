package com.MOA.zupzup.mailbox;

import com.MOA.zupzup.mailbox.Mailbox;
import com.MOA.zupzup.mailbox.MailboxService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
public class MailboxController {

    private final MailboxService mailboxService;

    public MailboxController(MailboxService mailboxService) {
        this.mailboxService = mailboxService;
    }

    @GetMapping("/getFirestoredMailboxes")
    public List<Mailbox> getFirestoredMailboxes() throws ExecutionException, InterruptedException {
        return mailboxService.getFirestoredMailboxes();
    }
}
