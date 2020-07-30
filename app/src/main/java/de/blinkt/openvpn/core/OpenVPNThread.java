

package de.blinkt.openvpn.core;

import android.annotation.SuppressLint;
import android.util.Log;

import com.ugt.premiumvpn.R;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import de.blinkt.openvpn.core.VpnStatus.ConnectionStatus;

public class OpenVPNThread implements Runnable {
    private static final String DUMP_PATH_STRING = "Dump path: ";
    @SuppressLint("SdCardPath")
    private static final String BROKEN_PIE_SUPPORT = "/data/data/de.blinkt.openvpn/cache/pievpn";
    private final static String BROKEN_PIE_SUPPORT2 = "syntax error";
    private static final String TAG = "OpenVPN";
    public static final int M_FATAL = (1 << 4);
    public static final int M_NONFATAL = (1 << 5);
    public static final int M_WARN = (1 << 6);
    public static final int M_DEBUG = (1 << 7);
    private String[] mArgv;
    private Process mProcess;
    private String mNativeDir;
    private OpenVPNService mService;
    private String mDumpPath;
    private boolean mBrokenPie = false;
    private boolean mNoProcessExitStatus = false;

    public OpenVPNThread(OpenVPNService service, String[] argv, String nativelibdir) {
        mArgv = argv;
        mNativeDir = nativelibdir;
        mService = service;
    }

    public void stopProcess() {
        mProcess.destroy();
    }

    void setReplaceConnection()
    {
        mNoProcessExitStatus=true;
    }

    @Override
    public void run() {
        try {
            Log.i(TAG, "Starting openvpn");
            startOpenVPNThreadArgs(mArgv);
            Log.i(TAG, "OpenVPN process exited");
        } catch (Exception e) {
            VpnStatus.logException("Starting OpenVPN Thread", e);
            Log.e(TAG, "OpenVPNThread Got " + e.toString());
        } finally {
            int exitvalue = 0;
            try {
                if (mProcess != null)
                    exitvalue = mProcess.waitFor();
            } catch (IllegalThreadStateException ite) {
                VpnStatus.logError("Illegal Thread state: " + ite.getLocalizedMessage());
            } catch (InterruptedException ie) {
                VpnStatus.logError("InterruptedException: " + ie.getLocalizedMessage());
            }
            if (exitvalue != 0) {
                VpnStatus.logError("Process exited with exit value " + exitvalue);
                if (mBrokenPie) {
                    
                    String[] noPieArgv = VPNLaunchHelper.replacePieWithNoPie(mArgv);

                    
                    if (!noPieArgv.equals(mArgv)) {
                        mArgv = noPieArgv;
                        VpnStatus.logInfo("PIE Version could not be executed. Trying no PIE version");
                        run();
                        return;
                    }

                }

            }

            if (!mNoProcessExitStatus)
                VpnStatus.updateStateString("NOPROCESS", "No process running.", R.string.state_noprocess, ConnectionStatus.LEVEL_NOTCONNECTED);

            if (mDumpPath != null) {
                try {
                    BufferedWriter logout = new BufferedWriter(new FileWriter(mDumpPath + ".log"));
                    SimpleDateFormat timeformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.GERMAN);
                    for (LogItem li : VpnStatus.getlogbuffer()) {
                        String time = timeformat.format(new Date(li.getLogtime()));
                        logout.write(time + " " + li.getString(mService) + "\n");
                    }
                    logout.close();
                    VpnStatus.logError(R.string.minidump_generated);
                } catch (IOException e) {
                    VpnStatus.logError("Writing minidump log: " + e.getLocalizedMessage());
                }
            }

            mService.processDied();
            Log.i(TAG, "Exiting");
        }
    }

    private void startOpenVPNThreadArgs(String[] argv) {
        LinkedList<String> argvlist = new LinkedList<String>();

        Collections.addAll(argvlist, argv);

        ProcessBuilder pb = new ProcessBuilder(argvlist);
        

        String lbpath = genLibraryPath(argv, pb);

        pb.environment().put("LD_LIBRARY_PATH", lbpath);

        pb.redirectErrorStream(true);
        try {
            mProcess = pb.start();
            
            mProcess.getOutputStream().close();
            InputStream in = mProcess.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(in));

            while (true) {
                String logline = br.readLine();
                if (logline == null)
                    return;

                if (logline.startsWith(DUMP_PATH_STRING))
                    mDumpPath = logline.substring(DUMP_PATH_STRING.length());

                if (logline.startsWith(BROKEN_PIE_SUPPORT) || logline.contains(BROKEN_PIE_SUPPORT2))
                    mBrokenPie = true;


                

                Pattern p = Pattern.compile("(\\d+).(\\d+) ([0-9a-f])+ (.*)");
                Matcher m = p.matcher(logline);
                if (m.matches()) {
                    int flags = Integer.parseInt(m.group(3), 16);
                    String msg = m.group(4);
                    int logLevel = flags & 0x0F;

                    VpnStatus.LogLevel logStatus = VpnStatus.LogLevel.INFO;

                    if ((flags & M_FATAL) != 0)
                        logStatus = VpnStatus.LogLevel.ERROR;
                    else if ((flags & M_NONFATAL) != 0)
                        logStatus = VpnStatus.LogLevel.WARNING;
                    else if ((flags & M_WARN) != 0)
                        logStatus = VpnStatus.LogLevel.WARNING;
                    else if ((flags & M_DEBUG) != 0)
                        logStatus = VpnStatus.LogLevel.VERBOSE;

                    if (msg.startsWith("MANAGEMENT: CMD"))
                        logLevel = Math.max(4, logLevel);


                    VpnStatus.logMessageOpenVPN(logStatus, logLevel, msg);
                } else {
                    VpnStatus.logInfo("P:" + logline);
                }
            }


        } catch (IOException e) {
            VpnStatus.logException("Error reading from output of OpenVPN process", e);
            stopProcess();
        }


    }

    private String genLibraryPath(String[] argv, ProcessBuilder pb) {
        
        String applibpath = argv[0].replaceFirst("/cache/.*$", "/lib");

        String lbpath = pb.environment().get("LD_LIBRARY_PATH");
        if (lbpath == null)
            lbpath = applibpath;
        else
            lbpath = applibpath + ":" + lbpath;

        if (!applibpath.equals(mNativeDir)) {
            lbpath = mNativeDir + ":" + lbpath;
        }
        return lbpath;
    }
}
