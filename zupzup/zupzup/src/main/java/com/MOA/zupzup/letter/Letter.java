package com.MOA.zupzup.letter;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class Letter {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "letter_id")
    private Long id;

    private String title;

    private String content;
    private String locate;

    private LocalDateTime dateTime;

    @Enumerated(EnumType.STRING)
    private LetterStatus status;

    private String pictureURL;
}
