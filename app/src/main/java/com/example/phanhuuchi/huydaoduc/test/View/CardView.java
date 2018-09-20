package com.example.phanhuuchi.huydaoduc.test.View;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.phanhuuchi.huydaoduc.test.R;
import com.example.phanhuuchi.huydaoduc.test.model.Word;

/**
 * Created by Chi on 12/21/2017.
 */

public class CardView extends RelativeLayout{

    ViewGroup _cardLayout;
    ImageView _icon;
    ImageView _imageContent;
    TextView _textContent;


    // @_isKey :  = true: card thể hiện phần Word Ten
    //            = false: card thể hiện phần translation
    boolean _isKey;

    boolean _isFlipDown;

    // từ mà card đang thể hiện
    private Word _curWord;

    public Word getCurWord() {
        return _curWord;
    }

    public boolean isFlipDown() {
        return _isFlipDown;
    }


    public CardView(Context context) {
        super(context);
        initialize();
    }

    public CardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    public CardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize();
    }

    private void initialize() {
        _isFlipDown = true;

        ViewGroup viewGroup = (ViewGroup) inflate(getContext(), R.layout.card_view, this);
        _cardLayout = (ViewGroup) viewGroup.getChildAt(0);
        _icon = (ImageView) _cardLayout.findViewById(R.id.iconBack);

        _imageContent = (ImageView) _cardLayout.findViewById(R.id.imageViewContent);
        _textContent = (TextView) _cardLayout.findViewById(R.id.textViewContent);

        _imageContent.setVisibility(INVISIBLE);
        _textContent.setVisibility(INVISIBLE);
    }


    public void setContent(Word word, boolean isKey)
    {
        _curWord = word;
        _isKey = isKey;
        if(isKey)
        {

            _textContent.setText(word.getTen());
        }
        else
        {
            if(word.getImageBitmap() != null)
            {
                _imageContent.setImageBitmap(word.getImageBitmap());
            }
            _textContent.setText((word.getMota()));
        }

    }

//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        if(_isFlipDown)
//        {
//            FlipCard();
//        }
//        return super.onTouchEvent(event);
//
//    }

    public void FlipCard()
    {
        if(_isFlipDown)
        {
            FlipUp();
        }
        else
        {
            FlipDown();
        }
    }

    public void FlipUp()
    {
        // nếu là value mà có image thì show image
        // còn nếu là value mà không có image hoặc k phải là value thì show text
        if(_isKey == false && _curWord.getImageBitmap() != null)
        {
            _imageContent.setVisibility(VISIBLE);
        }
        else
            _textContent.setVisibility(VISIBLE);

        _icon.setVisibility(INVISIBLE);
        _isFlipDown = false;

    }

    public void FlipDown()
    {
        _imageContent.setVisibility(INVISIBLE);
        _textContent.setVisibility(INVISIBLE);
        _icon.setVisibility(VISIBLE);
        _isFlipDown = true;
    }
}
