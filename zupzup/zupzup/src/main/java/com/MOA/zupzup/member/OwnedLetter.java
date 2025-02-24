package com.MOA.zupzup.member;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class OwnedLetter {

    private String name;
    private String imageUrl;
    private int count;
    private boolean isBasic;

    public OwnedLetter(String name, String imageUrl, int count, boolean isBasic) {
        this.name = name;
        this.imageUrl = imageUrl;
        this.count = count;
        this.isBasic = isBasic;
    }
}