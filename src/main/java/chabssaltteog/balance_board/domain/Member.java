package chabssaltteog.balance_board.domain;


import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;


@Entity
@Table(name = "user")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@EqualsAndHashCode(of = "userId")
public class Member extends BaseTimeEntity implements UserDetails  {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id", updatable = false, nullable = false)
    private long userId;        //pk

    /**
    @Column(nullable = false, name = "provider_id")
    private String providerId;    //providerId -> google 고유 ID

    @Column(nullable = false)
    private String provider;
    */

    @Column(nullable = false, unique = true)
    private String email;


//    @Column(nullable = false)
//    private String name;

    @Column(nullable = false)
    private String password;

    private String nickname;

    @Column(name = "birth_year")
    private String birthYear;            //사용자 입력값

    private String gender;      //사용자 입력값


//    @Column(name = "image_url")
//    private String imageUrl;    //프로필 사진

//    @Enumerated(EnumType.STRING)
//    @Column(nullable = false)
//    private MyRole role;

//    @Builder
//    public Member(String providerId, String provider, String email, String name, String nickname, int birthYear, String gender, String imageUrl, MyRole role) {
//        this.providerId = providerId;
//        this.provider = provider;
//        this.email = email;
//        this.name = name;
//        this.nickname = nickname;
//        this.birthYear = birthYear;
//        this.gender = gender;
//        this.imageUrl = imageUrl;
//        this.role = role;
//    }

//    public String getRoleKey() {
//        return this.role.getKey();
//    }

//    public Member updateGoogle(String name, String imageUrl) { // todo
//        this.name = name;
//        this.imageUrl = imageUrl;
//        return this;
//    }
//    public Member updateNickNameBirthYearGender(String nickname, int birthYear, String gender) {
//        this.nickname = nickname;
//        this.birthYear = birthYear;
//        this.gender = gender;
//        return this;
//    }

    public Member(String email, String password, String nickname, String birthYear, String gender) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.birthYear = birthYear;
        this.gender = gender;
    }

    @ElementCollection(fetch = FetchType.EAGER)
    @Builder.Default
    private List<String> roles = new ArrayList<>();

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.roles.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    @Override
    public String getUsername() {
        return null;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
