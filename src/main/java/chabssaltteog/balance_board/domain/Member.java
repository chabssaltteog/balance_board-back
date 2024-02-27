package chabssaltteog.balance_board.domain;


import chabssaltteog.balance_board.domain.post.Post;
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
    private Long userId;        //pk

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

    @OneToMany(mappedBy = "user") //읽기 전용
    private List<Post> posts = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL) // 사용자가 참여한 투표 목록
    private List<VoteMember> voteMembers = new ArrayList<>();

    @Column(name = "image_type")
    private int imageType;    //프로필 사진


    public Member(String email, String password, String nickname, String birthYear, String gender, int imageType) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.birthYear = birthYear;
        this.gender = gender;
        this.imageType = imageType;
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
