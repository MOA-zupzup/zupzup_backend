package com.MOA.zupzup.login;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.Storage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class FirebaseStorageService {

    private final Storage storage;
    private final String bucketName;

    @Autowired
    public FirebaseStorageService(Storage storage, @Value("${firebase.firebase-bucket}") String bucketName) {
        this.storage = storage;
        this.bucketName = bucketName;
    }

    public List<String> getLetterImages() {
        Bucket bucket = storage.get(bucketName);
        if (bucket == null) {
            throw new IllegalStateException("Bucket not found: " + bucketName);
        }

        List<String> imageUrls = new ArrayList<>();
        for (Blob blob : bucket.list().iterateAll()) {
            imageUrls.add(blob.getMediaLink());
        }
        return imageUrls;
    }

    public String getLetterImageByIndex(int index) {
        List<String> images = getLetterImages();
        if (index < 0 || index >= images.size()) {
            throw new IndexOutOfBoundsException("Invalid index: " + index);
        }
        return images.get(index);
    }
}