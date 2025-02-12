package com.MOA.zupzup.shop;

import com.MOA.zupzup.shop.dto.ShopResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/shop-items")
@Tag(name = "Shop API", description = "API for managing shop items")
public class ShopController {

    private final ShopService shopService;

    @PostMapping("/new")
    @Operation(summary = "Create Shop Item", description = "Creates a new shop item.")
    public ResponseEntity<Void> createShopItem(@RequestBody ShopLetterItem request) {
        String itemId = shopService.createShopItem(request);
        return ResponseEntity.created(URI.create("/shop-items/" + itemId)).build();
    }

    @GetMapping("/{itemId}")
    @Operation(summary = "Get Shop Item", description = "Retrieves a specific shop item by its ID.")
    public ResponseEntity<ShopResponse> findShopItem(
            @Parameter(description = "ID of the shop item to retrieve") @PathVariable String itemId) {
        ShopResponse response = ShopResponse.from(shopService.findShopItem(itemId));
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/index/{index}")
    @Operation(summary = "Get Shop Item by Index", description = "Retrieves a specific shop item by its index.")
    public ResponseEntity<ShopResponse> findShopItemByIndex(
            @Parameter(description = "Index of the shop item to retrieve") @PathVariable int index) {
        ShopResponse response = ShopResponse.from(shopService.findShopItemByIndex(index));
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/all")
    @Operation(summary = "List All Shop Items", description = "Retrieves all shop items.")
    public ResponseEntity<List<ShopResponse>> findAllShopItems() {
        List<ShopResponse> responses = shopService.findAllShopItems().stream()
                .map(ShopResponse::from)
                .collect(Collectors.toList());
        return ResponseEntity.ok().body(responses);
    }

    @PutMapping("/index/{index}")
    @Operation(summary = "Update Shop Item by Index", description = "Updates a specific shop item by its index.")
    public ResponseEntity<Void> updateShopItemByIndex(
            @Parameter(description = "Index of the shop item to update") @PathVariable int index,
            @RequestBody ShopLetterItem request) {
        ShopLetterItem existingItem = shopService.findShopItemByIndex(index);
        request.setId(existingItem.getId());
        shopService.updateShopItem(request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/index/{index}")
    @Operation(summary = "Delete Shop Item by Index", description = "Deletes a specific shop item by its index.")
    public ResponseEntity<Void> deleteShopItemByIndex(
            @Parameter(description = "Index of the shop item to delete") @PathVariable int index) {
        ShopLetterItem existingItem = shopService.findShopItemByIndex(index);
        shopService.deleteShopItem(existingItem.getId());
        return ResponseEntity.noContent().build();
    }
}