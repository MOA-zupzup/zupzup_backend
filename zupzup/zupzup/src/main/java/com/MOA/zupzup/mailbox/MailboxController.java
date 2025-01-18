package com.MOA.zupzup.mailbox;

import com.MOA.zupzup.mailbox.MailboxService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/mailbox")
public class MailboxController {

    @Autowired
    private MailboxService mailboxService;

    @GetMapping("/nearby")
    public List<Mailbox> getMailboxesNearby(@RequestParam double latitude,
                                            @RequestParam double longitude,
                                            @RequestParam double searchRadius){
        return mailboxService.getMailboxesNearby(latitude, longitude, searchRadius);
    }

    @PostMapping("/{mailboxId}/letters")
    public void addLetterToMailbox(@PathVariable Long mailboxId,
                                   @RequestParam Long letterId) {
        mailboxService.addLetterToMailbox(mailboxId, letterId);
    }

    @GetMapping("/{mailboxId}/letters")
    public List<Long> getLettersInMailbox(@PathVariable Long mailboxId) {
        return mailboxService.getLettersInMailbox(mailboxId);
    }
}