package com.MOA.zupzup.member;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/member")
@Tag(name = "Member API", description = "API for managing members")
public class MemberController {
    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @GetMapping("/{userId}/stationery")
    @Operation(summary = "사용자가 소유한 모든 편지지 조회", description = "Retrieves all letters owned by the user.")
    public ResponseEntity<?> getAllOwnedStationery(
            @Parameter(description = "ID of the user to retrieve letters for") @PathVariable String userId) {
        try {
            List<Map<String, Object>> stationeryList = memberService.getAllOwnedStationery(userId);
            return ResponseEntity.ok(stationeryList);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("편지지 조회 실패: " + e.getMessage());
        }
    }

    @GetMapping("/{userId}/stationery/{stationeryId}")
    @Operation(summary = "사용자가 소유한 특정 편지지 조회", description = "Retrieves a specific letter owned by the user.")
    public ResponseEntity<?> getOwnedStationery(
            @Parameter(description = "ID of the user to retrieve the letter for") @PathVariable String userId,
            @Parameter(description = "ID of the letter to retrieve") @PathVariable String stationeryId) {
        try {
            Map<String, Object> stationery = memberService.getOwnedStationery(userId, stationeryId);
            return (stationery != null) ? ResponseEntity.ok(stationery) : ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("편지지 조회 실패: " + e.getMessage());
        }
    }

//    @PostMapping("/{userId}/stationery/{stationeryId}")
//    @Operation(summary = "사용자가 소유한 편지지 추가", description = "Adds a letter to the user's collection.")
//    public ResponseEntity<?> addOwnedStationery(
//            @Parameter(description = "ID of the user to add the letter for") @PathVariable String userId,
//            @Parameter(description = "ID of the letter to add") @PathVariable String stationeryId,
//            @RequestBody Map<String, Object> stationeryData) {
//        try {
//            memberService.addOwnedStationery(userId, stationeryId, stationeryData);
//            return ResponseEntity.ok("편지지 추가 성공");
//        } catch (Exception e) {
//            return ResponseEntity.badRequest().body("편지지 추가 실패: " + e.getMessage());
//        }
//    }

    @DeleteMapping("/{userId}/stationery/{stationeryId}")
    @Operation(summary = "사용자가 소유한 편지지 삭제", description = "Deletes a letter from the user's collection.")
    public ResponseEntity<?> deleteOwnedStationery(
            @Parameter(description = "ID of the user to delete the letter for") @PathVariable String userId,
            @Parameter(description = "ID of the letter to delete") @PathVariable String stationeryId) {
        try {
            memberService.deleteOwnedStationery(userId, stationeryId);
            return ResponseEntity.ok("편지지 삭제 성공");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("편지지 삭제 실패: " + e.getMessage());
        }
    }

    @GetMapping("/{userId}/stationery/{stationeryId}/count")
    @Operation(summary = "특정 편지지 보유 수 조회", description = "Retrieves the count of a specific letter owned by the user.")
    public ResponseEntity<?> getStationeryCount(
            @Parameter(description = "ID of the user to retrieve the letter count for") @PathVariable String userId,
            @Parameter(description = "ID of the letter to retrieve the count for") @PathVariable String stationeryId) {
        try {
            int count = memberService.setStationeryCount(userId, stationeryId);
            return ResponseEntity.ok(Map.of("count", count));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("편지지 개수 조회 실패: " + e.getMessage());
        }
    }

    @PutMapping("/{userId}/stationery/{stationeryId}/count")
    @Operation(summary = "특정 편지지 보유 수 업데이트", description = "Updates the count of a specific letter owned by the user.")
    public ResponseEntity<?> updateStationeryCount(
            @Parameter(description = "ID of the user to update the letter count for") @PathVariable String userId,
            @Parameter(description = "ID of the letter to update the count for") @PathVariable String stationeryId,
            @RequestParam int newCount) {
        try {
            boolean success = memberService.updateStationeryCount(userId, stationeryId, newCount);
            return success ? ResponseEntity.ok("편지지 개수 업데이트 성공") : ResponseEntity.badRequest().body("업데이트 실패");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("편지지 개수 업데이트 실패: " + e.getMessage());
        }
    }
}