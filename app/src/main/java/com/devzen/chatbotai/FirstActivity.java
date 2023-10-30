package com.devzen.chatbotai;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.icu.text.DateFormat;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.Date;
import java.util.Locale;

import com.devzen.chatbotai.R;

public class FirstActivity extends AppCompatActivity {
    String versionName;
    private AdView bigBanner;
    LottieAnimationView runing_animation;
    LinearLayout rateUs,emailUs,moreApps,shareApp;
    SharedPreferences prefs = null;
    SharedPreferences preftoday = null;
    int mode = DarkModeSharedPrefs.getAppNightDayMode(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);
        RelativeLayout btnstartStop = findViewById(R.id.btnStartStop);
        rateUs = findViewById(R.id.about_us_rate);
        shareApp = findViewById(R.id.about_us_share);
        moreApps = findViewById(R.id.about_us_more);
        emailUs = findViewById(R.id.about_us_email);
        bigBanner = findViewById(R.id.myBigBanner);
        AdRequest adRequest = new AdRequest.Builder().build();
        bigBanner.loadAd(adRequest);

        prefs = getSharedPreferences(getApplicationContext().getPackageName().toString(), MODE_PRIVATE);
        preftoday = getSharedPreferences(getApplicationContext().getPackageName()+"daily", MODE_PRIVATE);

        if (prefs.getBoolean("firstrun", true)) {
            prefs.edit().putBoolean("firstrun", false).commit();
            creditBalance(getcreditBalance() + Integer.parseInt(getResources().getString(R.string.howmany_credit_for_first_download)));
        }


        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
            Date date = new Date();
            String today = dateFormat.format(date)+"";
            String todaypref = preftoday.getString("firstrunaday", "");
            if (!todaypref.equals(today)){
                preftoday.edit().putString("firstrunaday", today).commit();
                creditBalance(getcreditBalance() + Integer.parseInt(getResources().getString(R.string.howmany_credits_every_day)));
            }
        }




        if (mode == AppCompatDelegate.MODE_NIGHT_YES){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }
        else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        LottieAnimationView runing_animation = (LottieAnimationView) findViewById(R.id.homeAnimation);
        if (runing_animation != null){
            runing_animation.setAnimation("robot_loading.json");
            runing_animation.loop(true);
            runing_animation.playAnimation();
        }
        rateUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rateApp();
            }
        });
        shareApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shareApp();
            }
        });

        moreApps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                moreApps();
            }
        });
        emailUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                emailUs();
            }
        });

        try{
            versionName = getApplicationContext().getPackageManager()
                    .getPackageInfo(getApplicationContext().getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return;
        }
        btnstartStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent startintent = new Intent(FirstActivity.this, MainActivityNew.class);
                startintent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(startintent);
            }
        });

    }


    public void rateApp() {
        final String appPackageName = getApplicationContext().getPackageName();

        startActivity(new Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://play.google.com/store/apps/details?id=com.devzen.chatbotai&hl=vi&gl=US" + appPackageName)));
    }

    public void shareApp() {

        Intent intent5 = new Intent("android.intent.action.SEND");
        intent5.setType("text/plain");
        intent5.putExtra("android.intent.extra.TEXT",
                getResources().getString(R.string.share_app_text)+" "+ getResources().getString(R.string.app_name)+" " +getResources().getString(R.string.share_app_text2)
                        + getPackageName());
        startActivity(Intent.createChooser(intent5, getString(R.string.share_firends)));
    }

    public void emailUs(){
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:"));
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{getString(R.string.email_adress)});
        intent.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.app_name));
        intent.putExtra(Intent.EXTRA_TEXT, getResources().getString(R.string.write_here) + "\n\n\n" + getResources().getString(R.string.app_name) + " " + getResources().getString(R.string.version) + " " + versionName);
        startActivity(Intent.createChooser(intent, getResources().getString(R.string.email)));

    }

    public void moreApps(){
        startActivity(new Intent(
                Intent.ACTION_VIEW,
                Uri.parse(getResources().getString(R.string.playStoreLink))));
    }
    private void creditBalance(int credit) {
        SharedPreferences sp = getSharedPreferences(getPackageName()+"balance", MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt("credits", credit);
        editor.commit();
    }

    private int getcreditBalance() {
        SharedPreferences sp = getSharedPreferences(getPackageName()+"balance", MODE_PRIVATE);
        int myIntValue = sp.getInt("credits", -1);
        return myIntValue;
    }

}