package com.euapps.test;

import java.util.List;

import com.euapps.googleplacesapi.PlacesRestaurant;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

public class RestaurantListActivity extends ListActivity {

	private List<PlacesRestaurant> restaurants;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list);

		restaurants = RestaurantMapActivity.getInstance().getRestaurants();
		if (restaurants != null && restaurants.size() > 0) {
			RestaurantAdapter adapter = new RestaurantAdapter(this,
					android.R.layout.two_line_list_item, restaurants);
			setListAdapter(adapter);
		}

	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		PlacesRestaurant restaurant = restaurants.get(position);
		RestaurantMapActivity.getInstance().flyToRestaurant(restaurant);
		finish();
	}

}
