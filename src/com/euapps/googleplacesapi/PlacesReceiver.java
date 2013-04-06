package com.euapps.googleplacesapi;

import java.util.List;

public interface PlacesReceiver {

	public void handleNextPage(String previousRequestUrl, String pageToken);

	public void handleResults(List<PlacesRestaurant> restaurants);

}
