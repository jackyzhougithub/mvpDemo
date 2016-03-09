package com.z.jk.linkagelistview.DialogFragment;

import android.app.DialogFragment;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.z.jk.linkagelistview.R;

/**
 * Created by $ zhoudeheng on 2016/3/4.
 * Email zhoudeheng@qccr.com
 */
public class TestDialogFragment extends DialogFragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setBackgroundDrawable(new
                ColorDrawable(Color.TRANSPARENT));
        View view = inflater.inflate(R.layout.test,container);
        return view;
    }


}
