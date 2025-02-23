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

    public ShopLetterItem findShopItemByName(String name) {
        try {
            Query query = getShopItemCollection().whereEqualTo("name", name);
            ApiFuture<QuerySnapshot> future = query.get();
            List<QueryDocumentSnapshot> documents = future.get().getDocuments();
            if (documents.isEmpty()) {
                throw new ShopException(ErrorCode.SHOP_ITEM_NOT_FOUND);
            }
            return documents.get(0).toObject(ShopLetterItem.class);
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
    public void updateShopItemByName(ShopLetterItem shopLetterItem) {
        try {
            ShopLetterItem existingItem = findShopItemByName(shopLetterItem.getName());
            DocumentReference docRef = getShopItemCollection().document(existingItem.getId());
            ApiFuture<WriteResult> result = docRef.set(shopLetterItem);
            result.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new ShopException(ErrorCode.SHOP_ITEM_UPDATE_FAILED);
        }
    }

    @Transactional
    public void deleteShopItemByName(String name) {
        try {
            ShopLetterItem existingItem = findShopItemByName(name);
            DocumentReference docRef = getShopItemCollection().document(existingItem.getId());
            ApiFuture<WriteResult> result = docRef.delete();
            result.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new ShopException(ErrorCode.SHOP_ITEM_DELETE_FAILED);
        }
    }

    @Transactional
    public void purchaseShopItemByName(String memberId, String name, int quantity) {
        try {
            // 멤버 패치
            DocumentReference memberDocRef = getMemberCollection().document(memberId);
            ApiFuture<DocumentSnapshot> memberFuture = memberDocRef.get();
            DocumentSnapshot memberDoc = memberFuture.get();
            if (!memberDoc.exists()) {
                throw new ShopException(ErrorCode.SHOP_MEMBER_NOT_FOUND);
            }
            Member member = memberDoc.toObject(Member.class);

            // 상점 아이템 패치
            ShopLetterItem item = findShopItemByName(name);

            // 멤버가 충분한 코인을 가지고 있는지 확인
            int totalPrice = item.getPrice() * quantity;
            if (member.getCoin() < totalPrice) {
                throw new ShopException(ErrorCode.SHOP_INSUFFICIENT_COINS);
            }

            // 가격만큼 멤버 코인 차감
            member.setCoin(member.getCoin() - totalPrice);

            // 멤버가 소유한 편지지 리스트에 추가하기
            CollectionReference ownedLettersRef = memberDocRef.collection("ownedLetter");
            DocumentReference ownedLetterDocRef = ownedLettersRef.document(item.getId());
            DocumentSnapshot ownedLetterDoc = ownedLetterDocRef.get().get();
            if (ownedLetterDoc.exists()) {
                OwnedLetter ownedLetter = ownedLetterDoc.toObject(OwnedLetter.class);
                ownedLetter.setCount(ownedLetter.getCount() + quantity);
                ownedLetterDocRef.set(ownedLetter);
            } else {
                ownedLetterDocRef.set(new OwnedLetter(item.getName(), item.getImageUrl(), quantity, false));
            }

            // 파이어스토어에 멤버의 코인 업데이트
            ApiFuture<WriteResult> writeResult = memberDocRef.set(member);
            writeResult.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new ShopException(ErrorCode.SHOP_ITEM_PURCHASE_FAILED);
        }
    }
}