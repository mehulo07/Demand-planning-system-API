package com.v1.DemandPlanningService;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.v1.DemandPlanningService.tokenutility.JWTAuthorizationFilter;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@SpringBootApplication
public class DemandPlanningServiceApplication  extends SpringBootServletInitializer{
	static{
		String logPath = "";
		if (System.getProperty("os.name").equalsIgnoreCase("Linux")) {
			logPath = "/tmp/BNS-LOG/DPS_RestService/";
		} else {
			logPath = "C:\\\\BNS-LOG\\\\DPS_RestService\\\\";
		}
		System.setProperty("logPath", logPath);
	}
	
    public static void main(String[] args) {
    	SpringApplication.run(DemandPlanningServiceApplication.class, args);
	}
    
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder springApplicationBuilder) {
    	return springApplicationBuilder.sources(DemandPlanningServiceApplication.class);
    }
    
    @EnableWebSecurity
	@Configuration
	class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    	@Override
		public void configure(WebSecurity web) throws Exception {
		    web.ignoring().antMatchers("/user/v1/DPS/login");
		}
		@Override
		protected void configure(HttpSecurity http) throws Exception {
			http.csrf().disable().authorizeRequests()
			.antMatchers(HttpMethod.POST, "/**").permitAll()
			.anyRequest().authenticated().and()
			.addFilterAfter(new JWTAuthorizationFilter(), UsernamePasswordAuthenticationFilter.class);
		}
	}
   
    
    @SuppressWarnings("deprecation")
	@Bean
    public WebMvcConfigurer corsConfigurer() {
       return new WebMvcConfigurerAdapter() {
          @Override
          public void addCorsMappings(CorsRegistry registry) {
             registry.addMapping("/**").allowedOrigins("http://localhost:4200");
          }
       };
    }
    
    
}
