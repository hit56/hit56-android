package com.hit56.android.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hit56.android.R;
import com.hit56.android.app.AppController;
import com.hit56.android.bean.RegisterResultBean;
import com.hit56.android.bean.User;
import com.hit56.android.utils.L;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Vector;

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
                sendText();
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

    private void sendText(){
        String str = mEditText.getText().toString();
        ArrayList<String> cars = new ArrayList<>();
        cars.add(str);
        User user = new User();
        AppController appController = AppController.getInstance();
        RegisterResultBean registerResultBean = appController.getUserData();
        user.setCars(cars);
        L.e("发布", registerResultBean.getUserName());
        user.setName(registerResultBean.getUserName());
        String url = "http://www.hit56.com:8083/getinfo/publish";
        send(user,url);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void send(final User user, String url){
        final String userName = user.getName();
        final ArrayList<String> cars = user.getCars();

        new AsyncTask<String, Void, RegisterResultBean>(){

            @Override
            protected RegisterResultBean doInBackground(String... params) {
                RegisterResultBean mResult = null;
                try {
                    String url = params[0];
                    DefaultHttpClient httpClient = new DefaultHttpClient();
                    HttpPost postRequest = new HttpPost(url);

                    JSONObject json = new JSONObject();
                    json.put("name",userName);
                    json.put("cars",cars);
//			StringEntity input = new StringEntity(
//					"{\"qty\":100,\"name\":\"iPad 4\"}");
                    StringEntity input = new StringEntity(json.toString(),"UTF-8");
                    input.setContentType("application/json");
                    postRequest.setEntity(input);

                    HttpResponse response = httpClient.execute(postRequest);

                    if (response.getStatusLine().getStatusCode() != 201) {
                        throw new RuntimeException("Failed : HTTP error code : "
                                + response.getStatusLine().getStatusCode());
                    }

                    BufferedReader br = new BufferedReader(new InputStreamReader(
                            (response.getEntity().getContent())));

                    String output;
                    mResult = new RegisterResultBean();
//			System.out.println("Output from Server .... \n");
                    while ((output = br.readLine()) != null) {
                        System.out.println(output);
                        JSONObject result = JSON.parseObject(output);
                        L.e("发布返回结果:",result.toString());
                        boolean isSuccess = result.getBoolean("success");
                        String info = result.getString("info");
                        mResult.setRegister(isSuccess);
                        mResult.setInfo(info);

                    }

                    httpClient.getConnectionManager().shutdown();

                } catch (MalformedURLException e) {

                    e.printStackTrace();
                } catch (IOException e) {

                    e.printStackTrace();

                }

                return mResult;
            }

            @Override
            protected void onPostExecute(RegisterResultBean registerResultBean) {
                if (registerResultBean == null){
                    return;
                }

                    Toast.makeText(ReleaseActivity.this, registerResultBean.getInfo(), Toast.LENGTH_SHORT).show();

            }
        }.execute(url);
    }
}
