package com.markupartist.crimesweeper;

import android.os.Bundle;
import android.graphics.drawable.Drawable;
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
public class StartActivity extends MapActivity {
    private MapView mapView;
    private MyLocationOverlay myLocationOverlay;
    private MapController mapController;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        List<CrimeSite> crimeSites = new ArrayList<CrimeSite>();
        crimeSites.add(new CrimeSite("Grand Theft Auto", 59414207, 18273497));
        crimeSites.add(new CrimeSite("Murder One", 59514207, 18173497));
        crimeSites.add(new CrimeSite("Close to me", 59279986, 1808275));
        
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

        myLocationOverlay.enableCompass();
        myLocationOverlay.enableMyLocation();
    }

    @Override
    protected void onPause() {
        super.onPause();

        myLocationOverlay.disableCompass();
        myLocationOverlay.disableMyLocation();
    }

    private void initMap() {
        myLocationOverlay = new MyLocationOverlay(this, mapView);
        mapView.getOverlays().add(myLocationOverlay);
        myLocationOverlay.enableCompass();
        myLocationOverlay.enableMyLocation();
        myLocationOverlay.runOnFirstFix(new Runnable() {
            public void run() {
                mapController.animateTo(myLocationOverlay.getMyLocation());
            }
        });
    }

    protected boolean isRouteDisplayed() {
        return false;
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
