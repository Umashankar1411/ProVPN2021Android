package com.ugt.premiumvpn.activity;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.net.VpnService;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.ViewGroup.LayoutParams;

import android.os.Bundle;


import android.support.v7.app.AlertDialog;
import android.util.Base64;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ugt.premiumvpn.BuildConfig;
import com.ugt.premiumvpn.R;
import com.ugt.premiumvpn.model.Server;
import com.ugt.premiumvpn.util.PropertiesService;
import com.ugt.premiumvpn.util.Stopwatch;
import com.ugt.premiumvpn.util.TotalTraffic;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import de.blinkt.openvpn.VpnProfile;
import de.blinkt.openvpn.core.ConfigParser;
import de.blinkt.openvpn.core.OpenVPNService;
import de.blinkt.openvpn.core.ProfileManager;
import de.blinkt.openvpn.core.VPNLaunchHelper;
import de.blinkt.openvpn.core.VpnStatus;

import static com.ugt.premiumvpn.Constant.UpgradePro;

public class VPNInfoActivity extends BaseActivity {

    private static final int START_VPN_PROFILE = 70;
    private BroadcastReceiver br;
    private BroadcastReceiver trafficReceiver;
    public final static String BROADCAST_ACTION = "de.blinkt.openvpn.VPN_STATUS";

    private static OpenVPNService mVPNService;
    private VpnProfile vpnProfile;

    private Server currentServer = null;
    private Button unblockCheck;
    private Button serverConnect;
    private TextView lastLog;
    private ProgressBar connectingProgress;
    private PopupWindow popupWindow;
    private LinearLayout parentLayout;
    private TextView trafficInTotally;
    private TextView trafficOutTotally;
    private TextView trafficIn;
    private TextView trafficOut;
    private ImageButton bookmark;
    public static final String EXTRA_COUNTRY = "country";
 //   private static boolean filterAds = false;
   // private static boolean defaultFilterAds = true;

    private boolean autoConnection;
    private boolean fastConnection;
    private Server autoServer;

    private boolean statusConnection = false;
    private boolean firstData = true;

    private WaitConnectionAsync waitConnection;
    private boolean inBackground;
    private static Stopwatch stopwatch;
    private boolean isBindedService = false;
    TextView mCircleView;
    private List<Server> countryList;
    private RelativeLayout homeContextRL;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vpninfo);
        countryList = dbHelper.getUniqueCountries();
        MobileAds.initialize(this, String.valueOf(R.string.admob_app_id));
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarr);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        final Drawable upArrow = getResources().getDrawable(R.drawable.ic_arrow_back_black_24dp);
        upArrow.setColorFilter(getResources().getColor(android.R.color.white), PorterDuff.Mode.SRC_ATOP);
        actionBar.setHomeAsUpIndicator(upArrow);




        homeContextRL = (RelativeLayout) findViewById(R.id.homeContextRL);




        parentLayout = (LinearLayout) findViewById(R.id.serverParentLayout);
        connectingProgress = (ProgressBar) findViewById(R.id.serverConnectingProgress);
        lastLog = (TextView) findViewById(R.id.serverStatus);
        serverConnect = (Button) findViewById(R.id.serverConnect);

        String totalIn = String.format(TotalTraffic.getTotalTraffic().get(0));
        trafficInTotally = (TextView) findViewById(R.id.serverTrafficInTotally);
        trafficInTotally.setText(totalIn);

        String totalOut = String.format(TotalTraffic.getTotalTraffic().get(1));
        trafficOutTotally = (TextView) findViewById(R.id.serverTrafficOutTotally);
        trafficOutTotally.setText(totalOut);

      //  trafficIn = (TextView) findViewById(R.id.serverTrafficIn);
//        trafficIn.setText("");
     //   trafficOut = (TextView) findViewById(R.id.serverTrafficOut);
       // trafficOut.setText("");

        br = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                receiveStatus(context, intent);
            }
        };

        registerReceiver(br, new IntentFilter(BROADCAST_ACTION));

        trafficReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                receiveTraffic(context, intent);
            }
        };

        registerReceiver(trafficReceiver, new IntentFilter(TotalTraffic.TRAFFIC_ACTION));







        lastLog.setText(R.string.server_not_connected);

        initView(getIntent());
        procheck();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initView(Intent intent) {


        TextView button2 = (TextView) findViewById(R.id.homeBtnChooseCountry);
        button2.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                sendTouchButton("homeBtnChooseCountry");
                chooseCountry();

            }
        });


        autoConnection = intent.getBooleanExtra("autoConnection", false);
        fastConnection = intent.getBooleanExtra("fastConnection", false);
        currentServer = (Server)intent.getParcelableExtra(Server.class.getCanonicalName());

        if (currentServer == null) {
            if (connectedServer != null) {
                currentServer = connectedServer;
            } else {
                onBackPressed();
                return;
            }
        }


        String code = currentServer.getCountryShort().toLowerCase();
        if (code.equals("do"))
            code = "dom";

        ((de.hdodenhof.circleimageview.CircleImageView) findViewById(R.id.serverFlag))
                .setImageResource(
                        getResources().getIdentifier(code,
                                "drawable",
                                getPackageName()));







        String localeCountryName = localeCountries.get(currentServer.getCountryShort()) != null ?
                localeCountries.get(currentServer.getCountryShort()) : currentServer.getCountryLong();

        TextView countryname = (TextView) findViewById(R.id.elapse);
        countryname.setText(localeCountryName);


        double speedValue = (double) Integer.parseInt(currentServer.getSpeed()) / 1048576;
        speedValue = new BigDecimal(speedValue).setScale(3, RoundingMode.UP).doubleValue();





        mCircleView = (TextView) findViewById(R.id.circleView);

        mCircleView.setText(Integer.parseInt(currentServer.getSpeed()) / 1048576 + "Mbps");
       // mCircleView.setUnit("Mbps");


       TextView  mCircleView2 = (TextView) findViewById(R.id.circleView2);

        mCircleView2.setText(String.valueOf(Integer.parseInt(currentServer.getNumVpnSessions())));

        if (checkStatus()) {
            serverConnect.setBackground(getResources().getDrawable(R.drawable.button3));
            serverConnect.setText(getString(R.string.server_btn_disconnect));
            ((TextView) findViewById(R.id.serverStatus)).setText(VpnStatus.getLastCleanLogMessage(getApplicationContext()));
        } else {
            serverConnect.setBackground(getResources().getDrawable(R.drawable.button2));
            serverConnect.setText(getString(R.string.server_btn_connect));
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        initView(intent);
    }

    private void chooseCountry() {
        View view = initPopUp(R.layout.choose_country, 0.6f, 0.8f, 0.8f, 0.7f);

        final List<String> countryListName = new ArrayList<String>();
        for (Server server : countryList) {
            String localeCountryName = localeCountries.get(server.getCountryShort()) != null ?
                    localeCountries.get(server.getCountryShort()) : server.getCountryLong();
            countryListName.add(localeCountryName);
        }

        ListView lvCountry = (ListView) view.findViewById(R.id.homeCountryList);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, countryListName);

        lvCountry.setAdapter(adapter);
        lvCountry.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                popupWindow.dismiss();
                onSelectCountry(countryList.get(position));
            }
        });

        popupWindow.showAtLocation(homeContextRL, Gravity.CENTER,0, 0);
    }

    private View initPopUp(int resourse,
                           float landPercentW,
                           float landPercentH,
                           float portraitPercentW,
                           float portraitPercentH) {

        LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(resourse, null);

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            popupWindow = new PopupWindow(
                    view,
                    (int)(widthWindow * landPercentW),
                    (int)(heightWindow * landPercentH)
            );
        } else {
            popupWindow = new PopupWindow(
                    view,
                    (int)(widthWindow * portraitPercentW),
                    (int)(heightWindow * portraitPercentH)
            );
        }


        popupWindow.setOutsideTouchable(false);
        popupWindow.setFocusable(true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());

        return view;
    }

    private void onSelectCountry(Server server) {
        Intent intent = new Intent(getApplicationContext(), VPNListActivity.class);
        intent.putExtra(EXTRA_COUNTRY, server.getCountryShort());
        startActivity(intent);
    }

    private void receiveTraffic(Context context, Intent intent) {
        if (checkStatus()) {
            String in = "";
            String out = "";
            if (firstData) {
                firstData = false;
            } else {
                in = String.format(getResources().getString(R.string.traffic_in),
                        intent.getStringExtra(TotalTraffic.DOWNLOAD_SESSION));
                out = String.format(getResources().getString(R.string.traffic_out),
                        intent.getStringExtra(TotalTraffic.UPLOAD_SESSION));
            }

//            trafficIn.setText(in);
          //  trafficOut.setText(out);

            String inTotal = String.format(intent.getStringExtra(TotalTraffic.DOWNLOAD_ALL));
            trafficInTotally.setText(inTotal);

            String outTotal = String.format(intent.getStringExtra(TotalTraffic.UPLOAD_ALL));
            trafficOutTotally.setText(outTotal);
        }
    }

    private void receiveStatus(Context context, Intent intent) {
        if (checkStatus()) {
            changeServerStatus(VpnStatus.ConnectionStatus.valueOf(intent.getStringExtra("status")));
            lastLog.setText(VpnStatus.getLastCleanLogMessage(getApplicationContext()));
        }

        if (intent.getStringExtra("detailstatus").equals("NOPROCESS")) {
            try {
                TimeUnit.SECONDS.sleep(1);
                if (!VpnStatus.isVPNActive())
                    prepareStopVPN();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        if (waitConnection != null)
            waitConnection.cancel(false);

        if (isTaskRoot()) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
    }

    private boolean checkStatus() {
        if (connectedServer != null && connectedServer.getHostName().equals(currentServer.getHostName())) {
            return VpnStatus.isVPNActive();
        }

        return false;
    }

    private void changeServerStatus(VpnStatus.ConnectionStatus status) {
        switch (status) {
            case LEVEL_CONNECTED:
                statusConnection = true;
                connectingProgress.setVisibility(View.GONE);

                if (!inBackground) {

                        chooseAction();

                }
                serverConnect.setBackground(getResources().getDrawable(R.drawable.button3));
                serverConnect.setText(getString(R.string.server_btn_disconnect));
                break;
            case LEVEL_NOTCONNECTED:
                serverConnect.setBackground(getResources().getDrawable(R.drawable.button2));
                serverConnect.setText(getString(R.string.server_btn_connect));
                break;
            default:
                serverConnect.setBackground(getResources().getDrawable(R.drawable.button3));
                serverConnect.setText(getString(R.string.server_btn_disconnect));
                statusConnection = false;
                connectingProgress.setVisibility(View.VISIBLE);
        }
    }

    private void prepareVpn() {
        connectingProgress.setVisibility(View.VISIBLE);
        if (loadVpnProfile()) {
            waitConnection = new WaitConnectionAsync();
            waitConnection.execute();
            serverConnect.setBackground(getResources().getDrawable(R.drawable.button3));
            serverConnect.setText(getString(R.string.server_btn_disconnect));
            startVpn();
        } else {
            connectingProgress.setVisibility(View.GONE);
            Toast.makeText(this, getString(R.string.server_error_loading_profile), Toast.LENGTH_SHORT).show();
        }
    }

    public void serverOnClick(View view) {
        switch (view.getId()) {
            case R.id.serverConnect:
                sendTouchButton("serverConnect");
                if (checkStatus()) {
                    stopVpn();
                } else {
                    prepareVpn();
                }
                break;


        }

    }

    private boolean loadVpnProfile() {
        byte[] data;
        try {
            data = Base64.decode(currentServer.getConfigData(), Base64.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        ConfigParser cp = new ConfigParser();
        InputStreamReader isr = new InputStreamReader(new ByteArrayInputStream(data));
        try {
            cp.parseConfig(isr);
            vpnProfile = cp.convertProfile();
            vpnProfile.mName = currentServer.getCountryLong();

            ProfileManager.getInstance(this).addProfile(vpnProfile);
        } catch (IOException | ConfigParser.ConfigParseError e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    private void prepareStopVPN() {
        if (!BuildConfig.DEBUG) {
            try {
                String download = trafficIn.getText().toString();
                download = download.substring(download.lastIndexOf(":") + 2);

            } catch (Exception e) {

            }
        }

        statusConnection = false;
        if (waitConnection != null)
            waitConnection.cancel(false);
        connectingProgress.setVisibility(View.GONE);
        lastLog.setText(R.string.server_not_connected);
        serverConnect.setBackground(getResources().getDrawable(R.drawable.button2));
        serverConnect.setText(getString(R.string.server_btn_connect));
        connectedServer = null;
    }

    private void stopVpn() {
        ProfileManager.setConntectedVpnProfileDisconnected(this);
        if (mVPNService != null && mVPNService.getManagement() != null)
            mVPNService.getManagement().stopVPN(false);

    }

    private void startVpn() {
        stopwatch = new Stopwatch();
        connectedServer = currentServer;
        hideCurrentConnection = true;

        Intent intent = VpnService.prepare(this);

        if (intent != null) {
            VpnStatus.updateStateString("USER_VPN_PERMISSION", "", R.string.state_user_vpn_permission,
                    VpnStatus.ConnectionStatus.LEVEL_WAITING_FOR_USER_INPUT);
            try {
                startActivityForResult(intent, START_VPN_PROFILE);
            } catch (ActivityNotFoundException ane) {
                VpnStatus.logError(R.string.no_vpn_support_image);
            }
        } else {
            onActivityResult(START_VPN_PROFILE, Activity.RESULT_OK, null);
        }
    }

    @Override
    protected void ipInfoResult() {
    }

    @Override
    protected void onResume() {
        super.onResume();
        inBackground = false;

        if (currentServer.getCity() == null)
            getIpInfo(currentServer);

        if (connectedServer != null && currentServer.getIp().equals(connectedServer.getIp())) {
            hideCurrentConnection = true;
            invalidateOptionsMenu();
        }


        Intent intent = new Intent(this, OpenVPNService.class);
        intent.setAction(OpenVPNService.START_SERVICE);
        isBindedService = bindService(intent, mConnection, Context.BIND_AUTO_CREATE);

        if (checkStatus()) {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (!checkStatus()) {
                connectedServer = null;
                serverConnect.setText(getString(R.string.server_btn_connect));
                serverConnect.setBackground(getResources().getDrawable(R.drawable.button2));
                lastLog.setText(R.string.server_not_connected);
            }
        } else {
            serverConnect.setText(getString(R.string.server_btn_connect));
            serverConnect.setBackground(getResources().getDrawable(R.drawable.button2));
            if (autoConnection) {
                prepareVpn();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        inBackground = true;

        if (isBindedService) {
            isBindedService = false;
            unbindService(mConnection);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(br);
        unregisterReceiver(trafficReceiver);
        if ( popupWindow != null && popupWindow.isShowing() ){
            popupWindow.dismiss();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case START_VPN_PROFILE :
                    VPNLaunchHelper.startOpenVpn(vpnProfile, getBaseContext());
                    break;

            }
        }
    }

    private void chooseAction() {
        LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.conected,null);

        popupWindow = new PopupWindow(
                view,
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT
        );

        popupWindow.setOutsideTouchable(false);
        popupWindow.setFocusable(true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());

        Button marketButton = (Button)view.findViewById(R.id.successPopUpBtnPlayMarket);
        marketButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendTouchButton("successPopUpBtnPlayMarket");
                final String appPackageName = getPackageName();
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                } catch (android.content.ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + appPackageName)));
                }
            }
        });


        ((Button)view.findViewById(R.id.successPopUpBtnBrowser)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendTouchButton("successPopUpBtnBrowser");
                startActivity( new Intent( Intent.ACTION_VIEW, Uri.parse("http://google.com")));
            }
        });
        ((Button)view.findViewById(R.id.successPopUpBtnDesktop)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendTouchButton("successPopUpBtnDesktop");
                Intent startMain = new Intent(Intent.ACTION_MAIN);
                startMain.addCategory(Intent.CATEGORY_HOME);
                startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(startMain);
            }
        });
        ((Button)view.findViewById(R.id.successPopUpBtnClose)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendTouchButton("successPopUpBtnClose");
                popupWindow.dismiss();
            }
        });


        popupWindow.showAtLocation(parentLayout, Gravity.CENTER,0, 0);

    }


    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            OpenVPNService.LocalBinder binder = (OpenVPNService.LocalBinder) service;
            mVPNService = binder.getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mVPNService = null;
        }

    };

    private class WaitConnectionAsync extends AsyncTask<Void, Void, Void>
    {
        @Override
        protected Void doInBackground(Void... params) {
            try {
                TimeUnit.SECONDS.sleep(PropertiesService.getAutomaticSwitchingSeconds());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (!statusConnection) {
                if (currentServer != null)
                    dbHelper.setInactive(currentServer.getIp());

                if (fastConnection) {
                    stopVpn();
                    newConnecting(getRandomServer(), true, true);
                } else if (PropertiesService.getAutomaticSwitching()){
                    if (!inBackground)
                        showAlert();
                }
            }
        }
    }

    private void showAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.try_another_server_text))
                .setPositiveButton(getString(R.string.try_another_server_ok),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                stopVpn();
                                autoServer = dbHelper.getSimilarServer(currentServer.getCountryLong(), currentServer.getIp());
                                if (autoServer != null) {
                                    newConnecting(autoServer, false, true);
                                } else {
                                    onBackPressed();
                                }
                            }
                        })
                .setNegativeButton(getString(R.string.try_another_server_no),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                if (!statusConnection) {
                                    waitConnection = new WaitConnectionAsync();
                                    waitConnection.execute();
                                }
                                dialog.cancel();
                            }
                        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void procheck()
    {
        boolean strPref = false;
        SharedPreferences shf = this.getSharedPreferences("config", MODE_PRIVATE);
        strPref = shf.getBoolean(UpgradePro, false);

        if(strPref)
        {
            AdView mAdMobAdView = (AdView) findViewById(R.id.admob_adview);
            mAdMobAdView.setVisibility(View.GONE);


        }
        else {
            AdView mAdMobAdView = (AdView) findViewById(R.id.admob_adview);
            AdRequest adRequest = new AdRequest.Builder()
                    .build();
            mAdMobAdView.loadAd(adRequest);

            final InterstitialAd mInterstitial = new InterstitialAd(this);
            mInterstitial.setAdUnitId(getString(R.string.interstitial_ad_unit));
            mInterstitial.loadAd(new AdRequest.Builder().build());
            mInterstitial.setAdListener(new AdListener() {
                @Override
                public void onAdLoaded() {
                    // TODO Auto-generated method stub
                    super.onAdLoaded();
                    if (mInterstitial.isLoaded()) {
                        mInterstitial.show();
                    }
                }
            });



        }

    }
}
