package com.MOA.zupzup.global;

import com.google.api.gax.paging.Page;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Storage;
import com.google.firebase.cloud.StorageClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
public class FirebaseStorageService {

    private final Storage storage;
    private final String bucketName;

    public FirebaseStorageService(Storage storage, @Value("${firebase.firebase-bucket}") String bucketName) {
        this.storage = storage;
        this.bucketName = bucketName;
    }

    // firebase Storage의 LetterImage 폴더 안에 있는 파일들의 url 리스트를 반환하는 메소드
    public List<String> getLetterImages() {
        List<String> imageUrls = new ArrayList<>();
        Bucket bucket = storage.get(bucketName);

        // LetterImage 폴더의 파일 목록 가져오기
        Page<Blob> blobs = bucket.list(Storage.BlobListOption.prefix("letterImage/"));

        for (Blob blob : blobs.iterateAll()) {
            if(!blob.isDirectory()) {
                String filePath = URLEncoder.encode(blob.getName(), StandardCharsets.UTF_8);
                String imageUrl = "https://firebasestorage.googleapi.com/v0/b/" + bucketName + "/o/" + filePath + "?alt=media";
                imageUrls.add(imageUrl);
            }
        }

        return imageUrls;
    }

    // firebase Storage에 파일을 업로드하는 메소드
    public String upLoadFirebaseStorage(MultipartFile multipartFile, String fileName) throws IOException {
        Bucket bucket = StorageClient.getInstance().bucket(bucketName);

        Blob blob = bucket.create(fileName, multipartFile.getInputStream(), multipartFile.getContentType());

        return blob.getMediaLink(); // firebase Storage 에 저장된 파일 url
    }

    // firebase Storage에 파일을 삭제하는 메소드
    public void deleteFirebaseStorage(String fileName) {
        Bucket bucket = StorageClient.getInstance().bucket(bucketName);

        bucket.get(fileName).delete();
    }
}