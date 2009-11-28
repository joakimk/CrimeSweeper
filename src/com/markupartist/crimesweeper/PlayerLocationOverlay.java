package com.markupartist.crimesweeper;

import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;
import android.content.Context;
import android.location.Location;
import android.os.AsyncTask;

import java.util.List;

public class PlayerLocationOverlay extends MyLocationOverlay {
    private CrimeLocationHitListener listener;
    private List<CrimeSite> mCrimeSites = null;

    public PlayerLocationOverlay(Context context, MapView mapView) {
        super(context, mapView);

        class PopulateCrimeSites extends AsyncTask<Void, Void, List<CrimeSite>> {
            @Override
            protected List<CrimeSite> doInBackground(Void... voids) {
                return CrimeSite.getCrimeSites(StartActivity.CRIME_SITE_TIME_INTERVAL);
            }

            @Override
            protected void onPostExecute(List<CrimeSite> crimeSites) {
                mCrimeSites = crimeSites;
            }
        }

        PopulateCrimeSites populateCrimeSites = new PopulateCrimeSites();
        populateCrimeSites.execute();
    }

    public void setCrimeLocationHitListener(CrimeLocationHitListener listener) {
        this.listener = listener;
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
