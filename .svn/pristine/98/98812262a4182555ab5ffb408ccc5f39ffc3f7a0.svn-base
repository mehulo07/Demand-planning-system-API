package com.v1.DemandPlanningService.tokenutility;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.stereotype.Service;

import com.v1.DemandPlanningService.bean.UserInfo;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;


@Service
public class JwtTokenUtil implements Serializable {
private static final long serialVersionUID = -7137666064717643826L;
	
	public static final long JWT_TOKEN_VALIDITY = 60 * 60 * 60;
	private static final String secret= "restServiceInUse";
	
	public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {	
		final Claims claims = getAllClaimsFromToken(token);
		return claimsResolver.apply(claims);
	}
	
	//retrieve username from jwt token
	public String getUsernameFromToken(String token) {
		System.out.println("getUsernameFromToken is called");
		return getClaimFromToken(token, Claims::getSubject);
	}
	
	
	//For retrieving  any information from token we will need the secret key
	public Claims getAllClaimsFromToken(String token) {
		return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
	}

	//check if the token has expired
	private Boolean isTokenExpired(String token) {
		final Date expiration = getExpirationDateFromToken(token);
		return expiration.before(new Date());
	}
	
	//retrieve expiration date from jwt token
	public Date getExpirationDateFromToken(String token) {
		return getClaimFromToken(token, Claims::getExpiration);
	}
	
	//generate token for user
	public String generateToken(UserInfo userDetails , Map<String, Object> claims) {	
		//Map<String, Object> claims = new HashMap<>();
		return doGenerateToken(claims, userDetails.getCustId());
	}
	
	private String doGenerateToken(Map<String, Object> claims, String subject) {
		return Jwts.builder().setClaims(claims)
			.setSubject(subject).setIssuedAt(new Date(System.currentTimeMillis()))
			.setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY * 1000))
			.signWith(SignatureAlgorithm.HS512, secret).compact();
	}		
	
	//validate token
	public Boolean validateToken(String token, UserInfo  userDetails) {
		final String username = getUsernameFromToken(token);
		return (username.equals(userDetails.getCustId()) && !isTokenExpired(token));
	}
	
	/*Claims validateToken(String jwtToken) {
		return Jwts.parser().setSigningKey(secret.getBytes()).parseClaimsJws(jwtToken).getBody();
	}*/
	
}
