package com.MOA.zupzup.mailbox;

import com.google.cloud.firestore.GeoPoint;
import lombok.*;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
public class Mailbox {

    private String id;
    private GeoPoint location;
    private double radius;
    private List<String> letterIds;
    private int letterCount;


    public Mailbox(String id, GeoPoint location, double radius, List<String> letterIds, int letterCount){
        this.id = id;
        this.location = location;
        this.radius = radius;
        this.letterIds = letterIds;
        this.letterCount = letterCount;
    }

    public void setId(String id){this.id = id;}

}