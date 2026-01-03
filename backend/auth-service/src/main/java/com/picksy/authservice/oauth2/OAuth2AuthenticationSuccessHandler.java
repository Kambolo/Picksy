package com.picksy.authservice.oauth2;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {

  private final OAuth2UserProcessorService oAuth2UserProcessorService;

  // frontend redirect
  private final String FRONTEND_REDIRECT = "http://localhost:5173/oauth2/callback";

  @Override
  public void onAuthenticationSuccess(
      HttpServletRequest request, HttpServletResponse response, Authentication authentication)
      throws IOException, ServletException {

    OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
    Map<String, Object> attributes = oauthToken.getPrincipal().getAttributes();

    oAuth2UserProcessorService.processOAuthPostLogin(attributes, response);

    String targetUrl =
        UriComponentsBuilder.fromUriString(FRONTEND_REDIRECT)
            .queryParam("oauth", "google")
            .build()
            .toUriString();

    response.sendRedirect(targetUrl);
  }
}
