package chabssaltteog.balance_board.domain;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "user")
@Getter
@Setter
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private long userId;        //pk

    @Column(name = "provider_id")
    private long providerId;    //providerId -> google 고유 ID

    private String email;       //googleProfile 클래스로 빼서 객체 추가해도 됨

    private String name;        //googleProfile 클래스로 빼서 객체 추가해도 됨

    private String nickname;    //사용자 입력값

    private int age;            //사용자 입력값

    private String gender;      //사용자 입력값

    @Column(name = "image_url")
    private String imageUrl;    //프로필 사진

    private LocalDateTime created;  //계정 생성 날짜

    private LocalDateTime updated;

    public Member(long providerId, String email, String name) { //google로부터 받는 컬럼들
        this.providerId = providerId;
        this.email = email;
        this.name = name;
    }

}
