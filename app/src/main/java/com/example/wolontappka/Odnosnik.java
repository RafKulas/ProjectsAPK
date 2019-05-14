package com.example.wolontappka;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import static android.os.Build.ID;
import static com.example.wolontappka.Logowanie.SHARED_PREFS;
import static com.example.wolontappka.Logowanie.TEXT;
import static java.lang.System.out;

public class Odnosnik extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String TEXT="PHPSESSIONID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_odnosnik);
        Odnosnik.fetchData proces = new Odnosnik.fetchData();
        proces.execute();
        Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Odnosnik.fetchdata2 proces2 = new Odnosnik.fetchdata2();
                SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
                String idSession = sharedPreferences.getString(TEXT, null);
                proces2.execute("http://linuch.ds.pg.gda.pl/php/join.php?PHPSESSID="+idSession);
            }
        });
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar2);
        //setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout2);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view2);
        navigationView.setNavigationItemSelectedListener(this);


    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int id = menuItem.getItemId();

        if (id == R.id.nav_ranking) {

            Intent intent = new Intent(Odnosnik.this, Ranking.class);
            startActivity(intent);
        } else if (id == R.id.nav_logout) {
            SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(TEXT, null);
            editor.apply();
            Intent intent = new Intent(Odnosnik.this, Logowanie.class);
            startActivity(intent);

        } else if (id == R.id.nav_main) {
            Intent intent = new Intent(Odnosnik.this, Zlecenia.class);
            startActivity(intent);

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout2);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public class fetchData extends AsyncTask<Void, Void, Void> {
        String FUNDACJA;
        String ZAWARTOSC;
        String buffer;
        @Override
        protected Void doInBackground(Void... voids) {
            HttpURLConnection connection=null;
            BufferedReader reader=null;
            try {
                String ID = getIntent().getStringExtra("id");
                URL url=new URL("http://linuch.ds.pg.gda.pl/php/get_request.php?id="+ID);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                InputStream stream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));
                String line;
                buffer="";
                while((line = reader.readLine()) != null) {
                    buffer += line;
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            JSONObject json= null;
            try {
                json = new JSONObject(buffer);
            } catch (JSONException e1) {
                e1.printStackTrace();
            }
            String status = null;
            try {
                status = json.getString("status");
            } catch (JSONException e1) {
                e1.printStackTrace();
            }
            if(status.equals("ok")) {
                try {
                    ZAWARTOSC=json.getJSONObject("data").getString("description");
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }
                try {
                    FUNDACJA=json.getJSONObject("data").getString("title");
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }
            }
            else
            {
                FUNDACJA="Blad";
                try {
                    ZAWARTOSC=json.getString("info");
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }
            }
            TextView textView_fundacja = (TextView) findViewById(R.id.textView_fundacja);
            TextView textView_zawartosc = (TextView) findViewById(R.id.textView_zawartosc);
            textView_fundacja.setText(FUNDACJA);
            textView_zawartosc.setText(ZAWARTOSC);

        }
    }

    public class fetchdata2 extends AsyncTask<String, String, String> {

        private String result;
        public static final String SHARED_PREFS = "sharedPrefs";
        public static final String TEXT="PHPSESSIONID";

        protected String getResult() {
            return result;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        String answer="";
        @Override
        protected void onPostExecute(String result) {
            JSONObject json= null;
            try {
                json = new JSONObject(answer);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                answer = json.getString("info");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Toast toast = Toast.makeText(getApplicationContext(), answer, Toast.LENGTH_LONG);
            toast.show();

            //SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
            //String idSession = sharedPreferences.getString(TEXT, null);
        }

        @Override
        protected String doInBackground(String... params) {
            HttpURLConnection connection=null;
            String ID2 = "id="+getIntent().getStringExtra("id");
            String urlString = params[0]; // URL to call
            answer="";
            try {
                URL url = new URL(urlString);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-type", "application/x-www-form-urlencoded");
                OutputStream out = new BufferedOutputStream(urlConnection.getOutputStream());

                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, "UTF-8"));
                writer.write(ID2);
                writer.flush();
                writer.close();
                out.close();

                BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                String line = "";
                StringBuilder responseOutput = new StringBuilder();

                while((line = br.readLine())!=null) {
                    responseOutput.append(line);
                }
                br.close();

                answer += responseOutput.toString();

                urlConnection.connect();


            } catch (Exception e) {
                out.println(e.getMessage());
            }
            /*Context context = getApplicationContext();
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(context, answer, duration);
            toast.show();*/
            return answer;
        }
    }


}