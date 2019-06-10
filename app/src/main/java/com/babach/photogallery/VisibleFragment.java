package com.babach.photogallery;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.widget.Toast;

public abstract class VisibleFragment extends Fragment
{
    private static final String TAG = "VisibleFragment";

    private BroadcastReceiver mOnShowNotification = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            Log.i(TAG, "Canceling notification");
            setResultCode(Activity.RESULT_CANCELED);
        }
    };

    /**
     * Called when the Fragment is visible to the user.  This is generally
     * tied to of the containing
     * Activity's lifecycle.
     */
    @Override
    public void onStart()
    {
        super.onStart();
        IntentFilter filter = new IntentFilter(PollService.ACTION_SHOW_NOTIFICATION);
        Log.i(VisibleFragment.class.getSimpleName(), PollService.ACTION_SHOW_NOTIFICATION);
        getActivity().registerReceiver(mOnShowNotification, filter, PollService.PERM_PRIVATE, null);
    }

    /**
     * Called when the Fragment is no longer started.  This is generally
     * tied to of the containing
     * Activity's lifecycle.
     */
    @Override
    public void onStop()
    {
        super.onStop();
        getActivity().unregisterReceiver(mOnShowNotification);
    }
}
