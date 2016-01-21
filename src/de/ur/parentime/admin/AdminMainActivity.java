package de.ur.parentime.admin;

/*
 *  This class is the Main Activity that handles the loading of fragments as well as showing
 */

import com.parse.ParseAnalytics;
import com.parse.ParseUser;

import de.ur.korbinian.kasberger.R;
import de.ur.parentime.main.LoginActivity;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class AdminMainActivity extends Activity {

//	Declares variables
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    private String[] mDrawerItems;
    private static final String TAG = "Debug";
    private final String PREFS_NAME = "MyPrefsFile";
	protected SharedPreferences settings;
	private Menu optionsMenu;
	

//	Creates the Activity, loads default fragment
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);   
        setContentView(R.layout.mainactivity_drawer);
        ParseAnalytics.trackAppOpened(getIntent());
        Fragment fragment = new HomeAdminFragment();
		FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment, "Home Admin").commit();
        drawerOnCreate();
       
        settings = getSharedPreferences(PREFS_NAME, 0);
    }
    
//	Creates the Navigation Drawer
    private void drawerOnCreate() {
    	 mTitle = mDrawerTitle = getTitle();
         mDrawerItems = getResources().getStringArray(R.array.drawer_items_admin);
         mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
         mDrawerList = (ListView) findViewById(R.id.left_drawer); 
         mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
         mDrawerList.setAdapter(new ArrayAdapter<String>(this,
                 R.layout.drawer_list_item, mDrawerItems));
         
         mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
         getActionBar().setDisplayHomeAsUpEnabled(true);
         getActionBar().setHomeButtonEnabled(true);
         mDrawerToggle = new ActionBarDrawerToggle(
                 this, /* host Activity */
                 mDrawerLayout, /* DrawerActivity object */
                 R.drawable.ic_navigation_drawer, /* nav drawer image to replace 'Up' caret */
                 R.string.drawer_open, /* "open drawer" description for accessibility */
                 R.string.drawer_close /* "close drawer" description for accessibility */
                 ) {
             public void onDrawerClosed(View view) {
                 getActionBar().setTitle(mTitle);
                 invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
             }

             public void onDrawerOpened(View drawerView) {
                 getActionBar().setTitle(mDrawerTitle);
                 invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
             }
         };
         mDrawerLayout.setDrawerListener(mDrawerToggle);
}

//    creates the Actionbar menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	this.optionsMenu = menu;
    	MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.admin, menu);
        
        if (optionsMenu != null) {
        	
        	optionsMenu.removeItem(R.id.refresh);
        	}
       
        return super.onCreateOptionsMenu(menu);
    }
   
//    Handles the clicks on Actionbar item s
    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
    	if (mDrawerToggle.onOptionsItemSelected(item)) {
    		Log.d(TAG, "drawer");
    		return true;
    	}	
    	switch (item.getItemId()) {
        case R.id.refresh:
        	
        return false;
        }
          return super.onOptionsItemSelected(item);
    }

//    Handles the Navigationbar item clicks
    public class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        	 selectItem(position);
        }
    }
    
    
//    Loads the fragment depending on the clicked position in the Navigation Drawer
    private void selectItem(int position) {
        Fragment fragment;
    	android.app.FragmentTransaction transaction;
        switch (position) {
		case 0:
			fragment = new HomeAdminFragment();
			transaction = getFragmentManager().beginTransaction();
	        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
	        transaction.replace(R.id.content_frame, fragment);
	        transaction.addToBackStack("Home Admin");
	        transaction.commit();   
			break;
		case 1:
			fragment = new CreateTeacherFragment();
			transaction = getFragmentManager().beginTransaction();
	        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
	        transaction.replace(R.id.content_frame, fragment);
	        transaction.addToBackStack("Manage Teacher");
	        transaction.commit();
			break;
		case 2:
			Log.v(TAG, "logout pressed");
			ParseUser.logOut();
			Intent i = new Intent(this,LoginActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
            finish();
			break;
		}

        setTitle(mDrawerItems[position]);
        mDrawerList.setItemChecked(position, true);
       
        mDrawerLayout.closeDrawer(mDrawerList);
    }

//    Sets the Title of the actionbar
    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getActionBar().setTitle(mTitle);
    }

// 	Sync the toggle state after onRestoreInstanceState has occurred.
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        mDrawerToggle.syncState();
    }
    
 // Pass any configuration change to the drawer toggls
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);    
        mDrawerToggle.onConfigurationChanged(newConfig);
    }
   
}