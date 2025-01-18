package com.MOA.zupzup.mailbox;

import com.MOA.zupzup.service.MailboxService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MailboxServiceTest {

    @Mock
    private MailboxRepository mailboxRepository;

    @InjectMocks
    private MailboxService mailboxService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetMailboxesNearby() {
        Mailbox mailbox1 = new Mailbox();
        mailbox1.setCenterLatitude(37.7749);
        mailbox1.setCenterLongitude(-122.4194);
        mailbox1.setRadius(5.0);
        mailbox1.setLetterIds(Arrays.asList(1L, 2L));

        Mailbox mailbox2 = new Mailbox();
        mailbox2.setCenterLatitude(37.7749);
        mailbox2.setCenterLongitude(-122.4194);
        mailbox2.setRadius(1.0);
        mailbox2.setLetterIds(Arrays.asList(3L, 4L));

        when(mailboxRepository.findAll()).thenReturn(Arrays.asList(mailbox1, mailbox2));

        double userLatitude = 37.7749;
        double userLongitude = -122.4194;

        List<Mailbox> nearbyMailboxes = mailboxService.getMailboxesNearby(userLatitude, userLongitude);

        assertEquals(2, nearbyMailboxes.size());
        assertTrue(nearbyMailboxes.contains(mailbox1));
        assertTrue(nearbyMailboxes.contains(mailbox2));
    }

    @Test
    void testAddLetterToMailbox() {
        Mailbox mailbox = new Mailbox();
        mailbox.setCenterLatitude(37.7749);
        mailbox.setCenterLongitude(-122.4194);
        mailbox.setRadius(5.0);
        mailbox.setLetterIds(Arrays.asList(1L, 2L));

        when(mailboxRepository.findById(anyLong())).thenReturn(java.util.Optional.of(mailbox));

        mailboxService.addLetterToMailbox(1L, 3L);

        verify(mailboxRepository, times(1)).save(mailbox);
        assertTrue(mailbox.getLetterIds().contains(3L));
    }

    @Test
    void testGetLettersInMailbox() {
        Mailbox mailbox = new Mailbox();
        mailbox.setCenterLatitude(37.7749);
        mailbox.setCenterLongitude(-122.4194);
        mailbox.setRadius(5.0);
        mailbox.setLetterIds(Arrays.asList(1L, 2L));

        when(mailboxRepository.findById(anyLong())).thenReturn(java.util.Optional.of(mailbox));

        List<Long> letterIds = mailboxService.getLettersInMailbox(1L);

        assertEquals(2, letterIds.size());
        assertTrue(letterIds.contains(1L));
        assertTrue(letterIds.contains(2L));
    }
}
