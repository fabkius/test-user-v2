package com.nissum.user.controller;

import com.nissum.user.domain.UserDto;
import com.nissum.user.domain.UserRsDto;
import com.nissum.user.service.IUserService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class SaveUserController {


    @Autowired
    private IUserService iUserService;

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public UserRsDto addUser(@RequestBody UserDto request){
        String token = getJWTToken(request.getEmail(),request.getPassword());
        UserRsDto response = iUserService.save(request);
        response.setToken(token);
        response.setIsactive(true);
        return response;
    }


    private String getJWTToken(String email,String password) {
        List<GrantedAuthority> grantedAuthorities = AuthorityUtils
                .commaSeparatedStringToAuthorityList("ROLE_USER");

        String token = Jwts
                .builder()
                .setId(email)
                .setSubject(email)
                .claim("authorities",
                        grantedAuthorities.stream()
                                .map(GrantedAuthority::getAuthority)
                                .collect(Collectors.toList()))
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 600000))
                .signWith(SignatureAlgorithm.HS512,
                        password.getBytes()).compact();

        return "Bearer " + token;
    }

}
