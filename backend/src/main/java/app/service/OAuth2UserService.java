package app.service;

import app.entity.User;
import app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OAuth2UserService extends OidcUserService {
    private final UserRepository userRepository;

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) {
        OidcUser oidcUser = super.loadUser(userRequest);
        String email = oidcUser.getEmail();
        String username = oidcUser.getFullName() != null ? oidcUser.getFullName() : email;
        String googleId = oidcUser.getSubject();

        User user = userRepository.findByEmail(email)
                .orElseGet(() -> {
                    User newUser = new User();
                    newUser.setEmail(email);
                    newUser.setUsername(username);
                    newUser.setPassword(UUID.randomUUID().toString());
                    newUser.setGoogleId(googleId);
                    newUser.setActive(true);
                    newUser.setCreateAt(LocalDateTime.now());
                    return userRepository.save(newUser);
                });

        if (!user.isActive()) {
            throw new OAuth2AuthenticationException(
                    new OAuth2Error("USER_BLOCKED", "Tài khoản của bạn đã bị khóa", null)
            );
        }

        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);

        return new DefaultOidcUser(
                Collections.singletonList(() -> user.isAdmin() ? "ADMIN" : "USER"),
                oidcUser.getIdToken(),
                oidcUser.getUserInfo()
        );
    }
}