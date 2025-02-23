package com.MOA.zupzup.shop;

import com.MOA.zupzup.login.ApiResponse;
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
    @Operation(summary = "상점 아이템 생성", description = "Creates a new shop item.")
    public ResponseEntity<ApiResponse<Void>> createShopItem(@RequestBody ShopLetterItem request) {
        try {
            String itemId = shopService.createShopItem(request);
            return ResponseEntity.created(URI.create("/shop-items/" + itemId))
                    .body(new ApiResponse<>(true, "아이템 생성 성공", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, "아이템 생성 실패: " + e.getMessage(), null));
        }
    }

    @GetMapping("/{name}")
    @Operation(summary = "상점 아이템을 name으로 Get", description = "Retrieves a specific shop item by its name.")
    public ResponseEntity<ApiResponse<ShopResponse>> findShopItem(
            @Parameter(description = "Name of the shop item to retrieve") @PathVariable String name) {
        try {
            ShopResponse response = ShopResponse.from(shopService.findShopItemByName(name));
            return ResponseEntity.ok(new ApiResponse<>(true, "아이템 조회 성공", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, "아이템 조회 실패: " + e.getMessage(), null));
        }
    }

    @GetMapping("/all")
    @Operation(summary = "모든 아이템을 Get", description = "Retrieves all shop items.")
    public ResponseEntity<ApiResponse<List<ShopResponse>>> findAllShopItems() {
        try {
            List<ShopResponse> responses = shopService.findAllShopItems().stream()
                    .map(ShopResponse::from)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(new ApiResponse<>(true, "모든 아이템 조회 성공", responses));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, "모든 아이템 조회 실패: " + e.getMessage(), null));
        }
    }

    @PutMapping("/{name}")
    @Operation(summary = "상점 아이템을 name으로 업데이트", description = "Updates a specific shop item by its name.")
    public ResponseEntity<ApiResponse<Void>> updateShopItem(
            @Parameter(description = "Name of the shop item to update") @PathVariable String name,
            @RequestBody ShopLetterItem request) {
        try {
            request.setName(name);
            shopService.updateShopItemByName(request);
            return ResponseEntity.ok(new ApiResponse<>(true, "아이템 업데이트 성공", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, "아이템 업데이트 실패: " + e.getMessage(), null));
        }
    }

    @DeleteMapping("/{name}")
    @Operation(summary = "상점 아이템을 name으로 삭제", description = "Deletes a specific shop item by its name.")
    public ResponseEntity<ApiResponse<Void>> deleteShopItem(
            @Parameter(description = "Name of the shop item to delete") @PathVariable String name) {
        try {
            shopService.deleteShopItemByName(name);
            return ResponseEntity.ok(new ApiResponse<>(true, "아이템 삭제 성공", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, "아이템 삭제 실패: " + e.getMessage(), null));
        }
    }

    @PostMapping("/purchase/{memberId}/{name}")
    @Operation(summary = "상점 아이템 구매", description = "Purchases a shop item using member's coins.")
    public ResponseEntity<ApiResponse<Void>> purchaseShopItem(
            @Parameter(description = "ID of the member purchasing the item") @PathVariable String memberId,
            @Parameter(description = "Name of the shop item to purchase") @PathVariable String name) {
        try {
            shopService.purchaseShopItemByName(memberId, name);
            return ResponseEntity.ok(new ApiResponse<>(true, "아이템 구매 성공", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, "아이템 구매 실패: " + e.getMessage(), null));
        }
    }
}