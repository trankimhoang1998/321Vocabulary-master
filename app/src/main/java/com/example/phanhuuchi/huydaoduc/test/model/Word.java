package com.example.phanhuuchi.huydaoduc.test.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;

import com.example.phanhuuchi.huydaoduc.test.Main.MyMediaPlayer;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

/**
 * Created by HuyDaoDuc on 20/11/2017.
 */

public class Word implements Serializable, Cloneable {
    private int Id;
    private String Ten;
    private String Mota;
    private String TheLoai;
    private byte[] Image;
    private byte[] Sound;

    public static MediaPlayer mediaPlayer = new MediaPlayer();            // dùng để phát âm thanh

    public Word(){

    }

    // clone
    public Word(Word word){
        Id = word.getId();
        Ten = word.getTen();
        Mota = word.getMota();
        Image = word.getImage();
        Sound = word.getSound();
    }


    public Word(String ten, String mota){
        Ten = ten;
        Mota = mota;
    }

    public Word(int id, String ten, String mota, byte[] image, byte[] sound) {
        Id = id;
        Ten = ten;
        Mota = mota;
        Image = image;
        Sound = sound;
    }

    public int getId() {
        return Id;
    }

    public Word setId(int id) {
        Id = id;
        return this;
    }

    public String getTen() {
        return Ten;
    }

    public Word setTen(String ten) {
        Ten = ten;
        return this;
    }

    public String getMota() {
        return Mota;
    }

    public Word setMota(String mota) {
        Mota = mota;
        return this;
    }

    public String getTheLoai() {
        return TheLoai;
    }

    public void setTheLoai(String theLoai) {
        TheLoai = theLoai;
    }

    public byte[] getImage() {
        return Image;
    }

    public Word setImage(byte[] image) {
        Image = image;
        return this;
    }
    //Chi:
    //// IMAGE
    // Các hàm hỗ trợ
    // Uri --> byte Array
    static public byte[] ImageUriToByteArray(Context context, Uri uri)
    {
        final InputStream imageStream;
        Bitmap selectedBitmap = null;
        try {
            // load bitmap from uri
            imageStream = context.getContentResolver().openInputStream(uri);
            selectedBitmap = BitmapFactory.decodeStream(imageStream);

            // convert to byte array
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            selectedBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);     // PNG cũng đc vì bản chất nó đẩy về array

            return stream.toByteArray();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
    // byte array --> Bitmap
    static public Bitmap ByteArrayToBitmap(byte[] bitmapdata)
    {
        return BitmapFactory.decodeByteArray(bitmapdata, 0, bitmapdata.length);
    }

    public Bitmap getImageBitmap() {
        if(Image == null)
            return null;

        Bitmap bm = BitmapFactory.decodeByteArray(Image,0,Image.length);
        return bm;
    }


    //// SOUND
    public byte[] getSound() {
        return Sound;
    }

    public void PlaySound(Context c) {
        if(getSound() != null)
        {
            try {
                // để phát âm 1 byte array ta cần tạo 1 file mp3 tạm vào bộ nhớ và dùng mediaplayer để phát file đó
                File tempMp3 = File.createTempFile("sound", "mp3", c.getCacheDir());
                tempMp3.deleteOnExit();
                FileOutputStream fos = new FileOutputStream(tempMp3);
                fos.write(getSound());
                fos.close();

                // đặt resource
                FileInputStream fis = new FileInputStream(tempMp3);
                MyMediaPlayer.getInstance().play(c,fis.getFD());

            } catch (IOException ex) {
                String s = ex.toString();
                ex.printStackTrace();
            }
        }
    }

    public static void StopPlayingSound()
    {
        MyMediaPlayer.getInstance().stop();
    }

    public Word setSound(byte[] sound) {
        Sound = sound;
        return this;
    }
    // hỗ trợ
    public static byte[] SoundUriToByteArray(Context context, Uri uri) {
        byte[] soundBytes = null;
        try {
            InputStream is = context.getContentResolver().openInputStream(uri);

            // lấy độ dài của input stream
            soundBytes = new byte[is.available()];

            ByteArrayOutputStream baos = null;
            try {
                // convert qua byte arr
                baos = new ByteArrayOutputStream();
                byte[] buff = new byte[1024000];
                int i = Integer.MAX_VALUE;
                while ((i = is.read(buff, 0, buff.length)) > 0) {
                    baos.write(buff, 0, i);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            soundBytes = baos.toByteArray();
        } catch(Exception e) {
            e.printStackTrace();
        }
        return  soundBytes;
    }


}

/*public class Word {
    //Dùng design pattems builder
    private int Id;
    private String Ten;
    private String Mota;

    public Word(Builder builder){
        this.Id = builder.WordId;
        this.Ten = builder.WordTen;
        this.Mota = builder.WordMota;
    }

    public int getId() {
        return Id;
    }

    public String getTen() {
        return Ten;
    }

    public String getMota() {
        return Mota;
    }


    public static class Builder{

        private int WordId;
        private String WordTen;
        private String WordMota;
        private byte[] WordImage;

        public Builder setWordId(int wordId) {
            WordId = wordId;
            return this;
        }

        public Builder setWordTen(String wordTen) {
            WordTen = wordTen;
            return this;
        }

        public Builder setWordMota(String wordMota) {
            WordMota = wordMota;
            return this;
        }

        public Builder setWordImage(byte[] wordImage) {
            WordImage = wordImage;
            return this;
        }

        public Word build(){
            return new Word(this);
        }
    }

    public static boolean validateInput (String WordId, String WordTen, String WordMota){
        return (TextUtils.isEmpty(WordId) || TextUtils.isEmpty(WordTen) || TextUtils.isEmpty(WordMota))
                ? true: false;
    }
}*/
