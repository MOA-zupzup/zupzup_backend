package com.MOA.zupzup.letter;

import com.MOA.zupzup.letter.dto.LetterRequest;
import com.MOA.zupzup.letter.dto.LetterResponse;
import com.MOA.zupzup.letter.dto.UnpickedLetterResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/letters")
public class LetterController {

    private final LetterService letterService;

    // 편지 남기기
    @PostMapping("/new")
    public ResponseEntity<Void> createLetter(@RequestBody LetterRequest request){
        letterService.createUnpickedLetter(request);
        return ResponseEntity.ok().build();
    }

    // 편지 줍기
/*

    // 편지 읽기
    @GetMapping("/{letterId}")
    public ResponseEntity<LetterResponse> findLetter(@PathVariable String id) {
        LetterResponse response = letterService.
        return ResponseEntity.ok().body(response);
    }
*/

/*
    // 우편함의 편지 리스트 반환
    @GetMapping("/letters-in-mailbox")
    public ResponseEntity<List<UnpickedLetterResponse>> findAllLettersInMailbox(){;}
    */

/*

    // 편지 삭제
    @DeleteMapping("/{letterId}")
    public ResponseEntity<Void> deleteLetter(@PathVariable String id) {
        letterService.
    }
*/

}
