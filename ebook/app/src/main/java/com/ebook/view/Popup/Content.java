package com.ebook.view.Popup;

import android.content.Context;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ebook.R;
import com.ebook.model.Book;

import java.util.List;


public class Content extends Popup {

    //可以实现数据纵向滚动
    private RecyclerView mRecyclerView;
    private LinearLayout mLinearLayout;

    private Book mBook;

    //监听选中的章节item
    private OnContentSelectedListener mListener;

    @Override
    protected View createConvertView() {
        return LayoutInflater.from(mContext)
                .inflate(R.layout.popup_content, null);
    }


    //监听选中的章节item
    public interface OnContentSelectedListener {
        void OnContentClicked(int paraIndex);
    }

    public void setOnContentClicked(OnContentSelectedListener listener)
    {
        mListener = listener;
    }

    public Content(Context context, Book book) {
        super(context);
        mBook = book;

        //显示章节目录
        mLinearLayout = (LinearLayout) mConvertView.findViewById(R.id.pop_content_linear_layout);
        mRecyclerView = (RecyclerView) mConvertView.findViewById(R.id.pop_contents_recycle_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mRecyclerView.setAdapter(new ContentsAdapter(mBook.getBookContents()));

    }

    //点击目录可以跳转到指定页面，
    private class ContentsAdapter extends RecyclerView.Adapter<ContentsHolder> {
        private List<String> mBookContents;
        public ContentsAdapter(List<String> bookContents) {
            mBookContents = bookContents;
        }

        @Override
        public ContentsHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            View view = inflater.inflate(android.R.layout.simple_list_item_1, parent, false);
            return new ContentsHolder(view);
        }
        @Override
        public void onBindViewHolder(ContentsHolder holder, int position) {
            holder.bind(mBookContents.get(position), position);

        }
        @Override
        public int getItemCount() {
            return mBookContents.size();
        }
    }



    private class ContentsHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView mTextView;
        private int mPosition;

        public ContentsHolder(View itemView) {
            super(itemView);
            mTextView = (TextView) itemView;
            itemView.setOnClickListener(this);
        }

        public void bind(String content, int position) {
            mPosition = position;
            mTextView.setText(content);
        }

        @Override
        public void onClick(View v) {

            if (mListener != null)
                mListener.OnContentClicked(mBook.getContentParaIndexs().get(mPosition));

        }

    }

    public void setBackgroundColor(int color) {
        mLinearLayout.setBackgroundColor(color);

    }


}
