package com.markupartist.crimesweeper;

import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.MapView;
import com.google.android.maps.GeoPoint;
import android.content.Context;
import android.location.Location;

import java.util.List;

public class PlayerLocationOverlay extends MyLocationOverlay {
    private CrimeLocationHitListener listener;
    private List<CrimeSite> _crimeSites = null;

    public PlayerLocationOverlay(Context context, MapView mapView) {
        super(context, mapView);
        _crimeSites = CrimeSite.getCrimeSites(24 * 60);
    }

    public void setCrimeLocationHitListener(CrimeLocationHitListener listener) {
        this.listener = listener;
    }

    @Override
    public void onLocationChanged(Location location) {
        super.onLocationChanged(location);

        for(CrimeSite crimeSite : _crimeSites) {
            if(crimeSite.intersectWithPlayer(location)) {
                listener.onCrimeLocationHit(crimeSite);
            }
        }
    }
}
