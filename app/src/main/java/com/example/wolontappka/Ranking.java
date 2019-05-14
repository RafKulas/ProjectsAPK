package com.example.wolontappka;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class Ranking extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String TEXT="PHPSESSIONID";
    JSONObject rankJSON;
    ArrayList<String> emails;
    ArrayList<String> points;
    TextView ranking;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_rank);
        ranking = (TextView) findViewById(R.id.rankingView);
        emails = new ArrayList<String>();
        points = new ArrayList<String>();

        CallApiRR carr = new CallApiRR();
        carr.execute("http://linuch.ds.pg.gda.pl/php/ranking.php");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar3);
        //setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout3);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view3);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int id = menuItem.getItemId();

        if (id == R.id.nav_ranking) {

            Intent intent = new Intent(Ranking.this, Ranking.class);
            startActivity(intent);
        } else if (id == R.id.nav_logout) {
            SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(TEXT, null);
            editor.apply();
            Intent intent = new Intent(Ranking.this, Logowanie.class);
            startActivity(intent);

        } else if (id == R.id.nav_main) {
            Intent intent = new Intent(Ranking.this, Zlecenia.class);
            startActivity(intent);

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout3);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    private class CallApiRR extends AsyncTask<String, String, String> {

        private String result;

        public CallApiRR() {
            //set context variables if required
        }

        protected String getResult() {
            return result;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String result) {
            int size = 0;
            try {
                rankJSON = new JSONObject(result);
                JSONArray array= rankJSON.getJSONArray("data");
                size = array.length();
                for(int i=0;i<size;i++){
                    emails.add(array.getJSONObject(i).getString("email"));
                    points.add(array.getJSONObject(i).getString("points"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String doRankingu="";
            for (int iteracja=0; iteracja<size; ++iteracja) {
                doRankingu+=(iteracja+1)+". "+ emails.get(iteracja)+" ("+points.get(iteracja)+")" + System.getProperty("line.separator");
            }
            ranking.setText(doRankingu);

        }

        @Override
        protected String doInBackground(String... params) {
            String urlString = params[0]; // URL to call
            OutputStream out = null;
            String answer = " ";


            try {
                URL url = new URL(urlString);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");
                out = new BufferedOutputStream(urlConnection.getOutputStream());

                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, "UTF-8"));
                writer.flush();
                writer.close();
                out.close();

                BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                String line = "";
                StringBuilder responseOutput = new StringBuilder();

                while ((line = br.readLine()) != null) {
                    responseOutput.append(line);
                }
                br.close();

                answer += responseOutput.toString();

                urlConnection.connect();


            } catch (Exception e) {
                System.out.println(e.getMessage());
            }


            return answer;
        }
    }
}
