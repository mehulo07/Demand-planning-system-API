package com.v1.DemandPlanningService.tokenutility;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;

@CrossOrigin(origins = "*", allowedHeaders = "*")
public class JWTAuthorizationFilter extends OncePerRequestFilter {

	private final String HEADER = "Authorization";
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
	try {
	
		JwtTokenUtil jwtTokenUtil = new JwtTokenUtil();
		
		 Enumeration<String> headerNames = request.getHeaderNames();
		 
	        while (headerNames.hasMoreElements()) {
	 
	            String headerName = headerNames.nextElement();
	            Enumeration<String> headers = request.getHeaders(headerName);
	            while (headers.hasMoreElements()) {
	                String headerValue = headers.nextElement();
	                System.out.println("HeaderName :"+ headerName + "  value :"+ headerValue);
	            }
	        }
	        String token = request.getHeader("Access-Control-Allow-Headers");
		System.out.println("customer id is :"+jwtTokenUtil.getUsernameFromToken(token));
		
		if(checkJWTToken(request, response)) {
			Claims claims = jwtTokenUtil.getAllClaimsFromToken(token);
			if (claims.get("authorities") != null) {
				setUpSpringAuthentication(claims);
			} else {
				SecurityContextHolder.clearContext();
			}
		}
	
	}catch (ExpiredJwtException | UnsupportedJwtException | MalformedJwtException e) {
		response.setStatus(HttpServletResponse.SC_FORBIDDEN);
		((HttpServletResponse) response).sendError(HttpServletResponse.SC_FORBIDDEN, e.getMessage());
		return;
	}
		filterChain.doFilter(request, response);
	}
	
	private void setUpSpringAuthentication(Claims claims) {
		@SuppressWarnings("unchecked")
		List<Object> authorities = (List) claims.get("authorities");
		List<String> strings = new ArrayList<>(authorities.size());
		
		for (Object object : authorities) {
		    strings.add(Objects.toString(object, null));
		}
		
		UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(claims.getSubject(), null,
				strings.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList()));
				SecurityContextHolder.getContext().setAuthentication(auth);
	}
	
	private boolean checkJWTToken(HttpServletRequest request, HttpServletResponse res) {
		String authenticationHeader = request.getHeader(HEADER);
		if (authenticationHeader == null ) {
			return false;
		}else {
			return true;
		}
	}
}
