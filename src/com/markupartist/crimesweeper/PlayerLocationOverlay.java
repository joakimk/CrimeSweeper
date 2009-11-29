package com.markupartist.crimesweeper;

import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.MapView;
import android.content.Context;
import android.location.Location;

import java.util.List;

public class PlayerLocationOverlay extends MyLocationOverlay {
    private CrimeLocationHitListener listener;
    private List<CrimeSite> mCrimeSites;

    public PlayerLocationOverlay(Context context, MapView mapView) {
        super(context, mapView);
    }

    public void setCrimeLocationHitListener(CrimeLocationHitListener listener) {
        this.listener = listener;
    }

    public void setCrimeSites(List<CrimeSite> crimeSites) {
        this.mCrimeSites = crimeSites;
    }

    @Override
    public void onLocationChanged(Location location) {
        super.onLocationChanged(location);

        if(listener == null || mCrimeSites == null) {
            return;
        }

        for(CrimeSite crimeSite : mCrimeSites) {
            if(crimeSite.intersectWithPlayer(location)) {
                listener.onCrimeLocationHit(crimeSite);
            }
        }
    }
}
