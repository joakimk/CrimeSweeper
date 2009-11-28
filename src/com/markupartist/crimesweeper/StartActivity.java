package com.markupartist.crimesweeper;

import android.os.Bundle;
import android.graphics.drawable.Drawable;
import android.widget.ListView;
import android.widget.ArrayAdapter;
import com.google.android.maps.*;

import java.util.List;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: johan
 * Date: Nov 22, 2009
 * Time: 1:34:04 PM
 * To change this template use File | Settings | File Templates.
 */
public class StartActivity extends MapActivity implements CrimeLocationHitListener {
    private ArrayAdapter<String> mLogAdapter;
    private MapView mapView;
    private PlayerLocationOverlay playerLocationOverlay;
    private MapController mapController;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        // Setup log view
        ListView crimeLogView = (ListView) findViewById(R.id.crime_log);
        ArrayList<String> crimeLogList = new ArrayList<String>();
        mLogAdapter = new ArrayAdapter<String>(this, R.layout.crime_log_row, crimeLogList);
        crimeLogView.setAdapter(mLogAdapter);

        List<CrimeSite> crimeSites = CrimeSite.GetCrimeSites(1400);
        
        mapView = (MapView) findViewById(R.id.mapview);
        mapController = mapView.getController();

        initMap();

        mapView.setClickable(true);
        mapView.setEnabled(true);
        mapView.setStreetView(true);

        List<Overlay> mapOverlays = mapView.getOverlays();
        Drawable drawable = this.getResources().getDrawable(android.R.drawable.picture_frame);
        HelloItemizedOverlay itemizedOverlay;

        itemizedOverlay = new HelloItemizedOverlay(drawable);

        GeoPoint sthlmCenterPoint = new GeoPoint(59314207, 18073497); 
        OverlayItem sthlmOverlayitem = new OverlayItem(sthlmCenterPoint, "aa", "bb");
        itemizedOverlay.addOverlay(sthlmOverlayitem);
        mapOverlays.add(itemizedOverlay);

        mapController.setZoom(15);
    }

    @Override
    protected void onResume() {
        super.onResume();

        playerLocationOverlay.enableCompass();
        playerLocationOverlay.enableMyLocation();
    }

    @Override
    protected void onPause() {
        super.onPause();

        playerLocationOverlay.disableCompass();
        playerLocationOverlay.disableMyLocation();
    }

    private void initMap() {
        playerLocationOverlay = new PlayerLocationOverlay(this, mapView);
        mapView.getOverlays().add(playerLocationOverlay);
        playerLocationOverlay.enableCompass();
        playerLocationOverlay.enableMyLocation();
        playerLocationOverlay.runOnFirstFix(new Runnable() {
            public void run() {
                mapController.animateTo(playerLocationOverlay.getMyLocation());
            }
        });
    }

    protected boolean isRouteDisplayed() {
        return false;
    }

    public void onCrimeLocationHit(CrimeSite crimeSite) {
        mLogAdapter.add(crimeSite.getTitle());
        mLogAdapter.notifyDataSetChanged();
    }

    private class HelloItemizedOverlay extends ItemizedOverlay {
        private ArrayList<OverlayItem> mOverlays = new ArrayList<OverlayItem>();

        public HelloItemizedOverlay(Drawable drawable) {
            super(boundCenterBottom(drawable));
        }

        public void addOverlay(OverlayItem overlay) {
            mOverlays.add(overlay);
            populate();
        }

        @Override
        protected OverlayItem createItem(int i) {
            return mOverlays.get(i);            
        }

        @Override
        public int size() {
            return mOverlays.size();
        }
    }
}
