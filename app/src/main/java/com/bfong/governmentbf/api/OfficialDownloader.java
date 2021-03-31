package com.bfong.governmentbf.api;

import android.net.Uri;
import android.util.Log;

import com.bfong.governmentbf.MainActivity;
import com.bfong.governmentbf.Official;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class OfficialDownloader implements Runnable {

    private static final String TAG = "OfficialDownloader";
    private static final String query1 = "https://www.googleapis.com/civicinfo/v2/representatives?key=AIzaSyCmReDXazbuUm3eDWCkih8kCcWr9aSm_r0&address=";
    private MainActivity mainAct;
    private String query;

    public OfficialDownloader(MainActivity mainAct, String location) {
        this.mainAct = mainAct;
        try {
            this.query = query1 + location.replace(" ", "+");
        } catch (Exception e) {
            this.query = query1 + location;
        }
    }

    @Override
    public void run() {
        Log.d(TAG, "run: ");
        Uri uriBuilder = Uri.parse(query);
        String urlString = uriBuilder.toString();
        Log.d(TAG, "run: " + uriBuilder.toString());

        StringBuilder sb = new StringBuilder();
        try {
            URL url = new URL(urlString);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();

            if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                Log.d(TAG, "run: HTTP ResponseCode not OK: " + conn.getResponseCode());
                mainAct.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mainAct.badURL();
                    }
                });
                return;
            }

            InputStream is = conn.getInputStream();
            BufferedReader reader = new BufferedReader((new InputStreamReader(is)));

            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }

            //Log.d(TAG, "run: " + sb.toString());
        } catch (Exception e) {
            Log.e(TAG, "run: ", e);
            return;
        }

        process(sb.toString());
        Log.d(TAG, "run: ");
    }

    private void process(String s) {
        try {
            JSONObject jOfficial = new JSONObject(s);

            JSONObject normalized = jOfficial.getJSONObject("normalizedInput");

            JSONArray offices = jOfficial.getJSONArray("offices");
            JSONArray officials = jOfficial.getJSONArray("officials");
            for (int i = 0; i < offices.length(); i++) {
                String location = String.format("%s, %s %s", normalized.getString("city"), normalized.getString("state"), normalized.getString("zip"));
                String name = "";
                String office;
                String address = "";
                String party = "";
                String phone = "";
                String website = "";
                String email = "";
                String photo = "";
                String fb = "";
                String twitter = "";
                String youtube = "";

                JSONObject jOffice = (JSONObject) offices.get(i);
                office = jOffice.getString("name");

                JSONArray jIndices = jOffice.getJSONArray("officialIndices");
                int[] indices = new int[jIndices.length()];
                for (int j = 0; j < jIndices.length(); j++) {
                    indices[j] = jIndices.getInt(j);
                }

                for (int x: indices) {
                    JSONObject o = (JSONObject) officials.get(x);
                    name = o.getString("name");

                    try {
                        StringBuilder sb = new StringBuilder();
                        JSONObject jAddress = (JSONObject) o.getJSONArray("address").get(0);
                        sb.append(jAddress.getString("line1"));
                        sb.append("\n");
                        try {
                            sb.append(jAddress.getString("line2"));
                            sb.append("\n");
                            sb.append(jAddress.getString("line3"));
                            sb.append("\n");
                        } catch (Exception e) {

                        }
                        sb.append(jAddress.getString("city"));
                        sb.append(", ");
                        sb.append(jAddress.getString("state"));
                        sb.append(" ");
                        sb.append(jAddress.getString("zip"));
                        address = sb.toString();
                    } catch (Exception e) {

                    }

                    party = o.getString("party").trim();
                    if (party.equals("")) {
                        party = "Unknown";
                    }

                    try {
                        phone = o.getJSONArray("phones").get(0).toString().trim();
                    } catch (Exception e) {

                    }

                    try {
                        website = o.getJSONArray("urls").get(0).toString().trim();
                    } catch (Exception e) {

                    }

                    try {
                        email = o.getJSONArray("emails").get(0).toString().trim();
                    } catch (Exception e) {

                    }

                    try {
                        photo = o.getString("photoUrl").trim();
                    } catch (Exception e) {

                    }

                    try {
                        JSONArray jChannels = o.getJSONArray("channels");
                        for (int k = 0; k < jChannels.length(); k++) {
                            JSONObject channel = jChannels.getJSONObject(k);
                            String type = channel.getString("type");
                            if (type.equals("Facebook")) {
                                fb = channel.getString("id");
                            } else if (type.equals("Twitter")) {
                                twitter = channel.getString("id");
                            } else if (type.equals("YouTube")) {
                                    youtube = channel.getString("id");
                            }
                        }
                    } catch (Exception e) {

                    }

                    final Official official = new Official(name, office, party, location);
                    official.setAddress(address);
                    official.setPhone(phone);
                    official.setWebsite(website);
                    official.setEmail(email);
                    official.setPhoto(photo);
                    official.setFb(fb);
                    official.setTwitter(twitter);
                    official.setYoutube(youtube);
                    mainAct.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mainAct.addOfficial(official);
                        }
                    });
                    Log.d(TAG, "process: added official " + name);
                }
            }
        } catch (Exception e) {
            Log.d(TAG, "process: error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
