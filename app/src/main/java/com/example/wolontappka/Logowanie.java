package com.example.wolontappka;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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


public class Logowanie extends AppCompatActivity {

    private EditText Email;
    private EditText Password;
    private Button Login;
    private TextView Register;
    private TextView Error;
    SharedPreferences sp;

    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String TEXT="PHPSESSIONID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logowanie);

        Error = (TextView) findViewById(R.id.error);
        Email = (EditText) findViewById(R.id.email);
        Password = (EditText) findViewById(R.id.pass);
        Login = (Button) findViewById(R.id.btnLogin);
        Register = (TextView) findViewById(R.id.rejestracja);

        Register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Logowanie.this, Rejestracja.class);
                startActivity(intent);
            }
        });

        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public  void onClick(View view) {
                String emailik, haslo;
                emailik = Email.getText().toString();
                haslo = Password.getText().toString();

                CallApi capi = new CallApi();
                capi.execute("http://linuch.ds.pg.gda.pl/php/login.php", emailik, haslo);
            }
        });

        Email.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {}

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                Error.setText(" ");
            }
        });

        Password.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {}

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                Error.setText(" ");
            }
        });
    }

    public void saveData(String value) {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(TEXT, value);
        editor.apply();
    }

    private class CallApi extends AsyncTask<String, String, String> {

        private String result;

        public CallApi(){
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
            try {
                JSONObject objJson = new JSONObject(result);
                Error.setText(objJson.getString("info"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
            String idSession = sharedPreferences.getString(TEXT, null);
            if(idSession!=null) {
                Intent intent = new Intent(Logowanie.this, Menu.class);
                startActivity(intent);
            }
        }

        @Override
        protected String doInBackground(String... params) {
            String urlString = params[0]; // URL to call
            String mail = params[1];
            String haslo = params[2];
            OutputStream out = null;
            String answer = " ";

            String paramek = "email="+mail+"&password="+haslo;

            try {
                URL url = new URL(urlString);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-type", "application/x-www-form-urlencoded");
                out = new BufferedOutputStream(urlConnection.getOutputStream());

                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, "UTF-8"));
                writer.write(paramek);
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
                System.out.println(e.getMessage());
            }

            try {
                JSONObject objJson = new JSONObject(answer);
                if(objJson.getString("status").equals("ok")) {
                    String sessionID = objJson.getJSONObject("data").getString("PHPSESSID");
                    saveData(sessionID);
                };
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return answer;
        }
    }

}
