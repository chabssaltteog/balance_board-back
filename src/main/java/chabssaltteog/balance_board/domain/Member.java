package chabssaltteog.balance_board.domain;


import jakarta.persistence.*;
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

    @Column(name = "provider_id")
    private long providerId;    //providerId -> google 고유 ID

    @Column(nullable = false)
    private String email;       //googleProfile 클래스로 빼서 객체 추가해도 됨

    @Column(nullable = false)
    private String name;        //googleProfile 클래스로 빼서 객체 추가해도 됨

    @Column(nullable = false)
    private String nickname;

    private int age;            //사용자 입력값

    private String gender;      //사용자 입력값

    @Column(name = "image_url")
    private String imageUrl;    //프로필 사진


}
