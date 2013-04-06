package com.euapps.test;

import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.euapps.googleplacesapi.PlacesReceiver;
import com.euapps.googleplacesapi.PlacesRestaurant;
import com.euapps.googleplacesapi.PlacesTask;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

@SuppressLint("NewApi")
public class RestaurantMapActivity extends Activity implements PlacesReceiver {

	private GoogleMap map;

	private List<PlacesRestaurant> restaurants;

	private String previousRequestUrl;
	private String pageToken;

	private static RestaurantMapActivity instance;

	public static RestaurantMapActivity getInstance() {
		return instance;
	}

	public List<PlacesRestaurant> getRestaurants() {
		return restaurants;
	}

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		instance = this;
		setContentView(R.layout.activity_map);
		map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
				.getMap();
		map.setMyLocationEnabled(true);

		refresh();
	}

	private void refresh() {
		Location location = getCurrentLocation();

		if (location != null) {
			PlacesTask.executeFor(this, location.getLatitude(),
					location.getLongitude(), 1);
			map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(
					location.getLatitude(), location.getLongitude()), 15));
		} else {
			Log.d("ShowMapActivity", "Cannot get location");
		}
	}

	public void flyToRestaurant(PlacesRestaurant restaurant) {
		map.moveCamera(CameraUpdateFactory.newLatLngZoom(
				restaurant.getPosition(), 17));
	}

	private Location getCurrentLocation() {
		Location location = map.getMyLocation();
		if (location == null) { // It seems that the above usually returns null
			LocationManager service = (LocationManager) getSystemService(LOCATION_SERVICE);
			Criteria criteria = new Criteria();
			String provider = service.getBestProvider(criteria, false);
			location = service.getLastKnownLocation(provider);
		}
		return location;
	}

	@Override
	public void handleNextPage(String previousRequestUrl, String pageToken) {
		this.pageToken = pageToken;
		this.previousRequestUrl = previousRequestUrl;
	}

	@Override
	public void handleResults(List<PlacesRestaurant> restaurants) {
		if (restaurants != null && restaurants.size() > 0) {
			map.clear();
			for (PlacesRestaurant restaurant : restaurants) {
				Log.d("ShowMapActivity", restaurant.toString());
				map.addMarker(new MarkerOptions()
						.position(restaurant.getPosition())
						.title(restaurant.getName())
						.snippet(restaurant.getVicinity()));
			}
			this.restaurants = restaurants;
		}
		// Leave the previous results if not
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		Log.d("ShowMapActivity", "onCreateOptionsMenu");
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		Log.d("ShowMapActivity", "onPrepareOptionsMenu");
		if (pageToken == null)
			menu.removeItem(R.id.action_more);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_list:
			final Intent intent = new Intent().setClass(this,
					RestaurantListActivity.class);
			startActivity(intent);
			return true;
		case R.id.action_more:
			if (pageToken != null)
				PlacesTask.executeFor(this, previousRequestUrl, pageToken);
			return true;
		case R.id.action_refresh:
			refresh();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

}
