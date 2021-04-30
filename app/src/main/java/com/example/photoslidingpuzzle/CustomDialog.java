package com.example.photoslidingpuzzle;

import android.app.ActionBar;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class CustomDialog extends DialogFragment implements View.OnClickListener{
    public static final String TAG = "custom_dialog";

    public CustomDialog() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.custom_dialog_fragment, container, false);
        view.findViewById(R.id.dialog_btn).setOnClickListener(this);


        // 백버튼, 취소버튼, 배경터치 막음
        if (getDialog() != null) {
            getDialog().setCanceledOnTouchOutside(false);
            //setCancelable(false);
        }


//        Dialog dialog = getDialog();
//
//        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        dialog.setCanceledOnTouchOutside(false);
        return view;
    }


    private void dismissDialog() {
        this.dismiss();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.dialog_btn:
                dismissDialog();
                break;
        }
    }
}