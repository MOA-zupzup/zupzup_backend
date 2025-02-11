package com.MOA.zupzup.letter;

import com.MOA.zupzup.letter.dto.DroppingLetterRequest;
import com.MOA.zupzup.letter.dto.LetterResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/letters")
@Tag(name = "편지 API", description = "편지를 남기고 줍는 API")
public class LetterController {

    private final LetterService letterService;

    @PostMapping("/new")
    @Operation(summary = "편지 남기기", description = "새로운 편지를 생성합니다.")
    public ResponseEntity<Void> createLetter(@RequestBody DroppingLetterRequest request) {
        String letterId = letterService.createUnpickedLetter(request);
        return ResponseEntity.created(URI.create("/letters" + letterId)).build();
    }

    @PutMapping("/{letterId}/pick")
    @Operation(summary = "편지 줍기", description = "편지를 특정 사용자가 주울 수 있습니다.")
    public ResponseEntity<Void> pickUpLetter(
            @Parameter(description = "줍고 싶은 편지의 ID") @PathVariable String letterId,
            @Parameter(description = "편지를 줍는 사용자의 ID") @RequestParam String receiverId) {
        letterService.pickUpLetter(letterId, receiverId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{letterId}")
    @Operation(summary = "편지 읽기", description = "특정 편지를 조회합니다.")
    public ResponseEntity<LetterResponse> findLetter(
            @Parameter(description = "조회할 편지의 ID") @PathVariable String letterId) {
        LetterResponse response = letterService.findLetter(letterId);
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/my-list")
    @Operation(summary = "내가 받은 편지 목록", description = "특정 사용자가 받은 모든 편지를 조회합니다.")
    public ResponseEntity<List<LetterResponse>> findMyLetters(
            @Parameter(description = "사용자의 ID") @RequestParam String memberId) {
        List<LetterResponse> responses = letterService.findAllLettersByMemberId(memberId);
        return ResponseEntity.ok().body(responses);
    }

    @DeleteMapping("/{letterId}")
    @Operation(summary = "편지 삭제", description = "특정 편지를 삭제합니다.")
    public ResponseEntity<Void> deleteLetter(
            @Parameter(description = "삭제할 편지의 ID") @PathVariable String letterId) {
        letterService.deleteLetter(letterId);
        return ResponseEntity.noContent().build();
    }
}
