package chabssaltteog.balance_board.domain;


import java.time.LocalDateTime;

public class Member {

    private long userId;        //pk

    private String email;       //googleProfile 클래스로 빼서 객체 추가해도 됨

    private String name;        //googleProfile 클래스로 빼서 객체 추가해도 됨

    private String nickname;    //사용자 입력값

    private int age;            //사용자 입력값

    private String gender;      //사용자 입력값

    private String imageUrl;    //프로필 사진

    private LocalDateTime created;  //계정 생성 날짜

    private LocalDateTime updated;

}
