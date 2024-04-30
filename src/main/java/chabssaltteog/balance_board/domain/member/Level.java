package chabssaltteog.balance_board.domain.member;

import lombok.Getter;

@Getter
public enum Level {

    레벨1(1), 레벨2(2), 레벨3(3), 레벨4(4), 레벨5(5),
    레벨6(6), 레벨7(7), 레벨8(8), 레벨9(9), 레벨10(10);

    private final int value;

    Level(int value) {
        this.value = value;
    }

}
