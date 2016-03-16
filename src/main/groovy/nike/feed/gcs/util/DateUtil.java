/**
 * 
 */
package nike.feed.gcs.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @author kaarthikeyan.palani
 *
 */
public class DateUtil {

    /**
     * Get current date as string
     */
    public static String getCurrentDateAsString(String dateFormat) {
        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);
        return simpleDateFormat.format(date);
    }
    
}
