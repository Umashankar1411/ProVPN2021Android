package com.ugt.premiumvpn.Fragments;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ugt.premiumvpn.R;
import com.ugt.premiumvpn.activity.MainActivity;
import com.ugt.premiumvpn.activity.ScannerCPU;
import com.ugt.premiumvpn.adapter.RecyclerAdapter;
import com.ugt.premiumvpn.model.Apps;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.recyclerview.animators.SlideInLeftAnimator;
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator;

public class CPUCooler_Fragment extends Fragment {

    TextView batterytemp, showmain, showsec, nooverheating;
    float temp;
    ImageView coolbutton, tempimg,ivtemping;
    RecyclerView recyclerView;
    RecyclerAdapter mAdapter;
    public static List<Apps> apps;
    List<Apps> apps2;
    int check = 0;

    private LinearLayout tools, myPage;

    BroadcastReceiver batteryReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            makeStabilityScanning(intent);
        }
    };

    View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.cpu_cooler, container, false);

        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbarr);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setTitle(R.string.cpu_cooler);
        final Drawable upArrow = getResources().getDrawable(R.drawable.ic_arrow_back_black_24dp);
        upArrow.setColorFilter(getResources().getColor(android.R.color.white), PorterDuff.Mode.SRC_ATOP);
        actionBar.setHomeAsUpIndicator(upArrow);

        AdView mAdMobAdView = (AdView) view.findViewById(R.id.admob_adview);
        AdRequest adRequest = new AdRequest.Builder()
                .build();
        mAdMobAdView.loadAd(adRequest);

        
        try {
            recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);

            ivtemping= (ImageView) view.findViewById(R.id.iv_tempimg);
            tempimg = (ImageView) view.findViewById(R.id.tempimg);
            showmain = (TextView) view.findViewById(R.id.showmain);
            showsec = (TextView) view.findViewById(R.id.showsec);
            coolbutton = (ImageView) view.findViewById(R.id.coolbutton);
            nooverheating = (TextView) view.findViewById(R.id.nooverheating);



            coolbutton.setImageResource(R.drawable.clear_btn);
            ivtemping.setImageResource(R.drawable.ic_after_cooling_icon);
            tempimg.setImageResource(R.drawable.ic_ultra_power_mode_rounded_bg);
            showmain.setText("NORMAL");
            showmain.setTextColor(Color.parseColor("#39c900"));
            showsec.setText("CPU Temperature is Good");
            showsec.setTextColor(Color.parseColor("#4e5457"));
            nooverheating.setText("Currently No App Causing Overheating");
            nooverheating.setTextColor(Color.parseColor("#4e5457"));

            coolbutton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    @SuppressLint("RestrictedApi") LayoutInflater inflater = getLayoutInflater(getArguments());
                    View layout = inflater.inflate(R.layout.my_toast, null);

                    ImageView image = (ImageView) layout.findViewById(R.id.image);

                    TextView text = (TextView) layout.findViewById(R.id.textView1);
                    text.setText("CPU Temperature is Already Normal.");

                    Toast toast = new Toast(getActivity());
                    toast.setGravity(Gravity.CENTER_VERTICAL, 0, 70);
                    toast.setDuration(Toast.LENGTH_LONG);
                    toast.setView(layout);
                    toast.show();
                }
            });


            batterytemp = (TextView) view.findViewById(R.id.batterytemp);

            if (!((System.currentTimeMillis() - getActivity().getSharedPreferences("APPS_CONFIGS", Context.MODE_PRIVATE).getLong("COOLER_LAST_UPDATE", 0)) < 1200000)) {
                makeStabilityScanning(null);
            }

            Log.e("Temperrature", temp + "");
        } catch (Exception e) {

        }

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {

            getActivity().unregisterReceiver(batteryReceiver);
        } catch (Exception e) {

        }
    }

    public void getAllICONS() {

        PackageManager pm = getActivity().getPackageManager();

        List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);


        if (packages != null) {
            for (int k = 0; k < packages.size(); k++) {
                
                String packageName = packages.get(k).packageName;
                Log.e("packageName-->", "" + packageName);

                if (!packageName.equals("fast.cleaner.battery.saver")) {



                    Drawable ico = null;
                    try {
                        String pName = (String) pm.getApplicationLabel(pm.getApplicationInfo(packageName, PackageManager.GET_META_DATA));
                        Apps app = new Apps();



                        File file = new File(pm.getApplicationInfo(packageName, PackageManager.GET_META_DATA).publicSourceDir);
                        long size = file.length();

                        Log.e("SIZE", size / 1000000 + "");
                        app.setSize(size / 1000000 + 20 + "MB");

                        ApplicationInfo a = pm.getApplicationInfo(packageName, PackageManager.GET_META_DATA);
                        app.setImage(ico = getActivity().getPackageManager().getApplicationIcon(packages.get(k).packageName));
                        getActivity().getPackageManager();
                        Log.e("ico-->", "" + ico);

                        if (((a.flags & ApplicationInfo.FLAG_SYSTEM) == 0)) {


                            if (check <= 5) {
                                check++;
                                apps.add(app);
                            } else {
                                getActivity().unregisterReceiver(batteryReceiver);

                                break;
                            }

                        }
                        mAdapter.notifyDataSetChanged();


                    } catch (PackageManager.NameNotFoundException e) {
                        Log.e("ERROR", "Unable to find icon for package '"
                                + packageName + "': " + e.getMessage());
                    }
                    
                }
            }

        }

        if (apps.size() > 1) {
            mAdapter = new RecyclerAdapter(apps);
            mAdapter.notifyDataSetChanged();
        }
    }

    private void makeStabilityScanning (Intent intent) {
        try {
            if (intent == null)
                intent = getActivity().registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));

            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
            temp = ((float) intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 21)) / 10;

            batterytemp.setText(temp + "°C");

            if (temp >= 30.0) {
                apps = new ArrayList<>();
                apps2 = new ArrayList<>();
                tempimg.setImageResource(R.drawable.ic_cpu_cooler_bg);
                ivtemping.setImageResource(R.drawable.ic_before_cpu_cooler_icon);
                coolbutton.setImageResource(R.drawable.clear_btn);
                showmain.setText("OVERHEATED");
                showmain.setTextColor(Color.parseColor("#F63030"));
                showsec.setText("Apps are causing problem hit cool down");
                nooverheating.setText("");


                coolbutton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("APPS_CONFIGS", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putLong("COOLER_LAST_UPDATE", System.currentTimeMillis());
                        editor.commit();

                        Intent i = new Intent(getActivity(), ScannerCPU.class);
                        startActivity(i);

                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {


                                nooverheating.setText("Currently No App Causing Overheating");
                                nooverheating.setTextColor(Color.parseColor("#4e5457"));
                                showmain.setText("NORMAL");
                                showmain.setTextColor(Color.parseColor("#39c900"));
                                showsec.setText("CPU Temperature is Good");
                                showsec.setTextColor(Color.parseColor("#4e5457"));
                                coolbutton.setImageResource(R.drawable.clear_btn);
                                ivtemping.setImageResource(R.drawable.ic_after_cooling_icon);
                                tempimg.setImageResource(R.drawable.ic_ultra_power_mode_rounded_bg);
                                batterytemp.setText("25.3" + "°C");
                                recyclerView.setAdapter(null);

                            }
                        }, 2000);


                        coolbutton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {


                                @SuppressLint("RestrictedApi") LayoutInflater inflater = getLayoutInflater(getArguments());
                                View layout = inflater.inflate(R.layout.my_toast, null);

                                ImageView image = (ImageView) layout.findViewById(R.id.image);

                                TextView text = (TextView) layout.findViewById(R.id.textView1);
                                text.setText("CPU Temperature is Already Normal.");

                                Toast toast = new Toast(getActivity());
                                toast.setGravity(Gravity.CENTER_VERTICAL, 0, 70);
                                toast.setDuration(Toast.LENGTH_LONG);
                                toast.setView(layout);
                                toast.show();
                            }
                        });
                    }
                });

                recyclerView.setItemAnimator(new SlideInLeftAnimator());

                recyclerView.getItemAnimator().setAddDuration(10000);

                mAdapter = new RecyclerAdapter(apps);
                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
                recyclerView.setLayoutManager(mLayoutManager);
                recyclerView.setItemAnimator(new SlideInUpAnimator(new OvershootInterpolator(1f)));
                recyclerView.computeHorizontalScrollExtent();
                recyclerView.setAdapter(mAdapter);
                getAllICONS();

            }
        }
        catch(Exception e) {}
    }

    @Override
    public boolean getUserVisibleHint() {

       
        return getUserVisibleHint();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (isVisibleToUser) {
       
        } else {

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            startActivity(new Intent(getActivity(), MainActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
