package com.ugt.premiumvpn.activity;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.ugt.premiumvpn.R;

public class ProActivitySuccess extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_pro_success);
        Toolbar toolbar=findViewById(R.id.action_bar);
        toolbar.setTitle(R.string.upgrade_button);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        //CPAleadTrack.postInstall(this, "");

        Button but1=(Button)findViewById(R.id.home2);

        but1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent int1= new Intent(ProActivitySuccess.this, MainActivity.class);
                startActivity(int1);
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            startActivity(new Intent(this, MainActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        // Do Here what ever you want do on back press;
    }
}