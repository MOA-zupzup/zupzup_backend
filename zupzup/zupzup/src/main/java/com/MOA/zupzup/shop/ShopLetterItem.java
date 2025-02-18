package com.MOA.zupzup.shop;

import com.google.cloud.Timestamp;
import com.google.cloud.firestore.annotation.DocumentId;
import com.google.cloud.firestore.annotation.PropertyName;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ShopLetterItem {

    @DocumentId
    private String id;

    @PropertyName("name")
    private String name;

    @PropertyName("imageUrl")
    private String imageUrl;

    @PropertyName("price")
    private int price;

    public ShopLetterItem(String id, String name,String imageUrl, int price) {
        this.id = id;
        this.name = name;
        this.imageUrl = imageUrl;
        this.price = price;
    }
}