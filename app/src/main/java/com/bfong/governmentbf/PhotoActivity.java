package com.bfong.governmentbf;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class PhotoActivity extends AppCompatActivity {

    private static final String TAG = "PhotoActivity";
    private ConstraintLayout constraintLayout;
    private ImageView portrait;
    private ImageView partyImg;
    private TextView locationView;
    private TextView office;
    private TextView name;
    private TextView party;

    private Official official;
    private String location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: ");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);

        constraintLayout = findViewById(R.id.layout);
        portrait = findViewById(R.id.portrait);
        partyImg = findViewById(R.id.partyImg);
        locationView = findViewById(R.id.locationView);
        office = findViewById(R.id.office);
        name = findViewById(R.id.name);
        party = findViewById(R.id.party);

        Intent intent = getIntent();
        if (intent.hasExtra("OFFICIAL")) {
            official = (Official) intent.getSerializableExtra("OFFICIAL");
        } else {
            official = null;
        }
        if (intent.hasExtra("LOC")) {
            location = intent.getStringExtra("LOC");
        }

        office.setText(official.getOffice());
        name.setText(official.getName());
        party.setText(official.getParty());
        locationView.setText(location);

        String party = official.getParty();
        if (party.equals("Democratic") || party.equals("Democratic Party")) {
            constraintLayout.setBackgroundColor(Color.parseColor("BLUE"));
            partyImg.setImageResource(R.drawable.dem_logo);
        } else if (party.equals("Republican") || party.equals("Republican Party")) {
            constraintLayout.setBackgroundColor(Color.parseColor("RED"));
            partyImg.setImageResource(R.drawable.rep_logo);
        }

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (!(netInfo != null && netInfo.isConnectedOrConnecting())) {
            portrait.setImageResource(R.drawable.brokenimage);
        } else{
            loadImage(official.getPhoto());
        }
    }

    private void loadImage(String link) {
        final long start = System.currentTimeMillis();

        Picasso.get().load(link)
                .error(R.drawable.brokenimage)
                .placeholder(R.drawable.placeholder)
                .into(portrait,
                        new Callback() {
                            @Override
                            public void onSuccess() {
                                long dur = System.currentTimeMillis() - start;
                            }

                            @Override
                            public void onError(Exception e) {

                            }
                        });
    }
}