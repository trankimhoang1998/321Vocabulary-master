package com.example.phanhuuchi.huydaoduc.test.model;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.phanhuuchi.huydaoduc.test.Data.DBSQL;
import com.example.phanhuuchi.huydaoduc.test.Main.MainActivity;
import com.example.phanhuuchi.huydaoduc.test.Main.MyMediaPlayer;
import com.example.phanhuuchi.huydaoduc.test.R;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class Detail_Word extends AppCompatActivity {
//    @BindView(R.id.image)
//    ImageView image;
//    @BindView(R.id.editDetailTen)
//    EditText editTen;
//    @BindView(R.id.editDetailMota)
//    EditText editMota;

    EditText editTen;
    EditText editMota;
    ImageView imageDetail;
    ImageButton imageButtonSound;

    Button btnAddImage;
    Button btnAddSound;

    TextView txtTag;

    private Word _curWord;

    @Override
    protected void onPause() {
        MyMediaPlayer.getInstance().stop();
        super.onPause();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail__word);
        ButterKnife.bind(this);
        init();
        setWord();

    }

    private void setWord() {

        editTen.setText(_curWord.getTen());
        editMota.setText(_curWord.getMota());

        if(_curWord.getTheLoai() != null && _curWord.getTheLoai().length() > 0)
        {
            txtTag.setText(_curWord.getTheLoai());
            txtTag.setVisibility(View.VISIBLE);
        }
        else
            txtTag.setVisibility(View.INVISIBLE);

        imageDetail.setVisibility(View.INVISIBLE);
        if(_curWord.getImageBitmap() != null)
        {
            imageDetail.setImageBitmap(_curWord.getImageBitmap());
            imageDetail.setVisibility(View.VISIBLE);
        }


        imageButtonSound.setVisibility(View.INVISIBLE);
        if(_curWord.getSound() != null)
        {
            imageButtonSound.setVisibility(View.VISIBLE);
            imageButtonSound.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view)
                {
                    if(MyMediaPlayer.getInstance().isPlaying())
                    {
                        MyMediaPlayer.getInstance().stop();
                    }
                    else
                    {
                        _curWord.PlaySound(getApplication());
                    }
                }
            });
        }

    }

    //get data
    private void init(){
        int id = getIntent().getIntExtra(DBSQL.WORD_ID_KEY_PUT_EXTRA,0);
        _curWord = WordList.getWordById(id);


        // bind view
        editTen = findViewById(R.id.editDetailTen);
        editMota = findViewById(R.id.editDetailMota);
        imageDetail = findViewById(R.id.imageDetail);
        imageButtonSound = findViewById(R.id.imageButtonSound);
        txtTag = findViewById(R.id.textTag);





        //Toast.makeText(getApplicationContext(),"id= " + WordTen,Toast.LENGTH_SHORT).show();
    }


    @OnClick(R.id.btnEdit)
    public void btnEdit(View view){
        final Dialog dialog = new Dialog(this);
        dialog.setTitle(R.string.edit);
        dialog.setContentView(R.layout.edit_word);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        Button btnUpdate = dialog.findViewById(R.id.btnEdit);
        Button btncancel = dialog.findViewById(R.id.btnCancel);

        btnAddImage = dialog.findViewById(R.id.btnUpdateImage);
        btnAddSound = dialog.findViewById(R.id.btnUpdateSound);

        final EditText editten = dialog.findViewById(R.id.editTen);
        final EditText editmota = dialog.findViewById(R.id.editMota);
        final EditText edittag = dialog.findViewById(R.id.editTag);

        editten.setText(_curWord.getTen());
        editmota.setText(_curWord.getMota());
        edittag.setText(_curWord.getTheLoai());

        // kiểm tra xem có image hoặc sound chưa để set lại text
        if(_curWord.getImage() != null)
        {
            setButtonAddImageIcon(false);
        }
        if(_curWord.getSound() != null)
        {
            setButtonAddSoundIcon(false);
        }

        // add event cho image button, sound button
        //Chi:
        btnAddImage.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                if(btnAddImage.getText() == getString(R.string.remove_image))
                {
                    // nếu đã thêm ảnh thì nhấn lại lần nữa nó sẽ xóa đi
                    _curImageByteArr = null;
                    setButtonAddImageIcon(true);
                }
                else
                {
                    loadImageByteArray();
                }
            }
        });


        btnAddSound.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                if(btnAddSound.getText() == getString(R.string.remove_sound))
                {
                    // nếu đã thêm thì nhấn lại lần nữa nó sẽ xóa đi
                    _curSoundteArr = null;
                    setButtonAddSoundIcon(true);
                }
                else
                {
                    loadSoundByteArray();
                }
            }
        });



        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editTen.getText().toString().equals("")==true||editMota.getText().toString().equals("")==true)
                {
                    Toast.makeText(Detail_Word.this,R.string.fill_missing_infor,Toast.LENGTH_SHORT).show();
                }
                else {
                    //Word word = new Word(editten.getText().toString(),editmota.getText().toString());
                    ContentValues row = new ContentValues();
                    row.put("Ten",editten.getText().toString());
                    row.put("Mota",editmota.getText().toString());
                    row.put("TheLoai",edittag.getText().toString());

                    // add image, sound
                    if(_curImageByteArr != null)
                    {
                        row.put("Image", _curImageByteArr);
                    }
                    else if(_curImageByteArr == null && _curWord.getImage() != null)
                    {
                        // xoa image
                        row.put("Image",_curImageByteArr);
                    }

                    if(_curSoundteArr != null)
                    {
                        row.put("Sound", _curSoundteArr);
                    }
                    else if(_curSoundteArr == null && _curWord.getSound() != null)
                    {
                        // xoa sound
                        row.put("Sound",_curSoundteArr);
                    }


                    MainActivity.database.update("WordDatabase",row,"id=?",new String[]{String.valueOf(_curWord.getId())});
                    MainActivity.database.close();
                    dialog.dismiss();
                    //Toast.makeText(getApplicationContext(),"id= " + WordId,Toast.LENGTH_SHORT).show();
                    Toast.makeText(Detail_Word.this, R.string.update_success,Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(Detail_Word.this,MainActivity.class);
                    startActivity(intent);
                }

            }
        });
        btncancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

    }

    @OnClick(R.id.btnDelete)
    public void btnDelete(View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.notification);
        builder.setMessage(R.string.sure_delete);

        builder.setPositiveButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        builder.setNegativeButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                WordList.removeWordFromDBById(_curWord.getId());
                Toast.makeText(getApplicationContext(),"Delete Successfully",Toast.LENGTH_SHORT).show();
                startActivity(new Intent(Detail_Word.this,MainActivity.class));
            }
        });

        builder.show();
    }


    @OnClick(R.id.btnInfo)
    public void btnInfo(View view){
        final Dialog dialog = new Dialog(Detail_Word.this);
        dialog.setContentView(R.layout.detail_dialog);
        dialog.setTitle("About");
        dialog.show();

        final TextView txtDialogTen = dialog.findViewById(R.id.txtDialogTen);
        final TextView txtDialogMota = dialog.findViewById(R.id.txtDialogMota);


        ImageButton btnDialogSound = dialog.findViewById(R.id.btnDialogSound);

        txtDialogTen.setText(_curWord.getTen());
        txtDialogMota.setText(_curWord.getMota());


    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        // nếu out ra mà nhạc còn phát thì tắt
        MyMediaPlayer.getInstance().stop();
        super.onWindowFocusChanged(hasFocus);
    }

    //// Load image from internal store
    // tạo intent để chọn hình cần load
    byte[] _curImageByteArr;               // biến dùng để lưu
    private void loadImageByteArray()
    {
        // To open up a gallery browser
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"),1);
    }

    //// Load sound from internal store
    byte[] _curSoundteArr;               // biến dùng để lưu
    private void loadSoundByteArray()
    {
        // To open up a gallery browser
        Intent intent = new Intent();
        intent.setType("audio/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Sound"),2);
    }

    // Xử ký sau khi lấy hình xong
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {

            if (requestCode == 1) {
                // lấy uri của image
                Uri uri = data.getData();
                _curImageByteArr = Word.ImageUriToByteArray(this,uri);
                if(_curImageByteArr != null)
                {
                    // sau khi add hình thì icon chuyển thành remove
                    setButtonAddImageIcon(false);
                }
            }

            if (requestCode == 2) {
                // lấy uri
                Uri uri = data.getData();
                _curSoundteArr = Word.SoundUriToByteArray(this,uri);
                if(_curSoundteArr != null)
                {
                    // sau khi add thì icon chuyển thành remove
                    setButtonAddSoundIcon(false);
                }
            }
        }
    }

    private void setButtonAddImageIcon(boolean isAdd)
    {
        if(isAdd)
        {
            Drawable img = getResources().getDrawable( R.drawable.ic_add );
            img.setBounds( 0, 0, img.getIntrinsicWidth(), img.getIntrinsicHeight() );
            btnAddImage.setCompoundDrawables( img, null, null, null );

            btnAddImage.setText(getString(R.string.add_image));
        }
        else
        {
            Drawable img = getResources().getDrawable( R.drawable.ic_remove );
            img.setBounds( 0, 0, img.getIntrinsicWidth(), img.getIntrinsicHeight() );
            btnAddImage.setCompoundDrawables( img, null, null, null );

            btnAddImage.setText(getString(R.string.remove_image));
        }

    }

    private void setButtonAddSoundIcon(boolean isAdd)
    {
        if(isAdd)
        {
            Drawable img = getResources().getDrawable( R.drawable.ic_add );
            img.setBounds( 0, 0, img.getIntrinsicWidth(), img.getIntrinsicHeight() );
            btnAddSound.setCompoundDrawables( img, null, null, null );

            btnAddSound.setText(getString(R.string.add_sound));
        }
        else
        {
            Drawable img = getResources().getDrawable( R.drawable.ic_remove );
            img.setBounds( 0, 0, img.getIntrinsicWidth(), img.getIntrinsicHeight() );
            btnAddSound.setCompoundDrawables( img, null, null, null );

            btnAddSound.setText(getString(R.string.remove_sound));
        }

    }


}
