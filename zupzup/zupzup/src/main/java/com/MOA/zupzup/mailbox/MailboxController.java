package com.MOA.zupzup.mailbox;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
public class MailboxController {

    @Autowired
    private MailboxService mailboxService;

    @GetMapping("/getMailboxes")
    public List<Mailbox> getMailboxes() throws ExecutionException, InterruptedException {
        return mailboxService.getMailboxes();
    }
}
