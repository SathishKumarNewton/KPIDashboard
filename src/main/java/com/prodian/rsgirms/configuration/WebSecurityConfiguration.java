
package com.prodian.rsgirms.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.prodian.rsgirms.userapp.service.MyUserDetailsService;

/**
 * @author CSS
 *
 */

@Configuration
@EnableWebSecurity
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private MyUserDetailsService userDetailsService;



    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
                auth
                    .userDetailsService(userDetailsService)
                    .passwordEncoder(bCryptPasswordEncoder);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
    	/*String loginPage = "/login";
        String logoutPage = "/logout";
    	http
        .csrf()
        .disable()
        .authorizeRequests()
        .antMatchers("/secure").authenticated()
        .anyRequest().anonymous()
        .and().csrf().disable()
        .formLogin()
        .loginPage(loginPage)
        .loginPage("/")
        .failureUrl("/login?error=true")
        .successHandler(new CustomAuthenticationSuccessHandler())
        .usernameParameter("user_name")
        .passwordParameter("password")
        .and().logout()
		.logoutRequestMatcher(new AntPathRequestMatcher(logoutPage)).logoutSuccessUrl(loginPage).and()
		.exceptionHandling();*/

        String loginPage = "/login";
        String logoutPage = "/logout";

       http.
                authorizeRequests()
                .antMatchers("/").permitAll()
                .antMatchers("/login**", "/").permitAll()     
                .antMatchers(loginPage).permitAll()
                .antMatchers("/registration").permitAll()
                .antMatchers("/admin/**").hasAnyAuthority("ADMIN")
                .antMatchers("/user/**").hasAnyAuthority("USER")
                .antMatchers("/admin/**").permitAll()
                .antMatchers("/user/**").permitAll()
                .antMatchers("/gwpPivot","/renewalRetention","/getAllModels","/motorRollOverRate").hasAnyAuthority("ADMIN","USER")
                .anyRequest()
                .authenticated()
                .and().csrf().disable()
                .formLogin()
                .loginPage(loginPage)
                .loginPage("/")
                .failureUrl("/login?error=true")
                .successHandler(new CustomAuthenticationSuccessHandler())
                .usernameParameter("user_name")
                .passwordParameter("password")
                .and().logout()
				.logoutRequestMatcher(new AntPathRequestMatcher(logoutPage)).logoutSuccessUrl(loginPage).and()
				.exceptionHandling();
    	
    	
	}

	@Override
	public void configure(WebSecurity web) throws Exception {
		web.ignoring().antMatchers("/resources/**", "/static/**", "/css/**", "/js/**", "/images/**","/assets/**"
				,"/plugins_cr/**","/styles/**");
	}

}
