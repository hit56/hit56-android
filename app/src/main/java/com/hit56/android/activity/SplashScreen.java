package com.hit56.android.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.hit56.android.R;
import com.hit56.android.app.AppController;

public class SplashScreen extends Activity {

	private ImageView splashImage;
	// Splash screen timer 3秒
	private static int SPLASH_TIME_OUT = 2000;
//	private static int SPLASH_TIME_OUT = 30;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		initView();
		new Handler().postDelayed(new Runnable() {

			/*
			 * Showing splash screen with a timer. This will be useful when you
			 * want to show case your app logo / company
			 */

			@Override
			public void run() {
				// This method will be executed once the timer is over
				// Start your app main activity
				//loadImage("http://hit56.com/images/user_imgs/294.png");
				Intent i = new Intent(SplashScreen.this, MainActivity.class);
				startActivity(i);

				// close this activity
				finish();
			}
		}, SPLASH_TIME_OUT);


	}

	private void initView(){
		splashImage = (ImageView) findViewById(R.id.imgLogo);
	}

	private void loadImage(String url){
		AppController mAppController = AppController.getInstance();
		RequestQueue requestQueue = mAppController.getRequestQueue();
		ImageRequest imageRequest = new ImageRequest(url, new Response.Listener<Bitmap>() {
			@Override
			public void onResponse(Bitmap response) {
				splashImage.setImageBitmap(response);
				Intent i = new Intent(SplashScreen.this, MainActivity.class);
				startActivity(i);

				// close this activity
				finish();
			}
		}, 0, 0, Bitmap.Config.ARGB_8888, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {

			}
		});

		requestQueue.add(imageRequest);
	}

}
