package com.bfong.governmentbf;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.text.util.Linkify;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class OfficialActivity extends AppCompatActivity
        implements View.OnClickListener {

    private static final String TAG = "OfficialActivity";
    private ConstraintLayout constraintLayout;
    private TextView locationView;
    private TextView officeView;
    private TextView nameView;
    private TextView partyView;
    private ImageView portrait;
    private ImageView partyPic;
    private TextView addr;
    private TextView addressView;
    private TextView phn;
    private TextView phoneView;
    private TextView eml;
    private TextView email;
    private TextView web;
    private TextView website;
    private ImageView facebook;
    private ImageView twitter;
    private ImageView youtube;

    private Official official;
    private String location;
    private String party = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_official);

        constraintLayout = findViewById(R.id.layout);

        Intent intent = getIntent();
        if (intent.hasExtra("OFFICIAL")) {
            official = (Official) intent.getSerializableExtra("OFFICIAL");
        } else {
            official = null;
        }
        if (intent.hasExtra("LOC")) {
            location = intent.getStringExtra("LOC");
        }

        locationView = findViewById(R.id.location);
        officeView = findViewById(R.id.office);
        nameView = findViewById(R.id.name);
        partyView = findViewById(R.id.party);
        portrait = findViewById(R.id.portrait);
        partyPic = findViewById(R.id.partyImg);
        addr = findViewById(R.id.addr);
        addressView = findViewById(R.id.address);
        phn = findViewById(R.id.phn);
        phoneView = findViewById(R.id.phone);
        eml = findViewById(R.id.eml);
        email = findViewById(R.id.email);
        web = findViewById(R.id.web);
        website = findViewById(R.id.website);
        facebook = findViewById(R.id.facebook);
        twitter = findViewById(R.id.twitter);
        youtube = findViewById(R.id.youtube);

        locationView.setText(location);
        officeView.setText(official.getOffice());
        nameView.setText(official.getName());
        partyView.setText(official.getParty());

        String party = official.getParty();
        if (party.equals("Democratic") || party.equals("Democratic Party")) {
            constraintLayout.setBackgroundColor(Color.parseColor("BLUE"));
            this.party = "DEM";
            partyPic.setImageResource(R.drawable.dem_logo);
        } else if (party.equals("Republican") || party.equals("Republican Party")) {
            constraintLayout.setBackgroundColor(Color.parseColor("RED"));
            partyPic.setImageResource(R.drawable.rep_logo);
            this.party = "REP";
        }

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (!(netInfo != null && netInfo.isConnectedOrConnecting())) {
            portrait.setImageResource(R.drawable.brokenimage);
        } else {
            loadImage(official.getPhoto());
        }

        if (!official.getAddress().equals("")) {
            addressView.setText(official.getAddress());
        }
        Linkify.addLinks(addressView, Linkify.ALL);

        if (!official.getPhone().equals("")) {
            phoneView.setText(official.getPhone());
        }
        Linkify.addLinks(phoneView, Linkify.ALL);

        if (!official.getEmail().equals("")) {
            email.setText(official.getEmail());
        }
        Linkify.addLinks(email, Linkify.ALL);

        if (!official.getWebsite().equals("")) {
            website.setText(official.getWebsite());
        }
        Linkify.addLinks(website, Linkify.ALL);

        if (!official.getFb().equals("")) {
            facebook.setImageResource(R.drawable.facebook);
        }

        if (!official.getTwitter().equals("")) {
            twitter.setImageResource(R.drawable.twitter);
        }

        if (!official.getYoutube().equals("")) {
            youtube.setImageResource(R.drawable.youtube);
        }
    }

    @Override
    public void onClick(View v) {
        Log.d(TAG, "onClick: clicked portrait");
        if (!official.getPhoto().equals("")) {
            Intent intent = new Intent(this, PhotoActivity.class);
            intent.putExtra("OFFICIAL", official);
            intent.putExtra("LOC", locationView.getText().toString());
            startActivity(intent);
        }
    }

    public void partySite(View v) {
        if (!party.equals("")) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            if (party.equals("DEM")) {
                intent.setData(Uri.parse("https://democrats.org"));
            } else if (party.equals("REP")) {
                intent.setData(Uri.parse("https://www.gop.com"));
            }
            startActivity(intent);

        }
    }

    private void loadImage(String link) {
        Log.d(TAG, "loadImage: " + link);
        if (link.equals("")) {
            portrait.setImageResource(R.drawable.missing);
        } else {
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

    public void facebookClicked(View v) {
        if (!official.getFb().equals("")) {
            String FACEBOOK_URL = "https://www.facebook.com/" + official.getFb();
            String urlToUse;
            PackageManager packageManager = getPackageManager();
            try {
                int versionCode = packageManager.getPackageInfo("com.facebook.katana", 0).versionCode;
                if (versionCode >= 3002850) {
                    urlToUse = "fb://facewebmodal/f?href=" + FACEBOOK_URL;
                } else {
                    urlToUse = "fb://page/" + official.getFb();
                }
            } catch (PackageManager.NameNotFoundException e) {
                urlToUse = FACEBOOK_URL;
            }
            Intent facebookIntent = new Intent(Intent.ACTION_VIEW);
            facebookIntent.setData(Uri.parse(urlToUse));
            startActivity(facebookIntent);
        }
    }

    public void twitterClicked(View v) {
        if (!official.getTwitter().equals("")) {
            Intent intent = null;
            String name = official.getTwitter();
            try {
                getPackageManager().getPackageInfo("com.twitter.android", 0);
                intent = new Intent(Intent.ACTION_VIEW, Uri.parse("twitter://user?screen_name=" + name));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            } catch (Exception e) {
                intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/" + name));
            }
            startActivity(intent);
        }
    }

    public void youTubeClicked(View v) {
        if (!official.getYoutube().equals("")) {
            String name = official.getYoutube();
            Intent intent = null;
            try {
                intent = new Intent(Intent.ACTION_VIEW);
                intent.setPackage("com.google.android.youtube");
                intent.setData(Uri.parse("https://www.youtube.com/" + name));
                startActivity(intent);
            } catch (ActivityNotFoundException e) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/" + name)));
            }
        }
    }
}