package com.hit56.android.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import com.hit56.android.R;
import com.hit56.android.widget.RLView;
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
        Button loginBt = (Button) view.findViewById(R.id.me_login_buton);
        RLView focus = (RLView) view.findViewById(R.id.me_focus);
        RLView sent = (RLView) view.findViewById(R.id.me_sent);
        RLView message = (RLView) view.findViewById(R.id.me_message);

        loginBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "", Toast.LENGTH_LONG).show();
            }
        });


    }
}
