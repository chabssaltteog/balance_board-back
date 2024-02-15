package chabssaltteog.balance_board.domain;

import lombok.Getter;

import java.io.Serializable;

@Getter
public class SessionMember implements Serializable {
    private String name;
    private String email;
    private String imageUrl;
}
