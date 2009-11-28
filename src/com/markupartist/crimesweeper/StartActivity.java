package com.markupartist.crimesweeper;

import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.view.View;
import android.app.Dialog;
import android.app.AlertDialog;
import android.widget.ListView;
import android.widget.TextView;
import android.util.Log;
import com.google.android.maps.*;

import java.util.ArrayList;
import java.util.List;
import java.util.LinkedList;

public class StartActivity extends MapActivity implements CrimeLocationHitListener, View.OnClickListener, CrimeSitesLoadedListener {
    private static int HIT_POINT = 10;
    private static long GAME_TIME = 3600000;
    public static int CRIME_SITE_TIME_INTERVAL = 60 * 24 * 30;
    private static final int DIALOG_GAME_FINISHED = 1;
    private ArrayAdapter<String> mLogAdapter;
    private MapView mapView;
    private PlayerLocationOverlay playerLocationOverlay;
    private MapController mapController;
    private TextView mPointsView;
    private TextView mCountDownView;
    private GameCountDown mGameCountDown;
    private List<CrimeSite> crimeSites;
    //List<CrimeSite> mFoundCrimeSites = new ArrayList<CrimeSite>();
    List<CrimeSite> mFoundCrimeSites = new ArrayList<CrimeSite>();
    private Button mStartButton;
    private LinkedList<String> mCrimeLogList;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        // Setup log view
        ListView crimeLogView = (ListView) findViewById(R.id.crime_log);
        //ArrayList<String> mCrimeLogList = new ArrayList<String>();
        mCrimeLogList = new LinkedList<String>();
        mLogAdapter = new ArrayAdapter<String>(this, R.layout.crime_log_row, mCrimeLogList);
        crimeLogView.setAdapter(mLogAdapter);

        // Setup points view
        mPointsView = (TextView) findViewById(R.id.points);

        // Setup countdown time
        mCountDownView = (TextView) findViewById(R.id.time);

        // Setup start button
        mStartButton = (Button) findViewById(R.id.start_game);
        mStartButton.setOnClickListener(this);
        mStartButton.setEnabled(false); // Start disables until all crime sites has been loaded.
        
        mapView = (MapView) findViewById(R.id.mapview);
        mapController = mapView.getController();

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
                crimeSites = CrimeSite.getCrimeSites(CRIME_SITE_TIME_INTERVAL);
                for(CrimeSite crimeSite: crimeSites) {
                    OverlayItem crimeSiteOverlayitem = new OverlayItem(crimeSite, crimeSite.getTitle(), "");
                    itemizedOverlay.addOverlay(crimeSiteOverlayitem);
                }

                return itemizedOverlay;
            }

            @Override
            protected void onPostExecute(HelloItemizedOverlay helloItemizedOverlay) {
                mapOverlays.add(itemizedOverlay);

                initMap();
                
                onCrimeSitesLoaded();
            }
        }

        new PopulateCrimeOverlaysTask().execute();

        mapController.setZoom(15);

        //onCrimeLocationHit(new CrimeSite("sdsds", 2222, 222));
        //onCrimeLocationHit(new CrimeSite("sdsds 111", 2222, 222));
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(playerLocationOverlay != null) {
            playerLocationOverlay.enableCompass();
            playerLocationOverlay.enableMyLocation();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if(playerLocationOverlay != null) {
            playerLocationOverlay.disableCompass();
            playerLocationOverlay.disableMyLocation();
        }
    }

    private void initMap() {
        playerLocationOverlay = new PlayerLocationOverlay(this, mapView, crimeSites);
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
        if(mFoundCrimeSites.contains(crimeSite))
          return;

        //mLogAdapter.add(crimeSite.getTitle());
        mCrimeLogList.addFirst(crimeSite.getTitle());
        mLogAdapter.notifyDataSetChanged();
        mFoundCrimeSites.add(crimeSite);
        increasePoints();
    }

    /**
     * Increases the player points.
     */
    private void increasePoints() {
        String currentPoints = (String) mPointsView.getText();
        int newPoints = Integer.parseInt(currentPoints) + HIT_POINT;
        mPointsView.setText(String.valueOf(newPoints));
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.start_game:
                if (mGameCountDown == null) {
                    mGameCountDown = new GameCountDown(GAME_TIME, 1000);
                }
                mGameCountDown.cancel();
                mGameCountDown.start();
                mPointsView.setText("0");
                // Register the callback for crime hits
                playerLocationOverlay.setCrimeLocationHitListener(this);
                // Reset the crime log
                mLogAdapter.clear();
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

    public void onCrimeSitesLoaded() {
        Log.d("Start", "onCrimeSitesLoaded");
        mStartButton.setEnabled(true);
        mStartButton.setText("Start");
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
          
            long seconds = millisUntilFinished / 1000;
            mCountDownView.setText(String.format("Time: %02d:%02d", seconds / 60,  seconds % 60));
        }

        public void onFinish() {
            // Remove callback for the callback for crime hits.
            playerLocationOverlay.setCrimeLocationHitListener(null);
            showDialog(DIALOG_GAME_FINISHED);
        }
    }
}
