package com.example.photoeditor4.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.photoeditor4.Interface.FiltersListFragmentListener;
import com.example.photoeditor4.R;
import com.zomato.photofilters.utils.ThumbnailItem;

import java.util.List;

public class ThumbnailAdapter extends RecyclerView.Adapter<ThumbnailAdapter.MyViewHolder>{

    private List<ThumbnailItem> thumbnailItemsList;
    private FiltersListFragmentListener listener;
    private Context context;
    private int selectedIndex = 0;

    public ThumbnailAdapter(List<ThumbnailItem> thumbnailItemsList, FiltersListFragmentListener listener, Context context){
        this.thumbnailItemsList = thumbnailItemsList;
        this.listener = listener;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.thumbnail_item,parent,false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        final ThumbnailItem thumbnailItem =thumbnailItemsList.get(position);

        holder.thumbnail.setImageBitmap(thumbnailItem.image);
        holder.thumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
            listener.onFilterSelected(thumbnailItem.filter);
            selectedIndex = position;
            notifyDataSetChanged();
            }
        });

        holder.filter_name.setText((thumbnailItem.filterName));

        if(selectedIndex == position)
            holder.filter_name.setTextColor(ContextCompat.getColor(context,R.color.selected_filter));
        else
            holder.filter_name.setTextColor(ContextCompat.getColor(context,R.color.normal_filter));
    }

    @Override
    public int getItemCount() {
        return thumbnailItemsList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView thumbnail;
        TextView filter_name;
        public MyViewHolder(View itemView){
            super(itemView);
            thumbnail = (ImageView)itemView.findViewById(R.id.thumbnail);
            filter_name = (TextView)itemView.findViewById(R.id.filter_name);
        }
    }

}
