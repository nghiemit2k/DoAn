package com.devzen.chatbotai.chathistory;


import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.speech.tts.TextToSpeech;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.devzen.chatbotai.R;

import java.util.List;
import java.util.Locale;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MyViewHolder> {

    List<Message> messageList;
    List<Message> time;
    View.OnLongClickListener onLongClickListener;
    TextToSpeech tts;
    MyInterfaceEtUser myInterfaceEtUser;


    public MessageAdapter(List<Message> messageList, View.OnLongClickListener longClickListener, MyInterfaceEtUser myInterfaceEtUser) {
        this.messageList = messageList;
        this.time = messageList;
        this.myInterfaceEtUser = myInterfaceEtUser;
        onLongClickListener = longClickListener;

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View chatView = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item, null);
        MyViewHolder myViewHolder = new MyViewHolder(chatView);

        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Message message = messageList.get(position);
        Message currentDateandTime = time.get(position);
        if (message.getSentBy().equals(Message.SENT_BY_ME)) {
            holder.leftChatView.setVisibility(View.GONE);
            holder.rightChatView.setVisibility(View.VISIBLE);
            holder.rightTextView.setText(message.getMessage());
        } else {
            holder.rightChatView.setVisibility(View.GONE);
            holder.leftChatView.setVisibility(View.VISIBLE);
            holder.leftTextView.setText(message.getMessage());
        }
        holder.botanswertime.setText(currentDateandTime.getTime());
        holder.usersendtime.setText(currentDateandTime.getTime());


        tts = new TextToSpeech(holder.itemView.getContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    tts.setLanguage(Locale.US);

                }
            }
        });


        holder.tts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!message.getMessage().toString().isEmpty()) {
                    if (tts.isSpeaking()) {
                        //   holder.tts.setImageResource(R.drawable.baseline_volume_up_24);
                        tts.stop();
                    } else {
                        //  holder.tts.setImageResource(R.drawable.baseline_volume_off_24);
                        tts.speak(message.getMessage().toString(), TextToSpeech.QUEUE_FLUSH, null, null);
                    }
                }

            }
        });


        holder.reanswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        holder.share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_TEXT, message.getMessage().toString());
                holder.share.getContext().startActivity(Intent.createChooser(shareIntent, holder.share.getResources().getString(R.string.send_to)));
                Toast.makeText(holder.share.getContext(), "Select app for sharing", Toast.LENGTH_LONG).show();

            }
        });


        holder.copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager clipboardManager = (ClipboardManager) holder.copy.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clipData = ClipData.newPlainText("text", message.getMessage());
                clipboardManager.setPrimaryClip(clipData);
                Toast.makeText(holder.copy.getContext(), "Text copied", Toast.LENGTH_LONG).show();
                Vibrator vibrator = (Vibrator) holder.copy.getContext().getSystemService(Context.VIBRATOR_SERVICE);
                if (vibrator.hasVibrator()) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        vibrator.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE));
                    } else {
                        vibrator.vibrate(100);
                    }
                }
            }
        });

        holder.editUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                myInterfaceEtUser.userText(holder.rightTextView.getText().toString() + " ");


                Vibrator vibrator = (Vibrator) holder.copy.getContext().getSystemService(Context.VIBRATOR_SERVICE);
                if (vibrator.hasVibrator()) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        vibrator.vibrate(VibrationEffect.createOneShot(55, VibrationEffect.DEFAULT_AMPLITUDE));
                    } else {
                        vibrator.vibrate(55);
                    }
                }
                // holder.query.setText();

            }
        });


    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        LinearLayout leftChatView, rightChatView;
        TextView leftTextView, rightTextView, usersendtime, botanswertime;
        ImageView reAnswer, share, copy, tts, reanswer, editUser;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            leftChatView = itemView.findViewById(R.id.left_chat_view);
            rightChatView = itemView.findViewById(R.id.right_chat_view);
            leftTextView = itemView.findViewById(R.id.left_chat_text_view);
            rightTextView = itemView.findViewById(R.id.right_chat_text_view);
            usersendtime = itemView.findViewById(R.id.question_time);
            botanswertime = itemView.findViewById(R.id.answer_time);
            editUser = itemView.findViewById(R.id.editUserInput);
            share = itemView.findViewById(R.id.sharebotResponse);
            copy = itemView.findViewById(R.id.copybotResponse);
            tts = itemView.findViewById(R.id.ttsButton);
            reanswer = itemView.findViewById(R.id.reAnswer);


            leftTextView.setOnLongClickListener(onLongClickListener);
            rightTextView.setOnLongClickListener(onLongClickListener);

        }

    }


}
