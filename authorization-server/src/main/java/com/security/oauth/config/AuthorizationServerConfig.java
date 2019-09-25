package com.security.oauth.config;

import java.io.IOException;
import java.nio.file.Paths;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

import io.micrometer.core.instrument.util.IOUtils;

@Configuration
@EnableAuthorizationServer
public class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {

	private final AuthenticationManager authenticationManager;

	private final UserDetailsService userDetailsService;

	private final DataSource dataSource;

	private final JwtConfig jwtConfig;

	@Autowired
	public AuthorizationServerConfig(final AuthenticationManager authenticationManager,
			final UserDetailsService userDetailsService, final DataSource dataSource, final JwtConfig jwtConfig) {
		this.authenticationManager = authenticationManager;
		this.userDetailsService = userDetailsService;
		this.dataSource = dataSource;
		this.jwtConfig = jwtConfig;
	}

	/**
	 * {@inheritDoc}
	 *
	 */
	@Override
	public void configure(final ClientDetailsServiceConfigurer client) throws Exception {
		client.jdbc(this.dataSource);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void configure(final AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
		endpoints.authenticationManager(this.authenticationManager).userDetailsService(this.userDetailsService)
				.accessTokenConverter(this.accessTokenConverter()).tokenStore(this.tokenStore());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
		security.tokenKeyAccess("permitAll()").checkTokenAccess("isAuthenticated()")
				.allowFormAuthenticationForClients();
	}

	/**
	 * creates tokens via random value and handles everything except for the
	 * persistence of the tokens which it delegates to a TokenStore. Following are
	 * the token stores that are available: 1. InMemoryTokenStore 2. JdbcTokenStore
	 * 3. Jwt Store
	 * 
	 * @return
	 * @throws IOException 
	 */
	@Bean
	public DefaultTokenServices tokenServices() throws IOException {
		DefaultTokenServices tokenServices = new DefaultTokenServices();
		tokenServices.setTokenStore(tokenStore());
		tokenServices.setSupportRefreshToken(true);
		return tokenServices;
	}

	/**
	 * JwtTokenStore is not really a "store" in the sense that it doesn't persist
	 * any data, but it plays the same role of translating between token values and
	 * authentication information in the DefaultTokenServices
	 * 
	 * @return
	 * @throws IOException 
	 */
	@Bean
	public TokenStore tokenStore() throws IOException {
		return new JwtTokenStore(accessTokenConverter());
	}

	/**
	 * required to sign the token using symmetric or asymmetric key
	 * 
	 * @return
	 * @throws IOException 
	 */
	@Bean
	public JwtAccessTokenConverter accessTokenConverter() throws IOException {
		JwtAccessTokenConverter tokenConverter = new JwtAccessTokenConverter();
		// will be using asymmetric key for signing and verifying the token
		tokenConverter.setSigningKey(getSigningKey());
		return tokenConverter;
	}
	
	private String getSigningKey() throws IOException {
		FileSystemResource resource = new FileSystemResource(Paths.get(jwtConfig.getKeyPath().toString()));
		return IOUtils.toString(resource.getInputStream());
	}


	@Configuration
	static class JwtConfig {

		@Value("${jwt.private-key.path}")
		private String keyPath;

		public String getKeyPath() {
			return keyPath;
		}

	}

}
