package com.MOA.zupzup.mailbox;

import com.google.cloud.firestore.GeoPoint;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Mailbox {
    private String id;
    private GeoPoint location;
    private double radius;
    private List<String> letterIds;
}