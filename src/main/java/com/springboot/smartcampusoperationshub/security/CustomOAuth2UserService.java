package com.springboot.smartcampusoperationshub.security;

import com.springboot.smartcampusoperationshub.model.AuthProvider;
import com.springboot.smartcampusoperationshub.model.User;
import com.springboot.smartcampusoperationshub.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        try {
            return processOAuth2User(userRequest, oAuth2User);
        } catch (Exception ex) {
            log.error("Error processing OAuth2 user", ex);
            throw ex;
        }
    }

    @Override
    public void setAttributesConverter(Converter<OAuth2UserRequest, Converter<Map<String, Object>, Map<String, Object>>> attributesConverter) {
        super.setAttributesConverter(attributesConverter);
    }

    private OAuth2User processOAuth2User(OAuth2UserRequest userRequest, OAuth2User oAuth2User) {
        Map<String, Object> attributes = oAuth2User.getAttributes();
        String email = (String) attributes.get("email");
        String name = (String) attributes.get("name");
        String picture = (String) attributes.get("picture");

        Optional<User> userOptional = userRepository.findByEmail(email);
        User user;

        if (userOptional.isPresent()) {
            user = userOptional.get();
            user.setName(name);
            user.setPicture(picture);
            user.setLastLogin(LocalDateTime.now());
            log.info("Existing user logged in: {}", email);
        } else {
            user = new User();
            user.setEmail(email);
            user.setName(name);
            user.setPicture(picture);
            user.setProvider(AuthProvider.google);
            user.setProviderId((String) attributes.get("sub"));
            user.setLastLogin(LocalDateTime.now());
            log.info("New user created: {}", email);
        }

        user = userRepository.save(user);

        return UserPrincipal.create(user);
    }
}