package com.babach.photogallery;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.webkit.WebView;

public class PhotoPageActivity extends SingleFragmentActivity
{

    public static Intent newIntent(Context context, Uri photoPageUri)
    {
        Intent i = new Intent(context, PhotoPageActivity.class);
        i.setData(photoPageUri);
        return i;
    }

    @Override
    protected Fragment createFragment()
    {
        return PhotoPageFragment.newInstance(getIntent().getData());
    }

    @Override
    public void onBackPressed()
    {
        WebView webView = findViewById(R.id.web_view);
        if (webView.canGoBack())
        {
            webView.goBack();
            return;
        }
        super.onBackPressed();
    }
}