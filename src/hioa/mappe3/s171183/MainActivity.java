package hioa.mappe3.s171183;

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
import android.view.Menu;
import android.view.MenuItem;

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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		fManager = this.getSupportFragmentManager();

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
			artistFragment.updateList(artistFragment.getDbAdapter()
					.getAllArtists());
			artistFragment.getArtistAdapter().notifyDataSetChanged();
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

		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		private String[] titles = { "Search or Scan", "Artists", "Albums", "Concerts in " + sharedPrefs.getString("city", "")};

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
			return titles.length;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return titles[position];
		}

	}

}
