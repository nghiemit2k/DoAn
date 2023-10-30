package com.devzen.chatbotai;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextPaint;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;

public class MenuActivity extends AppCompatActivity {
    ImageView closeMenu,darkLighImageView;
    AdView mAdView;
    TextView myAppsNameTxt,darkLightTextview,version;
    LinearLayout darkLightlinear,howtoUseIntro,moreAppsByUs,rateUs,shareUs,emailUs,getBalance;
    String versionName;
    private RewardedAd rewardedAd;
    AdRequest adRequest = new AdRequest.Builder().build();
    int io = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);


        darkLightTextview = findViewById(R.id.dark_light_text);
        darkLighImageView = findViewById(R.id.dark_light_image);
        howtoUseIntro = findViewById(R.id.welcomeIntroLinear);
        version = findViewById(R.id.versioncODEName);

        int mode = DarkModeSharedPrefs.getAppNightDayMode(this);

        darkLightTextview = findViewById(R.id.dark_light_text);
        darkLighImageView = findViewById(R.id.dark_light_image);
        howtoUseIntro = findViewById(R.id.welcomeIntroLinear);
        version = findViewById(R.id.versioncODEName);

        if (mode == AppCompatDelegate.MODE_NIGHT_YES){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            darkLightTextview.setText(getResources().getString(R.string.enbl_light_mode));
            darkLighImageView.setImageResource(R.drawable.ic_baseline_wb_sunny_24);

        }
        else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            darkLightTextview.setText(getResources().getString(R.string.enable_dark_mode));
            darkLighImageView.setImageResource(R.drawable.ic_baseline_dark_mode_24_1);
        }


        try{
            versionName = getApplicationContext().getPackageManager()
                    .getPackageInfo(getApplicationContext().getPackageName(), 0).versionName;
            version.setText(versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return;
        }
        RewardedAd.load(this, getResources().getString(R.string.admob_rewarded_id),
                adRequest, new RewardedAdLoadCallback() {
                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error.
                        rewardedAd = null;
                    }

                    @Override
                    public void onAdLoaded(@NonNull RewardedAd ad) {
                        rewardedAd = ad;
                    }
                });

        closeMenu = findViewById(R.id.closeMenuBtn);
        moreAppsByUs = findViewById(R.id.moreApps);
        rateUs = findViewById(R.id.rateUs);
        getBalance = findViewById(R.id.getBalance);
        shareUs = findViewById(R.id.share_app_linear);
        emailUs = findViewById(R.id.writeUs);


        rateUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String appPackageName = getPackageName();
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.devzen.chatbotai&hl=vi&gl=US" + appPackageName)));
                } catch (android.content.ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.devzen.chatbotai&hl=vi&gl=US" + appPackageName)));
                }
            }
        });

        getBalance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog("GET CREDITS!",MenuActivity.this);
            }
        });

        emailUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:"));
                intent.putExtra(Intent.EXTRA_EMAIL  , new String[] {getString(R.string.email_adress) });
                intent.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.app_name));
                intent.putExtra(Intent.EXTRA_TEXT,getResources().getString(R.string.write_here)+"\n\n\n"+getResources().getString(R.string.app_name)+" "
                        +getResources().getString(R.string.version)+" "+versionName);
                startActivity(Intent.createChooser(intent, getResources().getString(R.string.email)));
            }
        });

        shareUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent5 = new Intent("android.intent.action.SEND");
                intent5.setType("text/plain");
                intent5.putExtra("android.intent.extra.TEXT",
                        getResources().getString(R.string.share_app_text)+" "+ getResources().getString(R.string.app_name)+" " +getResources().getString(R.string.share_app_text2)
                                + getPackageName());
                startActivity(Intent.createChooser(intent5, getString(R.string.share_firends)));
            }
        });

        closeMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MenuActivity.this.finish();
            }
        });


        howtoUseIntro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MenuActivity.this, MainActivityNew.class));

            }
        });


        mAdView = findViewById(R.id.adViewSettings);
        mAdView.loadAd(adRequest);

        myAppsNameTxt = (TextView) findViewById(R.id.myAppsNameTxtSettings);
        darkLightlinear = (LinearLayout) findViewById(R.id.dark_light_linear);

        darkLightlinear.setOnClickListener(view -> {
            if (mode == AppCompatDelegate.MODE_NIGHT_NO) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                DarkModeSharedPrefs.setInt(MenuActivity.this, DarkModeSharedPrefs.PREF_NIGHT_MODE,AppCompatDelegate.MODE_NIGHT_YES);
                darkLightTextview.setText(getResources().getString(R.string.enbl_light_mode));
                darkLighImageView.setImageResource(R.drawable.ic_baseline_wb_sunny_24);


            }else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                DarkModeSharedPrefs.setInt(MenuActivity.this, DarkModeSharedPrefs.PREF_NIGHT_MODE,AppCompatDelegate.MODE_NIGHT_NO);
                darkLightTextview.setText(getResources().getString(R.string.enable_dark_mode));
                darkLighImageView.setImageResource(R.drawable.ic_baseline_dark_mode_24_1);


            }
        });


//        myAppsNameTxt.setText(getResources().getString(R.string.your_domain_apps));

//        myAppsNameTxt.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                startActivity(new Intent(
//                        Intent.ACTION_VIEW,
//                        Uri.parse(getResources().getString(R.string.playStoreLink))));
//            }
//        });


        moreAppsByUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse(getResources().getString(R.string.playStoreLink))));
            }
        });

        TextPaint paint = myAppsNameTxt.getPaint();
//        float width = paint.measureText(getResources().getString(R.string.your_domain_apps));

//        Shader textShader = new LinearGradient(0, 0, width, myAppsNameTxt.getTextSize(),
//                new int[]{
//                        Color.parseColor("#F97C3C"),
//                        Color.parseColor("#FDB54E"),
//                        Color.parseColor("#64B678"),
//                        Color.parseColor("#478AEA"),
//                        Color.parseColor("#8446CC"),
//                }, null, Shader.TileMode.CLAMP);
//        myAppsNameTxt.getPaint().setShader(textShader);


        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdClicked() {
            }

            @Override
            public void onAdClosed() {
                myAppsNameTxt.setVisibility(View.GONE);

            }

            @Override
            public void onAdFailedToLoad(LoadAdError adError) {
                // Code to be executed when an ad request fails.
            }

            @Override
            public void onAdImpression() {
                // Code to be executed when an impression is recorded
                // for an ad.
            }

            @Override
            public void onAdLoaded() {
                myAppsNameTxt.setVisibility(View.GONE);

            }

            @Override
            public void onAdOpened() {
                mAdView.setVisibility(View.GONE);
                myAppsNameTxt.setVisibility(View.VISIBLE);
            }
        });

    }
    public void addCreditBalance(Integer balance){
        SharedPreferences sp = getSharedPreferences(getPackageName()+"balance", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt("credits", balance);
        editor.commit();
    }
    public int getcreditBalance(){
        SharedPreferences sp = getSharedPreferences(getPackageName()+"balance", Activity.MODE_PRIVATE);
        int myIntValue = sp.getInt("credits", -1);
        return myIntValue;
    }


    private void showDialog(String title, Activity activity) {
        Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.rewardpopup);
        dialog.setCanceledOnTouchOutside(true);

        TextView body = (TextView) dialog.findViewById(R.id.body);
        body.setText(title);
        TextView cointxt = (TextView) dialog.findViewById(R.id.cointxt);
        LinearLayout cancelBTN = (LinearLayout) dialog.findViewById(R.id.cancel_linear);
        LinearLayout watchBtn = (LinearLayout) dialog.findViewById(R.id.watch_linear);
        cointxt.setText("You will get " + getResources().getString(R.string.howmany_credit_each_ad) + " coins for each watched ads");

        LottieAnimationView runing_animation = (LottieAnimationView) dialog.findViewById(R.id.coinlottie);
        if (runing_animation != null) {
            runing_animation.setAnimation("coin.json");
            runing_animation.loop(true);
            runing_animation.playAnimation();
        }

        watchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                io++;
                if (io > 2) {
                    RewardedAd.load(MenuActivity.this, getResources().getString(R.string.admob_rewarded_id), adRequest, new RewardedAdLoadCallback() {
                        @Override
                        public void onAdFailedToLoad(LoadAdError adError) {
                            rewardedAd = null;
                            Toast.makeText(getApplicationContext(), "Try again later there is no ads to load!", Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onAdLoaded(RewardedAd ad) {
                            rewardedAd = ad;
                        }
                    });
                    io = 0;
                }
                if (rewardedAd != null) {
                    rewardedAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                        @Override
                        public void onAdClicked() {
                            // Called when a click is recorded for an ad.
                        }

                        @Override
                        public void onAdDismissedFullScreenContent() {
                            // Called when ad is dismissed.
                            // Set the ad reference to null so you don't show the ad a second time.
                            rewardedAd = null;
                        }

                        @Override
                        public void onAdImpression() {
                            // Called when an impression is recorded for an ad.
                        }

                        @Override
                        public void onAdShowedFullScreenContent() {
                            // Called when ad is shown.
                        }
                    });

                    rewardedAd.show(MenuActivity.this, new OnUserEarnedRewardListener() {
                        @Override
                        public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                            addCreditBalance(getcreditBalance() + Integer.parseInt(getResources().getString(R.string.howmany_credit_each_ad)));
                            Toast.makeText(getApplicationContext(), "You got " + getResources().getString(R.string.howmany_credit_each_ad)+" credits!", Toast.LENGTH_LONG).show();
                            // dialog.dismiss();
                        }
                    });
                }
            }
        });

        cancelBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }


}