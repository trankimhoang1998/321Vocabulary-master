package com.example.phanhuuchi.huydaoduc.test.Adapter;

import android.content.Context;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.example.phanhuuchi.huydaoduc.test.R;
import com.example.phanhuuchi.huydaoduc.test.model.Word;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by HuyDaoDuc on 20/11/2017.
 */

//Chi: đổi extends Base adapter thành ArrayAdapter để dùng multible choice
public class WordAdapter extends ArrayAdapter<Word> implements Filterable {
    private Context context;
    private int layout;
    private List<Word> wordList;

    // SparseBooleanArray : mapping Integer values to booleans  -->  more memory efficient
    private SparseBooleanArray mSelectedItemsIds;

    //Chi
    CusFilter filter;
    List<Word> filterList;

    public WordAdapter(Context context, int resourceId, List<Word> Wordlist) {
        super(context, resourceId, Wordlist);
        mSelectedItemsIds = new SparseBooleanArray();
        this.context = context;
        this.layout = resourceId;
        this.wordList = Wordlist;

        // Filter
        this.filterList = wordList;
    }


    @Override
    public int getCount() {
        return wordList.size();
    }

    @Override
    public Word getItem(int i) {
        return wordList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(layout,null);

        TextView txtTen = (TextView) view.findViewById(R.id.txtTen);
        TextView txtTheLoai = (TextView) view.findViewById(R.id.txtTheLoai);
        TextView txtMota = (TextView) view.findViewById(R.id.txtMota);

        Word word = wordList.get(i);
        if(word !=null){
            txtTen.setText(word.getTen());
            txtMota.setText(word.getMota());

            if(word.getTheLoai() != null)
            {
                txtTheLoai.setText(word.getTheLoai());
            }
            else
            {
                txtTheLoai.setVisibility(View.GONE);
            }
        }

        //animation
//        Animation animation = AnimationUtils.loadAnimation(context,R.anim.scale_list);
//        view.startAnimation(animation);

        return view;
    }

    @Override
    public void remove(Word object) {
        wordList.remove(object);
        notifyDataSetChanged();
    }

    public List<Word> getWord() {
        return wordList;
    }

    public void toggleSelection(int position) {
        selectView(position, !mSelectedItemsIds.get(position));
    }

    public void removeSelection() {
        // xóa toàn bộ select item bằng cách tạo SparseBooleanArray mới
        mSelectedItemsIds = new SparseBooleanArray();
        notifyDataSetChanged();
    }

    public void selectView(int position, boolean value) {
        if (value)
            mSelectedItemsIds.put(position, value);     // put view vào ds đang select
        else
            mSelectedItemsIds.delete(position);         // xóa view khỏi ds đang select
        notifyDataSetChanged();
    }

    public int getSelectedCount() {
        return mSelectedItemsIds.size();
    }

    public SparseBooleanArray getSelectedIds() {
        return mSelectedItemsIds;
    }

    //// Chi: FILTER

    @Override
    public Filter getFilter() {
        if(filter == null)
        {
            filter = new CusFilter();
        }
        return filter;
    }

    class CusFilter extends Filter
    {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            FilterResults rs = new FilterResults();

            if(constraint != null && constraint.length() > 0)
            {
                // chuyển upercase để tránh xét thiếu
                constraint = constraint.toString().toUpperCase();

                List<Word> outFilter = new ArrayList<Word>();

                // duyệt list
                for (int i = 0; i < filterList.size(); i++) {
                    // điều kiện
                    // TH1: chứa word.ten
                    if(filterList.get(i).getTen().toUpperCase().contains(constraint))
                    {
                        Word word = new Word(filterList.get(i));

                        outFilter.add(word);
                    }
                    // TH2: chứa word.mota
                    else if(filterList.get(i).getMota().toUpperCase().contains(constraint))
                    {
                        Word word = new Word(filterList.get(i));

                        outFilter.add(word);
                    }
                    // TH3: chứa word.theloai , lưu ý giá trị có thể null nên xét null trước
                    else if(filterList.get(i).getTheLoai() != null)
                    {
                        if(filterList.get(i).getTheLoai().toUpperCase().contains(constraint))
                        {
                            Word word = new Word(filterList.get(i));

                            outFilter.add(word);
                        }
                    }
                }

                rs.count = outFilter.size();
                rs.values = outFilter;
            }
            else
            {
                rs.count = filterList.size();
                rs.values = filterList;
            }
            return rs;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults rs) {

            wordList = (List<Word>) rs.values;
            notifyDataSetChanged();

        }
    }
}
