package com.inkubatorit.appssakti;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.inkubatorit.custom.MainMenuListener;
import com.inkubatorit.custom.OnCardClickListener;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;


public class MainActivity extends AppCompatActivity
		implements NavigationDrawerFragment.NavigationDrawerCallbacks, OnCardClickListener, MainMenuListener {

	public static final int MENU_LIST = 0;
	public static final int INFORMATION = 1;
	public static final int PAGES = 2;
	public static JSONObject searchObj;
	public static int searchType;

	/**
	 * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
	 */
	private NavigationDrawerFragment mNavigationDrawerFragment;
	private ActionBarDrawerToggle drawerToggle;

	/**
	 * Used to store the last screen title. For use in {@link #restoreActionBar()}.
	 */

	private List<String> mainMenuItem;
	private List<Integer> mainMenuIcon;

	// Data
	private JSONArray jsonArray;
	private JSONObject aboutIntegrasi;
	private JSONObject aboutApp;
	private JSONObject kemahasiswaan;
	private JSONArray lembagaPusat;
	private JSONObject himpunan;
	private JSONObject unit;
	private JSONObject kantin;
	private JSONObject ruangan;

	private static final int STATE_SHOW_BURGER = 1;
	private static final int STATE_HIDE_BURGER = 0;
	private int state;

	//Social Media Intent
	private String facebook;
	private String twitter;
	private String line;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mNavigationDrawerFragment = (NavigationDrawerFragment)
				getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);

		new JSONTask().execute();

		// Set up the drawer.
		mNavigationDrawerFragment.setUp(R.id.navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout));
		drawerToggle = mNavigationDrawerFragment.getDrawerToggle();

		state = STATE_SHOW_BURGER;
	}

	@Override
	public void onNavigationDrawerItemSelected(int position) {

		// update the main content by replacing fragments
		state = STATE_SHOW_BURGER;
		FragmentManager fragmentManager = getSupportFragmentManager();
		switch(position){
			//Home
			case 0 :
				fragmentManager.beginTransaction()
					.replace(R.id.container, MainMenuFragment.newInstance(this, mainMenuItem,
							mainMenuIcon))
					.commit();
				 break;
			//Integrasi ITB
			case 1 :
				fragmentManager.beginTransaction()
					.replace(R.id.container, SlideViewFragment.newInstance(aboutIntegrasi), "integrasi")
					.commit();
				 break;
			//About
			case 2:
				fragmentManager.beginTransaction()
					.replace(R.id.container, SlideViewFragment.newInstance(aboutApp), "app")
					.commit();
				 break;

		}
	}

	public void restoreActionBar() {
		try {
			getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		}
		catch (Exception ignored) {}
	}

	@Override
	protected void onResume() {
		super.onResume();
		FragmentManager fragmentManager = getSupportFragmentManager();

		if(searchObj != null){
			if(searchType == INFORMATION){
				fragmentManager.beginTransaction()
						.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
						.replace(R.id.container, LembagaInformationFragment.newInstance(searchObj))
						.addToBackStack(null)
						.commit();
			}
			else if(searchType == MENU_LIST){
				fragmentManager.beginTransaction()
						.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
						.replace(R.id.container, ListIconFragment.newInstance(R.layout
								.text_icon_item, searchObj))
						.addToBackStack(null)
						.commit();
			}
			else if(searchType == PAGES){
				fragmentManager.beginTransaction()
						.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
						.replace(R.id.container, SlideViewFragment.newInstance(searchObj))
						.addToBackStack(null)
						.commit();
			}

			searchObj = null;
			searchType = -1;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (!mNavigationDrawerFragment.isDrawerOpen()) {
			// Only show items in the action bar relevant to this screen
			// if the drawer is not showing. Otherwise, let the drawer
			// decide what to show in the action bar.
			getMenuInflater().inflate(R.menu.global, menu);
			restoreActionBar();
			return true;
		}
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks
		state = STATE_HIDE_BURGER;

		if(drawerToggle.onOptionsItemSelected(item)){
			FragmentManager manager = getSupportFragmentManager();
			if(manager.getBackStackEntryCount() > 0){
				mNavigationDrawerFragment.closeDrawer();
				manager.popBackStackImmediate();
				if(manager.getBackStackEntryCount() == 0){
					drawerToggle.syncState();
					state = STATE_SHOW_BURGER;
				}
			}
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onMenuClicked(int code) {
		state = STATE_HIDE_BURGER;
		try {
			switch (code) {
				case 0:
					onCardClicked(MENU_LIST, kemahasiswaan);
					break;
				case 1:
					onCardClicked(INFORMATION, lembagaPusat.getJSONObject(0)); // Kabinet
					break;
				case 2:
					onCardClicked(MENU_LIST, himpunan);
					break;
				case 3:
					onCardClicked(MENU_LIST, unit);
					break;
				case 4:
					onCardClicked(INFORMATION, lembagaPusat.getJSONObject(1)); // Kongres
					break;
				case 5:
					onCardClicked(INFORMATION, lembagaPusat.getJSONObject(2)); // MWA-WM
					break;
				case 6:
					onCardClicked(MENU_LIST, kantin);
					break;
				case 7:
					onCardClicked(MENU_LIST, ruangan);
					break;

			}
		}
		catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onCardClicked(int type, JSONObject obj) {
		state = STATE_HIDE_BURGER;
		FragmentManager fragmentManager = getSupportFragmentManager();

		switch(type){
			//Memunculkan Menu lain atau card lain
			case MENU_LIST:
				fragmentManager.beginTransaction()
						.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
						.replace(R.id.container, ListIconFragment
								.newInstance(R.layout.text_icon_item, obj))
						.addToBackStack(null)
						.commit();
				break;

			//Menunculkan informasi (ada tulisan di header)
			case INFORMATION:
				fragmentManager.beginTransaction()
						.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
						.replace(R.id.container, LembagaInformationFragment.newInstance(obj))
						.addToBackStack(null)
						.commit();
				setSocmedIntent(obj);
				break;

			//Munculkan informasi dalam pages
			case PAGES:
				fragmentManager.beginTransaction()
						.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
						.replace(R.id.container, SlideViewFragment.newInstance(obj))
						.addToBackStack(null)
						.commit();
				break;
		}

	}

	@Override
	public void onBackPressed() {
		FragmentManager manager = getSupportFragmentManager();
		if(manager.getBackStackEntryCount() > 0){
			mNavigationDrawerFragment.closeDrawer();
			manager.popBackStackImmediate();
			if(manager.getBackStackEntryCount() == 0){
				drawerToggle.syncState();
				state = STATE_SHOW_BURGER;
			}
		}else{
			super.onBackPressed();
		}

	}

	public boolean isBurgerShow(){
		return state == STATE_SHOW_BURGER;
	}


	//Social Media information for intent
	public void setSocmedIntent(JSONObject json) {

		try {
			if (json.has("facebook") && !json.getString("facebook").equals("-")) {
                facebook = json.getString("facebook");
            }
			if (json.has("twitter") && !json.getString("twitter").equals("-")) {
				twitter = json.getString("twitter");
			}
			if (json.has("line") && !json.getString("line").equals("-")) {
				line = json.getString("line");
			}
		}
		catch (JSONException ignored) {}
	}

	//onClick callback
	public  void facebookIntent(View view) {

		String url = "https://www.facebook.com/" + facebook;
		Context ctx = getApplicationContext();
		PackageManager pm = ctx.getPackageManager();

		try {
			int versionCode = pm.getPackageInfo("com.facebook.katana", 0).versionCode;

			if (versionCode >= 3002850) { //newer versions of fb app
				Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("fb://facewebmodal/f?href=" + url));
				startActivity(intent);
			}
			else { //older versions of fb app
				Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("fb://fb://page/" + facebook));
				startActivity(intent);
			}
		}
		catch (PackageManager.NameNotFoundException e) {
			Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
			startActivity(intent);
		}
	}

	public void twitterIntent(View view) {

		try {
			//try to open page in twitter native app.
			String screen_name = twitter.replace("@", "");
			String uri = "twitter://user?screen_name=" + screen_name;    //Cutsom URL
			Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
			startActivity(intent);
		}
		catch (ActivityNotFoundException ex){
			//twitter native app isn't available, use browser.
			String uri = "https://www.twitter.com/" + twitter;  //Normal URL
			Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
			startActivity(i);
		}
	}

	public void lineIntent(View view) {

		try {
			//try to open page in line native app.
			String uri = "line://ti/p/" + line;    //Cutsom URL
			Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
			startActivity(intent);
		}
		catch (ActivityNotFoundException ex){
			//twitter native app isn't available, show message
			String to_toast = "Anda tidak memiliki aplikasi LINE";
			Toast toast = Toast.makeText(getApplicationContext(), to_toast, Toast.LENGTH_SHORT);
			toast.show();
		}

	}

	private class JSONTask extends AsyncTask<Void, Void, Void> {
		@Override
		protected Void doInBackground(Void... params) {

			try {
				jsonArray = loadJSON(R.raw.data).getJSONArray("DATA");
				aboutIntegrasi = jsonArray.getJSONObject(0);
				aboutApp = jsonArray.getJSONObject(1);
				kemahasiswaan = jsonArray.getJSONObject(2);
				lembagaPusat = jsonArray.getJSONObject(3).getJSONArray("isi");
				himpunan = jsonArray.getJSONObject(4);
				unit = jsonArray.getJSONObject(5);
				kantin = jsonArray.getJSONObject(6);
				ruangan = jsonArray.getJSONObject(7);
			} catch (JSONException | IOException e) {
				e.printStackTrace();
			}

			return null;
		}
	}

	private JSONObject loadJSON(int resId) throws IOException, JSONException {
		String line;
		StringBuilder result = new StringBuilder();

		BufferedReader reader = new BufferedReader(new InputStreamReader(getResources()
				.openRawResource(resId)));
		while((line = reader.readLine()) != null){
			result.append(line);
		}
		return new JSONObject(result.toString());
	}

	public static void loadImage(Context context, ImageView dest, String uri) {

		String baseURL = "https://api.backendless.com/A73CAAF6-16BC-99FD-FFDB-36CE5C026900/v1/files/media/";

		Glide.with(context)
				.load(baseURL + uri + ".png")
				.diskCacheStrategy(DiskCacheStrategy.ALL)
				.into(dest);

	}

}
