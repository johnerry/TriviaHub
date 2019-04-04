package com.example.johnfash.triviahub;

import android.content.Intent;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    public DBAdapterForPlayers myDb;
    public DBAdapterForSettings myDbSettings;
    public boolean twice = false;
    public LinearLayout firstScreen_Home, secondScreen_Score, thirdScreen_Settings, fourthScreen_Player, fifthScreen_NewPlayer, sixthScreen_Category;
    public Button play, score, settings, scoreBackButton;
    public Switch soundSwitch, musicSwitch;
    public Spinner timerSpinner;
    public Button settingsBackButton, previousPlayer, nextPlayer;
    public TextView selectedPlayer;
    public Button next, playerBackButton, addNewPlayer;
    public EditText inputPlayerName;
    public Button next2, newPlayerBackButton, technology, sports, history, entertainment, random, categoryBackButton;
    public MediaPlayer player;
    public SoundPool mySound;
    public int buttonId;
    public int gameOptionId;
    public int switchSoundId;
    public int timerId,sadToneId;
    public TextView scoreText, playerText;
    public String soundBoolean, musicBoolean, timerString, syncSound, syncMusic;
    public int nextOrPreviousNumber = 0;
    public String name = "";
    public String category = "";
    public Animation scale1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        openDB();
        openDBSettings();
        Cursor settingCursor = myDbSettings.getAllRows();
        final Cursor playerCursor = myDb.getAllRows();

        if (settingCursor.moveToFirst()) {
            soundBoolean = settingCursor.getString(1);
            musicBoolean = settingCursor.getString(2);
            timerString = settingCursor.getString(3);
        }


        syncSound = soundBoolean;
        syncMusic = musicBoolean;

        setContentView(R.layout.activity_main);

        populateListView();
        selectedPlayer = findViewById(R.id.selectedPlayer);
        scale1 = AnimationUtils.loadAnimation(this, R.anim.scale);
        addNewPlayer = findViewById(R.id.addNewPlayer);

        final String[] result = new String[playerCursor.getCount()];
        playerCursor.moveToFirst();
        if (playerCursor.getCount() == 0) {
            selectedPlayer.setText("");
            addNewPlayer.startAnimation(scale1);
        } else {
            for (int i = 0; i < playerCursor.getCount(); i++) {
                String row = playerCursor.getString(1);
                result[i] = row;
                playerCursor.moveToNext();
            }
            selectedPlayer.setText(result[0]);
        }

        mySound = new SoundPool(3, AudioManager.STREAM_MUSIC, 0);
        buttonId = mySound.load(this, R.raw.buttonsound, 1);
        gameOptionId = mySound.load(this, R.raw.gameoption, 1);
        switchSoundId = mySound.load(this, R.raw.switchsound, 1);
        sadToneId = mySound.load(this, R.raw.sad_tone, 1);
        timerId = mySound.load(this, R.raw.timer, 1);

        player = MediaPlayer.create(this, R.raw.new_gametone);
        playerMusic();

        firstScreen_Home = findViewById(R.id.firstScreen_Home);
        secondScreen_Score = findViewById(R.id.secondScreen_Score);
        thirdScreen_Settings = findViewById(R.id.thirdScreen_Settings);
        fourthScreen_Player = findViewById(R.id.fourthScreen_Player);
        fifthScreen_NewPlayer = findViewById(R.id.fifthScreen_NewPlayer);
        sixthScreen_Category = findViewById(R.id.sixthScreen_Category);
        play = findViewById(R.id.play);
        score = findViewById(R.id.score);
        settings = findViewById(R.id.settings);
        scoreBackButton = findViewById(R.id.scoreBackButton);
        soundSwitch = findViewById(R.id.soundSwitch);
        musicSwitch = findViewById(R.id.musicSwitch);
        timerSpinner = findViewById(R.id.timerSpinner);
        settingsBackButton = findViewById(R.id.settingsBackButton);
        previousPlayer = findViewById(R.id.previousPlayer);
        nextPlayer = findViewById(R.id.nextPlayer);
        next = findViewById(R.id.next);
        playerBackButton = findViewById(R.id.playerBackButton);
        inputPlayerName = findViewById(R.id.inputPlayerName);
        next2 = findViewById(R.id.next2);
        newPlayerBackButton = findViewById(R.id.newPlayerBackButton);
        technology = findViewById(R.id.technology);
        sports = findViewById(R.id.sports);
        history = findViewById(R.id.history);
        entertainment = findViewById(R.id.entertainment);
        random = findViewById(R.id.random);
        categoryBackButton = findViewById(R.id.categoryBackButton);
        play.startAnimation(scale1);

        if ("on".equals(soundBoolean)) {
            soundSwitch.setChecked(true);
        } else {
            soundSwitch.setChecked(false);
        }
        if ("on".equals(musicBoolean)) {
            musicSwitch.setChecked(true);
        } else {
            musicSwitch.setChecked(false);
        }

//        start of  firstScreen_Home layout methods are here
        score.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                play(buttonId);
                populateListView();
                displayLayout(firstScreen_Home, thirdScreen_Settings, fourthScreen_Player, fifthScreen_NewPlayer, sixthScreen_Category, secondScreen_Score);
            }
        });


//        clicking the settings button
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                play(buttonId);
                displayLayout(firstScreen_Home, secondScreen_Score, fourthScreen_Player, fifthScreen_NewPlayer, sixthScreen_Category, thirdScreen_Settings);
            }
        });
//        clicking the play button
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                play(buttonId);
                displayLayout(firstScreen_Home, secondScreen_Score, thirdScreen_Settings, fifthScreen_NewPlayer, sixthScreen_Category, fourthScreen_Player);
            }
        });

//        clicking the score layout back button
        scoreBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                play(buttonId);
                displayLayout(thirdScreen_Settings, secondScreen_Score, fourthScreen_Player, fifthScreen_NewPlayer, sixthScreen_Category, firstScreen_Home);
            }
        });
//        end of secondScreen_Score methods


//        start of thirdScreen_Settings methods
//        setting switch method goes here
        soundSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                play(switchSoundId);
                if (isChecked == true) {
                    syncSound = "on";
                    myDbSettings.updateRow(1, "on", musicBoolean, timerString);
                } else {
                    syncSound = "off";
                    myDbSettings.updateRow(1, "off", musicBoolean, timerString);
                }
            }
        });

        musicSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                play(switchSoundId);
                if (isChecked == true) {
                    syncMusic = "on";
                    myDbSettings.updateRow(1, soundBoolean, "on", timerString);
                    playerMusic();
                } else {
                    syncMusic = "off";
                    myDbSettings.updateRow(1, soundBoolean, "off", timerString);
                    playerMusic();
                }
            }
        });

        //        appending array timer list to spinner adapter
        ArrayAdapter adapter = ArrayAdapter.createFromResource(
                this,
                R.array.timer,
                R.layout.my_spinner);
        Spinner time = findViewById(R.id.timerSpinner);
        time.setAdapter(adapter);

        if ("five".equals(timerString)) {
            time.setSelection(0);
        } else if ("ten".equals(timerString)) {
            time.setSelection(1);
        } else if ("fifteen".equals(timerString)) {
            time.setSelection(2);
        } else if ("twenty".equals(timerString)) {
            time.setSelection(3);
        }

        time.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                play(timerId);
                if (position == 0) {
                    myDbSettings.updateRow(1, soundBoolean, musicBoolean, "7 seconds");
                } else if (position == 1) {
                    myDbSettings.updateRow(1, soundBoolean, musicBoolean, "10 seconds");
                } else if (position == 2) {
                    myDbSettings.updateRow(1, soundBoolean, musicBoolean, "15 seconds");
                } else if (position == 3) {
                    myDbSettings.updateRow(1, soundBoolean, musicBoolean, "20 seconds");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

//        setting funtionality to back button of setting layout
        settingsBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                play(buttonId);
                displayLayout(thirdScreen_Settings, secondScreen_Score, fourthScreen_Player, fifthScreen_NewPlayer, sixthScreen_Category, firstScreen_Home);
            }
        });
//        end of thirdScreen_Settings layout methods

//        start of fourthScreen_Player layout methods
//        implementing the drawable previous button
        previousPlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (result.length < 2 || result == null || result.length == 0) {

                } else if (selectedPlayer.getText().equals(result[0])) {

                } else {
                    play(buttonId);
                    selectedPlayer.setText(result[nextOrPreviousNumber - 1]);
                    nextOrPreviousNumber--;
                }
            }

        });

//        implementing the drawable next button
        nextPlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (result.length < 2 || result == null || result.length == 0) {

                } else if (selectedPlayer.getText().equals(result[result.length - 1])) {

                } else {
                    play(buttonId);
                    selectedPlayer.setText(result[nextOrPreviousNumber + 1]);
                    nextOrPreviousNumber++;
                }
//                Toast.makeText(MainActivity.this,pl, Toast.LENGTH_SHORT).show();
            }
        });

//        implementing the next option button
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (result == null || result.length == 0) {
                    Toast.makeText(MainActivity.this, "No player record, Add new", Toast.LENGTH_SHORT).show();
                } else {
                    play(buttonId);
                    name = selectedPlayer.getText().toString();
                    displayLayout(firstScreen_Home, secondScreen_Score, thirdScreen_Settings, fourthScreen_Player, fifthScreen_NewPlayer, sixthScreen_Category);
                }
            }
        });

//        implementing fourthScreen_Player back button
        playerBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                play(buttonId);
                displayLayout(thirdScreen_Settings, secondScreen_Score, fourthScreen_Player, fifthScreen_NewPlayer, sixthScreen_Category, firstScreen_Home);
            }
        });

//        implementing the addNewPlayer button
        addNewPlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                play(buttonId);
                displayLayout(firstScreen_Home, secondScreen_Score, thirdScreen_Settings, fourthScreen_Player, sixthScreen_Category, fifthScreen_NewPlayer);
            }
        });
//        end of fourthScreen_Player methods

//        start of fifthScreen_NewPlayer methods
//        implementing the inputPlayerName edittext value
        inputPlayerName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                if (!inputPlayerName.getText().toString().matches("^[a-zA-Z0-9 ]{3,20}$")){
//                    inputPlayerName.setError("Only alphanumeric and space allowed!");
//                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!inputPlayerName.getText().toString().matches("^[a-zA-Z0-9 ]+$")) {
                    inputPlayerName.setError("Only alphanumeric and space allowed!");
                } else {

                }
            }
        });


//        implementing the next2 button for addNewPlayer case
        next2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                play(buttonId);
                if (inputPlayerName.getText().toString() != "" && inputPlayerName.getText().toString().matches("^[a-zA-Z0-9 ]+$")) {
                    String data = inputPlayerName.getText().toString().trim();
                    Boolean n = false;
//                    Use of already created array to find element instead of querying the database
                    if (playerCursor.getCount() != 0) {
                        for (int c = 0; c < result.length; c++) {
                            if (data.equalsIgnoreCase(result[c].toString().trim())) {
                                n = true;
                                break;
                            }
                        }

                        if (n == true) {
                            inputPlayerName.setError("Name already EXIST");
                        } else {
                            name = inputPlayerName.getText().toString();
                            myDb.inserRow(name, "0");
                            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                            displayLayout(firstScreen_Home, secondScreen_Score, thirdScreen_Settings, fourthScreen_Player, fifthScreen_NewPlayer, sixthScreen_Category);
                        }
                    } else {
                        name = inputPlayerName.getText().toString();
                        myDb.inserRow(name, "0");
                        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                        displayLayout(firstScreen_Home, secondScreen_Score, thirdScreen_Settings, fourthScreen_Player, fifthScreen_NewPlayer, sixthScreen_Category);
                    }
                } else {
                    inputPlayerName.setError("Only alphanumeric and space allowed!");
                }
            }
        });

//        implementing the newPlayerBackButton method
        newPlayerBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                play(buttonId);
                displayLayout(firstScreen_Home, secondScreen_Score, thirdScreen_Settings, fifthScreen_NewPlayer, sixthScreen_Category, fourthScreen_Player);
            }
        });
//        end of fifthScreen_NewPlayer methods

//        start of sixthScreen_Category method
//        implementing technology method
        technology.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                category = "Technology";
//                play(buttonId);
//                Intent change = new Intent(MainActivity.this, game.class);
//                change.putExtra("NAME", name);
//                change.putExtra("CATEGORY", category);
//                startActivity(change);
                Toast.makeText(MainActivity.this,"Not AVAILABLE, Please choose RANDOM",Toast.LENGTH_SHORT).show();

            }
        });

        //        implementing sports method
        sports.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                category = "Sports";
//                play(buttonId);
//                Intent change = new Intent(MainActivity.this, game.class);
//                change.putExtra("NAME", name);
//                change.putExtra("CATEGORY", category);
//                startActivity(change);
                Toast.makeText(MainActivity.this,"Not AVAILABLE, Please choose RANDOM",Toast.LENGTH_SHORT).show();

            }
        });

        //        implementing history method
        history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                category = "History";
//                play(buttonId);
//                Intent change = new Intent(MainActivity.this, game.class);
//                change.putExtra("NAME", name);
//                change.putExtra("CATEGORY", category);
//                startActivity(change);
                Toast.makeText(MainActivity.this,"Not AVAILABLE, Please choose RANDOM",Toast.LENGTH_SHORT).show();

            }
        });

        //        implementing entertainment method
        entertainment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                category = "Entertainment";
//                play(buttonId);
//                Intent change = new Intent(MainActivity.this, game.class);
//                change.putExtra("NAME", name);
//                change.putExtra("CATEGORY", category);
//                startActivity(change);
                Toast.makeText(MainActivity.this,"Not AVAILABLE, Please choose RANDOM",Toast.LENGTH_SHORT).show();

            }
        });

        //        implementing random method
        random.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                category = "Random";
                play(buttonId);
                Intent change = new Intent(MainActivity.this, game.class);
                change.putExtra("NAME", name);
                change.putExtra("CATEGORY", category);
                startActivity(change);

            }
        });

        //        implementing categoryBackButton method
        categoryBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                play(buttonId);
                finish();
                overridePendingTransition(0, 0);
                startActivity(getIntent());
                overridePendingTransition(0, 0);
            }
        });


//    end of saved instant state oncreate method
    }

    //        start of public, private function to be run inside the oncreate state
    @Override
    public void onBackPressed() {
        if (twice == true) {
            Intent exitApp = new Intent(Intent.ACTION_MAIN);
            exitApp.addCategory(Intent.CATEGORY_HOME);
            exitApp.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(exitApp);
            finish();
            System.exit(0);
        }
        twice = true;

//        super.onBackPressed();    =   normal hardware button exit
//        in three seconds delay before twice turn true again
        Toast.makeText(MainActivity.this, "Please press BACK again to exit", Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                twice = false;
            }
        }, 1000);
    }

    public void displayLayout(LinearLayout hide1, LinearLayout hide2, LinearLayout hide3, LinearLayout hide4, LinearLayout hide5, LinearLayout show) {
        hide1.setVisibility(View.GONE);
        hide2.setVisibility(View.GONE);
        hide3.setVisibility(View.GONE);
        hide4.setVisibility(View.GONE);
        hide5.setVisibility(View.GONE);
        show.setVisibility(View.VISIBLE);
    }

    public void play(int soundId) {
        if ("on".equals(soundBoolean) && "on".equals(syncSound)) {
            mySound.play(soundId, 1, 1, 1, 0, 1);
        } else {
            mySound.pause(soundId);
        }
    }

    public void playerMusic() {
        if ("on".equals(musicBoolean) && "on".equals(syncMusic)) {
            player.start();
            player.setLooping(true);
        } else {
            player.pause();
        }
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
        if ("on".equals(musicBoolean) && "on".equals(syncMusic)) {
            player.start();
            player.setLooping(true);
        } else {
            player.pause();
        }
    }

    private void openDB() {
        myDb = new DBAdapterForPlayers(this);
        myDb.open();
    }

    private void openDBSettings() {
        myDbSettings = new DBAdapterForSettings(this);
        myDbSettings.open();
    }

    //    populating the custom adapter with players details
    private void populateListView() {
        Cursor cursor = myDb.getAllRows();
        String[] fromFieldNames = new String[]{DBAdapterForPlayers.KEY_NAME, DBAdapterForPlayers.KEY_SCORE};
        int[] toViewIDs = new int[]{R.id.textViewItemNumber, R.id.textViewItemTask};
        SimpleCursorAdapter myCursorAdaptor;
        myCursorAdaptor = new SimpleCursorAdapter(getBaseContext(), R.layout.item_layout, cursor, fromFieldNames, toViewIDs, 0);
        ListView myList = (ListView) findViewById(R.id.listView);
        myList.setAdapter(myCursorAdaptor);
    }

}
