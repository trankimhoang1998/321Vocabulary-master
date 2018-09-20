package com.example.phanhuuchi.huydaoduc.test.model;

import com.example.phanhuuchi.huydaoduc.test.Main.MainActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Chi on 12/20/2017.
 */

public class WordList {

    static List<Word> _wordList ;


    //Chi: singleton
    static public List<Word> getWordList()
    {
        if(_wordList == null)
        {
            _wordList = new ArrayList<>();
            return _wordList;
        }
        else
            return _wordList;
    }

    static public Word getWordById(int id)
    {
        for (Word word:_wordList) {
            if(word.getId() == id)
                return word;
        }
        return null;
    }

    static public Word getWordByTen(String ten)
    {
        for (Word word:_wordList) {
            if(word.getTen().toUpperCase().equals(ten.toUpperCase()))
                return word;
        }
        return null;
    }

    static public boolean removeWordFromDBById(int id)
    {
        MainActivity.database.delete("WordDatabase","id=?",new String[]{String.valueOf(id)});
        return true;
    }
}
