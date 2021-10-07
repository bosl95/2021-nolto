package com.wooteco.nolto.auth.infrastructure.oauth;

import com.wooteco.nolto.auth.domain.SocialType;
import com.wooteco.nolto.auth.infrastructure.oauth.dto.GithubUserResponse;
import com.wooteco.nolto.auth.ui.dto.OAuthTokenResponse;
import com.wooteco.nolto.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;

@Component
@RequiredArgsConstructor
public class NoltoHubClient extends OAuthClientDetail {

    private static final String NOLTOHUB_USERINFO_REQUEST_URL = "http://noltohub.kro.kr/user";
    private static final String GITHUB_TOKEN_REQUEST_URL = "http://noltohub.kro.kr/token";

    private final NoltoHubOAuthInfo noltoHubOAuthInfo;

    @Override
    public User generateUserInfo(OAuthTokenResponse oauthToken) {
        return super.requestUserInfo(oauthToken, NOLTOHUB_USERINFO_REQUEST_URL, GithubUserResponse.class);
    }

    @Override
    public OAuthTokenResponse generateAccessToken(String code) {
        return super.requestAccessToken(code, GITHUB_TOKEN_REQUEST_URL);
    }

    @Override
    public boolean checkType(SocialType socialType) {
        return SocialType.NOLTOHUB.equals(socialType);
    }

    @Override
    protected MultiValueMap<String, String> generateAccessTokenRequestParam(String code) {
        return noltoHubOAuthInfo.generateAccessTokenRequestParam(code);
    }
}

