package com.MOA.zupzup.shop.dto;

import com.MOA.zupzup.shop.ShopLetterItem;
import lombok.Getter;

@Getter
public class ShopResponse {
    private String id;
    private String name;
    private String imageUrl;
    private int price;

    public static ShopResponse from(ShopLetterItem item) {
        ShopResponse response = new ShopResponse();
        response.id = item.getId();
        response.name = item.getName();
        response.imageUrl = item.getImageUrl();
        response.price = item.getPrice();
        return response;
    }
}