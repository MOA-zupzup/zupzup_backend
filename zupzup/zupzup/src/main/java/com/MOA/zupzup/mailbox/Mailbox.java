package com.MOA.zupzup.mailbox;

import com.google.cloud.firestore.GeoPoint;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
public class Mailbox {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mailbox")
    private Long id;

    @Column(name = "centerLatitude")
    private double centerLatitude;

    @Column(name = "centerLongitude")
    private double centerLongitude;

    @Column(name = "radius")
    private double radius;

    @ElementCollection
    private List<Long> letterIds;

    public GeoPoint getLocation(){
        return new GeoPoint(centerLatitude, centerLongitude);
    }
}