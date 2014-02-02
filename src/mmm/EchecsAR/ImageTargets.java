/*==============================================================================
Copyright (c) 2010-2013 QUALCOMM Austria Research Center GmbH.
All Rights Reserved.

@file
    ImageTargets.java

@brief
    Sample for ImageTargets

==============================================================================*/

package mmm.EchecsAR;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Vector;

import mmm.bluetooth.BluetoothChatService;
import mmm.bluetooth.DeviceListActivity;
import mmm.jeu.control.CEchiquier;
import mmm.jeu.model.Coord;
import mmm.jeu.model.ToolsModel;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.qualcomm.QCAR.QCAR;

/** The main activity for the ImageTargets sample. */
public class ImageTargets extends Activity implements Adapter {
	// Focus mode constants:
	private static final int FOCUS_MODE_NORMAL = 0;
	private static final int FOCUS_MODE_CONTINUOUS_AUTO = 1;

	// Application status constants:
	private static final int APPSTATUS_UNINITED = -1;
	private static final int APPSTATUS_INIT_APP = 0;
	private static final int APPSTATUS_INIT_QCAR = 1;
	private static final int APPSTATUS_INIT_TRACKER = 2;
	private static final int APPSTATUS_INIT_APP_AR = 3;
	private static final int APPSTATUS_LOAD_TRACKER = 4;
	private static final int APPSTATUS_INITED = 5;
	private static final int APPSTATUS_CAMERA_STOPPED = 6;
	private static final int APPSTATUS_CAMERA_RUNNING = 7;

	// Name of the native dynamic libraries to load:
	private static final String NATIVE_LIB_SAMPLE = "ImageTargets";
	private static final String NATIVE_LIB_QCAR = "QCAR";

	// Bluetooth
	// Message types sent from the BluetoothChatService Handler
	public static final int MESSAGE_STATE_CHANGE = 1;
	public static final int MESSAGE_READ = 2;
	public static final int MESSAGE_WRITE = 3;
	public static final int MESSAGE_DEVICE_NAME = 4;
	public static final int MESSAGE_TOAST = 5;

	// Intent request codes
	private static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
	private static final int REQUEST_CONNECT_DEVICE_INSECURE = 2;
	private static final int REQUEST_ENABLE_BT = 3;

	// Key names received from the BluetoothChatService Handler
	public static final String DEVICE_NAME = "device_name";
	public static final String TOAST = "toast";

	// Constants for Hiding/Showing Loading dialog
	static final int HIDE_LOADING_DIALOG = 0;
	static final int SHOW_LOADING_DIALOG = 1;

	private View mLoadingDialogContainer;

	// Our OpenGL view:
	private QCARSampleGLView mGlView;

	// Our renderer:
	private ImageTargetsRenderer mRenderer;

	// Display size of the device:
	private int mScreenWidth = 0;
	private int mScreenHeight = 0;

	// Constant representing invalid screen orientation to trigger a query:
	private static final int INVALID_SCREEN_ROTATION = -1;

	// Last detected screen rotation:
	private int mLastScreenRotation = INVALID_SCREEN_ROTATION;

	// The current application status:
	private int mAppStatus = APPSTATUS_UNINITED;

	// The async tasks to initialize the QCAR SDK:
	private InitQCARTask mInitQCARTask;
	private LoadTrackerTask mLoadTrackerTask;

	// An object used for synchronizing QCAR initialization, dataset loading and
	// the Android onDestroy() life cycle event. If the application is destroyed
	// while a data set is still being loaded, then we wait for the loading
	// operation to finish before shutting down QCAR:
	private Object mShutdownLock = new Object();

	// QCAR initialization flags:
	private int mQCARFlags = 0;

	// The textures we will use for rendering:
	private Vector<Texture> mTextures;

	// Detects the double tap gesture for launching the Camera menu
	private GestureDetector mGestureDetector;

	// Contextual Menu Options for Camera Flash - Autofocus
	private boolean mFlash = false;
	private boolean mContAutofocus = false;

	// The menu item for swapping data sets:
	MenuItem mDataSetMenuItem = null;

	private RelativeLayout mUILayout;

	private CEchiquier ech;

	// Bluetooth
	// Local Bluetooth adapter
	private BluetoothAdapter mBluetoothAdapter = null;
	// Member object for the chat services
	private BluetoothChatService mChatService = null;
	// Name of the connected device
	private String mConnectedDeviceName = null;

	/** Static initializer block to load native libraries on start-up. */
	static {
		loadLibrary(NATIVE_LIB_QCAR);
		loadLibrary(NATIVE_LIB_SAMPLE);
	}

	/**
	 * Creates a handler to update the status of the Loading Dialog from an UI
	 * Thread
	 */
	static class LoadingDialogHandler extends Handler {
		private final WeakReference<ImageTargets> mImageTargets;

		LoadingDialogHandler(ImageTargets imageTargets) {
			mImageTargets = new WeakReference<ImageTargets>(imageTargets);
		}

		public void handleMessage(Message msg) {
			ImageTargets imageTargets = mImageTargets.get();
			if (imageTargets == null) {
				return;
			}

			if (msg.what == SHOW_LOADING_DIALOG) {
				imageTargets.mLoadingDialogContainer
						.setVisibility(View.VISIBLE);

			} else if (msg.what == HIDE_LOADING_DIALOG) {
				imageTargets.mLoadingDialogContainer.setVisibility(View.GONE);
			}
		}
	}

	private final Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MESSAGE_STATE_CHANGE:
				switch (msg.arg1) {
				case BluetoothChatService.STATE_CONNECTED:
					break;
				case BluetoothChatService.STATE_CONNECTING:
					break;
				case BluetoothChatService.STATE_LISTEN:
				case BluetoothChatService.STATE_NONE:
					break;
				}
				break;
			case MESSAGE_WRITE:
				byte[] writeBuf = (byte[]) msg.obj;
				// construct a string from the buffer
				String writeMessage = new String(writeBuf);
				break;
			case MESSAGE_READ:
				byte[] readBuf = (byte[]) msg.obj;
				// construct a string from the valid bytes in the buffer
				String readMessage = new String(readBuf, 0, msg.arg1);
				receiveMove(readMessage);
				break;
			case MESSAGE_DEVICE_NAME:
				// save the connected device's name
				mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
				Toast.makeText(getApplicationContext(),
						"Connected to " + mConnectedDeviceName,
						Toast.LENGTH_SHORT).show();
				break;
			case MESSAGE_TOAST:
				Toast.makeText(getApplicationContext(), msg.getData().getString(TOAST),
                        Toast.LENGTH_SHORT).show();
				break;
			}
		}
	};

	private Handler loadingDialogHandler = new LoadingDialogHandler(this);

	/** An async task to initialize QCAR asynchronously. */
	private class InitQCARTask extends AsyncTask<Void, Integer, Boolean> {
		// Initialize with invalid value:
		private int mProgressValue = -1;

		protected Boolean doInBackground(Void... params) {
			// Prevent the onDestroy() method to overlap with initialization:
			synchronized (mShutdownLock) {
				QCAR.setInitParameters(ImageTargets.this, mQCARFlags);

				do {
					// QCAR.init() blocks until an initialization step is
					// complete, then it proceeds to the next step and reports
					// progress in percents (0 ... 100%).
					// If QCAR.init() returns -1, it indicates an error.
					// Initialization is done when progress has reached 100%.
					mProgressValue = QCAR.init();

					// Publish the progress value:
					publishProgress(mProgressValue);

					// We check whether the task has been canceled in the
					// meantime (by calling AsyncTask.cancel(true)).
					// and bail out if it has, thus stopping this thread.
					// This is necessary as the AsyncTask will run to completion
					// regardless of the status of the component that
					// started is.
				} while (!isCancelled() && mProgressValue >= 0
						&& mProgressValue < 100);

				return (mProgressValue > 0);
			}
		}

		protected void onProgressUpdate(Integer... values) {
			// Do something with the progress value "values[0]", e.g. update
			// splash screen, progress bar, etc.
		}

		protected void onPostExecute(Boolean result) {
			// Done initializing QCAR, proceed to next application
			// initialization status:
			if (result) {
				DebugLog.LOGD("InitQCARTask::onPostExecute: QCAR "
						+ "initialization successful");

				updateApplicationStatus(APPSTATUS_INIT_TRACKER);
			} else {
				// Create dialog box for display error:
				AlertDialog dialogError = new AlertDialog.Builder(
						ImageTargets.this).create();

				dialogError.setButton(DialogInterface.BUTTON_POSITIVE, "Close",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								// Exiting application:
								System.exit(1);
							}
						});

				String logMessage;

				// NOTE: Check if initialization failed because the device is
				// not supported. At this point the user should be informed
				// with a message.
				if (mProgressValue == QCAR.INIT_DEVICE_NOT_SUPPORTED) {
					logMessage = "Failed to initialize QCAR because this "
							+ "device is not supported.";
				} else {
					logMessage = "Failed to initialize QCAR.";
				}

				// Log error:
				DebugLog.LOGE("InitQCARTask::onPostExecute: " + logMessage
						+ " Exiting.");

				// Show dialog box with error message:
				dialogError.setMessage(logMessage);
				dialogError.show();
			}
		}
	}

	/** An async task to load the tracker data asynchronously. */
	private class LoadTrackerTask extends AsyncTask<Void, Integer, Boolean> {
		protected Boolean doInBackground(Void... params) {
			// Prevent the onDestroy() method to overlap:
			synchronized (mShutdownLock) {
				// Load the tracker data set:
				return (loadTrackerData() > 0);
			}
		}

		protected void onPostExecute(Boolean result) {
			DebugLog.LOGD("LoadTrackerTask::onPostExecute: execution "
					+ (result ? "successful" : "failed"));

			if (result) {
				// Done loading the tracker, update application status:
				updateApplicationStatus(APPSTATUS_INITED);
			} else {
				// Create dialog box for display error:
				AlertDialog dialogError = new AlertDialog.Builder(
						ImageTargets.this).create();

				dialogError.setButton(DialogInterface.BUTTON_POSITIVE, "Close",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								// Exiting application:
								System.exit(1);
							}
						});

				// Show dialog box with error message:
				dialogError.setMessage("Failed to load tracker data.");
				dialogError.show();
			}
		}
	}

	/** Stores screen dimensions */
	private void storeScreenDimensions() {
		// Query display dimensions:
		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		mScreenWidth = metrics.widthPixels;
		mScreenHeight = metrics.heightPixels;
	}

	/**
	 * Called when the activity first starts or the user navigates back to an
	 * activity.
	 */
	protected void onCreate(Bundle savedInstanceState) {
		DebugLog.LOGD("ImageTargets::onCreate");
		super.onCreate(savedInstanceState);
		// Load any sample specific textures:
		mTextures = new Vector<Texture>();
		loadTextures();

		// Query the QCAR initialization flags:
		mQCARFlags = getInitializationFlags();

		// Creates the GestureDetector listener for processing double tap
		mGestureDetector = new GestureDetector(this, new GestureListener());

		// Update the application status to start initializing application:
		updateApplicationStatus(APPSTATUS_INIT_APP);

		ech = new CEchiquier(this);

		// Get local Bluetooth adapter
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

		// If the adapter is null, then Bluetooth is not supported
		if (mBluetoothAdapter == null) {
			Toast.makeText(this, "Bluetooth is not available",
					Toast.LENGTH_LONG).show();
			finish();
			return;
		}
	}

	@Override
	public void onStart() {
		super.onStart();

		// If BT is not on, request that it be enabled.
		// setupChat() will then be called during onActivityResult
		if (!mBluetoothAdapter.isEnabled()) {
			Intent enableIntent = new Intent(
					BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableIntent, 3);
			// Otherwise, setup the chat session
		} else {
			if (mChatService == null) {
				// Initialize the BluetoothChatService to perform bluetooth
				// connections
				mChatService = new BluetoothChatService(this, mHandler);
			}
		}
	}

	/**
	 * We want to load specific textures from the APK, which we will later use
	 * for rendering.
	 */
	private void loadTextures() {
		mTextures.add(Texture.loadTextureFromApk("white.jpg", getAssets()));
		mTextures.add(Texture.loadTextureFromApk("black.jpg", getAssets()));
		mTextures.add(Texture.loadTextureFromApk("selected.jpg", getAssets()));
		mTextures.add(Texture.loadTextureFromApk("available.png", getAssets()));
	}

	/** Configure QCAR with the desired version of OpenGL ES. */
	private int getInitializationFlags() {
		int flags = 0;

		// Query the native code:
		if (getOpenGlEsVersionNative() == 1) {
			flags = QCAR.GL_11;
		} else {
			flags = QCAR.GL_20;
		}

		return flags;
	}

	/**
	 * Native method for querying the OpenGL ES version. Returns 1 for OpenGl ES
	 * 1.1, returns 2 for OpenGl ES 2.0.
	 */
	public native int getOpenGlEsVersionNative();

	/** Native tracker initialization and deinitialization. */
	public native int initTracker();

	public native void deinitTracker();

	/** Native functions to load and destroy tracking data. */
	public native int loadTrackerData();

	public native void destroyTrackerData();

	/** Native sample initialization. */
	public native void onQCARInitializedNative();

	/** Native methods for starting and stopping the camera. */
	private native void startCamera();

	private native void stopCamera();

	/**
	 * Native method for setting / updating the projection matrix for AR content
	 * rendering
	 */
	private native void setProjectionMatrix();

	/** Called when the activity will start interacting with the user. */
	protected void onResume() {
		DebugLog.LOGD("ImageTargets::onResume");
		super.onResume();

		// QCAR-specific resume operation
		QCAR.onResume();

		// We may start the camera only if the QCAR SDK has already been
		// initialized
		if (mAppStatus == APPSTATUS_CAMERA_STOPPED) {
			updateApplicationStatus(APPSTATUS_CAMERA_RUNNING);
		}

		// Resume the GL view:
		if (mGlView != null) {
			mGlView.setVisibility(View.VISIBLE);
			mGlView.onResume();
		}
	}

	private void updateActivityOrientation() {
		Configuration config = getResources().getConfiguration();

		boolean isPortrait = false;

		switch (config.orientation) {
		case Configuration.ORIENTATION_PORTRAIT:
			isPortrait = true;
			break;
		case Configuration.ORIENTATION_LANDSCAPE:
			isPortrait = false;
			break;
		case Configuration.ORIENTATION_UNDEFINED:
		default:
			break;
		}

		DebugLog.LOGI("Activity is in "
				+ (isPortrait ? "PORTRAIT" : "LANDSCAPE"));
		setActivityPortraitMode(isPortrait);
	}

	/**
	 * Updates projection matrix and viewport after a screen rotation change was
	 * detected.
	 */
	public void updateRenderView() {
		int currentScreenRotation = getWindowManager().getDefaultDisplay()
				.getRotation();
		if (currentScreenRotation != mLastScreenRotation) {
			// Set projection matrix if there is already a valid one:
			if (QCAR.isInitialized()
					&& (mAppStatus == APPSTATUS_CAMERA_RUNNING)) {
				DebugLog.LOGD("ImageTargets::updateRenderView");

				// Query display dimensions:
				storeScreenDimensions();

				// Update viewport via renderer:
				mRenderer.updateRendering(mScreenWidth, mScreenHeight);

				// Update projection matrix:
				setProjectionMatrix();

				// Cache last rotation used for setting projection matrix:
				mLastScreenRotation = currentScreenRotation;
			}
		}
	}

	/** Callback for configuration changes the activity handles itself */
	public void onConfigurationChanged(Configuration config) {
		DebugLog.LOGD("ImageTargets::onConfigurationChanged");
		super.onConfigurationChanged(config);

		updateActivityOrientation();

		storeScreenDimensions();

		// Invalidate screen rotation to trigger query upon next render call:
		mLastScreenRotation = INVALID_SCREEN_ROTATION;
	}

	/** Called when the system is about to start resuming a previous activity. */
	protected void onPause() {
		DebugLog.LOGD("ImageTargets::onPause");
		super.onPause();

		if (mGlView != null) {
			mGlView.setVisibility(View.INVISIBLE);
			mGlView.onPause();
		}

		if (mAppStatus == APPSTATUS_CAMERA_RUNNING) {
			updateApplicationStatus(APPSTATUS_CAMERA_STOPPED);
		}

		// Disable flash when paused
		if (mFlash) {
			mFlash = false;
			activateFlash(mFlash);
		}

		// QCAR-specific pause operation
		QCAR.onPause();
	}

	/** Native function to deinitialize the application. */
	private native void deinitApplicationNative();

	/** The final call you receive before your activity is destroyed. */
	protected void onDestroy() {
		DebugLog.LOGD("ImageTargets::onDestroy");
		super.onDestroy();

		// Cancel potentially running tasks
		if (mInitQCARTask != null
				&& mInitQCARTask.getStatus() != InitQCARTask.Status.FINISHED) {
			mInitQCARTask.cancel(true);
			mInitQCARTask = null;
		}

		if (mLoadTrackerTask != null
				&& mLoadTrackerTask.getStatus() != LoadTrackerTask.Status.FINISHED) {
			mLoadTrackerTask.cancel(true);
			mLoadTrackerTask = null;
		}

		// Ensure that all asynchronous operations to initialize QCAR
		// and loading the tracker datasets do not overlap:
		synchronized (mShutdownLock) {

			// Do application deinitialization in native code:
			deinitApplicationNative();

			// Unload texture:
			mTextures.clear();
			mTextures = null;

			// Destroy the tracking data set:
			destroyTrackerData();

			// Deinit the tracker:
			deinitTracker();

			// Deinitialize QCAR SDK:
			QCAR.deinit();
		}

		System.gc();
	}

	/**
	 * NOTE: this method is synchronized because of a potential concurrent
	 * access by ImageTargets::onResume() and InitQCARTask::onPostExecute().
	 */
	private synchronized void updateApplicationStatus(int appStatus) {
		// Exit if there is no change in status:
		if (mAppStatus == appStatus)
			return;

		// Store new status value:
		mAppStatus = appStatus;

		// Execute application state-specific actions:
		switch (mAppStatus) {
		case APPSTATUS_INIT_APP:
			// Initialize application elements that do not rely on QCAR
			// initialization:
			initApplication();

			// Proceed to next application initialization status:
			updateApplicationStatus(APPSTATUS_INIT_QCAR);
			break;

		case APPSTATUS_INIT_QCAR:
			// Initialize QCAR SDK asynchronously to avoid blocking the
			// main (UI) thread.
			//
			// NOTE: This task instance must be created and invoked on the
			// UI thread and it can be executed only once!
			try {
				mInitQCARTask = new InitQCARTask();
				mInitQCARTask.execute();
			} catch (Exception e) {
				DebugLog.LOGE("Initializing QCAR SDK failed");
			}
			break;

		case APPSTATUS_INIT_TRACKER:
			// Initialize the ImageTracker:
			if (initTracker() > 0) {
				// Proceed to next application initialization status:
				updateApplicationStatus(APPSTATUS_INIT_APP_AR);
			}
			break;

		case APPSTATUS_INIT_APP_AR:
			// Initialize Augmented Reality-specific application elements
			// that may rely on the fact that the QCAR SDK has been
			// already initialized:
			initApplicationAR();

			// Proceed to next application initialization status:
			updateApplicationStatus(APPSTATUS_LOAD_TRACKER);
			break;

		case APPSTATUS_LOAD_TRACKER:
			// Load the tracking data set:
			//
			// NOTE: This task instance must be created and invoked on the
			// UI thread and it can be executed only once!
			try {
				mLoadTrackerTask = new LoadTrackerTask();
				mLoadTrackerTask.execute();
			} catch (Exception e) {
				DebugLog.LOGE("Loading tracking data set failed");
			}
			break;

		case APPSTATUS_INITED:
			// Hint to the virtual machine that it would be a good time to
			// run the garbage collector:
			//
			// NOTE: This is only a hint. There is no guarantee that the
			// garbage collector will actually be run.
			System.gc();

			// Native post initialization:
			onQCARInitializedNative();

			// Activate the renderer:
			mRenderer.mIsActive = true;

			// Now add the GL surface view. It is important
			// that the OpenGL ES surface view gets added
			// BEFORE the camera is started and video
			// background is configured.
			addContentView(mGlView, new LayoutParams(LayoutParams.MATCH_PARENT,
					LayoutParams.MATCH_PARENT));

			// Sets the UILayout to be drawn in front of the camera
			mUILayout.bringToFront();

			// Start the camera:
			updateApplicationStatus(APPSTATUS_CAMERA_RUNNING);

			break;

		case APPSTATUS_CAMERA_STOPPED:
			// Call the native function to stop the camera:
			stopCamera();
			break;

		case APPSTATUS_CAMERA_RUNNING:
			// Call the native function to start the camera:
			startCamera();

			// Hides the Loading Dialog
			loadingDialogHandler.sendEmptyMessage(HIDE_LOADING_DIALOG);

			// Sets the layout background to transparent
			mUILayout.setBackgroundColor(Color.TRANSPARENT);

			// Set continuous auto-focus if supported by the device,
			// otherwise default back to regular auto-focus mode.
			// This will be activated by a tap to the screen in this
			// application.
			if (!setFocusMode(FOCUS_MODE_CONTINUOUS_AUTO)) {
				mContAutofocus = false;
				setFocusMode(FOCUS_MODE_NORMAL);
			} else {
				mContAutofocus = true;
			}
			break;

		default:
			throw new RuntimeException("Invalid application state");
		}
	}

	/** Tells native code whether we are in portait or landscape mode */
	private native void setActivityPortraitMode(boolean isPortrait);

	/** Initialize application GUI elements that are not related to AR. */
	private void initApplication() {
		// Set the screen orientation:
		// NOTE: Use SCREEN_ORIENTATION_LANDSCAPE or SCREEN_ORIENTATION_PORTRAIT
		// to lock the screen orientation for this activity.
		int screenOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR;

		// This is necessary for enabling AutoRotation in the Augmented View
		if (screenOrientation == ActivityInfo.SCREEN_ORIENTATION_SENSOR) {
			// NOTE: We use reflection here to see if the current platform
			// supports the full sensor mode (available only on Gingerbread
			// and above.
			try {
				// SCREEN_ORIENTATION_FULL_SENSOR is required to allow all
				// 4 screen rotations if API level >= 9:
				Field fullSensorField = ActivityInfo.class
						.getField("SCREEN_ORIENTATION_FULL_SENSOR");
				screenOrientation = fullSensorField.getInt(null);
			} catch (NoSuchFieldException e) {
				// App is running on API level < 9, do nothing.
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		// Apply screen orientation
		setRequestedOrientation(screenOrientation);

		updateActivityOrientation();

		// Query display dimensions:
		storeScreenDimensions();

		// As long as this window is visible to the user, keep the device's
		// screen turned on and bright:
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
	}

	/** Native function to initialize the application. */
	private native void initApplicationNative(int width, int height);

	/** Initializes AR application components. */
	private void initApplicationAR() {
		// Do application initialization in native code (e.g. registering
		// callbacks, etc.):
		initApplicationNative(mScreenWidth, mScreenHeight);

		// Create OpenGL ES view:
		int depthSize = 16;
		int stencilSize = 0;
		boolean translucent = QCAR.requiresAlpha();

		mGlView = new QCARSampleGLView(this);
		mGlView.init(mQCARFlags, translucent, depthSize, stencilSize);

		mRenderer = new ImageTargetsRenderer();
		mRenderer.mActivity = this;
		mGlView.setRenderer(mRenderer);

		LayoutInflater inflater = LayoutInflater.from(this);
		mUILayout = (RelativeLayout) inflater.inflate(R.layout.camera_overlay,
				null, false);

		mUILayout.setVisibility(View.VISIBLE);
		mUILayout.setBackgroundColor(Color.BLACK);

		// Gets a reference to the loading dialog
		mLoadingDialogContainer = mUILayout
				.findViewById(R.id.loading_indicator);

		// Shows the loading indicator at start
		loadingDialogHandler.sendEmptyMessage(SHOW_LOADING_DIALOG);

		// Adds the inflated layout to the view
		addContentView(mUILayout, new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT));
	}

	/** Tells native code to switch dataset as soon as possible */
	private native void switchDatasetAsap();

	private native boolean autofocus();

	private native boolean setFocusMode(int mode);

	/** Activates the Flash */
	private native boolean activateFlash(boolean flash);

	/** Returns the number of registered textures. */
	public int getTextureCount() {
		return mTextures.size();
	}

	/** Returns the texture object at the specified index. */
	public Texture getTexture(int i) {
		return mTextures.elementAt(i);
	}

	/** A helper for loading native libraries stored in "libs/armeabi*". */
	public static boolean loadLibrary(String nLibName) {
		try {
			System.loadLibrary(nLibName);
			DebugLog.LOGI("Native library lib" + nLibName + ".so loaded");
			return true;
		} catch (UnsatisfiedLinkError ulee) {
			DebugLog.LOGE("The library lib" + nLibName
					+ ".so could not be loaded");
		} catch (SecurityException se) {
			DebugLog.LOGE("The library lib" + nLibName
					+ ".so was not allowed to be loaded");
		}

		return false;
	}

	/**
	 * Shows the Camera Options Dialog when the Menu Key is pressed
	 */
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_MENU) {
			showCameraOptionsDialog();
			return true;
		}

		return super.onKeyUp(keyCode, event);
	}

	/** Native function to receive touch events. */
	public native void nativeTouchEvent(int actionType, int pointerId, float x,
			float y);

	/** Send touch events to native. */
	public boolean onTouchEvent(MotionEvent event) {
		int action = event.getAction();
		int actionType = -1;
		int pointerIndex = -1;

		switch (action & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_DOWN:
			actionType = 0;
			break;

		case MotionEvent.ACTION_POINTER_DOWN:
			pointerIndex = (action & MotionEvent.ACTION_POINTER_INDEX_MASK) >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
			actionType = 0;
			break;

		case MotionEvent.ACTION_MOVE:
			actionType = 1;
			break;

		case MotionEvent.ACTION_UP:
			actionType = 2;
			break;

		case MotionEvent.ACTION_POINTER_UP:
			pointerIndex = (action & MotionEvent.ACTION_POINTER_INDEX_MASK) >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
			actionType = 2;
			break;

		case MotionEvent.ACTION_CANCEL:
			actionType = 3;
			break;
		}

		if (pointerIndex != -1) {
			int pointerId = event.getPointerId(pointerIndex);
			float x = event.getX(pointerIndex);
			float y = event.getY(pointerIndex);
			nativeTouchEvent(actionType, pointerId, x, y);

		} else {
			for (int i = 0; i < event.getPointerCount(); i++) {
				int pointerId = event.getPointerId(i);
				float x = event.getX(i);
				float y = event.getY(i);
				nativeTouchEvent(actionType, pointerId, x, y);
			}
		}

		return true;
	}

	/**
	 * Process Double Tap event for showing the Camera options menu
	 */
	private class GestureListener extends
			GestureDetector.SimpleOnGestureListener {
		public boolean onDown(MotionEvent e) {
			return true;
		}

		public boolean onSingleTapUp(MotionEvent e) {
			// Calls the Autofocus Native Method
			autofocus();

			// Triggering manual auto focus disables continuous
			// autofocus
			mContAutofocus = false;

			return true;
		}

		// Event when double tap occurs
		public boolean onDoubleTap(MotionEvent e) {
			// Shows the Camera options
			showCameraOptionsDialog();
			return true;
		}
	}

	/**
	 * Shows an AlertDialog with the camera options available
	 */
	private void showCameraOptionsDialog() {
		// Only show camera options dialog box if app has been already inited
		if (mAppStatus < APPSTATUS_INITED) {
			return;
		}

		final int itemCameraIndex = 0;
		final int itemAutofocusIndex = 1;
		final int itemScanForDevices = 2;

		AlertDialog cameraOptionsDialog = null;

		CharSequence[] items = { getString(R.string.menu_flash_on),
				getString(R.string.menu_contAutofocus_off),
				getString(R.string.button_scan) };

		// Updates list titles according to current state of the options
		if (mFlash) {
			items[itemCameraIndex] = (getString(R.string.menu_flash_off));
		} else {
			items[itemCameraIndex] = (getString(R.string.menu_flash_on));
		}

		if (mContAutofocus) {
			items[itemAutofocusIndex] = (getString(R.string.menu_contAutofocus_off));
		} else {
			items[itemAutofocusIndex] = (getString(R.string.menu_contAutofocus_on));
		}

		// Builds the Alert Dialog
		AlertDialog.Builder cameraOptionsDialogBuilder = new AlertDialog.Builder(
				ImageTargets.this);
		cameraOptionsDialogBuilder
				.setTitle(getString(R.string.menu_camera_title));
		cameraOptionsDialogBuilder.setItems(items,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int item) {
						if (item == itemCameraIndex) {
							// Turns focus mode on/off by calling native
							// method
							if (activateFlash(!mFlash)) {
								mFlash = !mFlash;
							} else {
								Toast.makeText(
										ImageTargets.this,
										"Unable to turn "
												+ (mFlash ? "off" : "on")
												+ " flash", Toast.LENGTH_SHORT)
										.show();
							}

							// Dismisses the dialog
							dialog.dismiss();
						} else if (item == itemAutofocusIndex) {
							if (mContAutofocus) {
								// Sets the Focus Mode by calling the native
								// method
								if (setFocusMode(FOCUS_MODE_NORMAL)) {
									mContAutofocus = false;
								} else {
									Toast.makeText(
											ImageTargets.this,
											"Unable to deactivate Continuous Auto-Focus",
											Toast.LENGTH_SHORT).show();
								}
							} else {
								// Sets the focus mode by calling the native
								// method
								if (setFocusMode(FOCUS_MODE_CONTINUOUS_AUTO)) {
									mContAutofocus = true;
								} else {
									Toast.makeText(
											ImageTargets.this,
											"Unable to activate Continuous Auto-Focus",
											Toast.LENGTH_SHORT).show();
								}
							}

							// Dismisses the dialog
							dialog.dismiss();
						} else if (item == itemScanForDevices) {
							startActivityForResult(
									new Intent(ImageTargets.this,
											DeviceListActivity.class),
									REQUEST_CONNECT_DEVICE_INSECURE);
							dialog.dismiss();
						}

					}
				});

		// Shows the Dialog
		cameraOptionsDialog = cameraOptionsDialogBuilder.create();
		cameraOptionsDialog.show();
	}

	// Bluetooth
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case REQUEST_CONNECT_DEVICE_SECURE:
			// When DeviceListActivity returns with a device to connect
			if (resultCode == Activity.RESULT_OK) {
				connectDevice(data, true);
			}
			break;
		case REQUEST_CONNECT_DEVICE_INSECURE:
			// When DeviceListActivity returns with a device to connect
			if (resultCode == Activity.RESULT_OK) {
				connectDevice(data, false);
			}
			break;
		case REQUEST_ENABLE_BT:
			// When the request to enable Bluetooth returns
			if (resultCode == Activity.RESULT_OK) {
				// Bluetooth is now enabled, so set up a chat session
				mChatService = new BluetoothChatService(this, mHandler);
			} else {
				// User did not enable Bluetooth or an error occurred
				Toast.makeText(this, "Bluetooth non actif", Toast.LENGTH_SHORT)
						.show();
				finish();
			}
		}
	}

	private void connectDevice(Intent data, boolean secure) {
		// Get the device MAC address
		String address = data.getExtras().getString(
				DeviceListActivity.EXTRA_DEVICE_ADDRESS);
		// Get the BluetoothDevice object
		BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
		// Attempt to connect to the device
		mChatService.connect(device, secure);
	}

	// Called from native to display a message
	@Override
	public void displayMessage(String text) {
		// We use a handler because this thread cannot change the UI
		Message message = mHandler.obtainMessage(ImageTargets.MESSAGE_TOAST);
		Bundle bundle = new Bundle();
		bundle.putString(ImageTargets.TOAST, text);
		message.setData(bundle);
		mHandler.sendMessage(message);
	}

	public String mouvementsPossibles(int row, int col) {
		List<Coord> mouvs = ech.mouvementPossibles(new Coord(row + 1, col + 1));
		String res = "";
		if (mouvs != null) {
			for (Coord c : mouvs) {
				res += c.toString() + ";";
			}
		}
		return res;
	}

	public boolean isWhiteMove() {
		return ech.tourDeJoueur == ToolsModel.blanc;
	}

	public boolean move(int fromrow, int fromcol, int torow, int tocol) {
		Coord from = new Coord(fromrow + 1, fromcol + 1);
		Coord to = new Coord(torow + 1, tocol + 1);
		List<Coord> mouvs = ech.mouvementPossibles(from);
		if (mouvs != null && mouvs.contains(to)) {
			ech.deplacerPiece(from, to);
			sendMove(from.getX(), from.getY(), to.getX(), to.getY());
			return true;
		} else {
			return false;
		}

	}

	private void receiveMove(String move) {
		String[] coords = move.split(";");
		String[] from = coords[0].split(",");
		String[] to = coords[1].split(",");
		int fromx = Integer.parseInt(from[0]);
		int fromy = Integer.parseInt(from[1]);
		int tox = Integer.parseInt(to[0]);
		int toy = Integer.parseInt(to[1]);
		Coord fromc = new Coord(fromx, fromy);
		Coord toc = new Coord(tox, toy);
		ech.deplacerPiece(fromc, toc);
		nativeMove(fromx, fromy, tox, toy);
	}

	private void sendMove(int fromrow, int fromcol, int torow, int tocol) {
		String move = fromrow + "," + fromcol + ";" + torow + "," + tocol;
		byte[] out = move.getBytes();
		mChatService.write(out);
	}

	@Override
	public void movePiece(int fromrow, int fromcol, int torow, int tocol) {
		nativeMove(fromrow, fromcol, torow, tocol);
		sendMove(fromrow, fromcol, torow, tocol);
	}

	public native void nativeMove(int fromrow, int fromcol, int torow, int tocol);
}
