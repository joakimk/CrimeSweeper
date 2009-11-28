package com.markupartist.crimesweeper;
import com.google.android.maps.GeoPoint;
import java.lang.Math;
import android.util.Log;

import javax.xml.parsers.DocumentBuilder;

public class CrimeSite {
	
	double collisionDistance = 2.3848992733381165;
	private static final String TAG = "Collision: ";
	
	String getTitle()
	{
		return _title;
	}
	
	int getLat()
	{
		return _lat;
	}
	
	int getLng()
	{
		return _lng;
	}
	
	boolean intersectWithPlayer(GeoPoint player)
	{
		Log.v(TAG, "distance=" + Double.toString(distanceBetweenCoords(_lat, _lng, player.getLatitudeE6(), player.getLongitudeE6())));
		return distanceBetweenCoords(_lat, _lng, player.getLatitudeE6(), player.getLongitudeE6()) <= collisionDistance;
	}

    static void GetCrimeSites(int minutes) {
          //new DocumentBuilder()
          //  
    }
	
	CrimeSite(String title, int lat, int lng) {
		_title = title;
		_lat = lat;
		_lng = lng;
	}
	
private
	
	String _title;
	int _lat;
	int _lng;
	
	/*
	 * The great circle distance d between two points with coordinates {lat1,lon1} and {lat2,lon2} is given by:
	 * d=acos(sin(lat1)*sin(lat2)+cos(lat1)*cos(lat2)*cos(lon1-lon2))
	 */
	
	double distanceBetweenCoords(int lat1, int lon1, int lat2, int lon2) {
		return Math.acos( 
			Math.sin(lat1) * Math.sin(lat2) + Math.cos(lat1) * Math.cos(lat2) * Math.cos(lon1 - lon2)
		);
	}
	
}
