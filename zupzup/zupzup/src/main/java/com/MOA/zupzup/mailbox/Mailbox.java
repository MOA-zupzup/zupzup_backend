package com.MOA.zupzup.mailbox;

import com.google.cloud.firestore.GeoPoint;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Mailbox {

    @Setter
    private String id;
    private GeoPoint location;
    private double radius;
    private List<String> letterIds = new ArrayList<>();
    private int letterCount;

    public void addLetter(String letterId){
        this.letterIds.add(letterId);
        this.letterCount = this.letterIds.size();
    }

    public void removeLetter(String letterId){
        letterIds.remove(letterId);
        letterCount = letterIds.size();
    }

}