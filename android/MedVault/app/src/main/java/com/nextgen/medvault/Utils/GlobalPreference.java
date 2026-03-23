package com.nextgen.medvault.Utils;

import android.content.Context;
import android.content.SharedPreferences;

public class GlobalPreference {

    private SharedPreferences prefs;
    private Context context;
    SharedPreferences.Editor editor;

    public GlobalPreference(Context context) {
        this.context = context;
        prefs = context.getSharedPreferences(Constants.SHARED_PREF, Context.MODE_PRIVATE);
        editor = prefs.edit();
    }

    public void addIP(String ip) {
        editor.putString(Constants.IP, ip);
        editor.apply();
    }

    public String RetriveIP() {
        return prefs.getString(Constants.IP, "10.115.241.113");
    }
    public void setUID(String uid) {
        editor.putString(Constants.UID, uid);
        editor.apply();
    }

    public String getUID() {
        return prefs.getString(Constants.UID, "");
    }

    public void setName(String name){
        editor.putString(Constants.NAME, name);
        editor.apply();
    }

    public String getName() {
        return prefs.getString(Constants.NAME, "");
    }

    public void setDOB(String dob){
        editor.putString(Constants.DOB, dob);
        editor.apply();
    }

    public void setImage(String image) {
        editor.putString(Constants.IMGE, image);
        editor.apply();
    }

    public String getImage() {
        return prefs.getString(Constants.IMGE, "");
    }


    public void setPDF(String pdf) {
        editor.putString(Constants.PDF, pdf);
        editor.apply();
    }

    public String getPDF() {
        return prefs.getString(Constants.PDF, "");
    }


    public void setUserDetails(String user_details) {
        editor.putString(Constants.USER_DETAILS, user_details);
        editor.apply();
    }

    public String getUserDetails() {
        return prefs.getString(Constants.USER_DETAILS, "");
    }



    public void setLoginStatus(Boolean status)     {
        editor.putBoolean(Constants.LOGIN_STATUS, status);
        editor.apply();
    }

    public Boolean getLoginStatus()
    {
        return prefs.getBoolean(Constants.LOGIN_STATUS,false);
    }

}