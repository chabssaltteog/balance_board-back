package chabssaltteog.balance_board.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MyRole {
    ADMIN("ROLE_ADMIN", "ADMIN"),
    USER("ROLE_USER", "일반 사용자");    // 스프링 시큐리티 : 권한 코드에 ROLE_ 필수

    private final String key;
    private final String string;
}