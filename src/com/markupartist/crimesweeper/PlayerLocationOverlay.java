package com.markupartist.crimesweeper;

import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.MapView;
import com.google.android.maps.GeoPoint;
import android.content.Context;
import android.location.Location;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: joakimb
 * Date: Nov 28, 2009
 * Time: 3:21:52 PM
 * To change this template use File | Settings | File Templates.
 */
public class PlayerLocationOverlay extends MyLocationOverlay {
    private CrimeLocationHitListener listener;

    public PlayerLocationOverlay(Context context, MapView mapView) {
        super(context, mapView);
    }

    public void setCrimeLocationHitListener(CrimeLocationHitListener listener) {
        this.listener = listener;
    }

    @Override
    public void onLocationChanged(Location location) {
        super.onLocationChanged(location);

        List<CrimeSite> crimeSites = CrimeSite.getCrimeSites(60);
        for(CrimeSite crimeSite : crimeSites) {
            if(crimeSite.intersectWithPlayer(location)) {
                listener.onCrimeLocationHit(crimeSite);
            }
        }
    }
}
