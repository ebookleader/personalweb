package personalwebsite.personalweb.config.auth;


import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import personalwebsite.personalweb.config.auth.dto.OAuthAttributes;
import personalwebsite.personalweb.config.auth.dto.SessionUser;
import personalwebsite.personalweb.domain.user.User;
import personalwebsite.personalweb.domain.user.UserRepository;
import personalwebsite.personalweb.web.advice.RestException;

import javax.servlet.http.HttpSession;
import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
@Service
public class CustomOauth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final UserRepository userRepository;
    private final HttpSession httpSession;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();    //구글, 네이버 구분
        String userNameAttributeName = userRequest.getClientRegistration().getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();

        OAuthAttributes attributes = OAuthAttributes.of(registrationId, userNameAttributeName, oAuth2User.getAttributes());

        User user = saveOrUpdate(attributes);

        httpSession.setAttribute("user", new SessionUser(user));

        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority(user.getRoleKye())),
                attributes.getAttributes(),
                attributes.getNameAttributeKey());
    }

    private User saveOrUpdate(OAuthAttributes attributes) {
        User user = userRepository.findByEmail(attributes.getEmail());  // 이메일에 맞는 유저 정보 찾아옴
        if (user == null) { // 해당 유저가 없을경우
            List<User> allUser = userRepository.findAll(); // 가입되어있는 모든 유저 정보 가져옴
            if (!allUser.isEmpty()) {   // 1명 가입되어있지만 이메일이 다를경우 exception 처리
                throw new RestException(HttpStatus.NOT_FOUND, "You are already registered with a different ID");
            }
            user = attributes.toEntity(); // 가입된 유저가 한명도 없는경우로, 새로 회원가입

        } else { // 유저가 있을경우 정보 업데이트
            user = user.update(attributes.getName(), attributes.getPicture());
        }
        return userRepository.save(user);
    }

}
