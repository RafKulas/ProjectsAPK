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
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import android.content.SharedPreferences;

public class Zlecenia extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String TEXT="PHPSESSIONID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fetchData proces = new fetchData("");
        proces.execute();
        EditText search=(EditText) findViewById(R.id.editText);
        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                fetchData proces = new fetchData(s.toString());
                proces.execute();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int id = menuItem.getItemId();

        if (id == R.id.nav_ranking) {

            Intent intent = new Intent(Zlecenia.this, Ranking.class);
            startActivity(intent);
        } else if (id == R.id.nav_logout) {
            SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(TEXT, null);
            editor.apply();
            Intent intent = new Intent(Zlecenia.this, Logowanie.class);
            startActivity(intent);

        } else if (id == R.id.nav_main) {
            Intent intent = new Intent(Zlecenia.this, Zlecenia.class);
            startActivity(intent);

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    class CustomAdapter extends BaseAdapter{

            List<String> FUNDACJE = new ArrayList<>();
            List<String> ZAWARTOSC = new ArrayList<>();
            List<String> ID = new ArrayList<>();
            CustomAdapter(List<String> FUNDACJE, List<String> ZAWARTOSC, List<String> ID){
                this.FUNDACJE=FUNDACJE;
                this.ZAWARTOSC=ZAWARTOSC;
                this.ID=ID;
            }
        @Override
        public int getCount() {
            return FUNDACJE.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            convertView = getLayoutInflater().inflate(R.layout.custumlayout,null);
            TextView textView_fundacja = (TextView)convertView.findViewById(R.id.textView_fundacja);
            TextView textView_zawartosc = (TextView)convertView.findViewById(R.id.textView_zawartosc);
            textView_fundacja.setText(FUNDACJE.get(position));
            textView_zawartosc.setText(ZAWARTOSC.get(position));

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Zlecenia.this, Odnosnik.class);
                    intent.putExtra("id", ID.get(position));
                    startActivity(intent);
                }
            });
            return convertView;
        }
    }

    public class fetchData extends AsyncTask<Void, Void, Void> {
        List<String> FUNDACJE = new ArrayList<>();
        List<String> ZAWARTOSC = new ArrayList<>();
        List<String> ID = new ArrayList<>();
        String buffer;
        String search;
        fetchData(String search){
            this.search=search;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            HttpURLConnection connection=null;
            BufferedReader reader=null;
            try {
                URL url=new URL("http://linuch.ds.pg.gda.pl/php/get_requests.php?search="+ URLEncoder.encode(search));
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
                JSONArray array = null;
                try {
                    array = json.getJSONArray("data");
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }
                for (int i = 0; i < array.length(); i++)
                {
                    JSONObject js= null;
                    try {
                        js = array.getJSONObject(i);
                    } catch (JSONException e1) {
                        e1.printStackTrace();
                    }
                    try {
                        FUNDACJE.add(js.getString("title"));
                    } catch (JSONException e1) {
                        e1.printStackTrace();
                    }
                    try {

                        ZAWARTOSC.add(js.getString("description")+System.getProperty("line.separator")+"Wiecej...");
                    } catch (JSONException e1) {
                        e1.printStackTrace();
                    }
                    try {
                        ID.add(js.getString("id"));
                    } catch (JSONException e1) {
                        e1.printStackTrace();
                    }
                }
            }
            else
            {
                FUNDACJE.add("Blad");
                try {
                    ZAWARTOSC.add(json.getString("info"));
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }
            }


            ListView listView=(ListView)findViewById(R.id.listView);
            Zlecenia.CustomAdapter customAdapter = new Zlecenia.CustomAdapter(FUNDACJE, ZAWARTOSC, ID);
            listView.setAdapter(customAdapter);
        }
    }
}


