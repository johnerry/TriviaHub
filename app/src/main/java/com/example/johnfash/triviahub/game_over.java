package com.example.johnfash.triviahub;

import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class game_over extends AppCompatActivity {
    public boolean twice = false;
    public TextView gameOverScore,correctAnswer;
    public Button replay,homeButton;
    public String score,scoreTotal,soundBoolean,musicBoolean,name,categoryName;
    public MediaPlayer player;
    public SoundPool mySound;
    public int buttonId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        score = getIntent().getStringExtra("SCORE");
        scoreTotal = getIntent().getStringExtra("SCORETOTAL");
        soundBoolean = getIntent().getStringExtra("SOUND");
        musicBoolean = getIntent().getStringExtra("MUSIC");
        name = getIntent().getStringExtra("NAME");
        categoryName = getIntent().getStringExtra("CATEGORY");
        mySound = new SoundPool(3, AudioManager.STREAM_MUSIC, 0);
        buttonId = mySound.load(this, R.raw.buttonsound, 1);
        player = MediaPlayer.create(this, R.raw.gameoverman);
        playerMusic();

        setContentView(R.layout.activity_game_over);

        gameOverScore = findViewById(R.id.gameOverScore);
        correctAnswer = findViewById(R.id.correctAnswer);
        replay = findViewById(R.id.replay);
        homeButton = findViewById(R.id.homeButton);

        gameOverScore.setText("Score: "+scoreTotal);
        correctAnswer.setText(score);

        replay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                play(buttonId);
                Intent change = new Intent(game_over.this, game.class);
                change.putExtra("NAME", name);
                change.putExtra("CATEGORY", categoryName);
                startActivity(change);
            }
        });
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                play(buttonId);
                Intent change = new Intent(game_over.this, MainActivity.class);
                startActivity(change);
            }
        });

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
            Intent change = new Intent(game_over.this, MainActivity.class);
            startActivity(change);
        }
        twice = true;

//        super.onBackPressed();    =   normal hardware button exit
//        in three seconds delay before twice turn true again
        Toast.makeText(game_over.this, "Please press BACK again to go HOME", Toast.LENGTH_SHORT).show();
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

}
