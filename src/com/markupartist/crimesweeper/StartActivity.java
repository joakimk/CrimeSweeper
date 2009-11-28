package com.markupartist.crimesweeper;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.graphics.drawable.Drawable;
import android.widget.ListView;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Button;
import android.view.View;
import android.app.Dialog;
import android.app.AlertDialog;
import android.content.DialogInterface;
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
public class StartActivity extends MapActivity implements CrimeLocationHitListener, View.OnClickListener {
    private static int HIT_POINT = 10;
    private static long GAME_TIME = 3600000;
    private static final int DIALOG_GAME_FINISHED = 1;
    private ArrayAdapter<String> mLogAdapter;
    private MapView mapView;
    private PlayerLocationOverlay playerLocationOverlay;
    private MapController mapController;
    private TextView mPointsView;
    private TextView mCountDownView;
    private GameCountDown mGameCountDown;

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

        // Setup points view
        mPointsView = (TextView) findViewById(R.id.points);

        // Setup countdown time
        mCountDownView = (TextView) findViewById(R.id.time);

        // Setup start button
        Button startButton = (Button) findViewById(R.id.start_game);
        startButton.setOnClickListener(this);

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
        playerLocationOverlay.setCrimeLocationHitListener(this);
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

        increasePoints();
    }

    /**
     * Increases the player points.
     */
    private void increasePoints() {
        int currentPoints = Integer.parseInt(mPointsView.getText().toString());
        mPointsView.setText(currentPoints + HIT_POINT);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.start_game:
                if (mGameCountDown == null) {
                    mGameCountDown = new GameCountDown(GAME_TIME, 1000);
                }
                mGameCountDown.cancel();
                mGameCountDown.start();
        }
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch(id) {
        case DIALOG_GAME_FINISHED:
            return new AlertDialog.Builder(this)
                    .setTitle("The End")
                    .setPositiveButton("Ok", null)
                    .setMessage(String.format("You got %s points", mPointsView.getText()))
                    .create();
        }
        return null;
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

    public class GameCountDown extends CountDownTimer {
        public GameCountDown(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        public void onTick(long millisUntilFinished) {
            mCountDownView.setText("Time: " + millisUntilFinished / 1000);
        }

        public void onFinish() {
            showDialog(DIALOG_GAME_FINISHED);
        }
    }
}
