package nike.feed.gcs.security

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.annotation.web.servlet.configuration.EnableWebMvcSecurity
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.web.util.AntPathRequestMatcher
import org.springframework.security.config.annotation.web.builders.WebSecurity
import org.springframework.core.annotation.Order

import org.slf4j.Logger
import org.slf4j.LoggerFactory

import nike.feed.gcs.security.CustomAuthenticationProvider

/**
 * GCS 4.8.6 : Implementation of spring security
 *
 * @author kaarthikeyan.palani
 */
@EnableWebMvcSecurity
@Configuration
@Order(2) // Order level 2 refers to second in the security chain, any REST request otherthan /feed mapping should passs through LDAP authentication basic
public class NikeWebSecurityConfigurationAdapter extends WebSecurityConfigurerAdapter {

    @Autowired
    CustomAuthenticationProvider customAuthenticationProvider

    @Override
    protected void configure(HttpSecurity http) throws Exception{

        //Disable Cross-Site Request Forgery (CSRF) token
        http.csrf().disable();

        http
                //below request mapping can to accessible by all without any authentication
                .authorizeRequests()
                .antMatchers("/", "/js/**", "/webjars/**", "/views/login.html", "/css/**", "/fonts/**", "/images/**").permitAll()
                .anyRequest().authenticated()
                .and()

                //login configurations
                .formLogin()
                .loginPage("/authenticate")
                .defaultSuccessUrl("/handleSuccessLogin")
                .failureUrl("/handleFailureLogin")
                .permitAll()
                .and()

                //logout configurations
                .logout()
                .deleteCookies("JSESSIONID")
                .invalidateHttpSession(true)
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                .logoutSuccessUrl("/handleSuccessLogout")
                .permitAll();

    }

    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {

        //Check for authentication in LDAP with Custom AuthenticationProvider
        auth.authenticationProvider(customAuthenticationProvider);

    }
}


