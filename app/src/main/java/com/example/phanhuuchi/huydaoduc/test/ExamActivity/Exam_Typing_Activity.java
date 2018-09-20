package com.example.phanhuuchi.huydaoduc.test.ExamActivity;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.phanhuuchi.huydaoduc.test.Main.MyMediaPlayer;
import com.example.phanhuuchi.huydaoduc.test.R;
import com.example.phanhuuchi.huydaoduc.test.model.Word;
import com.example.phanhuuchi.huydaoduc.test.model.WordList;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Exam_Typing_Activity extends AppCompatActivity {

    // answer view
    EditText _answerEditText;
    Button _checkButton;
    Button _hintButton;
    Button _nextQuestionButton;

    // question view
    TextView _questionText;
    ImageButton _questionSound;
    ImageView _questionImage;

    List<Word> _wordList;
    List<Integer> _wordIndexSelectedList;

    boolean _isShowingReasult;

    public Exam_Typing_Activity() {

    }

    @Override
    protected void onPause() {
        MyMediaPlayer.getInstance().stop();
        super.onPause();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam_typing);

        init();
        check();

    }

    private void init() {
        _isShowingReasult = false;

        _wordList = WordList.getWordList();

        // dùng để tránh trường hợp 1 từ xuất hiện quá nhiều lần
        _wordIndexSelectedList = new ArrayList<>();

        // bind view
        _answerEditText = (EditText) findViewById(R.id.edtxtAnswer);
        _checkButton = findViewById(R.id.btnCheck);
        _hintButton = findViewById(R.id.btnHint);
        _nextQuestionButton = findViewById(R.id.btnNext);

        _questionText = findViewById(R.id.txtQuestion);
        _questionImage = findViewById(R.id.imaQuestion);
        _questionSound = findViewById(R.id.imaBtnQuestionSound);


    }

    private void OnRestart()
    {
        _isShowingReasult = false;

        // lấy danh sách từ
        _wordList = WordList.getWordList();

        _checkButton.setBackgroundResource(R.drawable.background_btn_blue);
        _checkButton.setTextColor(getResources().getColor(R.color.text_color_blue));

        _answerEditText.setText("");

        StopSound();
    }

    /*
    Vì 1 Word có thể có Image, sound, translate text --> nên khi làm ktr
    ta có thể lấy bất kì Image hay sound hay text ra kiểm tra
    hàm này sẽ lấy ngẫu nhiên
    */
    private void getQuestion(final Word word)
    {
        _questionText.setVisibility(View.INVISIBLE);
        _questionImage.setVisibility(View.INVISIBLE);
        _questionSound.setVisibility(View.INVISIBLE);

        Random r = new Random();
        final int rI = r.nextInt(3);
        if(word.getImage() != null && rI == 0)
        {
            // use image
            _questionImage.setImageBitmap(word.getImageBitmap());
            _questionImage.setVisibility(View.VISIBLE);

        }
        else if (word.getSound() != null && rI == 1)
        {
            // use sound
            _questionSound.setVisibility(View.VISIBLE);
            _questionSound.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(Word.mediaPlayer.isPlaying())
                        StopSound();
                    else
                    {
                        word.PlaySound(getApplication());
                        //_questionSound.setBackgroundResource(R.drawable.ic_stop_sound);
                    }
                }
            });
        }
        else
        {
            // use text
            _questionText.setText(word.getMota());
            _questionText.setVisibility(View.VISIBLE);
        }
    }


    private void check() {
        OnRestart();
        Random r = new Random();

        //// Lấy 1 Word bất kì
        int curentIndex =  -1;
        if (_wordIndexSelectedList.size() == _wordList.size())
        {
            // nếu ds đủ số lượng từ hiện tại thì ds sẽ chạy lại 1 vòng mới
            _wordIndexSelectedList.clear();
        }
        // xem từ lấy ra đã có chưa nếu có rồi thì chạy lại tìm từ mới
        boolean flag = true;
        while (flag) {
            flag = false;
            curentIndex = r.nextInt(_wordList.size());
            for (int selectedIndex : _wordIndexSelectedList) {
                if (selectedIndex == curentIndex) {
                    flag = true;
                }
            }
        }
        _wordIndexSelectedList.add(curentIndex);

        // từ đang hỏi
        final Word curWord = _wordList.get(curentIndex);
        // đặt câu hỏi
        getQuestion(curWord);

        final long endTime = 1400;      // thời gian thể hiện kết quả trước khi chạy cái mới

        _checkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(_isShowingReasult == false)
                {
                    if(_answerEditText.getText().toString().trim().equals(""))
                    {
                        return;

                    }
                    else if (_answerEditText.getText().toString().trim().toUpperCase().equals(curWord.getTen().trim().toUpperCase()))
                    {
                        _checkButton.setBackgroundResource(R.drawable.background_btn_green);
                        _checkButton.setTextColor(getResources().getColor(R.color.text_color_white));
                        MyMediaPlayer.getInstance().playNew(getApplication(),R.raw.correct_answer);

                        final CountDownTimer start = new CountDownTimer(endTime, endTime) {
                            @Override
                            public void onTick(long l) {}

                            public void onFinish() {
                                check();
                            }
                        }.start();
                        _isShowingReasult = true;
                    }
                    else
                    {
                        _checkButton.setBackgroundResource(R.drawable.background_btn_red);
                        _checkButton.setTextColor(getResources().getColor(R.color.text_color_blue));
                        MyMediaPlayer.getInstance().playNew(getApplication(),R.raw.wrong_answer);

                        final CountDownTimer start = new CountDownTimer(endTime, endTime) {
                            @Override
                            public void onTick(long l) {}

                            public void onFinish() {
                                _checkButton.setBackgroundResource(R.drawable.background_btn_blue);
                                _isShowingReasult = false;
                            }
                        }.start();
                        _isShowingReasult = true;
                    }
                }
            }
        });

        _hintButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(_isShowingReasult == false)
                {
                    getHint(curWord);
                }
            }
        });

        _nextQuestionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                check();
            }
        });

    }

    private void getHint(Word word)
    {
        /*
        Hàm này sẽ gợi ý từng từ một cho user.
        + TH1: Nếu thấy user đã nhập 1 phần từ là đúng thì nó sẽ tiếp tục gợi ý từ tiếp theo
        + TH2: nếu thấy user nhập sai thì nó sẽ lấy phần đúng, xóa phần sai và gợi ý từ đó
                VD: user nhập: "appple" ---> xuất ra: "app"

        */

        String tempString = "";
        String wordTen = word.getTen().toString().toLowerCase();
        String editText = _answerEditText.getText().toString().toLowerCase();

        // TH1:
        for (int i = 1; i <= wordTen.length(); i++)
        {
            // nếu i lớn hơn độ dài của đoạn text đang nhập thì chỉ lấy ra wordTen dài hơn 1 phần tử
            if(i > editText.length())
            {
                tempString = wordTen.substring(0,editText.length() + 1);
                break;
            }

            // so sánh 2 phần tử cuối, nếu khác nhau thì xuất ra string có độ dài bằng i
            String wordLastChar = wordTen.substring(i-1,i);
            String editTextLastChar = editText.substring(i-1,i);
            if(! wordLastChar.equals(editTextLastChar))
            {
                tempString = wordTen.substring(0,i);
                break;
            }

        }

        // trường hợp editText đã nhập đúng thì k cần thêm
        if(tempString.equals(""))
        {
            tempString = wordTen;       // tránh trường hợp nhập đúng mà dư
        }
        // kiểm tra TH2
        else if(!tempString.equals("") &&!editText.equals("") && editText.length() > tempString.length())
            if(editText.contains(tempString.substring(0, tempString.length() - 1)) )//
            {
                tempString = tempString.substring(0, tempString.length() - 1);

            }

        _answerEditText.setText(tempString);

        // đặt lại dấu nháy nằm ở cuối
        _answerEditText.setSelection(_answerEditText.getText().length());
    }

    private void StopSound()
    {
        if(Word.mediaPlayer.isPlaying())
        {
            Word.StopPlayingSound();
        }
    }


}

