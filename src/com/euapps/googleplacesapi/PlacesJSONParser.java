package com.euapps.googleplacesapi;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import android.util.Log;

public class PlacesJSONParser {

	private JSONObject jObject;

	public PlacesJSONParser(JSONObject jObject) {
		this.jObject = jObject;
	}

	public String getNextPage() {
		try {
			if (jObject.has("next_page_token"))
				return jObject.getString("next_page_token");
			else
				Log.d("PlacesJSONParser", "This is the last page");
		} catch (JSONException e) {
			Log.e("PlacesJSONParser", "Error parsing next page");
		}
		return null;
	}

	public List<PlacesRestaurant> parse() {
		List<PlacesRestaurant> r = new ArrayList<PlacesRestaurant>();
		JSONArray jPlaces = null;
		try {
			jPlaces = jObject.getJSONArray("results");
			int placesCount = jPlaces.length();
			for (int i = 0; i < placesCount; i++) {
				JSONObject p = (JSONObject) jPlaces.get(i);
				PlacesRestaurant place = new PlacesRestaurant();
				try {
					if (p.has("name"))
						place.setName(p.getString("name"));
					if (p.has("vicinity"))
						place.setName(p.getString("vicinity"));
					if (p.has("geometry")) {
						JSONObject g = p.getJSONObject("geometry");
						if (g.has("location")) {
							JSONObject l = g.getJSONObject("location");
							if (l.has("lat") && l.has("lng"))
								place.setPosition(l.getDouble("lat"),
										l.getDouble("lng"));
						}
					}
					r.add(place);
				} catch (JSONException e) {
					Log.e("PlacesJSONParser", "Error parsing place");
					e.printStackTrace();
				}
			}
		} catch (JSONException e) {
			Log.e("PlacesJSONParser", "Error parsing results");
			e.printStackTrace();
		}
		return r;
	}

}