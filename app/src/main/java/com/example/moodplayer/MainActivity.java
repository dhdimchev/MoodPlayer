package com.example.moodplayer;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;

import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.spotify.protocol.types.Track;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    //The Client Id is taken from the registration of the app in spotify.developers and the redirect id is the url of the page inside the developers dashboard.
    private static final String CLIENT_ID = "72ddaaa7b1cf4a1e985ec3752105940f";
    private static final String REDIRECT_URI = "https://developer.spotify.com/dashboard/applications/72ddaaa7b1cf4a1e985ec3752105940f";
    private SpotifyAppRemote mSpotifyAppRemote;
    private boolean pause;
    //A variable that holds the current mood /The default is bored.
    private String mood = "bored";
    // A list that holds all of the played songs.
    public List<String> playedSongs = new ArrayList<>();

    // On create set the content to activity main as this is the main layout
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


    }
    // On start authenticate the user of spotify in order to use Spotify's API
    @Override
    protected void onStart() {
        super.onStart();
        ConnectionParams connectionParams =
                new ConnectionParams.Builder(CLIENT_ID)
                        .setRedirectUri(REDIRECT_URI)
                        .showAuthView(true)
                        .build();

        SpotifyAppRemote.connect(this, connectionParams, new Connector.ConnectionListener() {
            @Override
            public void onConnected(SpotifyAppRemote spotifyAppRemote) {
                mSpotifyAppRemote = spotifyAppRemote;
                Log.d("MainActivity", "Connected successfully!");

            }

            @Override
            public void onFailure(Throwable throwable) {
                Log.e("MainActivity", throwable.getMessage(), throwable);
            }

        });
    }
    //Disconnect from spotify app remote
    @Override
    protected void onStop() {
        super.onStop();
        SpotifyAppRemote.disconnect(mSpotifyAppRemote);
    }
    //When the Play button is clicked pass the value of the spinner to the play() method to play a song.
    public void playSong(View MainActivity) {
        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        mood = spinner.getSelectedItem().toString().toLowerCase();
        setContentView(R.layout.activity_player);

        play(mood);
    }
    // plays song from a playlist depending on the currently selected mood (key = mood selected in spinner)
    @SuppressLint("SetTextI18n")
    private void play(String key) {
        //Setting the current mood
        playerSetup();
        //Choosing a playlist for the current mood.
        switch (key) {
            case "happiness":
                mSpotifyAppRemote.getPlayerApi().play("spotify:playlist:5apyfDc5hudFwYTfeMbF6t");


                break;
            case "sadness":
                mSpotifyAppRemote.getPlayerApi().play("spotify:playlist:2ialHGHKmEjAK522BxXEfH");

                break;
            case "anger":
                mSpotifyAppRemote.getPlayerApi().play("spotify:playlist:47CtQQLVA1ucH2STYOvG7A");

                break;
            case "surprise":
                mSpotifyAppRemote.getPlayerApi().play("spotify:playlist:4svaeSt9p8RopBOr37aIPt");

                break;
            case "fear":
                mSpotifyAppRemote.getPlayerApi().play("spotify:playlist:6ACEZ7GO8z6PjIIf0jhlH8");

                break;
            case "tired":
                mSpotifyAppRemote.getPlayerApi().play("spotify:playlist:00TnyYOnm3B257VxBfL5yQ");

                break;
            case "relax":
                mSpotifyAppRemote.getPlayerApi().play("spotify:playlist:284nPuSmzE0ZVRk7GUXv6B");

                break;

            case "bored":
            default:// By default the boredom playlist is played
                mSpotifyAppRemote.getPlayerApi().play("spotify:playlist:7nh1zJEQMviETrd6i9BmWW");

        }


    }

    // Updates the Image to the image of the current song
    @SuppressLint("SetTextI18n")
    private void updateSongAndImage(TextView song, TextView artist, ImageView songImage){
        mSpotifyAppRemote.getPlayerApi().subscribeToPlayerState().setEventCallback(playerState -> {
            final Track track = playerState.track;
            if (track != null) {
                Log.d("MainActivity", track.name + " by " + track.artist.name);
                song.setText(track.name);
                artist.setText("by " + track.artist.name);
//Here a the song is added to the played sons list on order to later be displayed on the TextView inside records.
                if (!playedSongs.contains(track.name + "\n by \n" + track.artist.name + "\n\n\n")) {
                    playedSongs.add(track.name + "\n by \n" + track.artist.name + "\n\n\n");
                    Log.d("MainActivity", playedSongs.get(playedSongs.size() - 1));


                }

            }
        });

        mSpotifyAppRemote.getPlayerApi().subscribeToPlayerState().setEventCallback(playerState -> {
            final Track track = playerState.track;
            if (track != null) {
                mSpotifyAppRemote.getImagesApi().getImage(track.imageUri).setResultCallback(bitmap -> {
                    /*
                    Bitmap is rounded and transferred into an image drawable.
                    Then the imageview is set to the image drawable
                    */
                    RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(getResources(), bitmap);

                    final float roundPx = (float) bitmap.getWidth() * 0.06f;
                    roundedBitmapDrawable.setCornerRadius(roundPx);
                    //songImage layout is set to the rounded bitmap
                    songImage.setImageDrawable(roundedBitmapDrawable);
                });
            }


        });
    }
    //used to exit the player layout and go back to the main layout
    public void moodShift(View activity_player) {

        setContentView(R.layout.activity_main);


    }
    //used to go back to the player layout without changing the mood.
    public void player(View MainActivity) {

        setContentView(R.layout.activity_player);
        playerSetup();


    }
    //Sets up the player layout with the right backgroundColor/textColor theme
    // as well as sets the mood according to the selected mood in the spinner from activity_main
    @SuppressLint("SetTextI18n")
    private void playerSetup()
    {

        TextView song = (TextView) findViewById(R.id.songName);
        TextView artist = (TextView) findViewById(R.id.songAuthor);
        ImageView songImage = (ImageView) findViewById(R.id.songImage);
        ConstraintLayout player = (ConstraintLayout) findViewById(R.id.activityPlayer);
        TextView currentMood = (TextView) findViewById(R.id.mood);
        String capital = mood.substring(0, 1).toUpperCase() + mood.substring(1);
        currentMood.setText(capital + "!");
        switch (mood) {
            case "happiness":
                player.setBackgroundColor(Color.rgb(255, 154, 85));
                currentMood.setTextColor(Color.BLACK);
                song.setTextColor(Color.BLACK);
                artist.setTextColor(Color.BLACK);

                break;
            case "sadness":
                player.setBackgroundColor(Color.rgb(42, 59, 144));
                currentMood.setTextColor(Color.WHITE);
                song.setTextColor(Color.WHITE);
                artist.setTextColor(Color.WHITE);
                break;
            case "anger":
                player.setBackgroundColor(Color.rgb(201, 41, 41));
                currentMood.setTextColor(Color.WHITE);
                song.setTextColor(Color.WHITE);
                artist.setTextColor(Color.WHITE);
                break;
            case "surprise":
                player.setBackgroundColor(Color.rgb(201, 147, 111));
                currentMood.setTextColor(Color.BLACK);
                song.setTextColor(Color.BLACK);
                artist.setTextColor(Color.BLACK);
                break;
            case "fear":
                player.setBackgroundColor(Color.rgb(51, 36, 36));
                currentMood.setTextColor(Color.WHITE);
                song.setTextColor(Color.WHITE);
                artist.setTextColor(Color.WHITE);
                break;
            case "tired":
                player.setBackgroundColor(Color.rgb(208, 199, 116));
                currentMood.setTextColor(Color.BLACK);
                song.setTextColor(Color.BLACK);
                artist.setTextColor(Color.BLACK);
                break;
            case "relax":
                player.setBackgroundColor(Color.rgb(253, 248, 226));
                currentMood.setTextColor(Color.BLACK);
                song.setTextColor(Color.BLACK);
                artist.setTextColor(Color.BLACK);
                break;
            case "bored":
            default:// By default the boredom playlist is played
                player.setBackgroundColor(Color.rgb(15, 174, 122));
                currentMood.setTextColor(Color.BLACK);
                song.setTextColor(Color.BLACK);
                artist.setTextColor(Color.BLACK);
        }
        updateSongAndImage(song,artist,songImage);
    }
    //Used for going from main_activity to record activity
    public void record(View MainActivity) {
        setContentView(R.layout.record);
    }
    // Used for updating the record list when tapping on the screen.
    public void updateList(View MainActivity) {
        TextView list = (TextView) findViewById(R.id.recordList);
        if (playedSongs.isEmpty()) return;
        list.setMovementMethod(new ScrollingMovementMethod());
        if (!list.getText().toString().isEmpty()) {
            list.setText("");
        }
        updateListItem(list);
    }

    //appends an item at the end of the text View(The item is previously formatted with /n/n/n at the end of the string.)
    private void updateListItem(TextView list) {
        for (String item : playedSongs) {
            list.append(item);
            Log.d("MainActivity", item);
        }
    }
    //Request a skip to the next song
    public void next(View MainActivity) {
        mSpotifyAppRemote.getPlayerApi().skipNext().setResultCallback(empty -> {


        });
    }
    //Request a skip back to the previous song (If song has played less more than 3 seconds it plays it from the beginning)
    public void previous(View MainActivity) {
        mSpotifyAppRemote.getPlayerApi().skipPrevious().setResultCallback(empty -> {


        });
    }
    //Pauses or plays the current song on the click of the button
    public void PlayOrPause(View MainActivity) {
        mSpotifyAppRemote.getPlayerApi().subscribeToPlayerState().setEventCallback(playerState -> pause = playerState.isPaused);

        if (pause) {
            resume();
        } else {
            stop();
        }
    }
    //Pauses the player
    private void stop() {
        mSpotifyAppRemote.getPlayerApi().pause();
    }
    //Resumes the player
    private void resume() {
        mSpotifyAppRemote.getPlayerApi().resume();
    }

}



