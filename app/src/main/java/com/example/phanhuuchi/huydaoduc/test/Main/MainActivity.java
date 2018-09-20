package com.example.phanhuuchi.huydaoduc.test.Main;

import android.Manifest;
import android.app.Dialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.phanhuuchi.huydaoduc.test.Adapter.WordAdapter;
import com.example.phanhuuchi.huydaoduc.test.Data.DBSQL;
import com.example.phanhuuchi.huydaoduc.test.ExamActivity.Exam_Activity;
import com.example.phanhuuchi.huydaoduc.test.ExamActivity.Exam_Card_Activity;
import com.example.phanhuuchi.huydaoduc.test.ExamActivity.Exam_Typing_Activity;
import com.example.phanhuuchi.huydaoduc.test.R;
import com.example.phanhuuchi.huydaoduc.test.Settings.SettingsActivity;
import com.example.phanhuuchi.huydaoduc.test.model.Detail_Word;
import com.example.phanhuuchi.huydaoduc.test.model.Word;
import com.example.phanhuuchi.huydaoduc.test.model.WordList;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import io.paperdb.Paper;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    String DATABASE_NAME="WordData.sqlite";
    String DB_PATH_SUFFIX = "/databases/";
    public static SQLiteDatabase database=null;

    ListView lvWord;
    static List<Word> dsWords = new ArrayList<>();

    // view
    SearchView searchView;

    // view của dialog add new word
    EditText editten ;
    EditText editmota;
    EditText edittheloai;
    Button btnadd;
    Button btncancel;
    Button btnAddImage;
    Button btnAddSound;

    // view của dialog chọn acitity game
    Button btn_game_quiz;
    Button btn_game_typing;
    Button btn_game_card;

    // setting
    SharedPreferences sharedPreferences;


    //public static WordAdapter adapterWords;
    public static WordAdapter adapterWords;

    private Word word;


    public static final String TITLE = "title";

    @Override
    protected void onResume() {
        super.onResume();
        //
        xuLyHienThiWord();


        //mute
        AudioManager mgr = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        mgr.setStreamMute(AudioManager.STREAM_SYSTEM, true);
        // listener cho setting
        Boolean soundSWitchPref = sharedPreferences.getBoolean(SettingsActivity.KEY_PREF_MUTESOUND_SWITCH,false);
        MyMediaPlayer.getInstance().setMute(soundSWitchPref);

        Boolean lockSreenSWitchPref = sharedPreferences.getBoolean(SettingsActivity.KEY_PREF_LOCKSCREEN_SWITCH,false);
        if(lockSreenSWitchPref == true)
            startService(new Intent(this,LockScreenService.class));
        else
            stopService(new Intent(this, LockScreenService.class));
    }

    @Override
    protected void onPause() {
        super.onPause();

        // widget
        updateWidget();

        //mute
        AudioManager mgr = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        if (mgr != null && Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            mgr.setStreamMute(AudioManager.STREAM_SYSTEM, false);
        }

        // listener cho setting
        Boolean sWitchPref = sharedPreferences.getBoolean(SettingsActivity.KEY_PREF_MUTESOUND_SWITCH,false);
        MyMediaPlayer.getInstance().setMute(sWitchPref);

        Boolean lockSreenSWitchPref = sharedPreferences.getBoolean(SettingsActivity.KEY_PREF_LOCKSCREEN_SWITCH,false);
        if(lockSreenSWitchPref == true)
            startService(new Intent(this,LockScreenService.class));
        else
            stopService(new Intent(this, LockScreenService.class));



    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 99: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else {
                    // permission bị từ chối
                    Toast.makeText(MainActivity.this, R.string.permission_deny, Toast.LENGTH_SHORT).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // ask permission
        ActivityCompat.requestPermissions(MainActivity.this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                99);


        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddNewWordDialog();
            }
        });

        FloatingActionButton fab_game = (FloatingActionButton) findViewById(R.id.fab_game);
        fab_game.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showGameDialog();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // start service
        startService(new Intent(this,LockScreenService.class));

        // lay dsword tu word list
        dsWords = WordList.getWordList();

        // đặt giá trị mặc định đọc từ file XML preference -- reading the values defined by each Preference item's
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        // gán setting pref
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        //
        xuLySaoChepCSDLTuAssetsVaoHeThongMobile();
        addControls();
        addEvents();
        xuLyHienThiWord();
        addNotification();

    }

    private void showGameDialog() {
        final Dialog dialog = new Dialog(MainActivity.this);
        dialog.setContentView(R.layout.dialog_game);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        btn_game_quiz = dialog.findViewById(R.id.btn_game_quiz);
        btn_game_typing = dialog.findViewById(R.id.btn_game_typing);
        btn_game_card = dialog.findViewById(R.id.btn_game_card);

        btn_game_quiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (dsWords.size() == 1)
                {
                    // nếu chỉ có 1 từ thì k được kiểm tra
                    Toast.makeText(getApplicationContext(), R.string.need_more_word, Toast.LENGTH_LONG).show();
                    return;
                }
                else
                {
                    //Intent intent = new Intent(getApplicationContext(),Exam_Activity.class);
                    Intent intent = new Intent(getApplicationContext(),Exam_Activity.class);
                    startActivity(intent);
                }
                dialog.dismiss();
            }
        });

        btn_game_typing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (dsWords.size() == 1)
                {
                    // nếu chỉ có 1 từ thì k được kiểm tra
                    Toast.makeText(getApplicationContext(), R.string.need_more_word, Toast.LENGTH_LONG).show();
                    return;
                }
                else
                {
                    //Intent intent = new Intent(getApplicationContext(),Exam_Activity.class);
                    Intent intent = new Intent(getApplicationContext(),Exam_Typing_Activity.class);
                    startActivity(intent);
                }
                dialog.dismiss();
            }
        });

        btn_game_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (dsWords.size() == 1)
                {
                    // nếu chỉ có 1 từ thì k được kiểm tra
                    Toast.makeText(getApplicationContext(), R.string.need_more_word, Toast.LENGTH_LONG).show();
                    return;
                }
                else
                {
                    //Intent intent = new Intent(getApplicationContext(),Exam_Activity.class);
                    Intent intent = new Intent(getApplicationContext(),Exam_Card_Activity.class);
                    startActivity(intent);
                }
                dialog.dismiss();
            }
        });
    }

    private void showAddNewWordDialog()
    {
        /*                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/
        final Dialog dialog = new Dialog(MainActivity.this);
        dialog.setTitle("New Word");
        dialog.setContentView(R.layout.dialog_layout);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        //init
        _curImageByteArr = null;

        // bind
        editten = (EditText)dialog.findViewById(R.id.editTen);
        editmota = (EditText)dialog.findViewById(R.id.editMota);
        edittheloai = (EditText)dialog.findViewById(R.id.editTheLoai);
        btnadd = (Button)dialog.findViewById(R.id.btnAdd);
        btncancel = (Button)dialog.findViewById(R.id.btnCancel);
        btnAddImage = (Button)dialog.findViewById(R.id.btnAddImage);
        btnAddSound = (Button)dialog.findViewById(R.id.btnAddSound);

        // add event
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

        btnadd.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                // TH1: thiếu dữ liệu cần thiết
                if (editten.getText().toString().equals("") || editmota.getText().toString().equals("")){
                    //tạo hàm kiễm tra
                    Toast.makeText(MainActivity.this, R.string.fill_missing_infor,Toast.LENGTH_SHORT).show();
                }
                // TH2: trùng dữ liệu
                else if (WordList.getWordByTen(editten.getText().toString()) != null){
                    //tạo hàm kiễm tra
                    Toast.makeText(MainActivity.this, R.string.duplicate_value,Toast.LENGTH_SHORT).show();
                }
                // TH3: cho phép thêm
                else {
                    adapterWords.notifyDataSetChanged();
                    //thêm
                    ContentValues row = new ContentValues();
                    row.put("Ten",editten.getText().toString());
                    row.put("Mota",editmota.getText().toString());
                    //Chi:
                    if(edittheloai.getText() != null && edittheloai.getText().toString().length() > 0)
                    {
                        row.put("TheLoai", edittheloai.getText().toString());
                    }
                    if(_curImageByteArr != null)
                    {
                        row.put("Image", _curImageByteArr);
                    }
                    if(_curSoundteArr != null)
                    {
                        row.put("Sound", _curSoundteArr);
                    }

                    database.insert("WordDatabase",null,row);
                    dialog.dismiss();
                    Toast.makeText(MainActivity.this, R.string.add_successfully,Toast.LENGTH_SHORT).show();
                    xuLyHienThiWord();
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



    public void xuLyHienThiWord() {
        database = openOrCreateDatabase(DATABASE_NAME,MODE_PRIVATE,null);
        Cursor cursor = database.rawQuery("select * from WordDatabase",null);
//        Cursor cursor=database.query("Word",null,null,null,null,null,null);
        dsWords.clear();
        while (cursor.moveToNext()){

            int id = cursor.getInt(0);
            String ten = cursor.getString(1);
            String mota = cursor.getString(2);
            String theLoai = cursor.getString(3);
            byte[] image = cursor.getBlob(4);
            byte[] sound = cursor.getBlob(5);

            Word word = new Word();
            word.setId(id);
            word.setTen(ten);
            word.setMota(mota);
            word.setImage(image);
            word.setSound(sound);
            word.setTheLoai(theLoai);
            dsWords.add(word);
        }
        cursor.close();
        adapterWords.notifyDataSetChanged();
    }


    private void addControls() {
        lvWord= (ListView) findViewById(R.id.lvWord);
        //adapterWords = new WordAdapter(this,R.layout.dong_word,dsWords);
        adapterWords = new WordAdapter(this,R.layout.dong_word,dsWords);
        lvWord.setAdapter(adapterWords);

        searchView = (SearchView)findViewById(R.id.searchWord);
    }


    private void addEvents() {
        addListViewEvents();


        //Chi: search
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query)
            {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query)
            {
                adapterWords.getFilter().filter(query);
                return false;
            }
        });


    }

    private void addListViewEvents() {
        //// Single Choice
        lvWord.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                addNotification();

                word = dsWords.get(i);
                Intent intent = new Intent(MainActivity.this,Detail_Word.class);
                intent.putExtra(DBSQL.WORD_ID_KEY_PUT_EXTRA,word.getId());
                startActivity(intent);

            }

        });

        //// Multible Choice
        lvWord.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);

        lvWord.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {

            @Override
            public void onItemCheckedStateChanged(ActionMode mode,
                                                  int position, long id, boolean checked) {
                // Capture total checked items
                final int checkedCount = lvWord.getCheckedItemCount();
                // đặt CAB title cho toàn bộ select items
                mode.setTitle(checkedCount + " Selected");
                // đánh dấu word được select
                adapterWords.toggleSelection(position);
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_deleteSelect:
                        // lấy danh sách id đã select
                        SparseBooleanArray selected = adapterWords.getSelectedIds();
                        // duyệt ds
                        for (int i = (selected.size() - 1); i >= 0; i--) {
                            if (selected.valueAt(i)) {
                                Word selectedWord = adapterWords.getItem(selected.keyAt(i));
                                // xóa word khỏi database
                                WordList.removeWordFromDBById(selectedWord.getId());
                                // Xóa word khỏi danh sách
                                adapterWords.remove(selectedWord);
                            }
                        }
                        // tắt CAB
                        mode.finish();
                        return true;
                    case R.id.action_exit:
                        // tắt CAB
                        mode.finish();
                        return true;
                    default:
                        return false;
                }
            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                // đẩy menu action vào
                mode.getMenuInflater().inflate(R.menu.menu_selected_item, menu);
                return true;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                adapterWords.removeSelection();
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }
        });



    }

    private void xuLySaoChepCSDLTuAssetsVaoHeThongMobile() {
        File dbFile = getDatabasePath(DATABASE_NAME);
        if (!dbFile.exists())
        {
            try
            {
                CopyDataBaseFromAsset();
                Toast.makeText(this, "Sao chép CSDL vào hệ thống thành công", Toast.LENGTH_LONG).show();
            }
            catch (Exception e)
            {
                Toast.makeText(this, "Đã có Data", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void CopyDataBaseFromAsset() {
        try
        {
            //myInput: cơ sở dữ liệu lấy từ assets
            InputStream myInput=getAssets().open(DATABASE_NAME);
            String outFileName = layDuongDanLuuTru();
            File f = new File(getApplicationInfo().dataDir + DB_PATH_SUFFIX);
            if(!f.exists())//kiểm tra database có chưa nếu có rồi ko tạo nữa
            {
                f.mkdir();
            }
            //myOutput: nơi truyền database từ Input để sử dụng
            OutputStream myOutput = new FileOutputStream(outFileName);
            byte[] buffer = new byte[1024];
            int length; //lấy toàn bộ data (có hay ko cũng đc)
            while ((length = myInput.read(buffer)) > 0)
            {
                myOutput.write(buffer, 0, length);
            }
            myOutput.flush();
            myOutput.close();
            myInput.close();
        }
        catch (Exception ex)
        {
            Log.e("Loi_SaoChep",ex.toString());
        }
    }

    private String layDuongDanLuuTru() {
        return getApplicationInfo().dataDir + DB_PATH_SUFFIX+ DATABASE_NAME;
    }


    //Lỗi
    private void updateWidget() {
        Random i = new Random();
        int a= i.nextInt(dsWords.size());
        Paper.init(this);
        Paper.book().write("Ten",dsWords.get(a).getTen());
        Paper.book().write("Mota",dsWords.get(a).getMota());
    }

    private void addNotification() {
        Random i = new Random();
        int a= i.nextInt(dsWords.size());
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(getApplicationContext())
                        .setSmallIcon(R.drawable.edit_40px)
                        .setContentTitle(dsWords.get(a).getTen())
                        .setContentText(dsWords.get(a).getMota());

        Intent resultIntent = new Intent(getApplicationContext(), Detail_Word.class);

        resultIntent.putExtra(DBSQL.WORD_ID_KEY_PUT_EXTRA,dsWords.get(a).getId());
        resultIntent.putExtra(DBSQL.WORD_TEN_KEY_PUT_EXTRA,dsWords.get(a).getTen());
        resultIntent.putExtra(DBSQL.WORD_MOTA_KEY_PUT_EXTRA,dsWords.get(a).getMota());

        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        getApplicationContext(),
                        0,
                        resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

        mBuilder.setContentIntent(resultPendingIntent);

        Uri uri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        mBuilder.setSound(uri);

//                        Uri newSound= Uri.parse("android.resource://"
//                                + getPackageName() + "/" + R.raw.gaugau);
//                        mBuilder.setSound(newSound);

        int mNotificationId = 155;
        // Gets an instance of the NotificationManager service
        NotificationManager mNotifyMgr =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        // Builds the notification and issues it.
        mNotifyMgr.notify(mNotificationId, mBuilder.build());

    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    //// MENU CONTEXT
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // đặt menu layout
        getMenuInflater().inflate(R.menu.menu_context, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // lấy menu item id
        int id = item.getItemId();

        // set event
        if (id == R.id.action_setting) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        if (id == R.id.action_exit) {
            DialogExit();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private void DialogExit() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Thông Báo");
        builder.setMessage("Bạn có chắc muốn thoát ??");
//        builder.setIcon(R.drawable.ic_person_white_24dp);

        builder.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.setNegativeButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        builder.show();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {

        getMenuInflater().inflate(R.menu.menu_context,menu);

        super.onCreateContextMenu(menu, v, menuInfo);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_manage) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_share) {

        } else if (id == R.id.about) {
            Intent intent = new Intent(MainActivity.this, SplashScreen.class);
            startActivity(intent);
        } else if (id == R.id.game) {
            showGameDialog();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
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
