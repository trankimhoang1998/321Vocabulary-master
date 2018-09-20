package com.example.phanhuuchi.huydaoduc.test.Main;

import android.content.Context;
import android.media.MediaPlayer;

import java.io.FileDescriptor;
import java.io.IOException;

/**
 * Created by Admin on 1/1/2018.
 */

public class MyMediaPlayer {

    // dùng quản lý âm thanh

    static MyMediaPlayer myMediaPlayer;

    private MediaPlayer _mediaPlayer;

    private boolean _isMute;

    MyMediaPlayer()
    {
        _isMute = false;
        _mediaPlayer = new MediaPlayer();
    }

    static public MyMediaPlayer getInstance()
    {
        if(myMediaPlayer == null)
        {
            myMediaPlayer = new MyMediaPlayer();
            return myMediaPlayer;
        }
        else
            return myMediaPlayer;
    }

    public boolean isPlaying()
    {
        return _mediaPlayer.isPlaying();
    }

    public void stop()
    {
        if(_mediaPlayer != null)
            if(this.isPlaying())
                _mediaPlayer.stop();
    }

    // nhận Resource là rId
    public void play(Context c, int rid) {
        if(!_isMute)
        {
            // resetting mediaplayer
            _mediaPlayer.reset();

            // đặt resource
            _mediaPlayer = MediaPlayer.create(c, rid);

            _mediaPlayer.start();
        }
    }

    // nhận resource là FileInputStream
    public void play(Context c, FileDescriptor fd) {
        if(!_isMute)
        {
            // resetting mediaplayer
            _mediaPlayer.reset();

            // đặt resource
            try {
                _mediaPlayer.setDataSource(fd);

                _mediaPlayer.prepare();
                _mediaPlayer.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void playNew(Context c, int rid) {
        if(!_isMute)
        {
            // Hàm này cho phép âm chạy độc lập k bị ảnh hưởng bởi các âm khác

            // đặt resource
            MediaPlayer mediaPlayer = MediaPlayer.create(c, rid);

            // vì thg này chạy độc lập nên khi chạy xong thì giải phóng bộ nhớ
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    // tắt các sound đang chạy
                    if (mediaPlayer != null) {
                        mediaPlayer.release();
                        mediaPlayer = null;
                    }
                }
            });

            mediaPlayer.start();
        }
    }

    public boolean isMute() {
        return _isMute;
    }

    public void setMute(boolean _isMute) {
        this._isMute = _isMute;
    }
}
