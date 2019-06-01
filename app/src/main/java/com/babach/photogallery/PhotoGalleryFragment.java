package com.babach.photogallery;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PhotoGalleryFragment extends Fragment
{

    private static final String TAG = PhotoGalleryFragment.class.getSimpleName();
    private RecyclerView mPhotoRecyclerView;
    private List<GalleryItem> mItems = new ArrayList<>();
    private ThumbnailDownloader<PhotoHolder> mPhotoHolderThumbnailDownloader;

    public static PhotoGalleryFragment newInstance()
    {
        return new PhotoGalleryFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        new FetchItemsTask().execute();

        Handler responseHandler = new Handler();
        mPhotoHolderThumbnailDownloader = new ThumbnailDownloader<>(responseHandler);
        mPhotoHolderThumbnailDownloader.setThumbnailDownloadListener(new ThumbnailDownloader.ThumbnailDownloadListener<PhotoHolder>()
        {
            @Override
            public void onThumbnailDownloaded(PhotoHolder target, Bitmap thumbnail)
            {
                Drawable drawable = new BitmapDrawable(getResources() ,thumbnail);
                target.bindDrawable(drawable);
            }
        });
        mPhotoHolderThumbnailDownloader.start();
        mPhotoHolderThumbnailDownloader.getLooper(); //Important to do to avoid race condition
        Log.i(TAG, "Background thread started!");
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        mPhotoHolderThumbnailDownloader.quit();
        Log.i(TAG, "Background thread destroyed!");
    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        mPhotoHolderThumbnailDownloader.clearQueue();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.fragment_photo_gallery, container, false);
        mPhotoRecyclerView = v.findViewById(R.id.photo_recycler_view);
        mPhotoRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));


        setupAdapter();
        return v;
    }

    private void setupAdapter()
    {
        if(isAdded())
        {
            mPhotoRecyclerView.setAdapter(new PhotoAdapter(mItems));
        }
    }

    private class PhotoHolder extends RecyclerView.ViewHolder
    {
        private ImageView mImageView;
        public PhotoHolder(View itemView)
        {
            super(itemView);
            mImageView = itemView.findViewById(R.id.item_image_view);
        }

        public void bindDrawable(Drawable drawable)
        {
            mImageView.setImageDrawable(drawable);
        }
    }


    private class PhotoAdapter extends RecyclerView.Adapter<PhotoHolder>
    {
        private List<GalleryItem> mGalleryItems;
        public PhotoAdapter(List<GalleryItem> galleryItems)
        {
            this.mGalleryItems = galleryItems;
        }

        @Override
        public PhotoHolder onCreateViewHolder(ViewGroup parent, int viewType)
        {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View view = inflater.inflate(R.layout.list_item_gallery, parent, false);
            return new PhotoHolder(view);
        }

        @Override
        public void onBindViewHolder(PhotoHolder holder, int position)
        {
            GalleryItem galleryItem = mGalleryItems.get(position);
            Drawable placeholder = getResources().getDrawable(R.drawable.bill_up_close);
            holder.bindDrawable(placeholder);
            mPhotoHolderThumbnailDownloader.queueThumbnail(holder, galleryItem.getUrl());
        }

        @Override
        public int getItemCount()
        {
            return mGalleryItems.size();
        }
    }


    private class FetchItemsTask extends AsyncTask<Void, Void, List<GalleryItem>>
    {
        @Override
        protected List<GalleryItem> doInBackground(Void... voids)
        {
                return new FlickrFetchr().fetchItems();
        }

        @Override
        protected void onPostExecute(List<GalleryItem> galleryItems)
        {
            mItems = galleryItems;
            setupAdapter();
        }
    }
}