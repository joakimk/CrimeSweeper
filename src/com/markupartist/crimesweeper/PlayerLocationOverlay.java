package com.markupartist.crimesweeper;

import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.MapView;
import android.content.Context;
import android.location.Location;

import java.util.List;

public class PlayerLocationOverlay extends MyLocationOverlay {
    private CrimeLocationHitListener listener;
    private List<CrimeSite> mCrimeSites = null;

    public PlayerLocationOverlay(Context context, MapView mapView) {
        super(context, mapView);
        mCrimeSites = CrimeSite.getCrimeSites(24 * 60);
    }

    public void setCrimeLocationHitListener(CrimeLocationHitListener listener) {
        this.listener = listener;
    }

    @Override
    public void onLocationChanged(Location location) {
        super.onLocationChanged(location);

        for(CrimeSite crimeSite : mCrimeSites) {
            if(crimeSite.intersectWithPlayer(location)) {
                listener.onCrimeLocationHit(crimeSite);
            }
        }
    }
}
