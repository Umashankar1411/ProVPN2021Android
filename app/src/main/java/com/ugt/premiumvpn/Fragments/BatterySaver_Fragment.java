package com.ugt.premiumvpn.Fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.BatteryManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ugt.premiumvpn.R;
import com.ugt.premiumvpn.activity.NormalMode;
import com.ugt.premiumvpn.activity.PopUp_SavingPower;
import com.ugt.premiumvpn.activity.UPopUp;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.shashank.sony.fancydialoglib.Animation;
import com.shashank.sony.fancydialoglib.FancyAlertDialog;
import com.shashank.sony.fancydialoglib.FancyAlertDialogListener;
import com.shashank.sony.fancydialoglib.Icon;

import me.itangqi.waveloadingview.WaveLoadingView;

import static android.content.Context.MODE_PRIVATE;

public class BatterySaver_Fragment extends Fragment {

    View view;
    WaveLoadingView mWaveLoadingView;
    ImageView powersaving, ultrasaving, normal;
    TextView hourn, minutes, hourp, minutep, houru, minutesu, hourmain, minutesmain;
    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;

    private LinearLayout tools, myPage;

    private BroadcastReceiver mBatInfoReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context ctxt, Intent intent) {
            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);

            mWaveLoadingView.setProgressValue(level);

            mWaveLoadingView.setCenterTitle(level + "%");

            if (level <= 5) {
                hourn.setText(0 + "");
                minutes.setText(15 + "");

                hourp.setText(2 + "");
                minutep.setText(25 + "");

                houru.setText(3 + "");
                minutesu.setText(55 + "");

                if (sharedpreferences.getString("mode", "0").equals("0")) {
                    hourmain.setText(0 + "");
                    minutesmain.setText(15 + "");
                }

                if (sharedpreferences.getString("mode", "0").equals("1")) {
                    hourmain.setText(2 + "");
                    minutesmain.setText(25 + "");
                }
            }
            if (level > 5 && level <= 10) {
                hourn.setText(0 + "");
                minutes.setText(30 + "");

                hourp.setText(3 + "");
                minutep.setText(5 + "");

                houru.setText(6 + "");
                minutesu.setText(0 + "");

                if (sharedpreferences.getString("mode", "0").equals("0")) {
                    hourmain.setText(0 + "");
                    minutesmain.setText(30 + "");
                }

                if (sharedpreferences.getString("mode", "0").equals("1")) {
                    hourmain.setText(3 + "");
                    minutesmain.setText(5 + "");
                }
            }
            if (level > 10 && level <= 15) {
                hourn.setText(0 + "");
                minutes.setText(45 + "");

                hourp.setText(3 + "");
                minutep.setText(50 + "");

                houru.setText(8 + "");
                minutesu.setText(25 + "");

                if (sharedpreferences.getString("mode", "0").equals("0")) {
                    hourmain.setText(0 + "");
                    minutesmain.setText(45 + "");
                }

                if (sharedpreferences.getString("mode", "0").equals("1")) {
                    hourmain.setText(3 + "");
                    minutesmain.setText(50 + "");
                }
            }
            if (level > 15 && level <= 25) {
                hourn.setText(1 + "");
                minutes.setText(30 + "");

                hourp.setText(4 + "");
                minutep.setText(45 + "");

                houru.setText(12 + "");
                minutesu.setText(55 + "");

                if (sharedpreferences.getString("mode", "0").equals("0")) {
                    hourmain.setText(1 + "");
                    minutesmain.setText(30 + "");
                }

                if (sharedpreferences.getString("mode", "0").equals("1")) {
                    hourmain.setText(4 + "");
                    minutesmain.setText(45 + "");
                }
            }
            if (level > 25 && level <= 35) {
                hourn.setText(2 + "");
                minutes.setText(20 + "");

                hourp.setText(6 + "");
                minutep.setText(2 + "");

                houru.setText(19 + "");
                minutesu.setText(2 + "");

                if (sharedpreferences.getString("mode", "0").equals("0")) {
                    hourmain.setText(2 + "");
                    minutesmain.setText(20 + "");
                }

                if (sharedpreferences.getString("mode", "0").equals("1")) {
                    hourmain.setText(6 + "");
                    minutesmain.setText(2 + "");
                }
            }
            if (level > 35 && level <= 50) {
                hourn.setText(5 + "");
                minutes.setText(20 + "");

                hourp.setText(9 + "");
                minutep.setText(25 + "");

                houru.setText(22 + "");
                minutesu.setText(0 + "");

                if (sharedpreferences.getString("mode", "0").equals("0")) {
                    hourmain.setText(5 + "");
                    minutesmain.setText(20 + "");
                }

                if (sharedpreferences.getString("mode", "0").equals("1")) {
                    hourmain.setText(9 + "");
                    minutesmain.setText(20 + "");
                }
            }
            if (level > 50 && level <= 65) {
                hourn.setText(7 + "");
                minutes.setText(30 + "");

                hourp.setText(11 + "");
                minutep.setText(1 + "");

                houru.setText(28 + "");
                minutesu.setText(15 + "");

                mWaveLoadingView.setCenterTitleColor(R.color.primary_white_text);

                if (sharedpreferences.getString("mode", "0").equals("0")) {
                    hourmain.setText(7 + "");
                    minutesmain.setText(30 + "");
                }

                if (sharedpreferences.getString("mode", "0").equals("1")) {
                    hourmain.setText(11 + "");
                    minutesmain.setText(1 + "");
                }
            }
            if (level > 65 && level <= 75) {
                hourn.setText(9 + "");
                minutes.setText(10 + "");

                hourp.setText(14 + "");
                minutep.setText(25 + "");

                houru.setText(30 + "");
                minutesu.setText(55 + "");
                mWaveLoadingView.setCenterTitleColor(R.color.primary_white_text);

                if (sharedpreferences.getString("mode", "0").equals("0")) {
                    hourmain.setText(9 + "");
                    minutesmain.setText(10 + "");
                }

                if (sharedpreferences.getString("mode", "0").equals("1")) {
                    hourmain.setText(14 + "");
                    minutesmain.setText(25 + "");
                }
            }
            if (level > 75 && level <= 85) {
                hourn.setText(14 + "");
                minutes.setText(15 + "");

                hourp.setText(17 + "");
                minutep.setText(10 + "");

                houru.setText(38 + "");
                minutesu.setText(5 + "");
                mWaveLoadingView.setCenterTitleColor(R.color.primary_white_text);

                if (sharedpreferences.getString("mode", "0").equals("0")) {
                    hourmain.setText(14 + "");
                    minutesmain.setText(15 + "");
                }

                if (sharedpreferences.getString("mode", "0").equals("1")) {
                    hourmain.setText(17 + "");
                    minutesmain.setText(10 + "");
                }
            }
            if (level > 85 && level <= 100) {
                hourn.setText(20 + "");
                minutes.setText(45 + "");

                hourp.setText(30 + "");
                minutep.setText(0 + "");

                houru.setText(60 + "");
                minutesu.setText(55 + "");
                mWaveLoadingView.setCenterTitleColor(R.color.primary_white_text);

                if (sharedpreferences.getString("mode", "0").equals("0")) {
                    hourmain.setText(20 + "");
                    minutesmain.setText(45 + "");
                }

                if (sharedpreferences.getString("mode", "0").equals("1")) {
                    hourmain.setText(30 + "");
                    minutesmain.setText(0 + "");
                }
            }

        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.battery_saver, container, false);

        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbarr);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setTitle(R.string.battery_saver);
        final Drawable upArrow = getResources().getDrawable(R.drawable.ic_arrow_back_black_24dp);
        upArrow.setColorFilter(getResources().getColor(android.R.color.white), PorterDuff.Mode.SRC_ATOP);
        actionBar.setHomeAsUpIndicator(upArrow);

        AdView mAdMobAdView = (AdView) view.findViewById(R.id.admob_adview);
        AdRequest adRequest = new AdRequest.Builder()
                .build();
        mAdMobAdView.loadAd(adRequest);
        checkshowint();
        mWaveLoadingView = (WaveLoadingView) view.findViewById(R.id.waveView);
        powersaving = (ImageView) view.findViewById(R.id.powersaving);
        ultrasaving = (ImageView) view.findViewById(R.id.ultra);
        normal = (ImageView) view.findViewById(R.id.normal);
        hourn = (TextView) view.findViewById(R.id.hourn);
        minutes = (TextView) view.findViewById(R.id.minutes);
        hourp = (TextView) view.findViewById(R.id.hourp);
        minutep = (TextView) view.findViewById(R.id.minutesp);
        houru = (TextView) view.findViewById(R.id.houru);
        minutesu = (TextView) view.findViewById(R.id.minutesu);
        hourmain = (TextView) view.findViewById(R.id.hourmain);
        minutesmain = (TextView) view.findViewById(R.id.minutesmain);
        sharedpreferences = getActivity().getSharedPreferences("was", MODE_PRIVATE);
        getActivity().registerReceiver(this.mBatInfoReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));


        try {
            powersaving.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(getActivity(), PopUp_SavingPower.class);
                    i.putExtra("hour", hourp.getText());
                    i.putExtra("minutes", minutep.getText());
                    i.putExtra("minutesnormal", minutes.getText());
                    i.putExtra("hournormal", hourn.getText());
                    startActivity(i);
                }
            });

            ultrasaving.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(getActivity(), UPopUp.class);
                    i.putExtra("hour", houru.getText());
                    i.putExtra("minutes", minutesu.getText());
                    i.putExtra("minutesnormal", minutes.getText());
                    i.putExtra("hournormal", hourn.getText());
                    startActivity(i);
                }
            });

            normal.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(getActivity(), NormalMode.class);
                    startActivity(i);
                }
            });


            mWaveLoadingView.setShapeType(WaveLoadingView.ShapeType.CIRCLE);

            mWaveLoadingView.setCenterTitleColor(Color.parseColor("#136af6"));
            mWaveLoadingView.setBottomTitleColor(Color.parseColor("#FFFFFF"));

            mWaveLoadingView.setAmplitudeRatio(30);
            mWaveLoadingView.setWaveColor(Color.parseColor("#136af6"));


            mWaveLoadingView.setTopTitleStrokeWidth(3);
            mWaveLoadingView.setAnimDuration(3000);




            mWaveLoadingView.startAnimation();


        } catch (Exception e) {

        }

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        getActivity().registerReceiver(this.mBatInfoReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
    }

    @Override
    public void onStop() {
        super.onStop();
        try {
            getActivity().unregisterReceiver(mBatInfoReceiver);
        }
        catch(Exception e) {}
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (isVisibleToUser) {
         
        } else {}
    }


    public void checkshowint()
    {
        boolean strPref = false;
        SharedPreferences shf = getActivity().getSharedPreferences("config", MODE_PRIVATE);
        strPref = shf.getBoolean("batterysaverint", false);

        if(strPref)
        {

        }
        else {
            showint();
        }
    }
    public void showint()
    {
        new FancyAlertDialog.Builder(getActivity())
                .setTitle(getActivity().getResources().getString(R.string.battery_saver))
                .setBackgroundColor(Color.parseColor("#0c7944"))  
                .setMessage(getActivity().getResources().getString(R.string.batterysavertxt))
                .setPositiveBtnBackground(Color.parseColor("#FF4081"))  
                .setPositiveBtnText("Ok")
                .setNegativeBtnBackground(Color.parseColor("#FFA9A7A8"))  
                .setNegativeBtnText("Cancel")
                .setAnimation(Animation.POP)
                .isCancellable(false)
                .setIcon(R.drawable.batterysaver, Icon.Visible)
                .OnPositiveClicked(new FancyAlertDialogListener() {
                    @Override
                    public void OnClick() {
                        SharedPreferences saveint = getActivity().getSharedPreferences("config", MODE_PRIVATE);
                        saveint.edit().putBoolean("batterysaverint", true).apply();

                    }
                })
                .OnNegativeClicked(new FancyAlertDialogListener() {
                    @Override
                    public void OnClick() {
                        SharedPreferences saveint = getActivity().getSharedPreferences("config", MODE_PRIVATE);
                        saveint.edit().putBoolean("batterysaverint", true).apply();

                    }
                })

                .build();
    }

}
