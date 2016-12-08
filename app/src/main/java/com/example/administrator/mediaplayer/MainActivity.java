package com.example.administrator.mediaplayer;

import java.util.LinkedList;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity implements View.OnClickListener, MediaPlayer.OnCompletionListener {
    //MediaPlayer物件
    private MediaPlayer mediaPlayer;

    private Button btnPrev;
    private Button btnNext;
    private Button btnPlay;
    private Button btnStop;
    private TextView txtSongName;

    //儲存音樂清單
    private LinkedList<Song> songList;

    //音樂播放索引(播到哪一首)
    private int index = 0;
    //是否為暫停狀態
    private boolean isPause;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        getMusics();
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }



    private void initView() {
        btnPrev = (Button) findViewById(R.id.btnPrev);
        btnNext = (Button) findViewById(R.id.btnNext);
        btnPlay = (Button) findViewById(R.id.btnPlay);
        btnStop = (Button) findViewById(R.id.btnStop);

        btnPrev.setOnClickListener(this);
        btnNext.setOnClickListener(this);
        btnPlay.setOnClickListener(this);
        btnStop.setOnClickListener(this);

        txtSongName = (TextView) findViewById(R.id.txtSongName);
        txtSongName.setText("");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnPrev:
                doPrev();
                break;
            case R.id.btnNext:
                doNext();
                break;

            case R.id.btnPlay:
                doPlay();
                break;

            case R.id.btnStop:
                doStop();
                break;

        }
    }

    private void doStop() {
        if (mediaPlayer != null) {
            isPause = false;
            mediaPlayer.stop();
            btnPlay.setText("Play");
        }

    }

    private void doPlay() {
        if (songList == null || songList.size() == 0) {
            return;
        }

        if (btnPlay.getText().toString().equals("Play")) {
            playing();
            btnPlay.setText("Pause");
        }else{
            isPause = true;
            mediaPlayer.pause();
            btnPlay.setText("Play");
        }

    }

    private void doNext() {
        if (songList == null || songList.size() == 0) {
            return;
        }

        if (index < songList.size() - 1) {
            index++;
            isPause = false;
            playing();
            btnPlay.setText("Pause");
        }
    }

    private void doPrev() {
        if (songList == null || songList.size() == 0) {
            return;
        }

        if (index > 0) {
            index--;
            isPause = false;
            playing();
            btnPlay.setText("Pause");
        }
    }

    private void playing(){
        if (mediaPlayer != null && !isPause) {
            mediaPlayer.release();
            mediaPlayer = null;
        }

        if (mediaPlayer == null) {
            long id = songList.get(index).getId();
            Uri songUri = ContentUris.withAppendedId(android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id);

            mediaPlayer = MediaPlayer.create(this, songUri);
            mediaPlayer.setOnCompletionListener(this);
        }

        mediaPlayer.start();

        txtSongName.setText("曲目: " + songList.get(index).getTitle() +
                "\n專輯: " + songList.get(index).getAlbum() +
                "\n(" + (index + 1) + "/" + songList.size() + ")");
    }


    private void getMusics(){
        songList = new LinkedList<Song>();


        ContentResolver contentResolver = getContentResolver();
        Uri uri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor cursor = contentResolver.query(uri, null, null, null, null);
        if (cursor == null) {
            Log.d("=======>", "查詢錯誤");
        } else if (!cursor.moveToFirst()) {
            Log.d("=======>", "沒有媒體檔");
        } else {
            int titleColumn = cursor.getColumnIndex(android.provider.MediaStore.Audio.Media.TITLE);
            int idColumn = cursor.getColumnIndex(android.provider.MediaStore.Audio.Media._ID);
            int albumColumn = cursor.getColumnIndex(android.provider.MediaStore.Audio.AudioColumns.ALBUM);
            do {
                long thisId = cursor.getLong(idColumn);
                String thisTitle = cursor.getString(titleColumn);
                String thisAlbum = cursor.getString(albumColumn);
                Log.d("=======>", "id: " + thisId + ", title: " + thisTitle);
                Song song = new Song();
                song.setId(thisId);
                song.setTitle(thisTitle);
                song.setAlbum(thisAlbum);

                songList.add(song);
            } while (cursor.moveToNext());
        }

        txtSongName.setText("共有 " + songList.size() + " 首歌曲");
    }



    @Override
    public void onCompletion(MediaPlayer mp) {
        doNext();
    }
}
