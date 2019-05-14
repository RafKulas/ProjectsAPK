package com.example.wolontappka;

import android.content.Intent;
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

public class Rejestracja extends AppCompatActivity {

    private EditText Email;
    private EditText Password;
    private EditText RepeatPass;
    private Button Register;
    private TextView Blad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rejestracja);

        Email = (EditText) findViewById(R.id.newUserEmail);
        Password = (EditText) findViewById(R.id.newUserPass);
        RepeatPass = (EditText) findViewById(R.id.rptUserPass);
        Register = (Button) findViewById(R.id.btnRegister);
        Blad = (TextView) findViewById(R.id.errorPass);

        Register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!Password.getText().toString().equals(RepeatPass.getText().toString())) {
                    Blad.setText("Hasła są różne!");
                }
                else if ((Password.getText().toString().length()<6)) {
                    Blad.setText("Hasło jest krótsze niż 6 znaków!");
                }
                else {
                    String emailik, haslo;
                    emailik = Email.getText().toString();
                    haslo = Password.getText().toString();
                    CallApiR capi = new CallApiR();
                    capi.execute("http://linuch.ds.pg.gda.pl/php/registration.php", emailik, haslo);
                }
            }
        });

        Email.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                Blad.setText(" ");
            }
        });

        RepeatPass.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                Blad.setText(" ");
            }
        });

        Password.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                Blad.setText(" ");
            }
        });
    }


    private class CallApiR extends AsyncTask<String, String, String> {

        private String result;

        public CallApiR() {
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
            JSONObject objJson = null;
            try {
                objJson = new JSONObject(result);
                Blad.setText(objJson.getString("info"));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
                if (objJson.getString("status").equals("ok")){
                    Intent intent = new Intent(Rejestracja.this, Logowanie.class);
                    startActivity(intent);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected String doInBackground(String... params) {
            String urlString = params[0]; // URL to call
            String mail = params[1];
            String haslo = params[2];
            OutputStream out = null;
            String answer = " ";

            String paramek = "email=" + mail + "&password=" + haslo;

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
