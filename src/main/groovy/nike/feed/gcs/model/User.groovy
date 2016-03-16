/**
 * 
 */
package nike.feed.gcs.model

import groovy.transform.ToString

/**
 *
 */
@ToString(includeNames=true)
class User {

	String username
	String displayName
    
        User(String username, String displayName){
            this.username = username
            this.displayName = displayName            
        }
}
