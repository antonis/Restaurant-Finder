package com.euapps.googleplacesapi;

import com.google.android.gms.maps.model.LatLng;

public class PlacesRestaurant {

	private String name;
	private String vicinity;
	private LatLng position;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getVicinity() {
		return vicinity;
	}

	public void setVicinity(String vicinity) {
		this.vicinity = vicinity;
	}

	public LatLng getPosition() {
		return position;
	}

	public void setPosition(double lat, double lon) {
		this.position = new LatLng(lat, lon);
	}

	@Override
	public String toString() {
		return "PlacesRestaurant.name=" + name;
	}

}
