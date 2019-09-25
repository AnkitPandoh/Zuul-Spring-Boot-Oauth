package com.security.oauth.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.jdbc.JdbcDaoImpl;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	private final JdbcConfig jdbcConfig;
	
	
	@Autowired
	public WebSecurityConfig(final JdbcConfig jdbcConfig) {
		this.jdbcConfig = jdbcConfig;
	}

	@Override
	protected void configure(AuthenticationManagerBuilder authBuilder) throws Exception {
		authBuilder.userDetailsService(userDetailsService()).passwordEncoder(passwordEncoder());
	}

	@Bean
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}

	@Bean
	public DataSource dataSource() {
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName(this.jdbcConfig.getDriverClassName());
		dataSource.setUrl(this.jdbcConfig.getUrl());
		dataSource.setUsername(this.jdbcConfig.getUser());
		dataSource.setPassword(this.jdbcConfig.getPassword());

		return dataSource;
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return PasswordEncoderFactories.createDelegatingPasswordEncoder();
	}

	@Bean
	public UserDetailsService userDetailsService() {
		UserDetailsService userDetails = new JdbcDaoImpl();
		((JdbcDaoImpl) userDetails).setDataSource(dataSource());
		return userDetails;
	}

	@Configuration
	static class JdbcConfig {

		@Value("${jdbc.driverClassName}")
		private String driverClassName;

		@Value("${jdbc.url}")
		private String url;

		@Value("${jdbc.user}")
		private String user;

		@Value("${jdbc.pass}")
		private String password;

		public String getDriverClassName() {
			return driverClassName;
		}

		public String getUrl() {
			return url;
		}

		public String getUser() {
			return user;
		}

		public String getPassword() {
			return password;
		}

	}
}
