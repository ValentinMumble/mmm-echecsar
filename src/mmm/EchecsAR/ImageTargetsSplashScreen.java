/*==============================================================================
Copyright (c) 2010-2013 QUALCOMM Austria Research Center GmbH.
All Rights Reserved.

@file
    ImageTargetsSplashScreen.java

@brief
    Splash screen Activity for the ImageTargets sample application
    Splash screen is displayed for 2 seconds, then the About Screen is shown.

==============================================================================*/

package mmm.EchecsAR;

import mmm.EchecsAR.R;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;

public class ImageTargetsSplashScreen extends Activity {
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Sets the Splash Screen Layout
		setContentView(R.layout.splash_screen);

		// Start directly the ImageTargets activity for faster testing
		Intent i = new Intent(ImageTargetsSplashScreen.this, ImageTargets.class);
		startActivity(i);

		/*
		// Generates a Handler to launch the About Screen
		// after 2 seconds
		final Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
			public void run() {
				// Starts the About Screen Activity
				startActivity(new Intent(ImageTargetsSplashScreen.this,
						AboutScreen.class));
			}
		}, 1000L);
		*/
	}

	public void onConfigurationChanged(Configuration newConfig) {
		// Manages auto rotation for the Splash Screen Layout
		super.onConfigurationChanged(newConfig);
		setContentView(R.layout.splash_screen);
	}
}
