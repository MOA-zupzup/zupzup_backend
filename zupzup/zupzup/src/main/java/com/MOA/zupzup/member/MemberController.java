package com.MOA.zupzup.member;

import com.MOA.zupzup.login.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/member")
@Tag(name = "Member API", description = "각 회원이 가진 편지지를 관리하는 API (편지 API와 다르므로 주의할 것)")
public class MemberController {
    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @GetMapping("/{userId}/stationery")
    @Operation(summary = "사용자가 소유한 모든 편지지 조회", description = "Retrieves all letters owned by the user.")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getAllOwnedStationery(
            @Parameter(description = "ID of the user to retrieve letters for") @PathVariable String userId) {
        try {
            List<Map<String, Object>> stationeryList = memberService.getAllOwnedStationery(userId);
            return ResponseEntity.ok(new ApiResponse<>(true, "편지지 조회 성공", stationeryList));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, "편지지 조회 실패: " + e.getMessage(), null));
        }
    }

    @GetMapping("/{userId}/stationery/{stationeryId}")
    @Operation(summary = "사용자가 소유한 특정 편지지 조회", description = "Retrieves a specific letter owned by the user.")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getOwnedStationery(
            @Parameter(description = "ID of the user to retrieve the letter for") @PathVariable String userId,
            @Parameter(description = "ID of the letter to retrieve") @PathVariable String stationeryId) {
        try {
            Map<String, Object> stationery = memberService.getOwnedStationery(userId, stationeryId);
            return (stationery != null) ? ResponseEntity.ok(new ApiResponse<>(true, "편지지 조회 성공", stationery)) : ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, "편지지 조회 실패: " + e.getMessage(), null));
        }
    }

    @DeleteMapping("/{userId}/stationery/{stationeryId}")
    @Operation(summary = "사용자가 소유한 편지지 삭제", description = "Deletes a letter from the user's collection.")
    public ResponseEntity<ApiResponse<Void>> deleteOwnedStationery(
            @Parameter(description = "ID of the user to delete the letter for") @PathVariable String userId,
            @Parameter(description = "ID of the letter to delete") @PathVariable String stationeryId) {
        try {
            memberService.deleteOwnedStationery(userId, stationeryId);
            return ResponseEntity.ok(new ApiResponse<>(true, "편지지 삭제 성공", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, "편지지 삭제 실패: " + e.getMessage(), null));
        }
    }

    @GetMapping("/{userId}/stationery/{stationeryId}/count")
    @Operation(summary = "특정 편지지 보유 수 조회", description = "Retrieves the count of a specific letter owned by the user.")
    public ResponseEntity<ApiResponse<Map<String, Integer>>> getStationeryCount(
            @Parameter(description = "ID of the user to retrieve the letter count for") @PathVariable String userId,
            @Parameter(description = "ID of the letter to retrieve the count for") @PathVariable String stationeryId) {
        try {
            int count = memberService.setStationeryCount(userId, stationeryId);
            return ResponseEntity.ok(new ApiResponse<>(true, "편지지 개수 조회 성공", Map.of("count", count)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, "편지지 개수 조회 실패: " + e.getMessage(), null));
        }
    }

    @PutMapping("/{userId}/stationery/{stationeryId}/count")
    @Operation(summary = "특정 편지지 보유 수 업데이트", description = "Updates the count of a specific letter owned by the user.")
    public ResponseEntity<ApiResponse<Void>> updateStationeryCount(
            @Parameter(description = "ID of the user to update the letter count for") @PathVariable String userId,
            @Parameter(description = "ID of the letter to update the count for") @PathVariable String stationeryId,
            @RequestParam int newCount) {
        try {
            boolean success = memberService.updateStationeryCount(userId, stationeryId, newCount);
            return success ? ResponseEntity.ok(new ApiResponse<>(true, "편지지 개수 업데이트 성공", null)) : ResponseEntity.badRequest().body(new ApiResponse<>(false, "업데이트 실패", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, "편지지 개수 업데이트 실패: " + e.getMessage(), null));
        }
    }
}