package com.MOA.zupzup.shop;

import com.MOA.zupzup.exception.ErrorCode;
import com.MOA.zupzup.exception.ShopException;
import com.MOA.zupzup.member.Member;
import com.MOA.zupzup.member.OwnedLetter;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ShopService {

    private static final String COLLECTION_SHOP = "shopItems";
    private static final String COLLECTION_MEMBER = "members";

    private CollectionReference getShopItemCollection() {
        Firestore db = FirestoreClient.getFirestore();
        return db.collection(COLLECTION_SHOP);
    }

    private CollectionReference getMemberCollection() {
        Firestore db = FirestoreClient.getFirestore();
        return db.collection(COLLECTION_MEMBER);
    }

    @Transactional
    public String createShopItem(ShopLetterItem shopLetterItem) {
        try {
            DocumentReference docRef = getShopItemCollection().document();
            shopLetterItem.setId(docRef.getId());
            ApiFuture<WriteResult> shopItemApiFuture = docRef.set(shopLetterItem);
            shopItemApiFuture.get();
            return docRef.getId();
        } catch (InterruptedException | ExecutionException e) {
            throw new ShopException(ErrorCode.SHOP_ITEM_CREATE_FAILED);
        }
    }

    public ShopLetterItem findShopItem(String itemId) {
        try {
            DocumentReference docRef = getShopItemCollection().document(itemId);
            ApiFuture<DocumentSnapshot> future = docRef.get();
            DocumentSnapshot document = future.get();
            return handleFirestoreResult(document, () -> new ShopException(ErrorCode.SHOP_ITEM_NOT_FOUND));
        } catch (InterruptedException | ExecutionException e) {
            throw new ShopException(ErrorCode.SHOP_ITEM_FIND_FAILED);
        }
    }

    public ShopLetterItem findShopItemByIndex(int index) {
        try {
            ApiFuture<QuerySnapshot> future = getShopItemCollection().orderBy(FieldPath.documentId()).offset(index).limit(1).get();
            QuerySnapshot querySnapshot = future.get();
            if (querySnapshot.isEmpty()) {
                throw new ShopException(ErrorCode.SHOP_ITEM_NOT_FOUND);
            }
            DocumentSnapshot document = querySnapshot.getDocuments().get(0);
            return document.toObject(ShopLetterItem.class);
        } catch (InterruptedException | ExecutionException e) {
            throw new ShopException(ErrorCode.SHOP_ITEM_FIND_FAILED);
        }
    }

    public List<ShopLetterItem> findAllShopItems() {
        try {
            ApiFuture<QuerySnapshot> future = getShopItemCollection().get();
            QuerySnapshot querySnapshot = future.get();
            return querySnapshot.getDocuments().stream()
                    .map(doc -> doc.toObject(ShopLetterItem.class))
                    .collect(Collectors.toList());
        } catch (InterruptedException | ExecutionException e) {
            throw new ShopException(ErrorCode.SHOP_ITEM_FIND_FAILED);
        }
    }

    @Transactional
    public void updateShopItem(ShopLetterItem shopLetterItem) {
        try {
            DocumentReference docRef = getShopItemCollection().document(shopLetterItem.getId());
            ApiFuture<WriteResult> result = docRef.set(shopLetterItem);
            result.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new ShopException(ErrorCode.SHOP_ITEM_UPDATE_FAILED);
        }
    }

    @Transactional
    public void deleteShopItem(String itemId) {
        try {
            DocumentReference docRef = getShopItemCollection().document(itemId);
            ApiFuture<WriteResult> result = docRef.delete();
            result.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new ShopException(ErrorCode.SHOP_ITEM_DELETE_FAILED);
        }
    }

    //-------- CRUD 이외 메서드 --------//

    @Transactional
    public void purchaseShopItem(String memberId, int index) {
        try {
            // Fetch member
            DocumentReference memberDocRef = getMemberCollection().document(memberId);
            ApiFuture<DocumentSnapshot> memberFuture = memberDocRef.get();
            DocumentSnapshot memberDoc = memberFuture.get();
            if (!memberDoc.exists()) {
                throw new ShopException(ErrorCode.SHOP_MEMBER_NOT_FOUND);
            }
            Member member = memberDoc.toObject(Member.class);

            // Fetch shop item
            ShopLetterItem item = findShopItemByIndex(index);

            // Check if member has enough coins
            if (member.getCoin() < item.getPrice()) {
                throw new ShopException(ErrorCode.SHOP_INSUFFICIENT_COINS);
            }

            // Deduct item price from member's coins
            member.setCoin(member.getCoin() - item.getPrice());

            // Add item to member's owned letters
            CollectionReference ownedLettersRef = memberDocRef.collection("ownedLetter");
            DocumentReference ownedLetterDocRef = ownedLettersRef.document(item.getId());
            ownedLetterDocRef.set(new OwnedLetter(item.getName(), item.getImageUrl(), 1, false));

            // Update member's coin balance in Firestore
            ApiFuture<WriteResult> writeResult = memberDocRef.set(member);
            writeResult.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new ShopException(ErrorCode.SHOP_ITEM_PURCHASE_FAILED);
        }
    }

    //=== Validation Methods ===//
    private ShopLetterItem handleFirestoreResult(DocumentSnapshot documentSnapshot, Supplier<? extends RuntimeException> supplier) {
        return documentSnapshot.exists() ? documentSnapshot.toObject(ShopLetterItem.class) : throwException(supplier);
    }

    private <T> T throwException(Supplier<? extends RuntimeException> exceptionSupplier) {
        throw exceptionSupplier.get();
    }
}