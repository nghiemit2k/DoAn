package com.devzen.chatbotai;


import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.devzen.chatbotai.chathistory.Message;
import com.devzen.chatbotai.chathistory.MessageAdapter;
import com.devzen.chatbotai.chathistory.MyInterfaceEtUser;
import com.devzen.chatbotai.chathistory.Session;
import com.devzen.chatbotai.R;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivityNew extends AppCompatActivity implements TextToSpeech.OnInitListener, RecognitionListener, MyInterfaceEtUser {
    RecyclerView recyclerView;
    LinearLayout welcomeTextView,creditBTN,clearChat;
    EditText messageEditText;
    ImageView sendButton,audioButton,menuBtn;
    List<Message> messageList;
    MessageAdapter messageAdapter;
    Boolean questionistext = true;
    TextView balanceTXT;
    private Session mySession;
    public static final MediaType JSON
            = MediaType.get("application/json; charset=utf-8");
    private static final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .build();
    private final View.OnLongClickListener lc = v -> {
        return false;
    };


    int i = 0;
    int io = 0;
    private RewardedAd rewardedAd = null;
    FirebaseRemoteConfig firebaseRemoteConfig = null;
    String api_key_firebase = "";
    private final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    AdRequest adRequest = new AdRequest.Builder().build();
    private TextToSpeech tts;
    private SpeechRecognizer speechRecognizer;
    private LoadingDialog loadingDialog;
    private LoadingVoiceDialog dialogVoice;
    private InterstitialAd mInterstitialAd = null;
    int mode = DarkModeSharedPrefs.getAppNightDayMode(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_new);
        messageList = new ArrayList<>();

        recyclerView = findViewById(R.id.recycler_view);
        welcomeTextView = findViewById(R.id.readyforaskLinear);
        messageEditText = findViewById(R.id.query);
        sendButton = findViewById(R.id.btnSend);
        audioButton = findViewById(R.id.btnAudio);
        menuBtn = findViewById(R.id.menuBtn);
        clearChat = findViewById(R.id.clearChat);
        creditBTN = findViewById(R.id.creditBTN);
        balanceTXT = findViewById(R.id.balanceTXT);
        tts = new TextToSpeech(this, this);
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        speechRecognizer.setRecognitionListener(this);
        loadingDialog = new LoadingDialog(this);
        dialogVoice = new LoadingVoiceDialog(this);
        MobileAds.initialize(this);
        firebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(0)
                .build();
        firebaseRemoteConfig.setConfigSettingsAsync(configSettings);
        Map<String, Object> defaultData = new HashMap<>();
        defaultData.put("api_key", "f2lvytfDNjxUAKW5AmklT3BlbkFJwTQDozaf9ZUHXzCOeI9K");
        firebaseRemoteConfig.setDefaultsAsync(defaultData);
        if (mode == AppCompatDelegate.MODE_NIGHT_YES){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }
        else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
        firebaseRemoteConfig.fetchAndActivate()
                .addOnCompleteListener(this, new OnCompleteListener<Boolean>() {
                    @Override
                    public void onComplete(@NonNull Task<Boolean> task) {
                        if (task.isSuccessful()) {
                            api_key_firebase = firebaseRemoteConfig.getString("api_key");
                        } else {
                            Toast.makeText(MainActivityNew.this, "Connection error. Please check your internet connection!", Toast.LENGTH_LONG).show();
                        }
                    }
                });



        InterstitialAd.load(this, getResources().getString(R.string.admob_interstitial_id), adRequest, new InterstitialAdLoadCallback() {
            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError adError) {
                mInterstitialAd = null;
            }

            @Override
            public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                mInterstitialAd = interstitialAd;
            }
        });



        LottieAnimationView runing_animation = findViewById(R.id.homeAnimation);
        if (runing_animation != null) {
            runing_animation.setAnimation("robot_logo.json");
            runing_animation.loop(true);
            runing_animation.playAnimation();
        }

        LottieAnimationView runing_animation2 = findViewById(R.id.homeAnimation2);
        if (runing_animation2 != null) {
            runing_animation2.setAnimation("robot_logo.json");
            runing_animation2.loop(true);
            runing_animation2.playAnimation();
        }


        balanceTXT.setText(getcreditBalance() + " credits");



        messageEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (messageEditText.length() < 1) {
                    sendButton.setVisibility(View.GONE);
                    audioButton.setVisibility(View.VISIBLE);
                } else {
                    audioButton.setVisibility(View.GONE);
                    sendButton.setVisibility(View.VISIBLE);
                }
            }
        });



        audioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                i++;
                if (ContextCompat.checkSelfPermission(MainActivityNew.this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivityNew.this,
                            new String[]{Manifest.permission.RECORD_AUDIO},
                            REQUEST_RECORD_AUDIO_PERMISSION);
                } else {
                    if(getcreditBalance()>0){
                        welcomeTextView.setVisibility(View.GONE);
                        int balance = getcreditBalance()-1;
                        creditBalance(balance);
                        balanceTXT.setText(balance + " credits");
                        questionistext=false;
                        dialogVoice.show();
                        dialogVoice.updateText("in listening");
                        if (tts.isSpeaking()) {
                            tts.stop();
                        }
                        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                        intent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true);
                        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
                        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "in listening");
                        try {
                            speechRecognizer.startListening(intent);
                        } catch (ActivityNotFoundException e) {
                            dialogVoice.dismiss();
                            System.out.println("Speech recognition not supported on this device");
                        }

                    }
                    else{
                        showDialog("GET CREDITS!",MainActivityNew.this);
                    }
                    if (i>=2){
                        i=0;
                        if (mInterstitialAd != null) {
                            mInterstitialAd.show(MainActivityNew.this);
                        }
                    }
                }

            }
        });


        menuBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivityNew.this, MenuActivity.class);
                startActivity(intent);
            }
        });

        clearChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new AlertDialog.Builder(MainActivityNew.this)
                        .setTitle("Clear history?")
                        .setMessage("Are you sure you want to delete chat history? AI will not remember last messages and will start new chat!")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                mySession.clearSession();
                                messageList.clear();
                                messageAdapter.notifyDataSetChanged();

                                clearChat.setVisibility(View.GONE);
                                welcomeTextView.setVisibility(View.VISIBLE);
                            }
                        })

                        // A null listener allows the button to dismiss the dialog and take no further action.
                        .setNegativeButton(android.R.string.no, null)
                        .setIcon(R.drawable.baseline_clear_all_24)
                        .show();

            }
        });

        RewardedAd.load(MainActivityNew.this, getResources().getString(R.string.admob_rewarded_id), adRequest, new RewardedAdLoadCallback() {
            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                rewardedAd = null;
            }

            @Override
            public void onAdLoaded(@NonNull RewardedAd rewardedAd) {
                MainActivityNew.this.rewardedAd = rewardedAd;
            }
        });


        creditBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog("Watch ads get coin", MainActivityNew.this);
            }
        });


        String character_desc = getString(R.string.character_desc);
        int conversation_max_tokens = Integer.parseInt(getString(R.string.conversation_max_tokens));
        mySession = new Session(conversation_max_tokens, character_desc);

        //setup recycler view
        messageAdapter = new MessageAdapter(messageList, lc,this);
        recyclerView.setAdapter(messageAdapter);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setStackFromEnd(true);
        recyclerView.setLayoutManager(llm);

        recyclerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tts.isSpeaking()){
                    tts.stop();
                }
                else {
                    tts.stop();
                }
            }
        });



        messageEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if (messageEditText.length()<1){
                    sendButton.setVisibility(View.GONE);
                    audioButton.setVisibility(View.VISIBLE);
                }
                else{
                    sendButton.setVisibility(View.VISIBLE);
                    audioButton.setVisibility(View.GONE);
                }

            }
        });


        sendButton.setOnClickListener((v) -> {
            i++;

            if(getcreditBalance()>0){
                int balance = getcreditBalance()-1;
                creditBalance(balance);
                balanceTXT.setText(balance + " credits");
                loadingDialog.show();

                try {
                    String question = messageEditText.getText().toString().trim();
                    addToChat(question, Message.SENT_BY_ME);
                    messageEditText.setText("");
                    callAPI(question);
                    welcomeTextView.setVisibility(View.GONE);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
            else {
                showDialog("GET CREDITS!",MainActivityNew.this);
            }
            if (i>=10){
                i=0;
                if (mInterstitialAd != null) {
                    mInterstitialAd.show(MainActivityNew.this);
                }
            }

        });
    }

    void addToChat(String message, String sentBy) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                messageList.add(new Message(message, sentBy));
                messageAdapter.notifyDataSetChanged();
                recyclerView.smoothScrollToPosition(messageAdapter.getItemCount());
                clearChat.setVisibility(View.VISIBLE);

            }
        });
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


    @Override
    protected void onResume() {
        super.onResume();
        tts = new TextToSpeech(this, this);

        balanceTXT.setText(Integer.toString(getcreditBalance()) + " credits");

    }

    @Override
    protected void onDestroy() {
        if (tts.isSpeaking()) {
            tts.stop();
        }
        speechRecognizer.destroy();

        tts.shutdown();
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        if (tts.isSpeaking()) {
            tts.stop();
        }
        tts.shutdown();
        super.onPause();
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            int result = tts.setLanguage(Locale.US);
            if (result == TextToSpeech.LANG_MISSING_DATA ||
                    result == TextToSpeech.LANG_NOT_SUPPORTED
            ) {
                System.out.println("Language not supported");
            }

        } else {
            System.out.println("Failed to initialize TextToSpeech");
        }

    }


    @Override
    public void onReadyForSpeech(Bundle params) {
        // Java equivalent code of: binding.question.text = "Speak now";
        //binding.question.setText("Speak now");
       // addToChat("Speak now",Message.SENT_BY_ME);

    }

    @Override
    public void onBeginningOfSpeech() {}

    @Override
    public void onRmsChanged(float rmsdB) {}

    @Override
    public void onBufferReceived(byte[] buffer) {}

    @Override
    public void onEndOfSpeech() {}

    @Override
    public void onError(int error) {
        System.out.println("Speech recognition error: " + error);
        dialogVoice.dismiss();
    }

    @Override
    public void onResults(Bundle results) {
        ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        if (matches != null && !matches.isEmpty()) {
            addToChat(matches.get(0), Message.SENT_BY_ME);
            try {
                callAPI(matches.get(0));
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }

        }
        else{
            dialogVoice.dismiss();

        }
    }

    @Override
    public void onPartialResults(Bundle partialResults) {
        dialogVoice.show();
        ArrayList<String> partialResultList = partialResults.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        if (partialResultList != null && !partialResultList.isEmpty()) {
            String partialResult = partialResultList.get(0);
            dialogVoice.updateText(partialResult.toString());

        }
        else{
            dialogVoice.dismiss();
        }
    }

    @Override
    public void onEvent(int eventType, Bundle params) {

    }



    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_RECORD_AUDIO_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission granted
                    // Do something
                } else {
                    // Permission denied
                    // Do something else
                }
                break;
        }
    }



    void addResponse(String response) {
        messageList.remove(messageList.size() - 1);
        addToChat(response, Message.SENT_BY_BOT);

        dialogVoice.dismiss();
        loadingDialog.dismiss();
        if (tts.isSpeaking()) {
            tts.stop();
        }
        else{
            if (!response.isEmpty()&&questionistext==false) {
                //.setImageResource(R.drawable.baseline_volume_off_24);

                tts.speak(response, TextToSpeech.QUEUE_FLUSH, null, null);
            }
        }
    }

    void callAPI(String question) throws JSONException {
        if (question.compareToIgnoreCase("Clear Memory") == 0) {
            mySession.clearSession();
            addToChat("Memory cleared", Message.SENT_BY_BOT);
            return;
        }
        //okhttp
        messageList.add(new Message(getString(R.string.Typing), Message.SENT_BY_BOT));
        JSONArray newQuestion = mySession.buildSessionQuery(question);

        JSONObject jsonBody = new JSONObject();
        try {
            //String newQuery = newQuestion.toString();
            jsonBody.put("model", "gpt-3.5-turbo");
            jsonBody.put("messages", newQuestion);
            jsonBody.put("max_tokens", 1200);
            jsonBody.put("temperature", 0.9);
            jsonBody.put("top_p", 1);
            jsonBody.put("frequency_penalty", 0.0);
            jsonBody.put("presence_penalty", 0.0);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(jsonBody.toString(), JSON);
        Request request = new Request.Builder()
                .url("https://api.openai.com/v1/chat/completions")
                .header("Authorization", api_key_firebase)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                addResponse(getString(R.string.failed_load_response));
//                mySession.clearSession();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        JSONArray jsonArray = jsonObject.getJSONArray("choices");
//                        Log.i(TAG, jsonArray.toString());
                        JSONObject jsonTokens = jsonObject.getJSONObject("usage");
                        int total_tokens = jsonTokens.getInt("total_tokens");
                        int completion_tokens = jsonTokens.getInt("completion_tokens");
                        if (completion_tokens > 0) {
                            String result = jsonArray.getJSONObject(0).getJSONObject("message").getString("content");//text
                            mySession.saveSession(total_tokens, result);
                            addResponse(result.trim());
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    addResponse(getString(R.string.failed_load_response));
//                    mySession.clearSession();
                }
            }
        });


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
        if (Integer.parseInt(getResources().getString(R.string.howmany_credits_every_day))>0){
            cointxt.setText("You will get " + getResources().getString(R.string.howmany_credit_each_ad) + " coins for each watched ads. Also when you open the application, you will receive "+ getResources().getString(R.string.howmany_credits_every_day)+" coins once a day.");
        }
        else {
            cointxt.setText("You will get " + getResources().getString(R.string.howmany_credit_each_ad) + " coins for each watched ads"+"");
        }

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
                    RewardedAd.load(MainActivityNew.this, getResources().getString(R.string.admob_rewarded_id), adRequest, new RewardedAdLoadCallback() {
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

                    rewardedAd.show(MainActivityNew.this, new OnUserEarnedRewardListener() {
                        @Override
                        public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                            creditBalance(getcreditBalance() + Integer.parseInt(getResources().getString(R.string.howmany_credit_each_ad)));
                            Toast.makeText(getApplicationContext(), "You got " + getResources().getString(R.string.howmany_credit_each_ad)+" credits!", Toast.LENGTH_LONG).show();
                            balanceTXT.setText(getcreditBalance() + " credits");
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


    @Override
    public void userText(String editTextValue) {
        messageEditText.setText(""+editTextValue);
    }
}




















