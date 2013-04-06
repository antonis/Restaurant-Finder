package com.euapps.googleplacesapi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;


import android.os.AsyncTask;
import android.util.Log;

public class PlacesTask extends AsyncTask<Void, Void, List<PlacesRestaurant>> {

	private final String API_URL = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=%.6f,%.6f&radius=%d&types=restaurant&sensor=false&key=%s";
	private final String API_KEY = "Your Google Places Api Key Goes Here";

	private PlacesReceiver receiver;

	private String nextRequestUrl;
	private String nextPage;

	private double lat;
	private double lon;
	private int radiusInMeters;

	private String previousRequestUrl;
	private String pageToken;

	/**
	 * Triggers a Google Places API nearbysearch request for restaurants
	 * 
	 * @param PlacesReceiver
	 *            receiver
	 * @param lat
	 *            Lat
	 * @param lon
	 *            Lon
	 * @param radiusInMiles
	 *            Radius in miles
	 * @return
	 */
	public static PlacesTask executeFor(PlacesReceiver receiver, double lat,
			double lon, int radiusInMiles) {
		PlacesTask placesTask = new PlacesTask();
		placesTask.receiver = receiver;
		placesTask.lat = lat;
		placesTask.lon = lon;
		placesTask.radiusInMeters = (int) (radiusInMiles * 1609.344);
		placesTask.execute();
		return placesTask;
	}

	/**
	 * Requests the next page. For some reason it does not work without the
	 * previous request url, though the documentation states that everything
	 * else other than page token is ignored
	 * 
	 * @param PlacesReceiver
	 *            receiver
	 * @param previousRequestUrl
	 *            Previous request url
	 * @param pageToken
	 *            Page token
	 * @return
	 */
	public static PlacesTask executeFor(PlacesReceiver receiver,
			String previousRequestUrl, String pageToken) {
		PlacesTask placesTask = new PlacesTask();
		placesTask.receiver = receiver;
		placesTask.previousRequestUrl = previousRequestUrl;
		placesTask.pageToken = pageToken;
		placesTask.execute();
		return placesTask;
	}

	@Override
	protected List<PlacesRestaurant> doInBackground(Void... p) {
		Log.d("PlacesTask", "doInBackground");

		String requestUrl;
		if (pageToken != null) {
			requestUrl = previousRequestUrl + "&pagetoken=" + pageToken;
			nextRequestUrl = previousRequestUrl;
		} else {
			requestUrl = String.format(API_URL, lat, lon, radiusInMeters,
					API_KEY);
			nextRequestUrl = requestUrl;
		}
		Log.d("PlacesTask", "requestUrl=" + requestUrl);

		String data = "";
		InputStream iStream = null;
		HttpURLConnection urlConnection = null;
		try {
			URL url = new URL(requestUrl);
			urlConnection = (HttpURLConnection) url.openConnection();
			urlConnection.connect();
			iStream = urlConnection.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(
					iStream));
			StringBuffer sb = new StringBuffer();
			String line = "";
			while ((line = br.readLine()) != null)
				sb.append(line);
			data = sb.toString();
			br.close();
		} catch (Exception e) {
			Log.e("Exception while downloading url", e.toString());
			e.printStackTrace();
		} finally {
			try {
				iStream.close();
			} catch (IOException e) {
				Log.e("Error closing stream", e.toString());
				e.printStackTrace();
			}
			urlConnection.disconnect();
		}

		// Log.d("PlacesTask", "response=" + data);

		if (data != null)
			try {
				JSONObject jObject = new JSONObject(data);

				PlacesJSONParser parser = new PlacesJSONParser(jObject);

				nextPage = parser.getNextPage();
				return parser.parse();

			} catch (JSONException e) {
				Log.e("Error parsing JSON response", e.toString());
				e.printStackTrace();
			}

		return null;
	}

	protected void onPostExecute(List<PlacesRestaurant> result) {
		super.onPostExecute(result);

		if (result != null) {
			Log.d("PlacesTask", "onPostExecute.result.count=" + result.size());
			receiver.handleResults(result);
			receiver.handleNextPage(nextRequestUrl, nextPage);
		} else {
			Log.d("PlacesTask", "onPostExecute.result.isEmpty");
		}

	}

}
