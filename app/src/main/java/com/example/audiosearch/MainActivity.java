package com.example.audiosearch;

import static com.arthenica.mobileffmpeg.Config.RETURN_CODE_CANCEL;
import static com.arthenica.mobileffmpeg.Config.RETURN_CODE_SUCCESS;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;

import com.arthenica.mobileffmpeg.Config;
import com.arthenica.mobileffmpeg.FFmpeg;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    Button buttonClear;
    Button buttonSearch;
    Button buttonStart;
    EditText editTextSearch;
    Intent serviceIntent;
    RecyclerView recyclerView;
    SpeechToTextConverter obj;
    String filename;
    SearchAudioFiles searchAudioFiles;
    public static MediaPlayer mplayer;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

         buttonClear=findViewById(R.id.button_clear);
         buttonSearch=findViewById(R.id.button_search);
         editTextSearch=findViewById(R.id.edittext_search);
         recyclerView=findViewById(R.id.recyclerview);
         buttonStart=findViewById(R.id.button_start);
         serviceIntent=new Intent(this,audioTrackerService.class);
         obj=new SpeechToTextConverter(getApplicationContext());
         searchAudioFiles=new SearchAudioFiles(getApplicationContext());
        //filename="android.resource:///com.example.audiosearch/raw/audio.mp3";
        filename = "storage/emulated/0/Music/audio_1.opus";
        mplayer=new MediaPlayer();



        File f_map = new File("/data/data/" + getPackageName() +  "/shared_prefs/" + "MAP_PERF" + ".xml");
        File f_count=new File("/data/data/" + getPackageName() +  "/shared_prefs/" + "COUNT_PERF" + ".xml");
        if(!f_map.exists()){
            SharedPreferences.Editor map= getSharedPreferences("MAP_PERF",Context.MODE_PRIVATE).edit();
            map.putString("dummy","dummy");
            map.apply();
        }

        if(!f_count.exists()){
            SharedPreferences.Editor cnt= getSharedPreferences("COUNT_PERF",Context.MODE_PRIVATE).edit();
            cnt.putInt("counter",0);
            cnt.apply();
        }
        String p="storage/emulated/0/Android/"+getPackageName()+"/converted/";
        File fd=new File(p);
        if(!fd.exists() && !fd.isDirectory())
        {// create empty directory
            if (fd.mkdirs())
            {
                Log.d("tilak","App dir created");
            }
            else
            {
                Log.d("tilak","Unable to create app dir!");
            }
        }
        else
        {
            Log.d("tilak","App dir already exists");
        }
        Log.d("tilak",p);





        buttonClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editTextSearch.setText("");
                String f_in="storage/emulated/0/Download/Atrangi_Re.mp4";

                int rc = FFmpeg.execute("-i "+ f_in+" storage/emulated/0/Download/file2.wav");

                if (rc == RETURN_CODE_SUCCESS) {
                    Log.i(Config.TAG, "Command execution completed successfully.");
                } else if (rc == RETURN_CODE_CANCEL) {
                    Log.i(Config.TAG, "Command execution cancelled by user.");
                } else {
                    Log.i(Config.TAG, String.format("Command execution failed with rc=%d and the output below.", rc));
                    Config.printLastCommandOutput(Log.INFO);
                }


            }
        });

        buttonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String word=editTextSearch.getText().toString();

                ArrayList<String> audioList=searchAudioFiles.searchFiles(word);
//                if(mplayer!=null){
//                    mplayer.stop();
//                    mplayer.reset();
//                }
                audioAdaptor adaptor=new audioAdaptor(audioList,getApplication(),mplayer);
                recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.super.getApplicationContext()));
                recyclerView.setAdapter(adaptor);

            }
        });

        buttonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startService(serviceIntent);

            }
        });

    }

    @Override
    protected void onDestroy() {
        stopService(serviceIntent);
        super.onDestroy();
    }
}