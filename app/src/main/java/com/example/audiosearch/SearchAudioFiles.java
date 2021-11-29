package com.example.audiosearch;

import android.content.Context;
import android.util.JsonReader;
import android.util.Log;

import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

public class SearchAudioFiles {

    Context mContext;
    Map<String, ?> allEntries;
    public SearchAudioFiles(Context context){
        mContext=context;
    }

    public void getFileMap(){
         allEntries= mContext.getSharedPreferences("MAP_PERF", Context.MODE_PRIVATE).getAll();

    }

    public ArrayList<String> getMatchedFiles(String searchedWord){
        ArrayList<String> matchedFiles=new ArrayList<>();

        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            Log.d("map values", entry.getKey() + ": " + entry.getValue().toString());
            String textPath=entry.getKey();
            try {
                File file=new File(textPath);
                FileInputStream in = new FileInputStream(file);
                int len = 0;
                byte[] data1 = new byte[1024];

                while ( -1 != (len = in.read(data1)) ){
                    String data=new String(data1, 0, len);
                    if(data.contains(searchedWord)){
                        Log.d("tilak data ",textPath);
                        matchedFiles.add(entry.getValue().toString());
                        break;
                    }


                }


            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        return matchedFiles;
    }

    public ArrayList<String> searchFiles(String searchWord){
        ArrayList<String> files=new ArrayList<>();
        getFileMap();
        files=getMatchedFiles(searchWord);

        return files;
    }

    public String getAudioFilePath(String textFilePath){
        getFileMap();
        return allEntries.get(textFilePath).toString();
    }






}
