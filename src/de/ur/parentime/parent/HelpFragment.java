package de.ur.parentime.parent;

/*
 * This Class shows the user a Viewpager with tutorial Images, explaining
 * how to setup an appointment 
 */
import de.ur.korbinian.kasberger.R;
import de.ur.parentime.custom.DynamicDataResponseHandler;
import de.ur.parentime.main.Constants;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.SimpleOnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class HelpFragment extends Fragment implements DynamicDataResponseHandler {
	
//	Variables
	private static TextView pagenumber;
	private ViewPager viewPager;
	private View v;
	private Handler handler;
	private Runnable runnable;
	protected SharedPreferences settings;
	private int[] mImages = new int[] {
	    	R.drawable.page0,
	    	R.drawable.page1,
	    	R.drawable.page2,
	    	R.drawable.page3,
	    	R.drawable.page3_5,
	    	R.drawable.page4,
//	    	R.drawable.page5,
	    	R.drawable.page6,
	    	R.drawable.page7,
	    	R.drawable.page8,
	    	R.drawable.page9,
	    	R.drawable.page10,
//	    	R.drawable.page11,
	    	R.drawable.page12
	    };
	private PageListener pageListener;
	
//	Enables actiobar items when Fragment is created
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }
	
//	Setup
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
       v = inflater.inflate(R.layout.help_layout, container,false);
       viewPager = (ViewPager) v.findViewById(R.id.pager);
       ImagePagerAdapter adapter = new ImagePagerAdapter();
       viewPager.setAdapter(adapter); 
       pageListener = new PageListener();
       viewPager.setOnPageChangeListener(pageListener); 
       pagenumber = (TextView) v.findViewById(R.id.pagenumber);
       pagenumber.setText(getResources().getString(R.string.page)
    		   +Constants.SPACE
    		   +Integer.toString(1));
        return v;
    }
	
//	Gets the menu xml and hides all items in it by default
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.main_menu, menu);
        for (int i = 0; i < menu.size(); i++){
            menu.getItem(i).setVisible(false);
        }
       
	}

//	Enables only the items needed in this Fragment
	@Override
    public void onPrepareOptionsMenu(Menu menu) {
		MenuItem share = menu.findItem(R.id.menu_item_share);
		share.setVisible(false);
		MenuItem refresh = menu.findItem(R.id.refresh);
		refresh.setVisible(false);
		menu.findItem(R.id.help)
		.setVisible(true)
		.setIcon(R.drawable.ic_action_navigation_cancel);

    }
	
//	Actionbar Item Clicklistener
	 public boolean onOptionsItemSelected(MenuItem item) {
		 switch(item.getItemId()){
		 case R.id.help:
			goBack();
			 break;
		 }
		return true;	 
	 }
	
//	When exiting the Help Fragment, the User gets send to the Welcome Fragment
	 private void goBack() {
		 Log.d("Debug","back");
		 switchToHome();
	}

//	 required
	@Override
	public void onUpdate(int position) {
		// nothing to do
	}
	
//	Loads the Welcome Fragment after a few seconds
	private void switchToHome() {
 		Fragment fragment = new WelcomeFragment();
    	FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment, "Home").commit();
	}

//	Shows the Actionbar, when User leaves Fragment
	@Override
	public void onPause(){
		super.onPause();
		if(runnable != null){
			handler.removeCallbacks(runnable);
		}
	}

//	PageAdapter class for the Images
	private class ImagePagerAdapter extends PagerAdapter {
	
//		gets the number of items in the image array
	    @Override
	    public int getCount() {
	      return mImages.length;
	    }

	    @Override
	    public boolean isViewFromObject(View view, Object object) {
	      return view == ((ImageView) object);
	    }
	    
//	    Displays the image depending on the current position in the pager, Clicklistener for the last page
	    @Override
	    public Object instantiateItem(ViewGroup container, final int position) {
	    	
	      Context context = getActivity();
	      ImageView imageView = new ImageView(context);
	      imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
	      imageView.setImageResource(mImages[position]);
	      ((ViewPager) container).addView(imageView);
	      if(position == mImages.length){
	    	  imageView.setOnLongClickListener(new OnLongClickListener() {
				
				@Override
				public boolean onLongClick(View v) {
					 Log.i("Debug", "clicked " + position +mImages.length);
	  				 switchToHome();
					return true;
	  				 }
	    	  }
			);
	      }
	      return imageView;
	    }

//	    Destroys the view
		@Override
	    public void destroyItem(ViewGroup container, int position, Object object) {
	      ((ViewPager) container).removeView((ImageView) object);
	    }
	  }

//	PageListener Class to display the current page number
	private static class PageListener extends SimpleOnPageChangeListener{
        public void onPageSelected(int position) {
            Log.i("Debug", "page selected " + position);
               pagenumber.setText(
            		  "Seite"
            		   +Constants.SPACE
            		   +Integer.toString(position+1));
               
    }
}
	
}
