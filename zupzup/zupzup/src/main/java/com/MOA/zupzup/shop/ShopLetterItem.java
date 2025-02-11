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

    @PropertyName("imageUrl")
    private String imageUrl;

    @PropertyName("price")
    private int price;

    public ShopLetterItem(String id, String imageUrl, int price, Timestamp createdAt) {
        this.id = id;
        this.imageUrl = imageUrl;
        this.price = price;
    }
}