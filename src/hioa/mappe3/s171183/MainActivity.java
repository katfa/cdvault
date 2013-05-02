package hioa.mappe3.s171183;

import java.util.concurrent.ExecutionException;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

@SuppressLint("NewApi")
public class MainActivity extends FragmentActivity implements
		ActionBar.TabListener {
	private ViewPager viewPager;
	private SectionsPagerAdapter pagerAdapter;
	private int currentPageNumber;

	private SearchAndScanFragment searchAndScanFragment;
	private AlbumListFragment albumListFragment;
	private ArtistFragment artistFragment;
	private ConcertByCityFragment concertByCityFragment;
	
	private FragmentManager fManager;
	private String[] titles;
	private SharedPreferences sharedPrefs;
	private String currentCity, currentCityId;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		fManager = this.getSupportFragmentManager();
		sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		currentCity = sharedPrefs.getString("city", "");
		currentCityId = sharedPrefs.getString("metroId", "");
		
		titles = new String[] {"Search & Scan", "Artists", "Albums", "Concerts in " + currentCity};

		searchAndScanFragment = new SearchAndScanFragment();
		artistFragment = new ArtistFragment();
		albumListFragment = new AlbumListFragment();
		concertByCityFragment = new ConcertByCityFragment();

		searchAndScanFragment.setArtistFragment(artistFragment);
		artistFragment.setAlbumListFragment(albumListFragment);

		pagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

		final ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		viewPager = (ViewPager) findViewById(R.id.pager);
		viewPager.setAdapter(pagerAdapter);

		viewPager
				.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
					@Override
					public void onPageSelected(int position) {
						currentPageNumber = position;
						invalidateOptionsMenu();
						actionBar.setSelectedNavigationItem(position);
					}
				});

		for (int i = 0; i < pagerAdapter.getCount(); i++) {
			actionBar
					.addTab(actionBar.newTab()
							.setText(pagerAdapter.getPageTitle(i))
							.setTabListener(this));
		}

	}
	
	@Override
	protected void onResume() {
		super.onResume();
		currentCityId = sharedPrefs.getString("metroId", "");
		currentCity = sharedPrefs.getString("city", "");
		titles[3] =  "Concerts in " + currentCity;
		updateConcertTab(titles[3]);
	}
	
	private void updateConcertTab(String newTitle){
		pagerAdapter.updateConcertTab(newTitle);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		if (currentPageNumber == 0)
			menu.findItem(R.id.refresh).setVisible(false);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.refresh:
			if(currentPageNumber == 1){
				artistFragment.updateList(artistFragment.getDbAdapter()
						.getAllArtists());
				artistFragment.getArtistAdapter().notifyDataSetChanged();
				Toast.makeText(this, "Artist List updated", Toast.LENGTH_SHORT).show();
				return false;
			}
			if(currentPageNumber == 2) {
				albumListFragment.updateList(albumListFragment.getDbAdapter().getAllAlbums());
				albumListFragment.getAlbumListAdapter().notifyDataSetChanged();
				Toast.makeText(this, "Album List updated", Toast.LENGTH_SHORT).show();
				return false;
			}
			if(currentPageNumber == 3) {
				try {
					concertByCityFragment.updateCity(currentCityId);
				} catch (InterruptedException e) {
					Log.e("ERROR", "interrupted. " + e.getMessage());
				} catch (ExecutionException e) {
					Log.e("ERROR", "execution error. " + e.getMessage());
				}
				Toast.makeText(this, "Concert List updated", Toast.LENGTH_SHORT).show();
				return false;
			}
			return false;
					
		case R.id.setLocation:
			startActivity(new Intent(this, LocationPicker.class));
			return false;
		
		case R.id.aboutApp:
			AboutDialog aboutDialog = new AboutDialog();
			aboutDialog.show(fManager, "aboutDialog");
			return false;
		}
		return false;
	}

	@Override
	public void onTabReselected(Tab tab, FragmentTransaction arg1) {
		viewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction transaction) {
		viewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(Tab arg0, FragmentTransaction arg1) {
	}

	private class SectionsPagerAdapter extends FragmentPagerAdapter {

		private String[] tabTitles = new String[] {"Search & Scan", "Artists", "Albums", "Concerts in " + currentCity};
		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int i) {
			switch (i) {
			case 0:
				return searchAndScanFragment;
			case 1:
				return artistFragment;
			case 2:
				return albumListFragment;
			case 3:
				return concertByCityFragment;

			default:
				return new Fragment();
			}
		}

		@Override
		public int getCount() {
			return this.tabTitles.length;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return this.tabTitles[position];
		}
		
		public void updateConcertTab(String title){
			tabTitles[3] = title;
			Tab mTab = getActionBar().getTabAt(3); 
			mTab.setText(tabTitles[3]);
		}

	}

}
