package com.MOA.zupzup.member;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserRecord;
import org.springframework.stereotype.Service;

@Service
public class MemberService {

    public UserRecord createMember(Member member) throws Exception {
        UserRecord.CreateRequest request = new UserRecord.CreateRequest()
                .setEmail(member.getEmail())
                .setPassword(member.getPassword())
                .setDisplayName(member.getNickname());

        return FirebaseAuth.getInstance().createUser(request);
    }

    public void deleteMember(String uid) throws Exception {
        FirebaseAuth.getInstance().deleteUser(uid);
    }

    public UserRecord getMember(String uid) throws Exception {
        return FirebaseAuth.getInstance().getUser(uid);
    }
}