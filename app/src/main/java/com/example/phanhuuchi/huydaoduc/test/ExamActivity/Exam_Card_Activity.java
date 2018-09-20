package com.example.phanhuuchi.huydaoduc.test.ExamActivity;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.phanhuuchi.huydaoduc.test.Main.MyMediaPlayer;
import com.example.phanhuuchi.huydaoduc.test.R;
import com.example.phanhuuchi.huydaoduc.test.View.CardView;
import com.example.phanhuuchi.huydaoduc.test.model.Word;
import com.example.phanhuuchi.huydaoduc.test.model.WordList;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Exam_Card_Activity extends AppCompatActivity {

    // Card
    List<CardView> _cardList;
    List<CardView> _curCardList;

    // 2 linear layout
    LinearLayout _firstLinear;
    LinearLayout _secondLinear;

    TextView _stepCountTextView;

    //
    List<Word> _wordList;
    List<Integer> _wordIndexUsingList;

    int _lastCardFlipUpIndex;                   // số thứ tự card vừa lật lên hồi nãy
    int _flipUpCount;                           // số card đang lật
    int _removeCont;                            // số bộ card đã xóa
    int _stepCount;                             // đếm số bước đã đi của user

    boolean _canClick;

    public Exam_Card_Activity() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam_card);
        init();
        start();
    }


    private void OnRestart()
    {
        _canClick = true;

        _lastCardFlipUpIndex = -1;
        _flipUpCount = 0;
        _removeCont = 0;
        _stepCount = 0;

        // lấy danh sách từ
        _wordList = WordList.getWordList();

    }

    private void start() {
        OnRestart();

        setCurCardList();
        setCardsContent();

        for (final CardView cardView:_cardList) {
            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(_canClick && cardView.isFlipDown())
                    {
                        cardView.FlipUp();
                        _flipUpCount++;
                        _stepCount++;
                        _stepCountTextView.setText("Step Count: " + _stepCount);

                        final int curCardFlipUpIndex = _cardList.indexOf(cardView);;

                        if(_flipUpCount == 1)
                        {
                            // set giá trị card vừa lật
                            _lastCardFlipUpIndex = curCardFlipUpIndex;
                        }
                        else if(_flipUpCount == 2)
                        {
                            // nếu đã flipUp 2 card thì sẽ đợi 1 lúc rồi xét 2 trường hợp
                            _canClick = false;
                            _flipUpCount = 0;
                            long endTime = 1000;

                            if(_cardList.get(_lastCardFlipUpIndex).getCurWord().getId() == cardView.getCurWord().getId())
                            {
                                // TH1: 2 card trùng nhau --> ẩn 2 card hiện tại và tăng _removeCount

                                MyMediaPlayer.getInstance().playNew(getApplication(),R.raw.correct_answer);

                                final CountDownTimer start = new CountDownTimer(endTime, endTime) {
                                    @Override
                                    public void onTick(long l) {}
                                    public void onFinish()
                                    {
                                        _cardList.get(_lastCardFlipUpIndex).setVisibility(View.INVISIBLE);
                                        cardView.setVisibility(View.INVISIBLE);
                                        _lastCardFlipUpIndex = -1;
                                        _canClick = true;
                                        _removeCont++;
                                        checkFinish();
                                    }
                                }.start();

                            }
                            else
                            {
                                // TH2: 2 card không trùng nhau --> flipDown lại
                                final CountDownTimer start = new CountDownTimer(endTime, endTime) {
                                    @Override
                                    public void onTick(long l) {}
                                    public void onFinish()
                                    {
                                        flipDownAll();
                                        _canClick = true;
                                    }
                                }.start();
                            }
                        }
                    }

                }
            });
        }
    }

    private void checkFinish() {
        // nếu hoàn tất thì chơi lại
        if(_removeCont == _curCardList.size()/2)
        {
            finish();
        }
    }

    private void init() {
        _canClick = true;

        _cardList = new ArrayList<>();
        _curCardList = new ArrayList<>();

        _wordList = WordList.getWordList();

        // dùng để tránh trường hợp 1 từ xuất hiện quá nhiều lần
        _wordIndexUsingList = new ArrayList<>();

        // get 2 linear layout
        _firstLinear = findViewById(R.id.linearLayout1);
        _secondLinear = findViewById(R.id.linearLayout2);

        _stepCountTextView = findViewById(R.id.stepCountTextView);

        // bind view
        _cardList.add((CardView)findViewById(R.id.cardView0));
        _cardList.add((CardView)findViewById(R.id.cardView1));
        _cardList.add((CardView)findViewById(R.id.cardView2));
        _cardList.add((CardView)findViewById(R.id.cardView3));
        _cardList.add((CardView)findViewById(R.id.cardView4));
        _cardList.add((CardView)findViewById(R.id.cardView5));
        _cardList.add((CardView)findViewById(R.id.cardView6));
        _cardList.add((CardView)findViewById(R.id.cardView7));

    }


    private void setCurCardList() {
        // hàm dựa vào số lượng từ hiện có để xác định số card cần sử dụng cho activity
        // số word - số card : " 2 - 4 ; 3 - 6 ; >= 4 - 8 "
        int i = _wordList.size();

        if(i >= 4)
        {
            _curCardList = _cardList;
            _firstLinear.setWeightSum(4);
            _secondLinear.setWeightSum(4);
        }
        else
        {
            if(i == 2)
            {
                for (int j = 0; j < 4; j++) {
                    _curCardList.add(_cardList.get(j));
                }
                for (int j = 4; j < 8; j++) {
                    _cardList.get(j).setVisibility(CardView.GONE);
                }

            }
            else if(i == 3)
            {
                for (int j = 0; j < 6; j++) {
                    _curCardList.add(_cardList.get(j));
                }
                for (int j = 6; j < 8; j++) {
                    _cardList.get(j).setVisibility(CardView.GONE);
                }

            }
            _firstLinear.setWeightSum(i);
            _secondLinear.setWeightSum(i);
        }




    }

    private void setCardsContent() {
        //_cardList.get(0).setContent(_wordList.get(0),true);
        // Hàm này sẽ lấy ngẫu nhiên n bộ từ và cho ngẫu nhiên vào các card
        Random r = new Random();

        List<Integer> cardPuttedContentList = new ArrayList<>();
        for (int i = 0; i < _curCardList.size()/2; i++)
        {
            // lấy 1 word bất kì không trùng với các word đã lấy ra
            int curIndex = -1;
            boolean conflict = true;
            while(conflict)
            {
                conflict = false;
                curIndex = r.nextInt(_wordList.size());
                for (int selectedIndex : _wordIndexUsingList) {
                    if (selectedIndex == curIndex) {
                        conflict = true;
                    }
                }
            }
            _wordIndexUsingList.add(curIndex);

            // lấy ra 2 số bất kì không trùng nhau và không trùng với nhau và không trùng với số thứ tự các card đã put content
            //  2 số này dùng xác định cặp card cần put content
            int cardKey = -1, cardValue = -1;
            conflict = true;
            while (conflict)
            {
                conflict = false;
                cardKey = r.nextInt(_curCardList.size());
                cardValue = r.nextInt(_curCardList.size());
                if(cardKey == cardValue)
                {
                    conflict = true;
                    continue;
                }
                for (int selectedIndex : cardPuttedContentList) {
                    if (cardKey == selectedIndex) {
                        conflict = true;
                    }
                    if (cardValue == selectedIndex) {
                        conflict = true;
                    }
                }
            }
            cardPuttedContentList.add(cardKey);
            cardPuttedContentList.add(cardValue);

            // put content
            _curCardList.get(cardKey).setContent(_wordList.get(curIndex), true);
            _curCardList.get(cardValue).setContent(_wordList.get(curIndex), false);

        }


    }


    private void flipDownAll() {
        for (CardView card: _curCardList)
        {
            card.FlipDown();
        }
    }

    private int getCardFlipUpCount() {
        int count = 0;
        for (CardView card: _curCardList)
        {
            // đếm số card đang nằm ngữa
            if(! card.isFlipDown())
                count += 1;
        }
        return  count;
    }

    private void StopSound()
    {
        if(Word.mediaPlayer.isPlaying())
        {
            Word.StopPlayingSound();
        }
    }

}

