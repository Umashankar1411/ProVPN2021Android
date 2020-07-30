package com.ugt.premiumvpn.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.ugt.premiumvpn.R;
import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;

import static com.ugt.premiumvpn.Constant.LICENSE_KEY;
import static com.ugt.premiumvpn.Constant.PRODUCT_ID;
import static com.ugt.premiumvpn.Constant.UpgradePro;

public class ProActivity extends AppCompatActivity implements BillingProcessor.IBillingHandler, View.OnClickListener {

    private SharedPreferences saveVerified;
    private BillingProcessor bp;
    private boolean readyToPurchase = false;

    private Button btnPurchase;
    private final String PURCHASE_ID = PRODUCT_ID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_pro);
        Toolbar toolbar = findViewById(R.id.action_bar);
        toolbar.setTitle(R.string.upgrade_button);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        bp = BillingProcessor.newBillingProcessor(this, LICENSE_KEY, this); // doesn't bind
        bp.initialize(); // binds

        initView();
    }

    private void initView() {
        btnPurchase = (Button) findViewById(R.id.subscribeButton);

        btnPurchase.setEnabled(false);

        btnPurchase.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.subscribeButton:
                if (readyToPurchase) {
                    bp.subscribe(this, PURCHASE_ID);
                } else {
                    Toast.makeText(this, "Unable to initiate purchase", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        updatePurchase();
    }

    private void updatePurchase() {
        if (bp.isSubscribed(PURCHASE_ID)) {
            saveVerified = getSharedPreferences("config", MODE_PRIVATE);
            saveVerified.edit().putBoolean(UpgradePro, true).apply();
            Toast.makeText(this, "You're already a member !", Toast.LENGTH_SHORT).show();
            startproActivityy();
        }
    }

    private void updatePurchases() {
        if (bp.isSubscribed(PURCHASE_ID)) {
            saveVerified = getSharedPreferences("config", MODE_PRIVATE);
            saveVerified.edit().putBoolean(UpgradePro, true).apply();
            Toast.makeText(this, "You're already a member !", Toast.LENGTH_SHORT).show();
            startproActivityy();
        }
    }
    @Override
    public void onBillingInitialized() {
        readyToPurchase = true;
        btnPurchase.setEnabled(true);
        updatePurchase();
    }

    @Override
    public void onProductPurchased(String productId, TransactionDetails details) {
        Toast.makeText(this, "Thanks for your Purchased!", Toast.LENGTH_SHORT).show();
        saveVerified = getSharedPreferences("config", MODE_PRIVATE);
        saveVerified.edit().putBoolean(UpgradePro, true).apply();
        startproActivity();
        //updatePurchase();
    }

    @Override
    public void onBillingError(int errorCode, Throwable error) {
        Toast.makeText(this, "Unable to process billing", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPurchaseHistoryRestored() {
        updatePurchases();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!bp.handleActivityResult(requestCode, resultCode, data))
            super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onDestroy() {
        if (bp != null)
            bp.release();

        super.onDestroy();
    }


    private void startproActivity() {
        startActivity(new Intent(this, ProActivitySuccess.class));
        finish();
    }

    private void startproActivityy() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


}

