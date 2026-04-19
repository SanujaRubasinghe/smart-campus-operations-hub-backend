package com.springboot.smartcampusoperationshub.security;

import com.springboot.smartcampusoperationshub.model.enums.AuthProvider;
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

        // Detect provider
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        boolean isGithub = "github".equalsIgnoreCase(registrationId);

        // --- Resolve email ---
        String email = (String) attributes.get("email");
        if (email == null && isGithub) {
            // GitHub users with private email: fall back to login@users.noreply.github.com
            String login = (String) attributes.get("login");
            email = (login != null ? login : "unknown") + "@users.noreply.github.com";
            log.warn("GitHub user has private email, using synthetic email: {}", email);
        }

        // --- Resolve name ---
        String name = (String) attributes.get("name");
        if (name == null && isGithub) {
            name = (String) attributes.get("login"); // GitHub login as fallback
        }

        // --- Resolve picture ---
        String picture;
        if (isGithub) {
            picture = (String) attributes.get("avatar_url");
        } else {
            picture = (String) attributes.get("picture");
        }

        // --- Resolve provider ID ---
        String providerId;
        if (isGithub) {
            Object idObj = attributes.get("id");
            providerId = idObj != null ? String.valueOf(idObj) : null;
        } else {
            providerId = (String) attributes.get("sub");
        }

        AuthProvider provider = isGithub ? AuthProvider.github : AuthProvider.google;

        Optional<User> userOptional = userRepository.findByEmail(email);
        User user;

        if (userOptional.isPresent()) {
            user = userOptional.get();
            user.setName(name);
            user.setPicture(picture);
            user.setLastLogin(LocalDateTime.now());
            log.info("Existing user logged in: {} via {}", email, registrationId);
        } else {
            user = new User();
            user.setEmail(email);
            user.setUsername(deriveUsername(isGithub ? (String) attributes.get("login") : null, email));
            user.setName(name);
            user.setPicture(picture);
            user.setProvider(provider);
            user.setProviderId(providerId);
            user.setLastLogin(LocalDateTime.now());
            log.info("New user created: {} via {}", email, registrationId);
        }

        user = userRepository.save(user);

        return UserPrincipal.create(user);
    }

    /** Derive a unique username: prefer GitHub login, fall back to email prefix */
    private String deriveUsername(String preferredLogin, String email) {
        String base = (preferredLogin != null && !preferredLogin.isBlank())
                ? preferredLogin.toLowerCase().replaceAll("[^a-z0-9._]", "_")
                : email.split("@")[0].toLowerCase().replaceAll("[^a-z0-9._]", "_");
        String candidate = base;
        int attempt = 0;
        while (userRepository.existsByUsername(candidate)) {
            candidate = base + (++attempt);
        }
        return candidate;
    }
}