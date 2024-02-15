package chabssaltteog.balance_board.domain;

import lombok.Getter;

import java.io.Serializable;

@Getter
public class SessionMember implements Serializable {    // 인증된 사용자 정보만 필요
    private String name;
    private String email;
    private String imageUrl;
    private String nickname;
    private int age;
    private String gender;

    public SessionMember(Member member) {
        this.name = member.getName();
        this.email = member.getEmail();
        this.imageUrl = member.getImageUrl();
        this.nickname = member.getNickname();
        this.age = member.getAge();
        this.gender = member.getGender();
    }
}
