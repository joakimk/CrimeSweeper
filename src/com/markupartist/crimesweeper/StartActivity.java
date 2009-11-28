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

        List<CrimeSite> crimeSites = new ArrayList<CrimeSite>();
        crimeSites.add(new CrimeSite("Grand Theft Auto", 59414207, 18273497));
        crimeSites.add(new CrimeSite("Murder One", 59514207, 18173497));
        
        MapView mapView = (MapView) findViewById(R.id.mapview);

        List<Overlay> mapOverlays;
        Drawable drawable;
        HelloItemizedOverlay itemizedOverlay;

        mapOverlays = mapView.getOverlays();
        drawable = this.getResources().getDrawable(android.R.drawable.picture_frame);
        itemizedOverlay = new HelloItemizedOverlay(drawable);

        mapView.setStreetView(true);
        MapController controller = mapView.getController();
        GeoPoint sthlmCenterPoint = new GeoPoint(59314207, 18073497); 
        controller.setCenter(sthlmCenterPoint);

        OverlayItem sthlmOverlayitem = new OverlayItem(sthlmCenterPoint, "aa", "bb");

        itemizedOverlay.addOverlay(sthlmOverlayitem);
        mapOverlays.add(itemizedOverlay);
//
        controller.stopPanning();
        controller.setZoom(15);
//        controller.zoomIn();

        MyLocationOverlay myLocationOverlay = new MyLocationOverlay(this, mapView);
        myLocationOverlay.enableCompass();
        myLocationOverlay.enableMyLocation();

        mapOverlays.add(myLocationOverlay);
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
