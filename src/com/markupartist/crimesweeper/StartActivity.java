package com.markupartist.crimesweeper;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.drawable.Drawable;
import android.*;
import android.view.View;
import android.view.MotionEvent;
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
        
        CrimeSite.GetCrimeSites(1400);
        
        
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
