package com.markupartist.crimesweeper;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.AsyncTask;
import android.graphics.drawable.Drawable;
import android.widget.ListView;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Button;
import android.view.View;
import android.app.Dialog;
import android.app.AlertDialog;
import android.util.Log;
import com.google.android.maps.*;

import java.util.List;
import java.util.ArrayList;

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
        
        mapView = (MapView) findViewById(R.id.mapview);
        mapController = mapView.getController();

        initMap();

        mapView.setClickable(true);
        mapView.setEnabled(true);
        mapView.setStreetView(true);
        mapView.setBuiltInZoomControls(true);

        final List<Overlay> mapOverlays = mapView.getOverlays();
        Drawable drawable = this.getResources().getDrawable(android.R.drawable.btn_star);
        final HelloItemizedOverlay itemizedOverlay = new HelloItemizedOverlay(drawable);

        class PopulateCrimeOverlaysTask extends AsyncTask<Void, Void, HelloItemizedOverlay> {
            @Override
            protected HelloItemizedOverlay doInBackground(Void... voids) {
                List<CrimeSite> crimeSites = CrimeSite.getCrimeSites(60*24);
                for(CrimeSite crimeSite: crimeSites) {
                    OverlayItem crimeSiteOverlayitem = new OverlayItem(crimeSite, crimeSite.getTitle(), "");
                    itemizedOverlay.addOverlay(crimeSiteOverlayitem);
                }

                return itemizedOverlay;
            }

            @Override
            protected void onPostExecute(HelloItemizedOverlay helloItemizedOverlay) {
                mapOverlays.add(itemizedOverlay);
            }
        }

        PopulateCrimeOverlaysTask populateCrimeOverlaysTask = new PopulateCrimeOverlaysTask();
        populateCrimeOverlaysTask.execute();

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
        Log.d("Start", "increasePoints");
        //String currentPoints = (String) mPointsView.getText();

        /*
        String currentPoints = (String) mPointsView.getText();
        if (currentPoints == null) {
            currentPoints = "0";
        }
        int points = Integer.parseInt(currentPoints) + HIT_POINT;

        mPointsView.setText(points);
        */
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.start_game:
                if (mGameCountDown == null) {
                    mGameCountDown = new GameCountDown(GAME_TIME, 1000);
                }
                mGameCountDown.cancel();
                mGameCountDown.start();
                mPointsView.setText("");
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
