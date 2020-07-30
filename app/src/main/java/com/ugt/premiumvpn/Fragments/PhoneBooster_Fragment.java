package com.ugt.premiumvpn.Fragments;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ugt.premiumvpn.R;
import com.ugt.premiumvpn.service.BoostAlarm;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.hookedonplay.decoviewlib.DecoView;
import com.hookedonplay.decoviewlib.charts.SeriesItem;
import com.hookedonplay.decoviewlib.events.DecoEvent;
import com.shashank.sony.fancydialoglib.FancyAlertDialog;
import com.shashank.sony.fancydialoglib.FancyAlertDialogListener;
import com.shashank.sony.fancydialoglib.Icon;

import net.grandcentrix.tray.AppPreferences;

import java.io.RandomAccessFile;
import java.text.DecimalFormat;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.content.Context.ACTIVITY_SERVICE;
import static android.content.Context.ALARM_SERVICE;
import static android.content.Context.MODE_PRIVATE;

public class PhoneBooster_Fragment extends Fragment {

    int mb = 1024 * 1024;
    View view;
    DecoView arcView, arcView2;
    TextView scanning, centree, totalram, usedram, appused, appsfreed, processes, top, bottom, ramperct;
    LinearLayout scanlay, optimizelay;
    public static ImageView optimizebutton;
    TimerTask timer = null;
    TimerTask timer2 = null;
    int x, y;
    int counter = 0;
    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;
    InterstitialAd mInterstitialAd;

    private LinearLayout tools, myPage;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.phone_booster, container, false);

        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbarr);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setTitle(R.string.speed_booster);
        final Drawable upArrow = getResources().getDrawable(R.drawable.ic_arrow_back_black_24dp);
        upArrow.setColorFilter(getResources().getColor(android.R.color.white), PorterDuff.Mode.SRC_ATOP);
        actionBar.setHomeAsUpIndicator(upArrow);

        AdView mAdMobAdView = (AdView) view.findViewById(R.id.admob_adview);
        AdRequest adRequest = new AdRequest.Builder()
                .build();
        mAdMobAdView.loadAd(adRequest);
        checkshowint();
        arcView = (DecoView) view.findViewById(R.id.dynamicArcView2);
        arcView2 = (DecoView) view.findViewById(R.id.dynamicArcView3);

        arcView2.setVisibility(View.GONE);
        arcView.setVisibility(View.VISIBLE);

        scanning = (TextView) view.findViewById(R.id.scanning);
        scanlay = (LinearLayout) view.findViewById(R.id.scanlay);
        optimizelay = (LinearLayout) view.findViewById(R.id.optimizelay);
        optimizebutton = (ImageView) view.findViewById(R.id.optbutton);
        centree = (TextView) view.findViewById(R.id.centree);
        totalram = (TextView) view.findViewById(R.id.totalram);
        usedram = (TextView) view.findViewById(R.id.usedram);
        appsfreed = (TextView) view.findViewById(R.id.appsfreed);
        appused = (TextView) view.findViewById(R.id.appsused);
        processes = (TextView) view.findViewById(R.id.processes);
        top = (TextView) view.findViewById(R.id.top);
        bottom = (TextView) view.findViewById(R.id.bottom);
        ramperct = (TextView) view.findViewById(R.id.ramperct);
        sharedpreferences = getActivity().getSharedPreferences("pharid", Context.MODE_PRIVATE);

        mInterstitialAd = new InterstitialAd(getActivity().getApplicationContext());
        AppPreferences preferences = new AppPreferences(getActivity());
        if(preferences.getBoolean("admob",false)){
            mInterstitialAd.setAdUnitId(getResources().getString(R.string.interstitial_ad_unit));
            AdRequest adRequestInter = new AdRequest.Builder().build();
            mInterstitialAd.setAdListener(new AdListener() {
                @Override
                public void onAdLoaded() {

                }
            });
            mInterstitialAd.loadAd(adRequestInter);
        }


        try {
            Random ran3 = new Random();
            ramperct.setText(ran3.nextInt(60) + 40 + "%");


            optimizebutton.setBackgroundResource(0);
            optimizebutton.setImageResource(0);
            optimizebutton.setImageResource(R.drawable.clear_btn);

            if (sharedpreferences.getString("booster", "1").equals("0")) {
                optimizebutton.setImageResource(0);
                optimizebutton.setImageResource(R.drawable.clear_btn);

                centree.setText(sharedpreferences.getString("value", "50MB"));

            }

            start();

            optimizebutton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (sharedpreferences.getString("booster", "1").equals("1")) {
                        optimize();

                        editor = sharedpreferences.edit();
                        editor.putString("booster", "0");
                        editor.commit();

                        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("APPS_CONFIGS", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putLong("BOOSTER_LAST_UPDATE", System.currentTimeMillis());
                        editor.commit();

                        Intent intent = new Intent(getActivity(), BoostAlarm.class);

                        PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(), 0,
                                intent, PendingIntent.FLAG_ONE_SHOT);

                        AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(ALARM_SERVICE);
                        alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + (100 * 1000), pendingIntent);
                    } else {



                        @SuppressLint("RestrictedApi") LayoutInflater inflater = getLayoutInflater(getArguments());
                        View layout = inflater.inflate(R.layout.my_toast, null);



                        TextView text = (TextView) layout.findViewById(R.id.textView1);
                        text.setText("Phone Is Aleady Optimized");

                        Toast toast = new Toast(getActivity());
                        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 70);
                        toast.setDuration(Toast.LENGTH_LONG);
                        toast.setView(layout);
                        toast.show();

                    }


                }
            });





        } catch (Exception e) {

        }

        return view;
    }


    public void optimize() {

        arcView2.setVisibility(View.VISIBLE);
        arcView.setVisibility(View.GONE);








        arcView.addSeries(new SeriesItem.Builder(Color.argb(255, 218, 218, 218))
                .setRange(0, 100, 0)
                .setInterpolator(new AccelerateInterpolator())
                .build());








        SeriesItem seriesItem1 = new SeriesItem.Builder(Color.parseColor("#00000000"))
                .setRange(0, 100, 0)
                .setLineWidth(32f)
                .build();

        SeriesItem seriesItem2 = new SeriesItem.Builder(Color.parseColor("#ffffff"))  
                .setRange(0, 100, 0)
                .setLineWidth(32f)
                .build();


        int series1Index2 = arcView2.addSeries(seriesItem2);

        arcView2.addEvent(new DecoEvent.Builder(DecoEvent.EventType.EVENT_SHOW, true)
                .setDelay(500)
                .setDuration(2000)
                .setListener(new DecoEvent.ExecuteEventListener() {
                    @Override
                    public void onEventStart(DecoEvent decoEvent) {
                        bottom.setText("");
                        top.setText("");
                        centree.setText("Optimizing...");
                    }

                    @Override
                    public void onEventEnd(DecoEvent decoEvent) {

                    }
                })
                .build());

        arcView2.addEvent(new DecoEvent.Builder(25).setIndex(series1Index2).setDelay(2000).setListener(new DecoEvent.ExecuteEventListener() {
            @Override
            public void onEventStart(DecoEvent decoEvent) {
                bottom.setText("");
                top.setText("");
                centree.setText("Optimizing...");
            }

            @Override
            public void onEventEnd(DecoEvent decoEvent) {
                AppPreferences preferences = new AppPreferences(getActivity());
                if(preferences.getBoolean("admob",false)) mInterstitialAd.show();

                bottom.setText("Found");
                top.setText("Storage");
                Random ran3 = new Random();
                ramperct.setText(ran3.nextInt(40) + 20 + "%");
            }
        }).build());

        ImageView img_animation = (ImageView) view.findViewById(R.id.waves);

        TranslateAnimation animation = new TranslateAnimation(0.0f, 1000.0f, 0.0f, 0.0f);          
        animation.setDuration(5000);  
        animation.setRepeatCount(0);
        animation.setInterpolator(new LinearInterpolator());

        animation.setFillAfter(true);

        img_animation.startAnimation(animation);

        int counter = 0;
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

                scanlay.setVisibility(View.VISIBLE);
                optimizelay.setVisibility(View.GONE);
                scanning.setText("SCANNING...");
                killall();
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                scanlay.setVisibility(View.GONE);
                optimizelay.setVisibility(View.VISIBLE);

                optimizebutton.setImageResource(R.drawable.clear_btn);


                Random ran = new Random();
                x = ran.nextInt(100) + 30;

                Random ran2 = new Random();
                int proc = ran2.nextInt(10) + 5;

                centree.setText(getUsedMemorySize() - x + " MB");

                editor = sharedpreferences.edit();
                editor.putString("value", getUsedMemorySize() - x + " MB");
                editor.commit();

                Log.e("used mem", getUsedMemorySize() + " MB");
                Log.e("used mem", getTotalRAM());

                totalram.setText(getTotalRAM());
                usedram.setText(getUsedMemorySize() - x + " MB/ ");

                appsfreed.setText(getTotalRAM());
                appused.setText(Math.abs(getUsedMemorySize() - x - 30) + " MB/ ");

                processes.setText(y - proc + "");

















































            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }


    public void start() {


        final Timer t = new Timer();
        timer = new TimerTask() {

            @Override
            public void run() {

                try {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {


                            counter++;
                            centree.setText(counter + "MB");
                        }
                    });
                } catch (Exception e) {

                }


            }

        };
        t.schedule(timer, 30, 30);


        Random ran2 = new Random();
        final int proc = ran2.nextInt(60) + 30;


        arcView.addSeries(new SeriesItem.Builder(Color.argb(255, 218, 218, 218))
                .setRange(0, 100, 0)
                .setInterpolator(new AccelerateInterpolator())
                .build());








        SeriesItem seriesItem1 = new SeriesItem.Builder(Color.parseColor("#00000000"))
                .setRange(0, 100, 0)
                .setLineWidth(32f)
                .build();

        SeriesItem seriesItem2 = new SeriesItem.Builder(Color.parseColor("#ffffff"))
                .setRange(0, 100, 0)
                .setLineWidth(32f)
                .build();




        int series1Index2 = arcView.addSeries(seriesItem2);

        arcView.addEvent(new DecoEvent.Builder(DecoEvent.EventType.EVENT_SHOW, true)
                .setDelay(0)
                .setDuration(600)
                .build());


        arcView.addEvent(new DecoEvent.Builder(proc).setIndex(series1Index2).setDelay(2000).setListener(new DecoEvent.ExecuteEventListener() {
            @Override
            public void onEventStart(DecoEvent decoEvent) {


            }

            @Override
            public void onEventEnd(DecoEvent decoEvent) {

                t.cancel();
                timer.cancel();
                t.purge();


                centree.setText(getUsedMemorySize() + " MB");

                if (sharedpreferences.getString("booster", "1").equals("0")) {

                    centree.setText(sharedpreferences.getString("value", "50MB"));
                }


                final Timer t = new Timer();
                final Timer t2 = new Timer();


                try {

                    timer2 = new TimerTask() {

                        @Override
                        public void run() {

                            try {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {


                                        centree.setText(getUsedMemorySize() + " MB");

                                        if (sharedpreferences.getString("booster", "1").equals("0")) {

                                            centree.setText(sharedpreferences.getString("value", "50MB"));
                                        }

                                        t2.cancel();
                                        timer2.cancel();
                                        t2.purge();
                                    }
                                });
                            } catch (Exception e) {

                            }

                        }

                    };

                } catch (Exception e) {

                }

                t2.schedule(timer2, 100, 100);


            }
        }).build());

        Log.e("used mem", getUsedMemorySize() + " MB");
        Log.e("used mem", getTotalRAM());

        totalram.setText(getTotalRAM());
        usedram.setText(getUsedMemorySize() + " MB/ ");
        appsfreed.setText(getTotalRAM());
        appused.setText(getUsedMemorySize() - x - 30 + " MB/ ");

        Random ran = new Random();
        y = ran.nextInt(50) + 15;

        processes.setText(y + "");







    }


    public String getTotalRAM() {

        RandomAccessFile reader = null;
        String load = null;
        DecimalFormat twoDecimalForm = new DecimalFormat("#.##");
        double totRam = 0;
        String lastValue = "";
        try {
            try {
                reader = new RandomAccessFile("/proc/meminfo", "r");
                load = reader.readLine();
            } catch (Exception e) {

            }

            
            Pattern p = Pattern.compile("(\\d+)");
            Matcher m = p.matcher(load);
            String value = "";
            while (m.find()) {
                value = m.group(1);
                
            }
            try {
                reader.close();
            } catch (Exception e) {

            }

            totRam = Double.parseDouble(value);
            

            double mb = totRam / 1024.0;
            double gb = totRam / 1048576.0;
            double tb = totRam / 1073741824.0;

            if (tb > 1) {
                lastValue = twoDecimalForm.format(tb).concat(" TB");
            } else if (gb > 1) {
                lastValue = twoDecimalForm.format(gb).concat(" GB");
            } else if (mb > 1) {
                lastValue = twoDecimalForm.format(mb).concat(" MB");
            } else {
                lastValue = twoDecimalForm.format(totRam).concat(" KB");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            
        }

        return lastValue;
    }


    public long getUsedMemorySize() {

        try {
            ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
            ActivityManager activityManager = (ActivityManager) getActivity().getSystemService(ACTIVITY_SERVICE);
            activityManager.getMemoryInfo(mi);
            long availableMegs = mi.availMem / 1048576L;

            return availableMegs;
        } catch (Exception e) {
            return 200;
        }

    }

    public void killall() {













    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);


        if (isVisibleToUser) {
            
        } else {

        }
    }


    public void checkshowint()
    {
        boolean strPref = false;
        SharedPreferences shf = getActivity().getSharedPreferences("config", MODE_PRIVATE);
        strPref = shf.getBoolean("speedint", false);

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
                .setTitle(getActivity().getResources().getString(R.string.speed_booster))
                .setBackgroundColor(Color.parseColor("#0c7944"))  
                .setMessage(getActivity().getResources().getString(R.string.speedtxt))
                .setPositiveBtnBackground(Color.parseColor("#FF4081"))  
                .setPositiveBtnText("Ok")
                .setNegativeBtnBackground(Color.parseColor("#FFA9A7A8"))  
                .setNegativeBtnText("Cancel")
                .setAnimation(com.shashank.sony.fancydialoglib.Animation.POP)
                .isCancellable(false)
                .setIcon(R.drawable.booster, Icon.Visible)
                .OnPositiveClicked(new FancyAlertDialogListener() {
                    @Override
                    public void OnClick() {
                        SharedPreferences saveint = getActivity().getSharedPreferences("config", MODE_PRIVATE);
                        saveint.edit().putBoolean("speedint", true).apply();

                    }
                })
                .OnNegativeClicked(new FancyAlertDialogListener() {
                    @Override
                    public void OnClick() {
                        SharedPreferences saveint = getActivity().getSharedPreferences("config", MODE_PRIVATE);
                        saveint.edit().putBoolean("speedint", true).apply();

                    }
                })

                .build();
    }


}