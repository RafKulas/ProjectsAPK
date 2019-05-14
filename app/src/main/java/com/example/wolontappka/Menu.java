package com.example.wolontappka;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Menu extends AppCompatActivity {

    private Button zlecenia;
    private Button ranking;
    private Button logOut;

    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String TEXT="PHPSESSIONID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        zlecenia = (Button) findViewById(R.id.btnZlecenia);
        ranking = (Button) findViewById(R.id.btnRanking);
        logOut = (Button) findViewById(R.id.btnWyloguj);

        zlecenia.setOnClickListener(new View.OnClickListener() {
            @Override
            public  void onClick(View view) {
                Intent intent = new Intent(Menu.this, Zlecenia.class);
                startActivity(intent);

            }
        });
        ranking.setOnClickListener(new View.OnClickListener() {
            @Override
            public  void onClick(View view) {
                Intent intent = new Intent(Menu.this, Ranking.class);
                startActivity(intent);
            }
        });
        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public  void onClick(View view) {
                SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(TEXT, null);
                editor.apply();
                Intent intent = new Intent(Menu.this, Logowanie.class);
                startActivity(intent);
            }
        });
    }
}
