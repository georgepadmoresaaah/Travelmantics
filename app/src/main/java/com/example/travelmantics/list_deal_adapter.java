package com.example.travelmantics;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class list_deal_adapter extends RecyclerView.Adapter<list_deal_adapter.ViewHolder> {

    View viewAll;
    private ArrayList<traveldeal> mlist;
    private Context mContext;


    list_deal_adapter(Context context, ArrayList<traveldeal> list) {
        mContext = context;

        mlist = list;
    }


    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i) {

        // mviewHolder = viewHolder;

        traveldeal merlist = mlist.get(i);


         ImageView imageView_deals;

        final TextView Title, amount_text, Description_text;


        Title = viewHolder.Title;
        amount_text = viewHolder.amount_text;
        Description_text = viewHolder.Description_text;
        imageView_deals=viewHolder.imageView_deals;


        Title.setText(merlist.getTitle());
        amount_text.setText(merlist.getPrice());
        Description_text.setText(merlist.getDescription());

        GlideApp.with(mContext)
                .load(merlist.getImageUrl())
                .centerCrop()
                .into(imageView_deals);


    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        Context context = viewGroup.getContext();
        View itemView = LayoutInflater.from(context)
                .inflate(R.layout.rv_list_deals, viewGroup, false);



       return new ViewHolder(itemView);


//        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
//        View view = layoutInflater.inflate(R.layout.rv_list_deals, viewGroup, false);
//
//        ViewHolder viewHolder = new ViewHolder(view);
//
//
//        return viewHolder;


    }


    @Override
    public int getItemCount() {


        return mlist.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {

         ImageView imageView_deals;

         TextView Title, amount_text, Description_text;

        public ViewHolder(View itemView) {

            super(itemView);


            Title = itemView.findViewById(R.id.Title_text);
            Description_text = itemView.findViewById(R.id.Description_text);
            amount_text = itemView.findViewById(R.id.amount_text);
            imageView_deals = itemView.findViewById(R.id.imageView_deals);
        }

    }

}
