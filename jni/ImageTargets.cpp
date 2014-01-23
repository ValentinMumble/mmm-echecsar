#include "ImageTargets.h"

// Object to receive update callbacks from QCAR SDK
class ImageTargets_UpdateCallback: public QCAR::UpdateCallback {
	virtual void QCAR_onUpdate(QCAR::State& /*state*/) {
		if (switchDataSetAsap) {
			switchDataSetAsap = false;

			// Get the image tracker:
			QCAR::TrackerManager& trackerManager =
			QCAR::TrackerManager::getInstance();
			QCAR::ImageTracker* imageTracker =
			static_cast<QCAR::ImageTracker*>(trackerManager.getTracker(
							QCAR::Tracker::IMAGE_TRACKER));
			if (imageTracker == 0 || dataSetCheckerboard == 0
					|| imageTracker->getActiveDataSet() == 0) {
				LOG("Failed to switch data set.");
				return;
			}
			imageTracker->activateDataSet(dataSetCheckerboard);
		}
	}
};

ImageTargets_UpdateCallback updateCallback;

JNIEXPORT int JNICALL
Java_mmm_EchecsAR_ImageTargets_getOpenGlEsVersionNative(JNIEnv *, jobject)
{
	return 2; //OpenGL ES 2.0
}

JNIEXPORT void JNICALL
Java_mmm_EchecsAR_ImageTargets_setActivityPortraitMode(JNIEnv *, jobject, jboolean isPortrait)
{
	isActivityInPortraitMode = isPortrait;
}

JNIEXPORT void JNICALL
Java_mmm_EchecsAR_ImageTargets_switchDatasetAsap(JNIEnv *, jobject)
{
	switchDataSetAsap = true;
}

JNIEXPORT int JNICALL
Java_mmm_EchecsAR_ImageTargets_initTracker(JNIEnv *, jobject)
{
	LOG("Java_mmm_EchecsAR_ImageTargets_initTracker");

	// Initialize the image tracker:
	QCAR::TrackerManager& trackerManager = QCAR::TrackerManager::getInstance();
	QCAR::Tracker* tracker = trackerManager.initTracker(QCAR::Tracker::IMAGE_TRACKER);
	if (tracker == NULL)
	{
		LOG("Failed to initialize ImageTracker.");
		return 0;
	}

	LOG("Successfully initialized ImageTracker.");
	return 1;
}

JNIEXPORT void JNICALL
Java_mmm_EchecsAR_ImageTargets_deinitTracker(JNIEnv *, jobject)
{
	LOG("Java_mmm_EchecsAR_ImageTargets_deinitTracker");

	// Deinit the image tracker:
	QCAR::TrackerManager& trackerManager = QCAR::TrackerManager::getInstance();
	trackerManager.deinitTracker(QCAR::Tracker::IMAGE_TRACKER);
}

JNIEXPORT int JNICALL
Java_mmm_EchecsAR_ImageTargets_loadTrackerData(JNIEnv *, jobject)
{
	LOG("Java_mmm_EchecsAR_ImageTargets_loadTrackerData");

	// Get the image tracker:
	QCAR::TrackerManager& trackerManager = QCAR::TrackerManager::getInstance();
	QCAR::ImageTracker* imageTracker = static_cast<QCAR::ImageTracker*>(
			trackerManager.getTracker(QCAR::Tracker::IMAGE_TRACKER));
	if (imageTracker == NULL)
	{
		LOG("Failed to load tracking data set because the ImageTracker has not"
				" been initialized.");
		return 0;
	}

	// Create the data sets
	dataSetCheckerboard = imageTracker->createDataSet();
	if (dataSetCheckerboard == 0)
	{
		LOG("Failed to create a new tracking data.");
		return 0;
	}

	if (!dataSetCheckerboard->load("checkerboard.xml", QCAR::DataSet::STORAGE_APPRESOURCE))
	{
		LOG("Failed to load data set.");
		return 0;
	}

	// Activate the data set:
	if (!imageTracker->activateDataSet(dataSetCheckerboard))
	{
		LOG("Failed to activate data set.");
		return 0;
	}

	LOG("Successfully loaded and activated data set.");
	return 1;
}

JNIEXPORT int JNICALL
Java_mmm_EchecsAR_ImageTargets_destroyTrackerData(JNIEnv *, jobject)
{
	LOG("Java_mmm_EchecsAR_ImageTargets_destroyTrackerData");

	// Get the image tracker:
	QCAR::TrackerManager& trackerManager = QCAR::TrackerManager::getInstance();
	QCAR::ImageTracker* imageTracker = static_cast<QCAR::ImageTracker*>(
			trackerManager.getTracker(QCAR::Tracker::IMAGE_TRACKER));
	if (imageTracker == NULL)
	{
		LOG("Failed to destroy the tracking data set because the ImageTracker has not"
				" been initialized.");
		return 0;
	}

	if (dataSetCheckerboard != 0)
	{
		if (imageTracker->getActiveDataSet() == dataSetCheckerboard &&
				!imageTracker->deactivateDataSet(dataSetCheckerboard))
		{
			LOG("Failed to destroy the tracking data set Checkerboard because the data set "
					"could not be deactivated.");
			return 0;
		}

		if (!imageTracker->destroyDataSet(dataSetCheckerboard))
		{
			LOG("Failed to destroy the tracking data set Checkerboard.");
			return 0;
		}

		LOG("Successfully destroyed the data set Checkerboard.");
		dataSetCheckerboard = 0;
	}

	return 1;
}

JNIEXPORT void JNICALL
Java_mmm_EchecsAR_ImageTargets_onQCARInitializedNative(JNIEnv *, jobject)
{
	// Register the update callback where we handle the data set swap:
	QCAR::registerCallback(&updateCallback);

	// initialisation des positions

	// initialisation des x et y a la case superieure gauche
	int x = -SQUARE_SIZE * 4 + SQUARE_SIZE / 2;
	int y = SQUARE_SIZE * 4 - SQUARE_SIZE / 2;

	for (int i = 0; i < N / 4; i++) {
		wPieces[i].position.data[0] = x;
		wPieces[i].position.data[1] = y;
		bPieces[i].position.data[0] = x;
		bPieces[i].position.data[1] = -y;

		wPieces[i].vertices = bPieces[i].vertices = vertices[i];
		wPieces[i].normals = bPieces[i].normals = normals[i];
		wPieces[i].texCoords = bPieces[i].texCoords = texCoords[i];
		wPieces[i].numVertices = bPieces[i].numVertices = numVertices[i];
		wPieces[i].textureId = 0;
		bPieces[i].textureId = 1;

		x += SQUARE_SIZE;

		updatePieceTransform(&wPieces[i]);
		updatePieceTransform(&bPieces[i]);
	}

	// Pawns
	x = -SQUARE_SIZE * 4 + SQUARE_SIZE / 2;
	y = SQUARE_SIZE * 3 - SQUARE_SIZE / 2;
	for (int i = 8; i < N / 2; i++) {
		wPieces[i].position.data[0] = x;
		wPieces[i].position.data[1] = y;
		bPieces[i].position.data[0] = x;
		bPieces[i].position.data[1] = -y;

		wPieces[i].vertices = bPieces[i].vertices = pawnVertices;
		wPieces[i].normals = bPieces[i].normals = pawnNormals;
		wPieces[i].texCoords = bPieces[i].texCoords = pawnTexCoords;
		wPieces[i].numVertices = bPieces[i].numVertices = pawnNumVertices;
		wPieces[i].textureId = 0;
		bPieces[i].textureId = 1;

		x += SQUARE_SIZE;

		updatePieceTransform(&wPieces[i]);
		updatePieceTransform(&bPieces[i]);
	}

	// Comment in to enable tracking of up to 2 targets simultaneously and
	// split the work over multiple frames:
	// QCAR::setHint(QCAR::HINT_MAX_SIMULTANEOUS_IMAGE_TARGETS, 2);
}

JNIEXPORT void JNICALL
Java_mmm_EchecsAR_ImageTargetsRenderer_renderFrame(JNIEnv *, jobject)
{
	//LOG("Java_mmm_EchecsAR_GLRenderer_renderFrame");

	// Clear color and depth buffer
	glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

	// Get the state from QCAR and mark the beginning of a rendering section
	QCAR::State state = QCAR::Renderer::getInstance().begin();

	// Explicitly render the Video Background
	QCAR::Renderer::getInstance().drawVideoBackground();

	glEnable(GL_DEPTH_TEST);

	// We must detect if background reflection is active and adjust the culling direction.
	// If the reflection is active, this means the post matrix has been reflected as well,
	// therefore standard counter clockwise face culling will result in "inside out" models.
	glEnable(GL_CULL_FACE);
	glCullFace(GL_BACK);
	if(QCAR::Renderer::getInstance().getVideoBackgroundConfig().mReflection == QCAR::VIDEO_BACKGROUND_REFLECTION_ON)
	glFrontFace(GL_CW);//Front camera
	else
	glFrontFace(GL_CCW);//Back camera

	// Did we find any trackables this frame?
	if (state.getNumTrackableResults() > 0) {

		// Get the first trackable
		const QCAR::TrackableResult* trackableResult = state.getTrackableResult(0);
		const QCAR::Trackable& trackable = trackableResult->getTrackable();

		modelViewMatrix = QCAR::Tool::convertPose2GLMatrix(
				trackableResult->getPose());

		glUseProgram(shaderProgramID);
		glActiveTexture(GL_TEXTURE0);
		glUniform1i(texSampler2DHandle, 0 /*GL_TEXTURE0*/);

		for (int i = 0; i < N / 2; i++) {
			drawPiece(&wPieces[i]);
			drawPiece(&bPieces[i]);
		}

		glDisableVertexAttribArray(vertexHandle);
		glDisableVertexAttribArray(normalHandle);
		glDisableVertexAttribArray(textureCoordHandle);

		SampleUtils::checkGlError("ImageTargets renderFrame");
	}

	glDisable(GL_DEPTH_TEST);

	QCAR::Renderer::getInstance().end();
}

void drawPiece(Piece *piece) {
	QCAR::Matrix44F modelViewProjection, objectMatrix;

	// On multiplie la matrice modelView avec celle de la piece et on stocke la matrice resultat dans objectMatrix
	SampleUtils::multiplyMatrix(&modelViewMatrix.data[0], &piece->transform.data[0], &objectMatrix.data[0]);
	// On multiplie objectMatrix avec la matrice de projection
	SampleUtils::multiplyMatrix(&projectionMatrix.data[0],
			&objectMatrix.data[0], &modelViewProjection.data[0]);

	glVertexAttribPointer(vertexHandle, 3, GL_FLOAT, GL_FALSE, 0,
			(const GLvoid*) &piece->vertices[0]);
	glVertexAttribPointer(normalHandle, 3, GL_FLOAT, GL_FALSE, 0,
			(const GLvoid*) &piece->normals[0]);
	glVertexAttribPointer(textureCoordHandle, 2, GL_FLOAT, GL_FALSE, 0,
			(const GLvoid*) &piece->texCoords[0]);
	glEnableVertexAttribArray(vertexHandle);
	glEnableVertexAttribArray(normalHandle);
	glEnableVertexAttribArray(textureCoordHandle);

	glBindTexture(GL_TEXTURE_2D, textures[piece->textureId]->mTextureID);
	glUniformMatrix4fv(mvpMatrixHandle, 1, GL_FALSE,
			(GLfloat*) &modelViewProjection.data[0]);
	glDrawArrays(GL_TRIANGLES, 0, piece->numVertices);
}

void updatePieceTransform(Piece *piece) {
	// Reset the piece transform to the identity matrix
	piece->transform = SampleMath::Matrix44FIdentity();
	float* transformPtr = &piece->transform.data[0];
	SampleUtils::translatePoseMatrix(piece->position.data[0], piece->position.data[1], 0, transformPtr);
	SampleUtils::scalePoseMatrix(kPieceScale, kPieceScale, kPieceScale, transformPtr);
}

JNIEXPORT void JNICALL
Java_mmm_EchecsAR_ImageTargets_nativeTouchEvent(JNIEnv* , jobject, jint actionType, jint pointerId, jfloat x, jfloat y)
{
	TouchEvent* touchEvent;

	// Determine which finger this event represents
	if (pointerId == 0) {
		touchEvent = &touch1;
	} else if (pointerId == 1) {
		touchEvent = &touch2;
	} else {
		return;
	}

	if (actionType == ACTION_DOWN) {
		// On touch down, reset the following:
		touchEvent->lastX = x;
		touchEvent->lastY = y;
		touchEvent->startX = x;
		touchEvent->startY = y;
		touchEvent->startTime = getCurrentTimeMS();
		touchEvent->didTap = false;
	} else {
		// Store the last event's position
		touchEvent->lastX = touchEvent->x;
		touchEvent->lastY = touchEvent->y;
	}

	// Store the lifetime of the touch, used for tap recognition
	unsigned long time = getCurrentTimeMS();
	touchEvent->dt = time - touchEvent->startTime;

	// Store the distance squared from the initial point, for tap recognition
	float dx = touchEvent->lastX - touchEvent->startX;
	float dy = touchEvent->lastY - touchEvent->startY;
	touchEvent->dist2 = dx * dx + dy * dy;

	if (actionType == ACTION_UP) {
		// On touch up, this touch is no longer active
		touchEvent->isActive = false;

		// Determine if this touch up ends a tap gesture
		// The tap must be quick and localized
		if (touchEvent->dt < MAX_TAP_TIMER && touchEvent->dist2 < MAX_TAP_DISTANCE2) {
			touchEvent->didTap = true;
			touchEvent->tapX = touchEvent->startX;
			touchEvent->tapY = touchEvent->startY;
		}
	} else {
		// On touch down or move, this touch is active
		touchEvent->isActive = true;
	}

	// Set the touch information for this event
	touchEvent->actionType = actionType;
	touchEvent->pointerId = pointerId;
	touchEvent->x = x;
	touchEvent->y = y;
	LOG("x: %f", x);
	LOG("y: %f", y);
}

// ----------------------------------------------------------------------------
// Time utility
// ----------------------------------------------------------------------------

unsigned long getCurrentTimeMS() {
	struct timeval tv;
	gettimeofday(&tv, NULL);
	unsigned long s = tv.tv_sec * 1000;
	unsigned long us = tv.tv_usec / 1000;
	return s + us;
}

void configureVideoBackground() {
	// Get the default video mode:
	QCAR::CameraDevice& cameraDevice = QCAR::CameraDevice::getInstance();
	QCAR::VideoMode videoMode = cameraDevice.getVideoMode(
			QCAR::CameraDevice::MODE_DEFAULT);

	// Configure the video background
	QCAR::VideoBackgroundConfig config;
	config.mEnabled = true;
	config.mSynchronous = true;
	config.mPosition.data[0] = 0.0f;
	config.mPosition.data[1] = 0.0f;

	if (isActivityInPortraitMode) {
		//LOG("configureVideoBackground PORTRAIT");
		config.mSize.data[0] = videoMode.mHeight
		* (screenHeight / (float) videoMode.mWidth);
		config.mSize.data[1] = screenHeight;

		if (config.mSize.data[0] < screenWidth) {
			LOG(
					"Correcting rendering background size to handle missmatch between screen and video aspect ratios.");
			config.mSize.data[0] = screenWidth;
			config.mSize.data[1] = screenWidth
			* (videoMode.mWidth / (float) videoMode.mHeight);
		}
	} else {
		//LOG("configureVideoBackground LANDSCAPE");
		config.mSize.data[0] = screenWidth;
		config.mSize.data[1] = videoMode.mHeight
		* (screenWidth / (float) videoMode.mWidth);

		if (config.mSize.data[1] < screenHeight) {
			LOG(
					"Correcting rendering background size to handle missmatch between screen and video aspect ratios.");
			config.mSize.data[0] = screenHeight
			* (videoMode.mWidth / (float) videoMode.mHeight);
			config.mSize.data[1] = screenHeight;
		}
	}

	LOG(
			"Configure Video Background : Video (%d,%d), Screen (%d,%d), mSize (%d,%d)",
			videoMode.mWidth, videoMode.mHeight, screenWidth, screenHeight,
			config.mSize.data[0], config.mSize.data[1]);

	// Set the config:
	QCAR::Renderer::getInstance().setVideoBackgroundConfig(config);
}

JNIEXPORT void JNICALL
Java_mmm_EchecsAR_ImageTargets_initApplicationNative(
		JNIEnv* env, jobject obj, jint width, jint height)
{
	LOG("Java_mmm_EchecsAR_ImageTargets_initApplicationNative");

	// Store screen dimensions
	screenWidth = width;
	screenHeight = height;

	// Handle to the activity class:
	jclass activityClass = env->GetObjectClass(obj);

	jmethodID getTextureCountMethodID = env->GetMethodID(activityClass,
			"getTextureCount", "()I");
	if (getTextureCountMethodID == 0)
	{
		LOG("Function getTextureCount() not found.");
		return;
	}

	textureCount = env->CallIntMethod(obj, getTextureCountMethodID);
	if (!textureCount)
	{
		LOG("getTextureCount() returned zero.");
		return;
	}

	textures = new Texture*[textureCount];

	jmethodID getTextureMethodID = env->GetMethodID(activityClass,
			"getTexture", "(I)Lmmm/EchecsAR/Texture;");

	if (getTextureMethodID == 0)
	{
		LOG("Function getTexture() not found.");
		return;
	}

	// Register the textures
	for (int i = 0; i < textureCount; ++i)
	{

		jobject textureObject = env->CallObjectMethod(obj, getTextureMethodID, i);
		if (textureObject == NULL)
		{
			LOG("GetTexture() returned zero pointer");
			return;
		}

		textures[i] = Texture::create(env, textureObject);
	}
	LOG("Java_mmm_EchecsAR_ImageTargets_initApplicationNative finished");
}

JNIEXPORT void JNICALL
Java_mmm_EchecsAR_ImageTargets_deinitApplicationNative(
		JNIEnv* env, jobject obj)
{
	LOG("Java_mmm_EchecsAR_ImageTargets_deinitApplicationNative");

	// Release texture resources
	if (textures != 0)
	{
		for (int i = 0; i < textureCount; ++i)
		{
			delete textures[i];
			textures[i] = NULL;
		}

		delete[]textures;
		textures = NULL;

		textureCount = 0;
	}
}

JNIEXPORT void JNICALL
Java_mmm_EchecsAR_ImageTargets_startCamera(JNIEnv *,
		jobject)
{
	LOG("Java_mmm_EchecsAR_ImageTargets_startCamera");

	// Select the camera to open, set this to QCAR::CameraDevice::CAMERA_FRONT
	// to activate the front camera instead.
	QCAR::CameraDevice::CAMERA camera = QCAR::CameraDevice::CAMERA_DEFAULT;

	// Initialize the camera:
	if (!QCAR::CameraDevice::getInstance().init(camera))
	return;

	// Configure the video background
	configureVideoBackground();

	// Select the default mode:
	if (!QCAR::CameraDevice::getInstance().selectVideoMode(
					QCAR::CameraDevice::MODE_DEFAULT))
	return;

	// Start the camera:
	if (!QCAR::CameraDevice::getInstance().start())
	return;

	// Uncomment to enable flash
	//if(QCAR::CameraDevice::getInstance().setFlashTorchMode(true))
	//    LOG("IMAGE TARGETS : enabled torch");

	// Uncomment to enable infinity focus mode, or any other supported focus mode
	// See CameraDevice.h for supported focus modes
	//if(QCAR::CameraDevice::getInstance().setFocusMode(QCAR::CameraDevice::FOCUS_MODE_INFINITY))
	//    LOG("IMAGE TARGETS : enabled infinity focus");

	// Start the tracker:
	QCAR::TrackerManager& trackerManager = QCAR::TrackerManager::getInstance();
	QCAR::Tracker* imageTracker = trackerManager.getTracker(QCAR::Tracker::IMAGE_TRACKER);
	if(imageTracker != 0)
	imageTracker->start();
}

JNIEXPORT void JNICALL
Java_mmm_EchecsAR_ImageTargets_stopCamera(JNIEnv *, jobject)
{
	LOG("Java_mmm_EchecsAR_ImageTargets_stopCamera");

	// Stop the tracker:
	QCAR::TrackerManager& trackerManager = QCAR::TrackerManager::getInstance();
	QCAR::Tracker* imageTracker = trackerManager.getTracker(QCAR::Tracker::IMAGE_TRACKER);
	if(imageTracker != 0)
	imageTracker->stop();

	QCAR::CameraDevice::getInstance().stop();
	QCAR::CameraDevice::getInstance().deinit();
}

JNIEXPORT void JNICALL
Java_mmm_EchecsAR_ImageTargets_setProjectionMatrix(JNIEnv *, jobject)
{
	LOG("Java_mmm_EchecsAR_ImageTargets_setProjectionMatrix");

	// Cache the projection matrix:
	const QCAR::CameraCalibration& cameraCalibration =
	QCAR::CameraDevice::getInstance().getCameraCalibration();
	projectionMatrix = QCAR::Tool::getProjectionGL(cameraCalibration, 2.0f, 2500.0f);
}

// ----------------------------------------------------------------------------
// Activates Camera Flash
// ----------------------------------------------------------------------------
JNIEXPORT jboolean JNICALL
Java_mmm_EchecsAR_ImageTargets_activateFlash(JNIEnv*, jobject, jboolean flash)
{
	return QCAR::CameraDevice::getInstance().setFlashTorchMode((flash==JNI_TRUE)) ? JNI_TRUE : JNI_FALSE;
}

JNIEXPORT jboolean JNICALL
Java_mmm_EchecsAR_ImageTargets_autofocus(JNIEnv*, jobject)
{
	return QCAR::CameraDevice::getInstance().setFocusMode(QCAR::CameraDevice::FOCUS_MODE_TRIGGERAUTO) ? JNI_TRUE : JNI_FALSE;
}

JNIEXPORT jboolean JNICALL
Java_mmm_EchecsAR_ImageTargets_setFocusMode(JNIEnv*, jobject, jint mode)
{
	int qcarFocusMode;

	switch ((int)mode)
	{
		case 0:
		qcarFocusMode = QCAR::CameraDevice::FOCUS_MODE_NORMAL;
		break;

		case 1:
		qcarFocusMode = QCAR::CameraDevice::FOCUS_MODE_CONTINUOUSAUTO;
		break;

		case 2:
		qcarFocusMode = QCAR::CameraDevice::FOCUS_MODE_INFINITY;
		break;

		case 3:
		qcarFocusMode = QCAR::CameraDevice::FOCUS_MODE_MACRO;
		break;

		default:
		return JNI_FALSE;
	}

	return QCAR::CameraDevice::getInstance().setFocusMode(qcarFocusMode) ? JNI_TRUE : JNI_FALSE;
}

JNIEXPORT void JNICALL
Java_mmm_EchecsAR_ImageTargetsRenderer_initRendering(
		JNIEnv* env, jobject obj)
{
	LOG("Java_mmm_EchecsAR_ImageTargetsRenderer_initRendering");

	// Define clear color
	glClearColor(0.0f, 0.0f, 0.0f, QCAR::requiresAlpha() ? 0.0f : 1.0f);

	// Now generate the OpenGL texture objects and add settings
	for (int i = 0; i < textureCount; ++i)
	{
		glGenTextures(1, &(textures[i]->mTextureID));
		glBindTexture(GL_TEXTURE_2D, textures[i]->mTextureID);
		glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
		glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, textures[i]->mWidth,
				textures[i]->mHeight, 0, GL_RGBA, GL_UNSIGNED_BYTE,
				(GLvoid*) textures[i]->mData);
	}

	shaderProgramID = SampleUtils::createProgramFromBuffer(cubeMeshVertexShader,
			cubeFragmentShader);

	vertexHandle = glGetAttribLocation(shaderProgramID,
			"vertexPosition");
	normalHandle = glGetAttribLocation(shaderProgramID,
			"vertexNormal");
	textureCoordHandle = glGetAttribLocation(shaderProgramID,
			"vertexTexCoord");
	mvpMatrixHandle = glGetUniformLocation(shaderProgramID,
			"modelViewProjectionMatrix");
	texSampler2DHandle = glGetUniformLocation(shaderProgramID,
			"texSampler2D");

}

JNIEXPORT void JNICALL
Java_mmm_EchecsAR_ImageTargetsRenderer_updateRendering(
		JNIEnv* env, jobject obj, jint width, jint height)
{
	LOG("Java_mmm_EchecsAR_ImageTargetsRenderer_updateRendering");

	// Update screen dimensions
	screenWidth = width;
	screenHeight = height;

	// Reconfigure the video background
	configureVideoBackground();
}

#ifdef __cplusplus
}
#endif
