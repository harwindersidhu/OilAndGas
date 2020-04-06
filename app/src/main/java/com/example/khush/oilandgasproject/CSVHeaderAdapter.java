package com.example.khush.oilandgasproject;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.LinkedList;
import java.util.List;
/*
This class is an adepter for recycler view.
 */
public class CSVHeaderAdapter extends RecyclerView.Adapter<CSVHeaderAdapter.MyViewHolder> {
    private final List<Headers> headerList;
    private final List<Headers>  selectedItems;
    private LayoutInflater mInflater;

    public List<Headers> getSelectedItems() {
        return selectedItems;
    }

    public CSVHeaderAdapter(Context context,
                            List<Headers> headerList) {
        mInflater = LayoutInflater.from(context);
        this.headerList = headerList;
        this.selectedItems = new LinkedList<>();
    }
    @NonNull
    @Override
    public CSVHeaderAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View mItemView = mInflater.inflate(R.layout.csvfile_headers,
                viewGroup, false);
        return new MyViewHolder(mItemView, this);
    }

    @Override
    public void onBindViewHolder(@NonNull CSVHeaderAdapter.MyViewHolder myViewHolder, int i) {
        Headers header = headerList.get(i);
        String mCurrent = header.getHeadersName();
        myViewHolder.csvHeaderTextView.setText(mCurrent);
        if (selectedItems.contains(header)) {
            myViewHolder.mySwitch.setChecked(true);
        }
        else {
            myViewHolder.mySwitch.setChecked(false);
        }
    }

    @Override
    public int getItemCount() {
        return headerList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public final TextView csvHeaderTextView;
        public final Switch mySwitch;
        final CSVHeaderAdapter mAdapter;

        public  MyViewHolder(View itemView, CSVHeaderAdapter mAdapter) {
            super(itemView);
            this.csvHeaderTextView = itemView.findViewById(R.id.headerName);
            this.mySwitch = itemView.findViewById(R.id.headerSwitch);
            this.mySwitch.setOnClickListener(this);
            this.mAdapter = mAdapter;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            // Get the position of the item that was clicked.
            int mPosition = getLayoutPosition();
            // Use that to access the affected item in mWordList.
            Headers header = headerList.get(mPosition);
            String element = header.getHeadersName();
            if (selectedItems.contains(header)) {
                this.mySwitch.setChecked(false);
                selectedItems.remove(header);
            }
            else {
                this.mySwitch.setChecked(true);
                selectedItems.add(header);
            }

            // Show toast message.
            String message = element + " clicked.";
            Toast.makeText(v.getContext(), message, Toast.LENGTH_SHORT).show();
            mAdapter.notifyDataSetChanged();
        }
    }
}
