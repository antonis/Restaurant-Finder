package com.euapps.test;

import java.util.List;

import com.euapps.googleplacesapi.PlacesRestaurant;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class RestaurantAdapter extends ArrayAdapter<PlacesRestaurant> {

	private List<PlacesRestaurant> restaurants;
	private int resourceId;

	public RestaurantAdapter(Context context, int textViewResourceId,
			List<PlacesRestaurant> objects) {
		super(context, textViewResourceId, objects);
		this.resourceId = textViewResourceId;
		this.restaurants = objects;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		View v = convertView;
		if (v == null) {
			LayoutInflater vi = (LayoutInflater) getContext().getSystemService(
					Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(resourceId, null);
		}
		
		PlacesRestaurant restaurant = restaurants.get(position);
		if (restaurant != null) {
			TextView name = (TextView) v.findViewById(android.R.id.text1);
			TextView vicinity = (TextView) v.findViewById(android.R.id.text2);
			name.setText(restaurant.getName());
			vicinity.setText(restaurant.getVicinity());
		}
		return v;
	}
}