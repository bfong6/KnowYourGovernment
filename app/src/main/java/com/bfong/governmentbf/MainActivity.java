package com.bfong.governmentbf;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bfong.governmentbf.api.OfficialDownloader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class MainActivity extends AppCompatActivity
        implements View.OnClickListener {

    private static final String TAG = "MainActivity";
    private static int MY_LOCATION_REQUEST_CODE_ID = 111;
    private static List<Official> officialList = new ArrayList<>();
    private RecyclerView recyclerView;
    private OfficialAdapter officialAdapter;

    private TextView locationView;
    private LocationManager locationManager;
    private Criteria criteria;
    private String location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recycler);
        officialAdapter = new OfficialAdapter(officialList, this);
        recyclerView.setAdapter(officialAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        locationView = findViewById(R.id.locationView);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        criteria = new Criteria();

        criteria.setPowerRequirement(Criteria.POWER_HIGH);
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setSpeedRequired(false);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION
                    },
                    MY_LOCATION_REQUEST_CODE_ID);
        } else {
            setLocation();
        }

        if (!checkNetworkConnection()) {
            noNetworkError();
        } else {
            fetchData();
        }
    }

    private void fetchData() {
        officialList.clear();
        OfficialDownloader officialDownloader = new OfficialDownloader(this, location);
        new Thread(officialDownloader).start();
    }

    @SuppressLint("MissingPermission")
    private void setLocation() {
        Location currentLocation = null;
        String bestProvider = locationManager.getBestProvider(criteria, true);
        if (bestProvider != null) {
            currentLocation = locationManager.getLastKnownLocation(bestProvider);
        }
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        if (currentLocation != null) {
            try {
                List<Address> addresses;
                addresses = geocoder.getFromLocation(currentLocation.getLatitude(), currentLocation.getLongitude(), 1);
                Address loc = addresses.get(0);
                this.location = loc.getPostalCode();
                String l = String.format("%s, %s %s", loc.getLocality(), loc.getAdminArea(), loc.getPostalCode());
                locationView.setText(l);
            } catch (IOException e) {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        } else {
            noNetworkError();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.aboutBtn:
                Log.d(TAG, "onOptionsItemSelected: Selected About");
                Intent intent = new Intent(this, AboutActivity.class);
                startActivity(intent);
                return true;
            case R.id.searchBtn:
                Log.d(TAG, "onOptionsItemSelected: Search button");
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                final EditText et = new EditText(this);
                et.setInputType(InputType.TYPE_CLASS_TEXT);
                et.setGravity(Gravity.CENTER_HORIZONTAL);
                builder.setView(et);
                builder.setTitle("Enter a City, State, or Zip Code");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (!checkNetworkConnection()) {
                            noNetworkError();
                        } else {
                            String query = et.getText().toString().trim();
                            location = query;
                            locationView.setText(location);
                            fetchData();
                            Log.d(TAG, String.format("onClick: query = %s", query));
                        }
                    }
                });
                builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(View v) {
        int pos = recyclerView.getChildLayoutPosition(v);
        Official o = officialList.get(pos);
        Intent intent = new Intent(this, OfficialActivity.class);
        intent.putExtra("OFFICIAL", o);
        intent.putExtra("LOC", locationView.getText().toString());
        startActivity(intent);
    }

    public void addOfficial(Official official) {
        if (official != null) {
            officialList.add(official);
            officialAdapter.notifyDataSetChanged();
        }
    }

    public void noNetworkError() {
        AlertDialog.Builder b = new AlertDialog.Builder(this);
        b.setTitle("No Network Connection");
        b.setMessage("No network connection detected!");
        b.setPositiveButton("DISMISS", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

            }
        });
        AlertDialog d = b.create();
        d.show();
    }

    public void badURL() {
        AlertDialog.Builder b = new AlertDialog.Builder(this);
        b.setTitle("No Results");
        b.setMessage("Query returned no results!");
        b.setPositiveButton("DISMISS", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

            }
        });
        AlertDialog d = b.create();
        d.show();
    }

    private boolean checkNetworkConnection() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
}