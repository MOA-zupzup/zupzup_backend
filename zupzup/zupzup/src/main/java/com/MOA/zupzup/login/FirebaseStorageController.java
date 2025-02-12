package com.MOA.zupzup.login;

import com.google.firebase.auth.FirebaseAuthException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/storage")
@Tag(name = "Firebase Storage", description = "Operations related to Firebase Storage")
public class FirebaseStorageController {

    private final FirebaseStorageService firebaseStorageService;

    @Autowired
    public FirebaseStorageController(FirebaseStorageService firebaseStorageService) {
        this.firebaseStorageService = firebaseStorageService;
    }

    @Operation(summary = "Firebase Storage의 특정 폴더에 이미지 업로드",
            description = "Firebase Storage의 특정 폴더에 이미지를 업로드하고 file Url을 리턴한다.")
    @PostMapping("/files/{folderName}")
    public String uploadFileToFolder(
            @RequestParam("file") MultipartFile file,
            @PathVariable("folderName") String folderName,
            @RequestParam("nameFile") String nameFile)
            throws IOException, FirebaseAuthException {
        if (file.isEmpty()) {
            return "File is empty";
        }
        return firebaseStorageService.uploadFileToFolder(file, folderName, nameFile);
    }

    @Operation(summary = "Firebase Storage 특정 폴더의 이미지를 삭제한다.",
            description = "Deletes a file from a specific folder in Firebase Storage")
    @DeleteMapping("/files/{folderName}/{fileName}")
    public boolean deleteFileFromFolder(
            @PathVariable("folderName") String folderName,
            @PathVariable("fileName") String fileName) {
        return firebaseStorageService.deleteFile(folderName, fileName);
    }

    @Operation(summary = "Firebase Storage의 특정 폴더 내의 모든 이미지 URL을 반환한다.",
            description = "**배열의 0번째 요소는 '폴더 경로'이다. 따라서 1번째 요소가 해당 폴더의 첫번째 이미지를 반환한다.**")
    @GetMapping("/files/{folderName}")
    public List<String> listFilesInFolder(@PathVariable("folderName") String folderName) throws IOException {
        return firebaseStorageService.listFilesInFolder(folderName);
    }

    @Operation(summary = "Firebase Storage의 특정 폴더 내의 특정 인덱스 이미지 URL을 반환한다.",
            description = "**배열의 0번째 요소는 '폴더 경로'이다. 따라서 1번째 요소가 해당 폴더의 첫번째 이미지를 반환한다.**")
    @GetMapping("/files/{folderName}/index/{index}")
    public String getFileByIndex(@PathVariable("folderName") String folderName, @PathVariable("index") int index) throws IOException {
        return firebaseStorageService.getFileByIndex(folderName, index);
    }

}