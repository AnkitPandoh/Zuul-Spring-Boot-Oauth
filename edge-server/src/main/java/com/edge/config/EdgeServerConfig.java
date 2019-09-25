package com.edge.config;

import java.io.IOException;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

import io.micrometer.core.instrument.util.IOUtils;

@Configuration
@EnableResourceServer
public class EdgeServerConfig extends ResourceServerConfigurerAdapter{
	
	private final JwtConfig jwtConfig;
	
	public EdgeServerConfig(final JwtConfig jwtConfig) {
		this.jwtConfig = jwtConfig;
	}
	
	@Override
	public void configure(HttpSecurity http) throws Exception{
		http.authorizeRequests().antMatchers("/oauth/**").permitAll()
		.antMatchers("/**/api/**").authenticated();
        
	}
	
	@Bean
	public DefaultTokenServices tokenServices() throws IOException {
		DefaultTokenServices tokenServices = new DefaultTokenServices();
		tokenServices.setTokenStore(tokenStore());
		tokenServices.setSupportRefreshToken(true);
		return tokenServices;
	}
	
	@Bean
	public TokenStore tokenStore() throws IOException {
		return new JwtTokenStore(accessTokenConverter());
	}
	
	@Bean
	public JwtAccessTokenConverter accessTokenConverter() throws IOException {
		JwtAccessTokenConverter tokenConverter = new JwtAccessTokenConverter();
		FileSystemResource resource = new FileSystemResource(Paths.get(jwtConfig.getKeyPath()));
		tokenConverter.setVerifierKey(IOUtils.toString(resource.getInputStream()));
		return tokenConverter;
	}
	
	@Configuration
	static class JwtConfig {

		@Value("${jwt.public-key.path}")
		private String keyPath;

		public String getKeyPath() {
			return keyPath;
		}

	}
}
