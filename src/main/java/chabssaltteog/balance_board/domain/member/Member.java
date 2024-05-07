package chabssaltteog.balance_board.domain.member;


import chabssaltteog.balance_board.domain.BaseTimeEntity;
import chabssaltteog.balance_board.domain.vote.VoteMember;
import chabssaltteog.balance_board.domain.post.Post;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


@Entity
@Table(name = "user")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@EqualsAndHashCode(of = "userId")
public class Member extends BaseTimeEntity implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id", updatable = false, nullable = false)
    private Long userId;        //pk

    @Column(nullable = false, name = "provider_id")
    private String providerId;    //providerId -> google 고유 ID

    @Column(nullable = false)
    private String provider;

    @Column(nullable = false, unique = true)
    private String email;

    /**
    @Column(nullable = false)
    private String password;
    */

    private String nickname;        // 사용자 입력값

    @Column(name = "birth_year")
    private String birthYear;            //사용자 입력값

    private String gender;      //사용자 입력값

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL) //읽기 전용
    private List<Post> posts = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL) // 사용자가 참여한 투표 목록
    private List<VoteMember> voteMembers = new ArrayList<>();

//    @Column(name = "image_type")
//    private int imageType;    //프로필 사진

    private String role;

    @Enumerated(EnumType.STRING)
    private Level level;

    @Column(name = "experience_points", columnDefinition = "INT DEFAULT 0")
    private int experiencePoints;

    public void addInfo(String nickname, String birthYear, String gender) {
        this.nickname = nickname;
        this.birthYear = birthYear;
        this.gender = gender;
    }

    public int incrementExperiencePoints(int points) {
        this.experiencePoints += points;
        return experiencePoints;
    }

    public int decrementExperiencePoints(int points) {
        this.experiencePoints -= points;
        return experiencePoints;
    }

    public int updateLevel(int experiencePoints) {
        if (experiencePoints >= 0 && experiencePoints < 30) {
            this.level = Level.레벨1;
        } else if (experiencePoints >= 30 && experiencePoints < 60) {
            this.level = Level.레벨2;
        } else if (experiencePoints >= 60 && experiencePoints < 110) {
            this.level = Level.레벨3;
        } else if (experiencePoints >= 110 && experiencePoints < 160) {
            this.level = Level.레벨4;
        } else if (experiencePoints >= 160 && experiencePoints < 230) {
            this.level = Level.레벨5;
        } else if (experiencePoints >= 230 && experiencePoints < 300) {
            this.level = Level.레벨6;
        } else if (experiencePoints >= 300 && experiencePoints < 390) {
            this.level = Level.레벨7;
        } else if (experiencePoints >= 390 && experiencePoints < 480) {
            this.level = Level.레벨8;
        } else if (experiencePoints >= 480 && experiencePoints < 590) {
            this.level = Level.레벨9;
        } else if (experiencePoints >= 590) {
            this.level = Level.레벨10;
        }
        return level.getValue();
    }

    /**
    @ElementCollection(fetch = FetchType.EAGER)
    @Builder.Default
    private List<String> roles = new ArrayList<>();
     */

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(role));
        return authorities;
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return email;
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
