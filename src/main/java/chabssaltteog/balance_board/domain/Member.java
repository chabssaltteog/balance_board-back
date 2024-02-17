package chabssaltteog.balance_board.domain;


import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Table(name = "user")
@Getter
@Setter
@NoArgsConstructor
public class Member extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private long userId;        //pk

    @Column(nullable = false, name = "provider_id")
    private String providerId;    //providerId -> google 고유 ID

    @Column(nullable = false)
    private String provider;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String nickname;

    private int age;            //사용자 입력값

    private String gender;      //사용자 입력값

    @Column(name = "image_url")
    private String imageUrl;    //프로필 사진

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MyRole role;

    @Builder
    public Member(String providerId, String provider, String email, String name, String nickname, int age, String gender, String imageUrl, MyRole role) {
        this.providerId = providerId;
        this.provider = provider;
        this.email = email;
        this.name = name;
        this.nickname = nickname;
        this.age = age;
        this.gender = gender;
        this.imageUrl = imageUrl;
        this.role = role;
    }

    public String getRoleKey() {
        return this.role.getKey();
    }

    public Member update(String name, String imageUrl) { // todo
        this.name = name;
        this.imageUrl = imageUrl;
        return this;
    }

    public Member updateNickNameAgeGender(String nickname, int age, String gender) {
        this.nickname = nickname;
        this.age = age;
        this.gender = gender;
        return this;
    }
}
