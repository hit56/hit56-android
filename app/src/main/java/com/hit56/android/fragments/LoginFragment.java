package com.hit56.android.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Toast;

import com.hit56.android.R;
import com.rengwuxian.materialedittext.MaterialEditText;

/**
 * Created by Stone on 16/9/28.
 */

public class LoginFragment extends Fragment{

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_login,container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        initView(view);
        super.onViewCreated(view, savedInstanceState);
    }

    /**
     *  绑定登录界面的组件
     * */
    private void initView(View view){
        MaterialEditText user_edt = (MaterialEditText) view.findViewById(R.id.login_user);
        MaterialEditText password_edt = (MaterialEditText) view.findViewById(R.id.login_password);
        CheckBox rem_password = (CheckBox) view.findViewById(R.id.login_checkbox);
        AppCompatButton login_bt = (AppCompatButton) view.findViewById(R.id.login_button);

    }
}
