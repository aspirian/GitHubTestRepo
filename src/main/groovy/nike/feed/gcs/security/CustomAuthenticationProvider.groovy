package nike.feed.gcs.security
 
import java.util.Collection

import org.slf4j.Logger
import org.slf4j.LoggerFactory
 
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.GrantedAuthority
import org.springframework.stereotype.Component
 
import com.mascon.ldap.LDAPAuthenticator

import nike.feed.gcs.model.User
 
@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {
 
    // Define the logger object for this class
    private final Logger LOG = LoggerFactory.getLogger(this.getClass())
    
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        
        String username = (String) authentication.getName()
        String password = (String) authentication.getCredentials()
        User user = null;
        
        try{
            user = getUserByUsernameAndPassword(username, password)
        } catch(BadCredentialsException e){
            throw e
        }
        
        if (user == null) {
            throw new BadCredentialsException("Invalid cridential !!!")
        }
 
        Collection<? extends GrantedAuthority> authorities = getAuthorities()
 
        return new UsernamePasswordAuthenticationToken(user, password, authorities)
    }
    
    private List<GrantedAuthority> getAuthorities() {
        
        List<GrantedAuthority> authList = new ArrayList<GrantedAuthority>()
        if(authList){
            authList.add(new SimpleGrantedAuthority("USER"))
        }

        return authList
      }
 
    @Override
    public boolean supports(Class<?> arg0) {
        return true;
    }
    
    private User getUserByUsernameAndPassword(String username, String password) throws BadCredentialsException {
        
        User user = null
        def displayName
        
        LDAPAuthenticator authenticator = new LDAPAuthenticator()
        
        boolean results = authenticator.authenticate(username, password)
        
        if (!results) {
            throw new BadCredentialsException("Invalid cridential !!!");
        }
        
        // Uncomment the below line once we deploy in SCHAWK servers
        //displayName = authenticator.getValue(username, "displayName")
        
        // comment the below line while uncommenting the above lines and vice versa
        displayName = username
        
        user = new User(username, displayName)
        return user
    }
}