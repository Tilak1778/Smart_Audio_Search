package com.example.audiosearch;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.util.ArrayList;

import es.claucookie.miniequalizerlibrary.EqualizerView;

public class audioAdaptor extends RecyclerView.Adapter<audioAdaptor.ViewHolder> {

    private ArrayList<String> mAudioList;
    SearchAudioFiles searchAudioFiles;
    Context mContext;
    MediaPlayer mMediaPlayer;
    SoundPlayer soundPlayer;
    int indexOfPlaying=-1;


    public audioAdaptor(ArrayList<String> list,Context context,MediaPlayer player){
        mAudioList=list;
        mContext=context;
        mMediaPlayer=player;
        soundPlayer=new SoundPlayer();


    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater=LayoutInflater.from(parent.getContext());
        View itemView=inflater.inflate(R.layout.audio_item,parent,false);
        ViewHolder viewHolder=new ViewHolder(itemView);

        return viewHolder;
    }

    @Override
    public void onViewAttachedToWindow(@NonNull ViewHolder holder) {
        super.onViewAttachedToWindow(holder);
    }

    @Override
    public void onViewDetachedFromWindow(@NonNull ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String item=mAudioList.get(position);
        holder.bind(position);
        holder.itemTextView.setText(getTitle(item));

        holder.itemTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("tilak","item: Clicked: "+holder.getAdapterPosition());
                //searchAudioFiles=new SearchAudioFiles(mContext);
                String audioPath=(mAudioList.get(holder.getAdapterPosition()));
                Log.d("tilak","index of playing: "+indexOfPlaying);
                indexOfPlaying=holder.getAdapterPosition();


                Log.d("tilak","position : " +holder.getAdapterPosition());

//                if(mMediaPlayer!=null){
//                    mMediaPlayer.stop();
//                    mMediaPlayer.reset();
//                }
                if(audioPath.contains(".mp4")){

                    String vidPath=audioPath.substring(0,audioPath.length()-4);

                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(audioPath));
                    intent.setDataAndType(Uri.parse(vidPath), "video/mp4");
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    mContext.startActivity(intent);
                }else
                {

                    try {
                        if (mMediaPlayer!=null){
                            mMediaPlayer.stop();
                            mMediaPlayer.reset();

                        }
                        mMediaPlayer.setDataSource(mContext, Uri.parse(audioPath));
                        mMediaPlayer.prepare();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            Log.d("tilak","onCompletion called");
                            Log.d("tilak","visibility before: " +holder.equalizerView.getVisibility());
                            mp.stop();
                            mp.reset();
                            //notifyDataSetChanged();
                            holder.equalizerView.stopBars();
                            holder.equalizerView.setVisibility(View.INVISIBLE);
                            holder.itemTextView.setTextColor(mContext.getColor(R.color.white));
                            Log.d("tilak","visibility: " +holder.equalizerView.getVisibility());



                        }
                    });
                    mMediaPlayer.start();
                    notifyDataSetChanged();


                }



               // soundPlayer.play(mContext.getApplicationContext(), audioPath,mMediaPlayer);

            }
        });

    }

    @Override
    public int getItemCount() {
        return mAudioList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView itemTextView;
        EqualizerView equalizerView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemTextView=itemView.findViewById(R.id.item_textview);
            equalizerView=itemView.findViewById(R.id.equalizer_anim);

        }
        public void bind(int index){
            if(indexOfPlaying==index){
                equalizerView.setVisibility(View.VISIBLE);
                equalizerView.animateBars();
                itemTextView.setTextColor(mContext.getResources().getColor(R.color.cherry_red));
            }else{
                    equalizerView.stopBars();
                    equalizerView.setVisibility(View.INVISIBLE);
                    itemTextView.setTextColor(mContext.getResources().getColor(R.color.white));
            }
        }


    }

    public String getTitle(String path){
        String title="";

        for(int i=0;i<path.length();i++){
            if(path.charAt(i)=='/'){
                title="";
            }else{
                title=title+path.charAt(i);
            }
        }

        return title;
    }


}
