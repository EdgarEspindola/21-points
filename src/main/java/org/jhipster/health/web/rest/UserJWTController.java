package org.jhipster.health.web.rest;

import org.jhipster.health.security.jwt.JWTConfigurer;
import org.jhipster.health.security.jwt.JWTFilter;
import org.jhipster.health.security.jwt.TokenProvider;
import org.jhipster.health.web.rest.vm.LoginVM;

import java.util.Collections;

import com.codahale.metrics.annotation.Timed;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api")
public class UserJWTController {

    private final Logger log = LoggerFactory.getLogger(JWTFilter.class);

    @Inject
    private TokenProvider tokenProvider;

    @Inject
    private AuthenticationManager authenticationManager;

    @PostMapping("/authenticate")
    @Timed
    public ResponseEntity<?> authorize(@Valid @RequestBody LoginVM loginVM, HttpServletResponse response) {

        UsernamePasswordAuthenticationToken authenticationToken =
            new UsernamePasswordAuthenticationToken(loginVM.getUsername(), loginVM.getPassword());

        try {
            log.debug("Al inicio del try");
            Authentication authentication = this.authenticationManager.authenticate(authenticationToken);
            log.debug("A la mitad de try 1");
            SecurityContextHolder.getContext().setAuthentication(authentication);
            log.debug("A la mitad de try 2");
            boolean rememberMe = (loginVM.isRememberMe() == null) ? false : loginVM.isRememberMe();
            log.debug("A la mitad de try 3");
            String jwt = tokenProvider.createToken(authentication, rememberMe);
            log.debug("A la mitad de try 4");
            response.addHeader(JWTConfigurer.AUTHORIZATION_HEADER, "Bearer " + jwt);
            log.debug("Al final del try");
            return ResponseEntity.ok(new JWTToken(jwt));
        } catch (AuthenticationException exception) {
            log.debug("Entra a una excwepcion");
            return new ResponseEntity<>(Collections.singletonMap("AuthenticationException",exception.getLocalizedMessage()), HttpStatus.UNAUTHORIZED);
        }
    }
}
