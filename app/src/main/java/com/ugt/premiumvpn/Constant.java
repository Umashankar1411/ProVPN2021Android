package com.ugt.premiumvpn;

import android.content.Context;
import android.content.SharedPreferences;

public class Constant {
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context _context;
    int PRIVATE_MODE = 0;

   // public static final String PRODUCT_ID = "android.test.purchased"; // PUT YOUR PRODUCT ID HERE
  //  public static final String LICENSE_KEY = null; // PUT YOUR LICENSE KEY HERE
    public static final String PRODUCT_ID = "vpnpro"; // PUT YOUR PRODUCT ID HERE
    public static final String LICENSE_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAucZPK2PAttULP/jjXn71sAUVc6o9YfQQSUk6wFF0JNQPLL/nYXEsAqCGgOqRVNmZCovcHdTG2/EH4XnHAD5i7leOvbIec84+MiyrYmjSlsVuTNrs8BWX34Ad3D3X51R+ax5nVhuNfG5TyPd81ctymaizTImnBYqDvp33bTU4kmNJAcOo0jgH0oRDqV4OK3D9mnrXMXu5VDmg1ofdE6AVD5cFl/PKL8W4RrfD+f4sRe/EzE4AYZIzeq7dFKjP5+64FjVoq8WJ3TZKwFWkPp5inEhJKSoXYzN8sx1T1vY+ZFG22W92kZMcBkiNiFa1qVsoJjqBkoBn8xUTsef74XMr0QIDAQAB"; // PUT YOUR LICENSE KEY HERE
    public static final String PrivacyPolicyUrl = "http://pharid.com/privacy-policy/"; // PUT YOUR Privacy Policy Url Here
    public static final String UpgradePro = "VPNPro2019"; //Please put something different for example you can put your app name here without spaces

    public static String URL_LOGIN = "http://pharid.com/vpnpro/api/login.php";
    public static String URL_REGISTER = "http://pharid.com/vpnpro/api/register.php";


    private static final String PREF_NAME = "snow-intro-slider";

    private static final String IS_FIRST_TIME_LAUNCH = "IsFirstTimeLaunch";

    public Constant(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void setFirstTimeLaunch(boolean isFirstTime) {
        editor.putBoolean(IS_FIRST_TIME_LAUNCH, isFirstTime);
        editor.commit();
    }

    public boolean isFirstTimeLaunch() {
        return pref.getBoolean(IS_FIRST_TIME_LAUNCH, true);
    }

}