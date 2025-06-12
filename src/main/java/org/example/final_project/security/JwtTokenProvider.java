package org.example.final_project.security;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.example.final_project.configs.SecurityContest;
import org.example.final_project.models.User;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
@Component  // thêm annotation này để chắc chắn

public class JwtTokenProvider {
	
	public JwtTokenClaims getClaimsFromToken(String token) {
		SecretKey key= Keys.hmacShaKeyFor(SecurityContest.JWT_KEY.getBytes());
		
	    Claims claims = Jwts.parser()
	            .setSigningKey(key)
	            .parseClaimsJws(token)
	            .getBody();
	    String username= String.valueOf(claims.get("username"));

	    JwtTokenClaims jwtTokenClaims = new JwtTokenClaims();
//	    jwtTokenClaims.setUsername(Long.parseLong(claims.getSubject()));
	    jwtTokenClaims.setUsername(username);

	    return jwtTokenClaims;
	}
	
	public String  generateJwtToken(User user) {
		SecretKey key=Keys.hmacShaKeyFor(SecurityContest.JWT_KEY.getBytes());
		
		String jwt=Jwts.builder()
				.setIssuer("Mai Xuan Tung")
				.claim("username",user.getEmail())
				.setIssuedAt(new Date())
				.setExpiration(new Date(new Date().getTime()+ 990000000))
				.signWith(key).compact();
		
		
		return jwt;
	}


}
