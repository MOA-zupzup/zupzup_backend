package com.MOA.zupzup.shop.dto;

import com.MOA.zupzup.shop.ShopLetterItem;
import com.google.cloud.Timestamp;

public record ShopResponse(
        String id,
        String imageUrl,
        int price
) {
    public static ShopResponse from(ShopLetterItem shopLetterItem) {
        return new ShopResponse(
                shopLetterItem.getId(),
                shopLetterItem.getImageUrl(),
                shopLetterItem.getPrice()
        );
    }
}