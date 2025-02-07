package com.MOA.zupzup.letter;

import com.MOA.zupzup.letter.dto.DroppingLetterRequest;
import com.MOA.zupzup.letter.dto.LetterResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/letters")
public class LetterController {

    private final LetterService letterService;

    // 편지 남기기
    @PostMapping("/new")
    public ResponseEntity<Void> createLetter(@RequestBody DroppingLetterRequest request){
        String letterId = letterService.createUnpickedLetter(request);
        return ResponseEntity.created(URI.create("/letters" + letterId)).build();
    }

    // 편지 줍기
    @PutMapping("/{letterId}/pick")
    public ResponseEntity<Void> pickUpLetter(@PathVariable String letterId, @RequestParam String receiverId){
        letterService.pickUpLetter(letterId, receiverId);
        return ResponseEntity.ok().build();
    }

    // 편지 읽기
    @GetMapping("/{letterId}")
    public ResponseEntity<LetterResponse> findLetter(@PathVariable String letterId) {
        LetterResponse response = letterService.findLetter(letterId);
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/my-list")
    public ResponseEntity<List<LetterResponse>> findMyLetters(@RequestParam String memberId){
        List<LetterResponse> responses = letterService.findAllLettersByMemberId(memberId);
        return ResponseEntity.ok().body(responses);
    }

    // 편지 삭제
    @DeleteMapping("/{letterId}")
    public ResponseEntity<Void> deleteLetter(@PathVariable String letterId) {
        letterService.deleteLetter(letterId);
        return ResponseEntity.noContent().build();
    }
}
