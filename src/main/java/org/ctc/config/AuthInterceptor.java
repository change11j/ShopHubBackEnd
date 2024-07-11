package org.ctc.config;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.AsyncHandlerInterceptor;

import static org.ctc.util.Jwt.parseToken;


@Component
public class AuthInterceptor implements AsyncHandlerInterceptor {

    private Logger log = LoggerFactory.getLogger(AuthInterceptor.class);

    public static final String AUTHORIZATION_PROPERTY = "Authorization";


    @Value("${jwt.key}")
    private String jwtKey;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = request.getHeader(AUTHORIZATION_PROPERTY);

      if(token==null){
          return true;
      }else {
          Claims claims = parseToken(token,jwtKey);
          return true;
      }


    }

}
