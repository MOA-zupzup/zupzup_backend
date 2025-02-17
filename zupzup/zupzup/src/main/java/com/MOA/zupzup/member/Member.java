package com.MOA.zupzup.member;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "member")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {
    @Id
    @Column(name = "user_id")
    private String userId;

    @Column(name = "password", length = 20, nullable = false)
    private String password;

    @Column(name = "nickname", length = 255, nullable = false)
    private String nickname;

    @Column(name = "email", length = 20, nullable = false)
    private String email;

    @Column(name = "coin")
    private Integer coin = 0;

    @Column(name = "phase")
    private Integer phase = 1;

    @Builder
    public Member(String userId, String password, String nickname, String email, Integer coin, Integer phase) {
        this.userId = userId;
        this.password = password;
        this.nickname = nickname;
        this.email = email;
        this.coin = coin != null ? coin : 0;
        this.phase = phase != null ? phase : 1;
    }

    // 코인 증가 메서드
    public void addCoin(int amount) {
        this.coin += amount;
    }

    // 페이즈 업데이트 메서드
    public void updatePhase(int phase) {
        this.phase = phase;
    }
}
