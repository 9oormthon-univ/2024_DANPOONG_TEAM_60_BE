    package com.example.goo.Auth.service;
    import com.example.goo.Auth.entity.User;
    import com.example.goo.Auth.repository.UserRepository;
    import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
    import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
    import org.springframework.security.oauth2.core.user.OAuth2User;
    import org.springframework.stereotype.Service;

    import java.util.Map;

    @Service
    public class CustomOAuth2UserService extends DefaultOAuth2UserService {

        private final UserRepository userRepository;

        public CustomOAuth2UserService(UserRepository userRepository) {
            this.userRepository = userRepository;
        }

        @Override
        public OAuth2User loadUser(OAuth2UserRequest userRequest) {
            OAuth2User oAuth2User = super.loadUser(userRequest); // 기본 OAuth2UserService 호출

            // 액세스 토큰 가져오기
            String accessToken = userRequest.getAccessToken().getTokenValue();
            System.out.println("Access Token: " + accessToken);

            Map<String, Object> attributes = oAuth2User.getAttributes();

            // 카카오에서 제공하는 사용자 정보 가져오기
            String provider = userRequest.getClientRegistration().getRegistrationId(); // 예: "kakao"
            String oauthId = String.valueOf(attributes.get("id")); // 카카오에서 제공하는 고유 ID

            // 'kakao_account' 하위 데이터 가져오기
            Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
            String email = kakaoAccount != null ? (String) kakaoAccount.get("email") : null;

            Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");
            String nickname = profile != null ? (String) profile.get("nickname") : null;

            // 데이터베이스에 저장 또는 업데이트
            User user = userRepository.findByProviderAndOauthId(provider, oauthId).orElse(null);
            if (user == null) {
                user = new User();
                user.setOauthId(oauthId);
                user.setProvider(provider);
                user.setEmail(email);
                user.setNickname(nickname);
                userRepository.save(user);
            } else {
                // 기존 사용자 업데이트 (필요 시)
                user.setEmail(email);
                user.setNickname(nickname);
                userRepository.save(user);
            }

            return oAuth2User; // OAuth2User 반환
        }
    }