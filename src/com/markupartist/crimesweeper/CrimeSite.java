package com.markupartist.crimesweeper;
import com.google.android.maps.GeoPoint;
import java.lang.Math;
import java.util.List;
import java.util.ArrayList;

import android.util.Log;
import android.location.Location;

import javax.xml.parsers.DocumentBuilder;

public class CrimeSite extends GeoPoint {
	float collisionDistance = 20.0f;
	private static final String TAG = "Collision: ";
    private String _title;
	
	String getTitle()
	{
		return _title;
	}

	boolean intersectWithPlayer(Location player)
	{
        Location crimeSiteLocation = new Location(player);
        crimeSiteLocation.setLatitude(this.getLatitudeE6() / 1E6);
        crimeSiteLocation.setLongitude(this.getLongitudeE6() / 1E6);

        float distance = player.distanceTo(crimeSiteLocation);
        return(distance <= this.collisionDistance);
	}

    static List<CrimeSite> getCrimeSites(int minutes) {
        List<CrimeSite> crimeSites = new ArrayList<CrimeSite>();
        crimeSites.add(new CrimeSite("Grand Theft Auto", 59414207, 18273497));
        crimeSites.add(new CrimeSite("Murder One", 59514207, 18173497));
        crimeSites.add(new CrimeSite("Close to me", 59279986, 1808275));

        return crimeSites;
    }
	
	CrimeSite(String title, int lat, int lng) {
        super(lat, lng);
		_title = title;
	}

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
