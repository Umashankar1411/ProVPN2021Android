package com.ugt.premiumvpn.activity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.widget.Toast;

import com.ugt.premiumvpn.Fragments.BatterySaver_Fragment;
import com.ugt.premiumvpn.Fragments.CPUCooler_Fragment;
import com.ugt.premiumvpn.Fragments.PhoneBooster_Fragment;
import com.ugt.premiumvpn.R;

public class FragmentWrapperActivity extends AppCompatActivity {

    public final static String REQUEST_ACTIVITY_CODE = "CODE_ACTIVITY";
    public final static String RUNTIME_MODE = "RUNTIME_MODE";

    public final static String JUNK_CLEANER_CODE = "JUNKCLEANER",
            BOOSTER_CODE = "PHONEBOOSTER",
            COOLER_CODE = "COOLER",
            BATTERY_SAVER_CODE = "BATTERYSAVER",
            NOTIFICATIONS_CLEANER_CODE = "NOTIFICATIONSCLEANER";

    private String requestStateCode;

    public static boolean notWaitJustRedirect = false;
    private boolean isExitingFromApp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment_wrapper);







        try {
            Intent intent = getIntent ();

            requestStateCode = intent.getStringExtra(REQUEST_ACTIVITY_CODE);
        } catch (Exception e) {

        }

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

        switch (requestStateCode) {

            case BOOSTER_CODE:
                PhoneBooster_Fragment speedBooster_fragment = new PhoneBooster_Fragment();
                ft.replace(R.id.wrapper, speedBooster_fragment);
                ft.commit();
                break;

            case COOLER_CODE:
                CPUCooler_Fragment cpuCooler_fragment = new CPUCooler_Fragment();
                ft.replace(R.id.wrapper, cpuCooler_fragment);
                ft.commit();
                break;

            case BATTERY_SAVER_CODE:
                BatterySaver_Fragment batterySaver_fragment = new BatterySaver_Fragment();
                ft.replace(R.id.wrapper, batterySaver_fragment);
                ft.commit();
                break;
            default:
                Toast.makeText(this, "Wrong app query, try now!", Toast.LENGTH_SHORT).show();
                finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (isExitingFromApp) {












            startActivity (new Intent (getApplicationContext (), MainActivity.class));

            finish();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
                if (requestStateCode.equals(NOTIFICATIONS_CLEANER_CODE)) {
                    notWaitJustRedirect = true;
                    redirectToSecureSettings();
                }
                break;
        }

        return super.onTouchEvent(event);
    }

    public void redirectToSecureSettings() {
        Toast.makeText(getApplicationContext(), "You should allow this app to use notification control before using",
                Toast.LENGTH_LONG).show();

        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:


                finish ();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }








    public static long TIME_INTERVAL_FOR_EXIT = 1500;
    private long lastTimeBackPressed;

    @Override
    public boolean onKeyDown(final int pKeyCode, final KeyEvent pEvent) {
        if(pKeyCode == KeyEvent.KEYCODE_BACK && pEvent.getAction() == KeyEvent.ACTION_DOWN) {





            Intent startHomescreen = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(startHomescreen);

            finish();

            return true;
        } else {
            return super.onKeyDown(pKeyCode, pEvent);
        }
    }
}
