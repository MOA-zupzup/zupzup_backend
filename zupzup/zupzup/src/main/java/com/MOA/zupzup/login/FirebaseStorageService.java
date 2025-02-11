package com.MOA.zupzup.login;

import com.google.api.gax.paging.Page;
import com.google.cloud.storage.*;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.cloud.StorageClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
public class FirebaseStorageService {

    @Value("${firebase.firebase-bucket}")
    private String firebaseBucket;

    // jpg, jpeg 확장자 이미지만 허용
    private static final String[] POSSIBLE_EXTENSIONS = {".jpg", ".jpeg"};

    // Firebase Storage의 특정 폴더 내 파일 목록을 가져와 URL 리스트로 반환하는 메서드
    public List<String> listFilesInFolder(String folderName) throws IOException {
        Bucket bucket = StorageClient.getInstance().bucket(firebaseBucket);
        Page<Blob> blobs = bucket.list(Storage.BlobListOption.prefix(folderName + "/"));

        List<String> urls = new ArrayList<>();
        for (Blob blob : blobs.iterateAll()) {
            if (!blob.isDirectory()) {
                // URL 인코딩 (Firebase에서 URL을 생성할 때 필수)
                String encodedUrl = "https://storage.googleapis.com/" + bucket.getName() + "/"
                        + URLEncoder.encode(blob.getName(), StandardCharsets.UTF_8.toString());

                // URL 디코딩 (원래의 '/' 형식으로 복원)
                String decodedUrl = URLDecoder.decode(encodedUrl, StandardCharsets.UTF_8.toString());

                urls.add(decodedUrl);
            }
        }
        return urls;
    }

    // firebase Storage에 특정 폴더에 파일을 업로드하는 메소드
    public String uploadFileToFolder(MultipartFile file, String folderName, String fileName)
            throws IOException, FirebaseAuthException {
        Bucket bucket = StorageClient.getInstance().bucket(firebaseBucket);
        InputStream content = new ByteArrayInputStream(file.getBytes());
        String fullPath = folderName + "/" + fileName;
        Blob blob = bucket.create(fullPath, content, file.getContentType());
        return blob.getMediaLink();
    }

    // firebase Storage에 특정 폴더에 파일을 삭제하는 메소드
    public boolean deleteFile(String folderName, String fileName) {
        Bucket bucket = StorageClient.getInstance().bucket(firebaseBucket);
        Blob blob = bucket.get(folderName + "/" + fileName);
        if (blob == null && !fileName.contains(".")) {
            for (String extension : POSSIBLE_EXTENSIONS) {
                blob = bucket.get(folderName + "/" + fileName + extension);
                if (blob != null) {
                    break;
                }
            }
        }
        return blob != null && blob.delete();
    }

    // firebase Storage의 특정 폴더의 특정 인덱스 파일을 가져오는 메소드
    public String getFileByIndex(String folderName, int index) throws IOException {
        Bucket bucket = StorageClient.getInstance().bucket(firebaseBucket);
        Page<Blob> blobs = bucket.list(Storage.BlobListOption.prefix(folderName + "/"));

        List<String> urls = new ArrayList<>();
        for (Blob blob : blobs.iterateAll()) {
            if (!blob.isDirectory()) {
                String encodedUrl = "https://storage.googleapis.com/" + bucket.getName() + "/"
                        + URLEncoder.encode(blob.getName(), StandardCharsets.UTF_8.toString());
                String decodedUrl = URLDecoder.decode(encodedUrl, StandardCharsets.UTF_8.toString());
                urls.add(decodedUrl);
            }
        }

        if (index >= 0 && index < urls.size()) {
            return urls.get(index);
        } else {
            throw new IndexOutOfBoundsException("Index out of range: " + index);
        }
    }
}