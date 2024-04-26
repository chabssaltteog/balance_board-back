package chabssaltteog.balance_board.domain.oauth.api;

import lombok.Data;

@Data
public class OauthToken {
    private String access_token;
    private String token_type;  //bearer
    private String refresh_token;
    private int expires_in; //access token 만료 시간(초) -> 720분 = 12시간
    private String scope;
    private int refresh_token_expires_in;   //refresh token 만료 시간(초) -> 만료 시간 X = 로그아웃 시 만료
}
