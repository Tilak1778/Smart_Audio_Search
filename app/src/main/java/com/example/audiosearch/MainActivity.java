package com.example.audiosearch;

import static com.arthenica.mobileffmpeg.Config.RETURN_CODE_CANCEL;
import static com.arthenica.mobileffmpeg.Config.RETURN_CODE_SUCCESS;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.arthenica.mobileffmpeg.Config;
import com.arthenica.mobileffmpeg.FFmpeg;
import com.google.android.material.snackbar.Snackbar;

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
    private static final int STORAGE_PERMISSION_CODE = 101;



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
        //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        //    checkPermission(Manifest.permission.MANAGE_EXTERNAL_STORAGE, STORAGE_PERMISSION_CODE);
        //}
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                Snackbar.make(findViewById(android.R.id.content), "Permission needed!", Snackbar.LENGTH_INDEFINITE)
                        .setAction("Settings", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                try {
                                    Uri uri = Uri.parse("package:" + BuildConfig.APPLICATION_ID);
                                    Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION, uri);
                                    startActivity(intent);
                                } catch (Exception ex) {
                                    Intent intent = new Intent();
                                    intent.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                                    startActivity(intent);
                                }
                            }
                        })
                        .show();
            }
        }


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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    }

    // Function to check and request permission.
        public void checkPermission( String permission, int requestCode)
        {
            if (ContextCompat.checkSelfPermission(MainActivity.this, permission) == PackageManager.PERMISSION_DENIED) {
                // Requesting the permission
                ActivityCompat.requestPermissions(MainActivity.this, new String[] { permission }, requestCode);
            }
            else {
                Toast.makeText(MainActivity.this, "Permission already granted", Toast.LENGTH_SHORT).show();
            }
        }

    @Override
    protected void onDestroy() {
        stopService(serviceIntent);
        super.onDestroy();
    }
}