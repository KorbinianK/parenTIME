package de.ur.parentime.parent;

/*
 * This class is the Main Activity that handles the loading of fragments as well as showing
 * new users helpful information
 */
import com.parse.ParseAnalytics;
import com.parse.ParseUser;

import de.ur.korbinian.kasberger.R;
import de.ur.parentime.main.LoginActivity;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ShareActionProvider;

public class ParentMainActivity extends Activity {
	
//	Variables
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    private String[] mDrawerItems;
    private static final int PEEK_DRAWER_TIME_SECONDS = 3;
    private long downTime;
    private long eventTime;
    private float x = 0.0f;
    private float y = 100.0f;
    private int metaState = 0;
    private boolean firstToggle;
    private static final String TAG = "Debug";
    private final String PREFS_NAME = "MyPrefsFile";
    private SharedPreferences settings;
	private ShareActionProvider mShareActionProvider;
	protected Menu optionsMenu;
    
//	Creates the Activity
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);   
        settings = getSharedPreferences(PREFS_NAME, 0);
        contentView();
        initAnalytics();
        drawerOnCreate();
    }
    
//  Handles the loading of the default fragment when activity is resumed
    @Override
    protected void onResume(){
    	super.onResume();
    	 loadFragment();
    }
  
//    Loads the fragment depending on saved settings
    private void loadFragment() {
		if (settings.getBoolean("doneTutorial", true)) { 
			firstToggle = true;
	    	ConfirmDialogFragment confirm = new ConfirmDialogFragment();
			confirm.show(getFragmentManager(), "Dialog");
	    }
		if(settings.getBoolean("firstToggle", true)){
	    	peekDrawer();
	    	firstFragment();	
	    }else{
	    	FragmentManager  fm = getFragmentManager();
	    	if(fm.findFragmentByTag("Profile") != null){	
	    	}else{
	    		firstFragment();
	    	}	 
	    }
	}


//    Sets up the layout
    private void contentView() {
    	  setContentView(R.layout.mainactivity_drawer);
	}

//    Parse Analytics started
	private void initAnalytics() {
    	ParseAnalytics.trackAppOpened(getIntent());
	}
	
//	Creates a Dialog Fragment for first time users
	@SuppressLint("ValidFragment")
   	public class ConfirmDialogFragment extends DialogFragment {
		
		
   		@Override
   	    public Dialog onCreateDialog(Bundle savedInstanceState) {
   			
   	        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
   	        builder.setMessage(R.string.dialog_start_tutorial)        
   	               .setTitle(R.string.dialog_tutorial_title)
   	               .setCancelable(false)
   	               .setPositiveButton(R.string.yes_button, new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int id) {
						
						getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
						 settings.edit().putBoolean("doneTutorial", false).commit(); 
						 helpFragment();
   	                	 dialog.dismiss();
   	                   }
   	               })
   	               .setNegativeButton(R.string.no_button, new DialogInterface.OnClickListener() {
   	                   public void onClick(DialogInterface dialog, int id) {
   						getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
   	            		 settings.edit().putBoolean("doneTutorial", false).commit(); 
   	            		 firstFragment();
   	                	 peekDrawer();
   	                	 dialog.dismiss();
   	                   }
   	               });
   	     
   	        return builder.create();
   	    }
   	}

//	Gets HelpFragment
	private void helpFragment() {
    	Fragment fragment = new HelpFragment();
 		FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment, "Help").commit();
	}

//	Gets default Appstart Fragment
	private void firstFragment() {
    	Fragment fragment = new WelcomeFragment();
 		FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment, "Welcome").commit();
	}

//	Lets the Drawer show for a few seconds to notify the user it's there
	protected void peekDrawer() {
		 mDrawerLayout.setClickable(false);
        downTime = SystemClock.uptimeMillis()+100;
        eventTime = SystemClock.uptimeMillis() + 200;
        MotionEvent motionEvent = MotionEvent.obtain(downTime, eventTime, MotionEvent.ACTION_DOWN, x, y, metaState);
        mDrawerLayout.dispatchTouchEvent(motionEvent);
        motionEvent.recycle();
     
        new Handler().postDelayed(new Runnable() {
          @Override
          public void run() {
	            downTime = SystemClock.uptimeMillis();
	            eventTime = SystemClock.uptimeMillis() + 200;
	            MotionEvent motionEvent = MotionEvent.obtain(downTime, eventTime, MotionEvent.ACTION_UP, x, y, metaState);
		        mDrawerLayout.dispatchTouchEvent(motionEvent);
		        motionEvent.recycle();
          	}
        }, (long) (PEEK_DRAWER_TIME_SECONDS * DateUtils.SECOND_IN_MILLIS));
        mDrawerLayout.setClickable(true);
      }
	
//	Setup for the Navigation Drawer
    private void drawerOnCreate() {
    	 mTitle = mDrawerTitle = getTitle();
         mDrawerItems = getResources().getStringArray(R.array.drawer_items);
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
            	 if(firstToggle){
            		 settings.edit().putBoolean("firstToggle", false).commit(); // detects that the user toggled and disables peekdrawer on the next app start
            		 Log.d(TAG, "First toggle");
            	 }
                 getActionBar().setTitle(mDrawerTitle);
                 invalidateOptionsMenu(); 
             }
         };
         mDrawerLayout.setDrawerListener(mDrawerToggle);
}

//    Setup for the option menu, 
//    Basic setup for share-intent and refresh, disabled by default
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	this.optionsMenu = menu;
    	MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        
        MenuItem refresh = menu.findItem(R.id.refresh);
		refresh.setVisible(false);

        MenuItem share = menu.findItem(R.id.menu_item_share);
		share.setVisible(false);
		share.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
		share.setIcon(R.drawable.ic_action_share);
		share.setVisible(false);
		optionsMenu.removeItem(R.id.menu_item_share);
        return super.onCreateOptionsMenu(menu);
    }

//    Dummy shareintent
	protected Intent createShareIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "");
        shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, "");
        return shareIntent;
    }

//	Sets the share intent
	public void setShareIntent(Intent shareIntent) {
        if (mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(shareIntent);
        }
    }
	
    /* Called whenever we call invalidateOptionsMenu() */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
    	return true; 
    }
    
//   Sends selected Item to the Navigation drawer, toggles the state
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	if (mDrawerToggle.onOptionsItemSelected(item)) {
    		return true;
    	}	switch(item.getItemId()){
    	case R.id.help:
    		helpFragment();
    		break;
    	}
          return super.onOptionsItemSelected(item);
    }

//    ClickListener for the Listitems in the Navigationdrawer
    public class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    }

//  Starts the fragments depending on the clicked position
    private void selectItem(int position) {
        Log.v(TAG, "menu selected=" + position);
        Fragment fragment;
    	android.app.FragmentTransaction transaction;
        switch (position) {
		case 0:
			fragment = new WelcomeFragment();
			transaction = getFragmentManager().beginTransaction();
	        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
	        transaction.replace(R.id.content_frame, fragment);
	        transaction.addToBackStack("Welcome");
	        transaction.commit();   
			break;
		case 1:
			fragment = new TeacherListFragment();
			transaction = getFragmentManager().beginTransaction();
	        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
	        transaction.replace(R.id.content_frame, fragment);
	        transaction.addToBackStack("Teacher");
	        transaction.commit();
			break;
		case 2:
			fragment = new ProfileFragment();
			transaction = getFragmentManager().beginTransaction();
	        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
	        transaction.replace(R.id.content_frame, fragment, "Profile");
	        transaction.addToBackStack("Profile");
	        transaction.commit();
			break;
		case 3:
			fragment = new HelpFragment();
			transaction = getFragmentManager().beginTransaction();
	        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
	        transaction.replace(R.id.content_frame, fragment);
	        transaction.addToBackStack("Help");
	        transaction.commit();
			break;
		case 4:
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

//    Sets the title in the Navigationbar depending on the selected Fragment
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
// 	Pass any configuration change to the drawer toggls
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }
   
}