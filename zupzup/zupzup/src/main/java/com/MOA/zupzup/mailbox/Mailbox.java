package com.MOA.zupzup.mailbox;

import lombok.Data;
import jakarta.persistence.*;
import java.util.List;

@Entity
@Data
public class Mailbox {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mailbox")
    private Long id;

    @Column(name = "centerLatitude", nullable = false)
    private double centerLatitude;

    @Column(name = "centerLongitude", nullable = false)
    private double centerLongitude;

    @Column(name = "radius", nullable = false)
    private double radius;

    @ElementCollection
    private List<Long> letterIds;
}