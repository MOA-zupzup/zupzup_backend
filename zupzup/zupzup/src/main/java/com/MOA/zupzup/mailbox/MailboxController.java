package com.MOA.zupzup.mailbox;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/mailboxes")
public class MailboxController {

    private final MailboxService mailboxService;

    @Autowired
    public MailboxController(MailboxService mailboxService){
        this.mailboxService = mailboxService;
    }

    @PostMapping
    public String createMailbox(@RequestBody Mailbox mailbox) throws ExecutionException, InterruptedException{
        mailboxService.saveMailbox(mailbox);
        return "Mailbox가 성공적으로 저장되었습니다!";
    }

    @GetMapping("/{id}")
    public Mailbox getMailbox(@PathVariable String id) throws ExecutionException, InterruptedException{
        return mailboxService.getMailboxById(id);
    }
}