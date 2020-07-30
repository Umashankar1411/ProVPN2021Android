package com.ugt.premiumvpn.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ugt.premiumvpn.R;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import android.graphics.Color;
import android.os.Looper;
import android.os.Message;
import android.os.StrictMode;
import android.view.KeyEvent;
import android.widget.Button;
import android.widget.SeekBar;
import android.os.Handler;
import com.cardiomood.android.controls.gauge.SpeedometerGauge;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.ugt.premiumvpn.Constant.UpgradePro;

public class SpeedTest2 extends AppCompatActivity {

    TextView textPing;
    static TextView textDownload;
    TextView textUpload;
    Button buttonOokla;
    private SpeedometerGauge speedometerGauge;
    private SeekBar seekBar;
    Handler myhandlerprogressping;
    Handler getpingmin;
    Handler downloadhandler;
    Handler getDownloadhandler;
    Handler uploadhandler;
    Handler getUploadHandler;
    String Chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speedtest);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarr);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        final Drawable upArrow = getResources().getDrawable(R.drawable.ic_arrow_back_black_24dp);
        upArrow.setColorFilter(getResources().getColor(android.R.color.white), PorterDuff.Mode.SRC_ATOP);
        actionBar.setHomeAsUpIndicator(upArrow);

        textPing = (TextView) findViewById(R.id.textping);
        textDownload = (TextView) findViewById(R.id.textdownload);
        textUpload = (TextView) findViewById(R.id.textupload);
        buttonOokla = (Button) findViewById(R.id.buttonOokla);

        TextView centree = (TextView) findViewById(R.id.centree);
        centree.setText("0");

        procheck();

        setspeedometer();
        setpinghandler();
        setdownload();
        setUploadhandler();
        buttonOokla.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                new Thread(runnable).start();


            }
        });


    }

    public boolean onKeyDown(int keyCode, KeyEvent event){
        if(keyCode == KeyEvent.KEYCODE_BACK)
        {
            Intent home = new Intent(SpeedTest2.this,MainActivity.class);
            startActivity(home);
            return true;
        }
        return super.onKeyDown(keyCode,event);
    }

    Runnable runnable = new Runnable() {
        public void run() {
            ArrayList<Long> longArrayList = new ArrayList<Long>();


            try {
                for (int i = 1; i < 12; i++) {
                    Random random = new Random();
                    int randomx = random.nextInt(99999999) + 111111111;
                    long diff_time = ping("http://speedtest.biznetnetworks.com/speedtest/latency.txt?x=" + randomx);
                    System.out.println("diff_time get ping ==> " + diff_time);
                    Bundle a = new Bundle();
                    Message msg = new Message();
                    a.putString("MS_PING",String.valueOf(diff_time));
                    a.putString("PROGRESS_PING",String.valueOf(i));

                    msg.setData(a);
                    msg.setTarget(myhandlerprogressping);
                    msg.sendToTarget();
                    longArrayList.add(diff_time);
                }

                Long min = Long.MAX_VALUE;
                for(Long diff : longArrayList)
                {
                    if(min > diff)
                    {
                        min = diff;
                    }
                }
                Bundle b = new Bundle();
                b.putString("MIN_PING",String.valueOf(min));
                Message msg1 = new Message();
                msg1.setTarget(getpingmin);
                msg1.setData(b);
                msg1.sendToTarget();

                //editPing.setText(String.valueOf(min));
            }catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }
    };


    public void setpinghandler()
    {
        seekBar.setMax(11);

        myhandlerprogressping = new Handler(){
            @Override
            public void handleMessage(Message inputMessage) {
                Bundle a = inputMessage.getData();
                final List<Double> pingRateList = new ArrayList<>();
                String progress =  a.getString("PROGRESS_PING");
                String pingms = a.getString("MS_PING");
                textPing.setText(pingms + " ms");
                pingRateList.add(Double.valueOf(pingms));
                seekBar.setProgress(Integer.parseInt(progress));

                XYSeries pingSeries = new XYSeries("");
                pingSeries.setTitle("");



                int count = 0;

                List<Double> tmpLs = new ArrayList<>(pingRateList);
                for (Double val : tmpLs) {
                    pingSeries.add(count++, val);
                }

                XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
                dataset.addSeries(pingSeries);


                final LinearLayout chartPing = (LinearLayout) findViewById(R.id.chartPing);
                XYSeriesRenderer pingRenderer = new XYSeriesRenderer();
                XYSeriesRenderer.FillOutsideLine pingFill = new XYSeriesRenderer.FillOutsideLine(XYSeriesRenderer.FillOutsideLine.Type.BOUNDS_ALL);
                pingFill.setColor(Color.parseColor("#4d5a6a"));
                pingRenderer.addFillOutsideLine(pingFill);
                pingRenderer.setDisplayChartValues(false);
                pingRenderer.setShowLegendItem(false);
                pingRenderer.setColor(Color.parseColor("#4d5a6a"));
                pingRenderer.setLineWidth(5);
                final XYMultipleSeriesRenderer multiPingRenderer = new XYMultipleSeriesRenderer();
                multiPingRenderer.setXLabels(0);
                multiPingRenderer.setYLabels(0);
                multiPingRenderer.setZoomEnabled(false);
                multiPingRenderer.setXAxisColor(Color.parseColor("#647488"));
                multiPingRenderer.setYAxisColor(Color.parseColor("#2F3C4C"));
                multiPingRenderer.setPanEnabled(true, true);
                multiPingRenderer.setZoomButtonsVisible(false);
                multiPingRenderer.setMarginsColor(Color.argb(0x00, 0xff, 0x00, 0x00));
                multiPingRenderer.addSeriesRenderer(pingRenderer);

                GraphicalView chartView = ChartFactory.getLineChartView(getBaseContext(), dataset, multiPingRenderer);
                chartPing.addView(chartView, 0);

            }

        };

        getpingmin = new Handler(){
            @Override
            public void handleMessage(Message inputMessage) {
                Bundle b = inputMessage.getData();
                String minping = b.getString("MIN_PING");
                textPing.setText(minping + " ms");
                new Thread(rundownload).start();
            }
        };

    }
    public long ping(String urls) throws IOException {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        URL url = new URL(urls.trim());
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
        httpURLConnection.setUseCaches(false);
        httpURLConnection.setConnectTimeout(10 * 1000);
        httpURLConnection.setDoInput(false);

        long before_time = System.currentTimeMillis();
        httpURLConnection.connect();
        long diff_time = System.currentTimeMillis() - before_time;

        httpURLConnection.disconnect();
        return diff_time;
    }

    public void setspeedometer()
    {
        seekBar = (SeekBar) findViewById(R.id.seekBar);
        speedometerGauge = (SpeedometerGauge) findViewById(R.id.speedometer);
        speedometerGauge.setLabelConverter(new SpeedometerGauge.LabelConverter() {
            public String getLabelFor(double progress, double maxprogress) {
                return String.valueOf((int) Math.round(progress));
            }
        });
        speedometerGauge.setMaxSpeed(30);
        speedometerGauge.setDefaultColor(Color.BLACK);
        speedometerGauge.setMajorTickStep(5);
        speedometerGauge.setDefaultColor(Color.WHITE);
        speedometerGauge.setLabelTextSize(30);
        speedometerGauge.bringToFront();
        speedometerGauge.addColoredRange(30, 140, Color.GREEN);
        speedometerGauge.addColoredRange(140, 180, Color.YELLOW);
        speedometerGauge.addColoredRange(180, 400, Color.RED);
    }



    @SuppressLint("HandlerLeak")
    public void setdownload()
    {
        final ArrayList<Double> doubleArrayList = new ArrayList<Double>();

        downloadhandler = new Handler(Looper.getMainLooper())
        {
            @Override
            public void handleMessage(Message inputMessage) {
                Bundle c = inputMessage.getData();
                String speed = c.getString("SPEED_TEMP");
                Double speedsdouble = Double.parseDouble(speed);
                if(speedsdouble.isInfinite()== false){
                    doubleArrayList.add(speedsdouble);
                    NumberFormat numberFormat = new DecimalFormat("#0.00");
                    Double speedsDoubleAfterFormat = Double.parseDouble(numberFormat.format(speedsdouble));
                    textDownload.setText(speedsDoubleAfterFormat+ " Mbps");
                    TextView centree = (TextView) findViewById(R.id.centree);
                    centree.setText(speedsDoubleAfterFormat + "");
                    final List<Double> downloadRateList = new ArrayList<>();
                    downloadRateList.add(speedsDoubleAfterFormat);
                    speedometerGauge.setSpeed(speedsDoubleAfterFormat,false);


                }





            }
        };

        getDownloadhandler = new Handler()
        {
            @Override
            public void handleMessage(Message inputMessage) {
                Bundle d = inputMessage.getData();
                String speed = d.getString("RESULT_SPEED");
                Double speedsdouble = Double.parseDouble(speed);
                doubleArrayList.remove(0);
                doubleArrayList.remove(doubleArrayList.size()-1);
                Double totaldata = 0.0000D;
                int iizz = 0;
                for(int i = 0; i < doubleArrayList.size();i++)
                {
                    totaldata += doubleArrayList.get(i);
                    iizz++;
                }
                Double avg = totaldata/iizz;
                NumberFormat numberFormat = new DecimalFormat("#0.00");
                Double avgAfterFormated = Double.parseDouble(numberFormat.format(avg));
                textDownload.setText(String.valueOf(avgAfterFormated) + " Mbps");
                TextView centree = (TextView) findViewById(R.id.centree);
                centree.setText(String.valueOf(avgAfterFormated) + "");
                speedometerGauge.setSpeed(avgAfterFormated,false);
                new Thread(runUpload).start();


                XYSeries downloadSeries = new XYSeries("");
                downloadSeries.setTitle("");
                final List<Double> downloadRateList = new ArrayList<>();
                downloadRateList.add(avgAfterFormated);
                List<Double> tmpLs = new ArrayList<>(downloadRateList);
                int count = 0;
                for (Double val : tmpLs) {
                    downloadSeries.add(count++, val);
                }

                XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
                dataset.addSeries(downloadSeries);

                final LinearLayout chartDownload = (LinearLayout) findViewById(R.id.chartDownload);
                XYSeriesRenderer downloadRenderer = new XYSeriesRenderer();
                XYSeriesRenderer.FillOutsideLine downloadFill = new XYSeriesRenderer.FillOutsideLine(XYSeriesRenderer.FillOutsideLine.Type.BOUNDS_ALL);
                downloadFill.setColor(Color.parseColor("#4d5a6a"));
                downloadRenderer.addFillOutsideLine(downloadFill);
                downloadRenderer.setDisplayChartValues(false);
                downloadRenderer.setColor(Color.parseColor("#4d5a6a"));
                downloadRenderer.setShowLegendItem(false);
                downloadRenderer.setLineWidth(5);
                final XYMultipleSeriesRenderer multiDownloadRenderer = new XYMultipleSeriesRenderer();
                multiDownloadRenderer.setXLabels(0);
                multiDownloadRenderer.setYLabels(0);
                multiDownloadRenderer.setZoomEnabled(false);
                multiDownloadRenderer.setXAxisColor(Color.parseColor("#647488"));
                multiDownloadRenderer.setYAxisColor(Color.parseColor("#2F3C4C"));
                multiDownloadRenderer.setPanEnabled(false, false);
                multiDownloadRenderer.setZoomButtonsVisible(false);
                multiDownloadRenderer.setMarginsColor(Color.argb(0x00, 0xff, 0x00, 0x00));
                multiDownloadRenderer.addSeriesRenderer(downloadRenderer);

                GraphicalView chartView = ChartFactory.getLineChartView(getBaseContext(), dataset, multiDownloadRenderer);
                chartDownload.addView(chartView, 0);

            }

        };


    }

    Runnable rundownload = new Runnable() {
        @Override
        public void run() {
            Random random = new Random();
            try {

                double speedinbps = speedBpsDownloadFileFromServer("http://speedtest.biznetnetworks.com:80/speedtest/random2500x2500.jpg?x=" +random.nextInt(999999999));
                double speedinMbps = speedinbps*8/1024/1024;
                Bundle resultSpeed = new Bundle();
                resultSpeed.putString("RESULT_SPEED",String.valueOf(speedinMbps));
                Message msgdownload = new Message();
                msgdownload.setData(resultSpeed);
                msgdownload.setTarget(getDownloadhandler);
                msgdownload.sendToTarget();


            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    };
    public double speedBpsDownloadFileFromServer(String Url) throws IOException
    {

        HttpParams httpParams = new BasicHttpParams();
        int connectiontimeout = 10*1000;
        int sotimeout = 20 *1000;
        int sample = 0;
        HttpConnectionParams.setConnectionTimeout(httpParams, connectiontimeout);
        HttpConnectionParams.setSoTimeout(httpParams,sotimeout);

        DefaultHttpClient defaultHttpClient = new DefaultHttpClient(httpParams);
        try {
            long before_time = System.currentTimeMillis();
            HttpGet httpGet = new HttpGet(Url.trim());
            httpGet.setHeader("Accept", "text/html,application/xhtml+xml,*/*");
            httpGet.setHeader("Accept-Charset", "UTF-8,US-ASCII");
            httpGet.setHeader("Accept-Encoding","gzip, deflate");
            httpGet.setHeader("User-Agent", "Mozilla/4.0 (compatible;)");
            HttpResponse httpResponse = defaultHttpClient.execute(httpGet);
            int statusCode = httpResponse.getStatusLine().getStatusCode();
            if(statusCode == HttpStatus.SC_OK)
            {
                if(httpResponse.containsHeader("Content-Type"))
                {
                    String strHeader = httpResponse.getHeaders("Content-Type").toString();
                }
            }
            InputStream inputStream = null;
            inputStream = httpResponse.getEntity().getContent();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffWeb = new byte[4096];
            int bytesRead;
            while (true) {
                bytesRead = inputStream.read(buffWeb, 0, buffWeb.length);
                if (bytesRead > 0) {
                    baos.write(buffWeb, 0, bytesRead);
                    long diff_time_temp = System.currentTimeMillis() - before_time;
                    double time_temp = diff_time_temp/1000;
                    double speed_temp_bps = baos.size()/time_temp;
                    double speed_temp_Mbps = speed_temp_bps *8 / 1024/1024;
                    Bundle temp_download = new Bundle();
                    temp_download.putString("SPEED_TEMP",String.valueOf(speed_temp_Mbps));
                    Message temp_download_msg = new Message();
                    temp_download_msg.setData(temp_download);
                    temp_download_msg.setTarget(downloadhandler);
                    temp_download_msg.sendToTarget();
                    if(diff_time_temp > 10000.0) {
                        break;
                    }
                } else {
                    break;
                }
            }
            inputStream.close();
            baos.flush();
            long diff_time = System.currentTimeMillis() - before_time;
            double time = diff_time/1000;
            double speed = baos.size()/time;
            return speed;
        }catch (Exception ex)
        {
            System.out.println(ex.getMessage());
        }
        return 0D;
    }



    Runnable runUpload = new Runnable() {
        @Override
        public void run() {

            Random random = new Random();
            ArrayList<Double> doubleArrayList = new ArrayList<Double>();
            try {
                for(int i = 1; i< 6; i++) {
                    double speedupload = speedBpsUploadFileFromServer("http://speedtest.biznetnetworks.com:80/speedtest/upload.php?x=0." + random.nextInt(999999999), 5);

                    doubleArrayList.add(speedupload);
                    Bundle uploadmax = new Bundle();
                    uploadmax.putString("TEMPSPEEDUPLOAD",String.valueOf(speedupload));
                    Message msgupload = new Message();
                    msgupload.setData(uploadmax);
                    msgupload.setTarget(uploadhandler);
                    msgupload.sendToTarget();
                }
                Double max = Double.MIN_VALUE;
                for(Double diff : doubleArrayList)
                {
                    if(max < diff)
                    {
                        max = diff;
                    }
                }
                Log.e("MAX ->>> ", "" + max +" Mbps");
                Bundle uploadmax1 = new Bundle();
                uploadmax1.putString("MAXUPLOAD",String.valueOf(max));
                Message msgupload = new Message();
                msgupload.setData(uploadmax1);
                msgupload.setTarget(getUploadHandler);
                msgupload.sendToTarget();
            } catch (IOException e) {
                e.printStackTrace();
            }


        }
    };

    public void setUploadhandler()
    {
        ArrayList<Double> doubleUploadArrayList = new ArrayList<Double>();
        uploadhandler = new Handler()
        {
            @Override
            public void handleMessage(Message inputMessage) {
                final List<Double> uploadRateList = new ArrayList<>();
                Bundle x = inputMessage.getData();
                String speedupload1 = x.getString("TEMPSPEEDUPLOAD");
                Double speedUpload1 = Double.parseDouble(speedupload1);
                NumberFormat uploadNumberFormat = new DecimalFormat("#0.00");
                Double uploadafterformated = Double.parseDouble(uploadNumberFormat.format(speedUpload1));
                textUpload.setText(String.valueOf(uploadafterformated)+" Mbps");
                TextView centree = (TextView) findViewById(R.id.centree);
                centree.setText(String.valueOf(uploadafterformated)+"");





                uploadRateList.add(uploadafterformated);

                final LinearLayout chartUpload = (LinearLayout) findViewById(R.id.chartUpload);
                XYSeriesRenderer uploadRenderer = new XYSeriesRenderer();
                XYSeriesRenderer.FillOutsideLine uploadFill = new XYSeriesRenderer.FillOutsideLine(XYSeriesRenderer.FillOutsideLine.Type.BOUNDS_ALL);
                uploadFill.setColor(Color.parseColor("#4d5a6a"));
                uploadRenderer.addFillOutsideLine(uploadFill);
                uploadRenderer.setDisplayChartValues(false);
                uploadRenderer.setColor(Color.parseColor("#4d5a6a"));
                uploadRenderer.setShowLegendItem(false);
                uploadRenderer.setLineWidth(5);
                final XYMultipleSeriesRenderer multiUploadRenderer = new XYMultipleSeriesRenderer();
                multiUploadRenderer.setXLabels(0);
                multiUploadRenderer.setYLabels(0);
                multiUploadRenderer.setZoomEnabled(false);
                multiUploadRenderer.setXAxisColor(Color.parseColor("#647488"));
                multiUploadRenderer.setYAxisColor(Color.parseColor("#2F3C4C"));
                multiUploadRenderer.setPanEnabled(false, false);
                multiUploadRenderer.setZoomButtonsVisible(false);
                multiUploadRenderer.setMarginsColor(Color.argb(0x00, 0xff, 0x00, 0x00));
                multiUploadRenderer.addSeriesRenderer(uploadRenderer);

                XYSeries uploadSeries = new XYSeries("");
                uploadSeries.setTitle("");

                int count = 0;
                List<Double> tmpLs = new ArrayList<>(uploadRateList);
                for (Double val : tmpLs) {
                    if (count == 0) {
                        val = 0.0;
                    }
                    uploadSeries.add(count++, val);
                }

                XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
                dataset.addSeries(uploadSeries);

                GraphicalView chartView = ChartFactory.getLineChartView(getBaseContext(), dataset, multiUploadRenderer);
                chartUpload.addView(chartView, 0);



                speedometerGauge.setSpeed(speedUpload1);
            }
        };
        getUploadHandler = new Handler(){

            @Override
            public void handleMessage(Message inputMessage)
            {
                Bundle x = inputMessage.getData();
                String speedupload = x.getString("MAXUPLOAD");
                Double speedUpload = Double.parseDouble(speedupload);
                NumberFormat uploadNumberFormat = new DecimalFormat("#0.00");
                Double speedUploadAfterFormated = Double.parseDouble(uploadNumberFormat.format(speedUpload));
                textUpload.setText(String.valueOf(speedUploadAfterFormated) + " Mbps");
                TextView centree = (TextView) findViewById(R.id.centree);
                centree.setText(String.valueOf(speedUploadAfterFormated) + "");
                speedometerGauge.setSpeed(speedUpload);

            }
        };


    }

    public String generatedataforupload(int maxfile) throws IOException {
        Random random = new Random();
        ArrayList<NameValuePair> result = new ArrayList<NameValuePair>();
        for(int sizecounter = 1; sizecounter < maxfile+1;sizecounter++ )
        {

            int size = sizecounter*60*1000;
            StringBuilder builder = new StringBuilder(size);
            String compressedString = null;
            for(int i = 0;i< size;++i)
            {
                builder.append(Chars.charAt(random.nextInt(Chars.length())));
            }

//            for(int i = 0;i< retryCount;i++)
//            {
//
//                result.add(new BasicNameValuePair("x",builder.toString()));
//            }

            return  builder.toString();

        }



        return null;
    }

    private String readInputStreamToString(HttpURLConnection connection) {
        String result = null;
        StringBuffer sb = new StringBuffer();
        InputStream is = null;

        try {
            is = new BufferedInputStream(connection.getInputStream());
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String inputLine = "";
            while ((inputLine = br.readLine()) != null) {
                sb.append(inputLine);
            }
            result = sb.toString();
        }
        catch (Exception e) {
            Log.e("Exception reader", "Error reading InputStream");
            result = null;
        }
        finally {
            if (is != null) {
                try {
                    is.close();
                }
                catch (IOException e) {
                    Log.e("Exception IS", "Error closing InputStream");
                }
            }
        }

        return result;
    }
    public double speedBpsUploadFileFromServer(String Url,int maxfile) throws IOException
    {long before = System.currentTimeMillis();
        URL url = new URL(Url.trim());
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
        httpURLConnection.setRequestMethod("POST");
        httpURLConnection.setReadTimeout(10000);
        httpURLConnection.setConnectTimeout(10000);
        httpURLConnection.setDoInput(true);
        httpURLConnection.setDoOutput(true);
        OutputStream outputStream = httpURLConnection.getOutputStream();
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream,"UTF-8"));
        writer.write(generatedataforupload(5));
        writer.flush();
        writer.close();
        outputStream.close();
        httpURLConnection.connect();
        String response = readInputStreamToString(httpURLConnection);

        httpURLConnection.disconnect();
        long diff_time = System.currentTimeMillis() - before;
        NumberFormat df = new DecimalFormat("#0.000");
        double diff_time_precission = Double.parseDouble(df.format(Double.parseDouble(df.format(diff_time)) / 1000));
        String[] spliter = response.split("=");
        Double sizes = Double.parseDouble(spliter[1]);
        Double speeds = sizes/(diff_time_precission);
        return speeds/1024;
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final int itemId = item.getItemId();
        if(itemId == android.R.id.home){
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}