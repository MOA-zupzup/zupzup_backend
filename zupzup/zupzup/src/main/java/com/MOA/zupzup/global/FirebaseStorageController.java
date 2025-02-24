package com.MOA.zupzup.login;

import com.MOA.zupzup.login.ApiResponse;
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
@Tag(name = "Firebase Storage", description = "Firebase Storage 관련 API")
public class FirebaseStorageController {

    private final FirebaseStorageService firebaseStorageService;

    @Autowired
    public FirebaseStorageController(FirebaseStorageService firebaseStorageService) {
        this.firebaseStorageService = firebaseStorageService;
    }

    @Operation(summary = "Firebase Storage의 특정 폴더에 이미지 업로드",
            description = "Firebase Storage의 특정 폴더에 이미지를 업로드하고 file Url을 리턴한다.")
    @PostMapping("/files/{folderName}")
    public ApiResponse<String> uploadFileToFolder(
            @RequestParam("file") MultipartFile file,
            @PathVariable("folderName") String folderName,
            @RequestParam("nameFile") String nameFile)
            throws IOException, FirebaseAuthException {
        if (file.isEmpty()) {
            return new ApiResponse<>(false, "File is empty", null);
        }
        String fileUrl = firebaseStorageService.uploadFileToFolder(file, folderName, nameFile);
        return new ApiResponse<>(true, "File uploaded successfully", fileUrl);
    }

    @Operation(summary = "Firebase Storage 특정 폴더의 이미지를 삭제한다.",
            description = "Deletes a file from a specific folder in Firebase Storage")
    @DeleteMapping("/files/{folderName}/{fileName}")
    public ApiResponse<Boolean> deleteFileFromFolder(
            @PathVariable("folderName") String folderName,
            @PathVariable("fileName") String fileName) {
        boolean result = firebaseStorageService.deleteFile(folderName, fileName);
        return new ApiResponse<>(result, result ? "File deleted successfully" : "File deletion failed", result);
    }

    @Operation(summary = "Firebase Storage의 특정 폴더 내의 모든 이미지 URL을 반환한다.",
            description = "**배열의 0번째 요소는 '폴더 경로'이다. 따라서 1번째 요소가 해당 폴더의 첫번째 fileUrl를 반환한다.**")
    @GetMapping("/files/{folderName}")
    public ApiResponse<List<String>> listFilesInFolder(@PathVariable("folderName") String folderName) throws IOException {
        List<String> fileUrls = firebaseStorageService.listFilesInFolder(folderName);
        return new ApiResponse<>(true, "Files retrieved successfully", fileUrls);
    }

    @Operation(summary = "Firebase Storage의 특정 폴더 내의 특정 인덱스 이미지 URL을 반환한다.",
            description = "**배열의 0번째 요소는 '폴더 경로'이다. 따라서 1번째 요소가 해당 폴더의 첫번째 이미지를 반환한다.**")
    @GetMapping("/files/{folderName}/index/{index}")
    public ApiResponse<String> getFileByIndex(@PathVariable("folderName") String folderName, @PathVariable("index") int index) throws IOException {
        String fileUrl = firebaseStorageService.getFileByIndex(folderName, index);
        return new ApiResponse<>(true, "File retrieved successfully", fileUrl);
    }
}