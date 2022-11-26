package com.example.photoeditor4;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.photoeditor4.Adapter.ThumbnailAdapter;
import com.example.photoeditor4.Interface.FiltersListFragmentListener;
import com.example.photoeditor4.Utilites.BitMapUtilities;
import com.example.photoeditor4.Utilites.SpacesItemDecoration;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.yalantis.ucrop.util.BitmapLoadUtils;
import com.zomato.photofilters.FilterPack;
import com.zomato.photofilters.imageprocessors.Filter;
import com.zomato.photofilters.utils.ThumbnailItem;
import com.zomato.photofilters.utils.ThumbnailsManager;

import java.util.ArrayList;
import java.util.List;

public class FiltersListFragment extends BottomSheetDialogFragment implements FiltersListFragmentListener {
    RecyclerView recyclerView;
    ThumbnailAdapter adapter;
    List<ThumbnailItem> thumbnailItemsList;
    FiltersListFragmentListener listener;
    static FiltersListFragment instance;
    static Bitmap bitmap;


    public static FiltersListFragment getInstance(Bitmap bitmapSave){
        if(instance==null)
            instance = new FiltersListFragment();
        bitmap = bitmapSave;
        return instance;
    }

    public void setListener(FiltersListFragmentListener listener) {
        this.listener = listener;
    }


    public FiltersListFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View itemView = inflater.inflate(R.layout.fragment_filter_list, container, false);
        thumbnailItemsList = new ArrayList<>();
        adapter = new ThumbnailAdapter(thumbnailItemsList,this,getActivity());

        recyclerView = (RecyclerView) itemView.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false));
        recyclerView.setItemAnimator((new DefaultItemAnimator()));
        int space = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics());
        recyclerView.addItemDecoration(new SpacesItemDecoration(space));
        recyclerView.setAdapter(adapter);

        displayThumbnail();
        return itemView;
    }

    public void displayThumbnail() {
        Runnable r = new Runnable() {
            @Override
            public void run() {

                Bitmap thumbImg;
                if(bitmap == null)
                    thumbImg = BitMapUtilities.getBitmapFromGallery(getActivity(),MainActivity.resultUri,100,100);
                else
                    thumbImg = Bitmap.createScaledBitmap(MainActivity.originalBitmap,100,100,false);

                if(thumbImg ==  null)
                    return;
                ThumbnailsManager.clearThumbs();
                thumbnailItemsList.clear();

                //We need a normal bitmap
                ThumbnailItem thumbnailItem= new ThumbnailItem();
                thumbnailItem.image = thumbImg;
                thumbnailItem.filterName="Normal";
                ThumbnailsManager.addThumb(thumbnailItem);

                List<Filter> filters = FilterPack.getFilterPack(getActivity());

                for(Filter filter: filters){
                    ThumbnailItem thumbI = new ThumbnailItem();
                    thumbI.image =thumbImg;
                    thumbI.filter = filter;
                    thumbI.filterName = filter.getName();
                    ThumbnailsManager.addThumb(thumbI);
                }
                thumbnailItemsList.addAll(ThumbnailsManager.processThumbs(getActivity()));

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.notifyDataSetChanged();
                    }
                });
            }
        };
        new Thread(r).start();
    }

    @Override
    public void onFilterSelected(Filter filter) {
        if(listener != null)
            listener.onFilterSelected(filter);
            //EditImageFragment.instance = null;
    }
}