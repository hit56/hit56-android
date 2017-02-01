package com.hit56.android.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.hit56.android.R;

public class ReleaseActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText mEditText;
    private ImageView mImageView;
    private Button mButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_release);
        initView();
    }

    private void initView(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("发布");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mButton = (Button) findViewById(R.id.release_button);
        mEditText = (EditText) findViewById(R.id.release_text_input);
        mImageView = (ImageView) findViewById(R.id.release_add_image);

        mButton.setOnClickListener(this);
        mImageView.setOnClickListener(this);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 点击事件处理
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.release_button://确认发布
                if(mEditText.getText().toString().isEmpty() || mEditText.getText().toString().trim().isEmpty()){
                    return;
                }
                break;
            case R.id.release_add_image://添加图片
                addImage();
                break;
            default:
                break;
        }
    }

    /**
     * add Images
     */
    private void addImage(){

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
