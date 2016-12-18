package com.hit56.android.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.hit56.android.R;

import com.rengwuxian.materialedittext.MaterialEditText;
import com.rengwuxian.materialedittext.validation.RegexpValidator;

public class ThreeFragment extends Fragment {

    private View rootView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        rootView =  inflater.inflate(R.layout.fragment_three, container, false);
        initEnableBt();
        initSingleLineEllipsisEt();
        initSetErrorEt();
        initValidationEt();

        return rootView;
        //add   test
    }


    private void initEnableBt() {
        final EditText basicEt = (EditText) rootView.findViewById(R.id.basicEt);
        final Button enableBt = (Button) rootView.findViewById(R.id.enableBt);
        enableBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                basicEt.setEnabled(!basicEt.isEnabled());
                enableBt.setText(basicEt.isEnabled() ? "DISABLE" : "ENABLE");
            }
        });
    }

    private void initSingleLineEllipsisEt() {
        EditText singleLineEllipsisEt = (EditText) rootView.findViewById(R.id.singleLineEllipsisEt);
        singleLineEllipsisEt.setSelection(singleLineEllipsisEt.getText().length());
    }

    private void initSetErrorEt() {
        final EditText bottomTextEt = (EditText) rootView.findViewById(R.id.bottomTextEt);
        final Button setErrorBt = (Button) rootView.findViewById(R.id.setErrorBt);
        setErrorBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomTextEt.setError("1-line Error!");
            }
        });
        final Button setError2Bt = (Button) rootView.findViewById(R.id.setError2Bt);
        setError2Bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomTextEt.setError("2-line\nError!");
            }
        });
        final Button setError3Bt = (Button) rootView.findViewById(R.id.setError3Bt);
        setError3Bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomTextEt.setError("So Many Errors! So Many Errors! So Many Errors! So Many Errors! So Many Errors! So Many Errors! So Many Errors! So Many Errors!");
            }
        });
    }

    private void initValidationEt() {
        final MaterialEditText validationEt = (MaterialEditText) rootView.findViewById(R.id.validationEt);
        validationEt.addValidator(new RegexpValidator("Only Integer Valid!", "\\d+"));
        final Button validateBt = (Button) rootView.findViewById(R.id.validateBt);
        validateBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // validate
                
                validationEt.validate();
            }
        });
    }
}
