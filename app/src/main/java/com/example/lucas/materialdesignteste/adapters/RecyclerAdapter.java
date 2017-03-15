package com.example.lucas.materialdesignteste.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.lucas.materialdesignteste.R;
import com.example.lucas.materialdesignteste.dao.DatabaseHelper;
import com.example.lucas.materialdesignteste.dao.model.Servico;
import com.example.lucas.materialdesignteste.interfaces.RecyclerViewOnClickListenerHack;

import java.util.List;

/**
 * Created by Lucas on 08/03/2017.
 */

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.MyViewHolder>{
    private List<Servico> mList;
    private LayoutInflater mLayoutInflater;
    private RecyclerViewOnClickListenerHack mRecyclerViewOnClickListenerHack;

    public RecyclerAdapter(List<Servico> mList, Context context) {
        this.mList = mList;
        this.mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = this.mLayoutInflater.inflate(R.layout.servico_item, viewGroup, false);
        MyViewHolder mvh = new MyViewHolder(v);
        return mvh;
    }

    @Override
    public void onBindViewHolder(MyViewHolder myViewHolder, int position) {
        myViewHolder.ivIcone.setImageResource(mList.get(position).getIcone());
    }

    @Override
    public int getItemCount() {
        return this.mList.size();
    }

    public void setRecyclerViewOnClickListenerHack(RecyclerViewOnClickListenerHack r){
        this.mRecyclerViewOnClickListenerHack = r;
    }

    public void addItemList(int position){
        notifyItemInserted(position);
    }

    public void removeItemList(int position){
        notifyItemRemoved(position);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public ImageView ivIcone;
        public MyViewHolder(View itemView){
            super(itemView);

            ivIcone = (ImageView) itemView.findViewById(R.id.icone_servico_item);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mRecyclerViewOnClickListenerHack != null){
                mRecyclerViewOnClickListenerHack.onClickListener(v, getAdapterPosition());
            }
        }
    }
}

