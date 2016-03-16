package nike.feed.gcs.security

import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.annotation.web.servlet.configuration.EnableWebMvcSecurity
import org.springframework.core.annotation.Order

/**
 * GCS 4.8.6 : Implementation of spring security 
 * 
 * @author kaarthikeyan.palani
 */
@EnableWebMvcSecurity
@Configuration
@Order(1) // Order level 1 refers to first in the security chain, any REST request with /feed mapping should passs through http basic
public class FeedWebSecurityConfigurationAdapter  extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http
                .antMatcher("/feed/**")
                .authorizeRequests()
                .anyRequest().authenticated()
                .and()
                .httpBasic();
    }

}
