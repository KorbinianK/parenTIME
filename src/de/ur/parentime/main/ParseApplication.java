package de.ur.parentime.main;
 
/*
 * Connects the Application to the Parse Database
 */
import com.parse.Parse;
import com.parse.ParseACL;
 
import android.app.Application;
 
public class ParseApplication extends Application {
 
	@Override
    public void onCreate() {
        super.onCreate();

        Parse.initialize(this, "ZXmBHzQvkry61g7iv09gTtrVpEuCzRkIqJHTrYNz", "sWtom4Ebk7kpOg8hOILSc0Pu24jMwPLmNp4INIpm"); 
 
        ParseACL defaultACL = new ParseACL();
 
        defaultACL.setPublicReadAccess(true);
 
        ParseACL.setDefaultACL(defaultACL, true);
    }
 
}
