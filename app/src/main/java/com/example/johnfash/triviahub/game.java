package com.example.johnfash.triviahub;

import android.content.Intent;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class game extends AppCompatActivity {
    //        Toast.makeText(game.this,"NAME: "+getIntent().getStringExtra("NAME")+" CATEGORY: "+getIntent().getStringExtra("CATEGORY"),Toast.LENGTH_SHORT).show();
    public DBAdapterForPlayers myDbPlayer;
    public DBAdapterForSettings myDbSettings;
    public boolean twice = false;
    private QuestionLibrary mQuestionLibrary = new QuestionLibrary();
    public LinearLayout firstScreen_Play;
    public TextView startingTimer, category, playerName, timerText, playerScore, live, question;
    public Button aButton, bButton, cButton, dButton;
    public String soundBoolean, musicBoolean, timerString, name, categoryName;
    public int mScore = 0;
    private int mLive = 9;
    private int mQuestionNumber = 0;
    private int mDefaultNumber = 0;
    public MediaPlayer player;
    public SoundPool mySound;
    public int buttonId;
    public int gameOptionId;
    public int sadToneId;
    private String mAnswer;
    public int counter = 3;
    public int counter2 = 0;
    public long timerString2int = 0;
    public boolean isButtonClicked = false;
    public CountDownTimer countdown;
    public Animation alpha1;
    public Object[] get;
    public String oldScore;
    public long idNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        name = getIntent().getStringExtra("NAME");
        categoryName = getIntent().getStringExtra("CATEGORY");
        openDBForPlayers();
        openDBSettings();
        Cursor settingCursor = myDbSettings.getAllRows();
        if (settingCursor.moveToFirst()) {
            soundBoolean = settingCursor.getString(1);
            musicBoolean = settingCursor.getString(2);
            timerString = settingCursor.getString(3);
        }

        mySound = new SoundPool(3, AudioManager.STREAM_MUSIC, 0);
        buttonId = mySound.load(this, R.raw.buttonsound, 1);
        gameOptionId = mySound.load(this, R.raw.gameoption, 1);
        sadToneId = mySound.load(this, R.raw.sad_tone, 1);

        player = MediaPlayer.create(this, R.raw.new_gametone);
        playerMusic();

        if ("7 seconds".equals(timerString)) {
            counter2 = 7;
            timerString2int = 8000;
        } else if ("10 seconds".equals(timerString)) {
            counter2 = 10;
            timerString2int = 11000;
        } else if ("15 seconds".equals(timerString)) {
            counter2 = 15;
            timerString2int = 16000;
        } else {
            counter2 = 20;
            timerString2int = 21000;
        }

        Cursor u = myDbPlayer.getRowByName(name);
        oldScore = u.getString(2);
        idNumber = u.getLong(0);


        Integer[] arr = new Integer[59];
        for (int i = 0; i < arr.length; i++) {
            arr[i] = i;
        }
        List myList = Arrays.asList(arr);
        Collections.shuffle(myList);
        get = myList.toArray();

        setContentView(R.layout.activity_game);
        firstScreen_Play = findViewById(R.id.firstScreen_Play);
        startingTimer = findViewById(R.id.startingTimer);
        category = findViewById(R.id.category);
        playerName = findViewById(R.id.playerName);
        timerText = findViewById(R.id.timerText);
        playerScore = findViewById(R.id.playerScore);
        live = findViewById(R.id.live);
        question = findViewById(R.id.question);
        aButton = findViewById(R.id.aButton);
        bButton = findViewById(R.id.bButton);
        cButton = findViewById(R.id.cButton);
        dButton = findViewById(R.id.dButton);
        category.setText(categoryName);
        playerName.setText(name);
        alpha1 = AnimationUtils.loadAnimation(this, R.anim.alpha);
        new CountDownTimer(4000, 1000) {
            public void onTick(long millisUntilFinished) {
                startingTimer.setText(String.valueOf(counter));
                counter--;

            }

            public void onFinish() {
                this.cancel();
                startingTimer.setText("");
                startingTimer.setVisibility(View.GONE);
                updateQuestion(get);
                firstScreen_Play.setVisibility(View.VISIBLE);
            }
        }.start();

//        aButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//             aButton.startAnimation(alpha1);
//            }
//        });

        aButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                isButtonClicked = true;
                if (mLive > -1 && mDefaultNumber != 61 ) {
                    if (aButton.getText() == mAnswer) {
                        play(gameOptionId);
                        mScore += 1;
                        aButton.setBackgroundResource(R.drawable.curved_question_button_correct);
                        aButton.startAnimation(alpha1);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                playerScore.setText(Integer.toString(mScore));
                                resetButtonColor();
                                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                isButtonClicked = false;
                                updateQuestion(get);
                            }
                        }, 1700L);
                    } else if (bButton.getText() == mAnswer){
                        mLive --;
                        play(sadToneId);
                        aButton.setBackgroundResource(R.drawable.curved_question_button_wrong);
                        bButton.setBackgroundResource(R.drawable.curved_question_button_correct);
                        bButton.startAnimation(alpha1);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                live.setText(Integer.toString(mLive));
                                resetButtonColor();
                                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                isButtonClicked = false;
                                updateQuestion(get);
                            }
                        }, 1700L);
                    } else if (cButton.getText() == mAnswer){
                        mLive--;
                        play(sadToneId);
                        aButton.setBackgroundResource(R.drawable.curved_question_button_wrong);
                        cButton.setBackgroundResource(R.drawable.curved_question_button_correct);
                        cButton.startAnimation(alpha1);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                live.setText(Integer.toString(mLive));
                                resetButtonColor();
                                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                isButtonClicked = false;
                                updateQuestion(get);
                            }
                        }, 1700L);
                    }else {
                        mLive--;
                        play(sadToneId);
                        aButton.setBackgroundResource(R.drawable.curved_question_button_wrong);
                        dButton.setBackgroundResource(R.drawable.curved_question_button_correct);
                        dButton.startAnimation(alpha1);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                live.setText(Integer.toString(mLive));
                                resetButtonColor();
                                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                isButtonClicked = false;
                                updateQuestion(get);
                            }
                        }, 1700L);
                    }
                } else if (mDefaultNumber == 61){
                    Toast.makeText(game.this,"End of demo A button",Toast.LENGTH_SHORT).show();
//                    do something if live is zero
                }
            }
        });

        bButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                isButtonClicked = true;
                if (mLive > -1 && mDefaultNumber != 61 ) {
                    if (bButton.getText() == mAnswer) {
                        play(gameOptionId);
                        mScore += 1;
                        bButton.setBackgroundResource(R.drawable.curved_question_button_correct);
                        bButton.startAnimation(alpha1);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                playerScore.setText(Integer.toString(mScore));
                                resetButtonColor();
                                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                isButtonClicked = false;
                                updateQuestion(get);
                            }
                        }, 1700L);
                    } else if (aButton.getText() == mAnswer){
                        play(sadToneId);
                        mLive --;
                        bButton.setBackgroundResource(R.drawable.curved_question_button_wrong);
                        aButton.setBackgroundResource(R.drawable.curved_question_button_correct);
                        aButton.startAnimation(alpha1);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                live.setText(Integer.toString(mLive));
                                resetButtonColor();
                                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                isButtonClicked = false;
                                updateQuestion(get);
                            }
                        }, 1700L);
                    } else if (cButton.getText() == mAnswer){
                        play(sadToneId);
                        mLive--;
                        bButton.setBackgroundResource(R.drawable.curved_question_button_wrong);
                        cButton.setBackgroundResource(R.drawable.curved_question_button_correct);
                        cButton.startAnimation(alpha1);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                live.setText(Integer.toString(mLive));
                                resetButtonColor();
                                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                isButtonClicked = false;
                                updateQuestion(get);
                            }
                        }, 1700L);
                    }else {
                        mLive--;
                        play(sadToneId);
                        bButton.setBackgroundResource(R.drawable.curved_question_button_wrong);
                        dButton.setBackgroundResource(R.drawable.curved_question_button_correct);
                        dButton.startAnimation(alpha1);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                live.setText(Integer.toString(mLive));
                                resetButtonColor();
                                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                isButtonClicked = false;
                                updateQuestion(get);
                            }
                        }, 1700L);
                    }
                } else if (mDefaultNumber == 61){
                    Toast.makeText(game.this,"End of demo B button",Toast.LENGTH_SHORT).show();
//                    do something if live is zero
                }
            }
        });


        cButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                isButtonClicked = true;
                if (mLive > -1 && mDefaultNumber != 61 ) {
                    if (cButton.getText() == mAnswer) {
                        play(gameOptionId);
                        mScore += 1;
                        cButton.setBackgroundResource(R.drawable.curved_question_button_correct);
                        cButton.startAnimation(alpha1);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                playerScore.setText(Integer.toString(mScore));
                                resetButtonColor();
                                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                isButtonClicked = false;
                                updateQuestion(get);
                            }
                        }, 1700L);
                    } else if (aButton.getText() == mAnswer){
                        play(sadToneId);
                        mLive --;
                        cButton.setBackgroundResource(R.drawable.curved_question_button_wrong);
                        aButton.setBackgroundResource(R.drawable.curved_question_button_correct);
                        aButton.startAnimation(alpha1);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                live.setText(Integer.toString(mLive));
                                resetButtonColor();
                                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                isButtonClicked = false;
                                updateQuestion(get);
                            }
                        }, 1700L);
                    } else if (bButton.getText() == mAnswer){
                        play(sadToneId);
                        mLive--;
                        cButton.setBackgroundResource(R.drawable.curved_question_button_wrong);
                        bButton.setBackgroundResource(R.drawable.curved_question_button_correct);
                        bButton.startAnimation(alpha1);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                live.setText(Integer.toString(mLive));
                                resetButtonColor();
                                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                isButtonClicked = false;
                                updateQuestion(get);
                            }
                        }, 1700L);
                    }else {
                        mLive--;
                        play(sadToneId);
                        cButton.setBackgroundResource(R.drawable.curved_question_button_wrong);
                        dButton.setBackgroundResource(R.drawable.curved_question_button_correct);
                        dButton.startAnimation(alpha1);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                live.setText(Integer.toString(mLive));
                                resetButtonColor();
                                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                isButtonClicked = false;
                                updateQuestion(get);
                            }
                        }, 1700L);
                    }
                } else if (mDefaultNumber == 61){
                    Toast.makeText(game.this,"End of demo C button",Toast.LENGTH_SHORT).show();
//                    do something if live is zero
                }
            }
        });


        dButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                isButtonClicked = true;
                if (mLive > -1 && mDefaultNumber != 61 ) {
                    if (dButton.getText() == mAnswer) {
                        play(gameOptionId);
                        mScore += 1;
                        dButton.setBackgroundResource(R.drawable.curved_question_button_correct);
                        dButton.startAnimation(alpha1);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                playerScore.setText(Integer.toString(mScore));
                                resetButtonColor();
                                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                isButtonClicked = false;
                                updateQuestion(get);
                            }
                        }, 1700L);
                    } else if (aButton.getText() == mAnswer){
                        play(sadToneId);
                        mLive --;
                        dButton.setBackgroundResource(R.drawable.curved_question_button_wrong);
                        aButton.setBackgroundResource(R.drawable.curved_question_button_correct);
                        aButton.startAnimation(alpha1);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                live.setText(Integer.toString(mLive));
                                resetButtonColor();
                                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                isButtonClicked = false;
                                updateQuestion(get);
                            }
                        }, 1700L);
                    } else if (bButton.getText() == mAnswer){
                        play(sadToneId);
                        mLive--;
                        dButton.setBackgroundResource(R.drawable.curved_question_button_wrong);
                        bButton.setBackgroundResource(R.drawable.curved_question_button_correct);
                        bButton.startAnimation(alpha1);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                live.setText(Integer.toString(mLive));
                                resetButtonColor();
                                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                isButtonClicked = false;
                                updateQuestion(get);
                            }
                        }, 1700L);
                    }else {
                        mLive--;
                        play(sadToneId);
                        dButton.setBackgroundResource(R.drawable.curved_question_button_wrong);
                        cButton.setBackgroundResource(R.drawable.curved_question_button_correct);
                        cButton.startAnimation(alpha1);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                live.setText(Integer.toString(mLive));
                                resetButtonColor();
                                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                isButtonClicked = false;
                                updateQuestion(get);
                            }
                        }, 1700L);
                    }
                } else if (mDefaultNumber == 61){
                    Toast.makeText(game.this,"End of demo D button",Toast.LENGTH_SHORT).show();
//                    do something if live is zero
                }
            }
        });



//        end of oncreate method
    }

    private void openDBForPlayers() {
        myDbPlayer = new DBAdapterForPlayers(this);
        myDbPlayer.open();
    }

    private void openDBSettings() {
        myDbSettings = new DBAdapterForSettings(this);
        myDbSettings.open();
    }


    public void updateQuestion(Object get) {
        if (mLive >= 0 && mDefaultNumber != 60){
            Object[] like = (Object[]) get;
            mQuestionNumber = (int) like[mDefaultNumber];
            question.setText(mQuestionLibrary.getQuestion(mQuestionNumber));
            aButton.setText(mQuestionLibrary.getChoice1(mQuestionNumber));
            bButton.setText(mQuestionLibrary.getChoice2(mQuestionNumber));
            cButton.setText(mQuestionLibrary.getChoice3(mQuestionNumber));
            dButton.setText(mQuestionLibrary.getChoice4(mQuestionNumber));
            mAnswer = mQuestionLibrary.getCorrectAnswer(mQuestionNumber);
            mDefaultNumber++;
            startTimer();
        }else if (mDefaultNumber == 20){
            Toast.makeText(game.this,"End of demo Update",Toast.LENGTH_SHORT).show();
        }else if(mLive < 0){
            countdown.cancel();
            gameOverThings();
        }
    }
    public void startTimer(){
       if (mLive >= 0 && mDefaultNumber != 61){
           countdown = new CountDownTimer(timerString2int, 1000) {
               @Override
               public void onTick(long millisUntilFinished) {
                   timerText.setText(String.valueOf((millisUntilFinished/1000)));
                   if (isButtonClicked){
                       this.cancel();
                   }
               }

               @Override
               public void onFinish() {
                   mLive--;
                   live.setText(Integer.toString(mLive));
                   this.cancel();
                   isButtonClicked = false;
                   updateQuestion(get);
               }
           }.start();
       }
    }

    private void resetButtonColor(){
        aButton.setBackgroundResource(R.drawable.curved_question_button);
        bButton.setBackgroundResource(R.drawable.curved_question_button);
        cButton.setBackgroundResource(R.drawable.curved_question_button);
        dButton.setBackgroundResource(R.drawable.curved_question_button);
    }

    public void play(int soundId) {
        if ("on".equals(soundBoolean)) {
            mySound.play(soundId, 1, 1, 1, 0, 1);
        } else {
            mySound.pause(soundId);
        }
    }

    public void playerMusic() {
        if ("on".equals(musicBoolean)) {
            player.start();
            player.setLooping(true);
        } else {
            player.pause();
        }
    }

    @Override
    public void onBackPressed() {
        if (twice == true) {
            Intent change = new Intent(game.this, MainActivity.class);
            startActivity(change);
        }
        twice = true;

//        super.onBackPressed();    =   normal hardware button exit
//        in three seconds delay before twice turn true again
        Toast.makeText(game.this, "Please press BACK again to go HOME", Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                twice = false;
            }
        }, 1000);
    }

    @Override
    protected void onStop() {
        super.onStop();
        player.pause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        player.pause();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        if ("on".equals(musicBoolean)) {
            player.start();
            player.setLooping(true);
        } else {
            player.pause();
        }
    }

    public void gameOverThings(){
        String score = Integer.toString(mScore);
        int intNewScore = Integer.parseInt(oldScore)+mScore;
        String newScore = Integer.toString(intNewScore);
        firstScreen_Play.setVisibility(View.GONE);
        myDbPlayer.updateRow(idNumber,name,newScore);
        Intent change = new Intent(game.this, game_over.class);
        change.putExtra("SCORE", score);
        change.putExtra("SOUND", soundBoolean);
        change.putExtra("MUSIC", musicBoolean);
        change.putExtra("SCORETOTAL", newScore);
        change.putExtra("NAME", name);
        change.putExtra("CATEGORY", categoryName);
        startActivity(change);
    }

//    end of activity
}
