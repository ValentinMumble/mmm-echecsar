
#include <cstdlib>
#include <time.h>

#include <jni.h>
#include <android/log.h>
#include <stdio.h>
#include <string.h>
#include <assert.h>

#include <GLES2/gl2.h>
#include <GLES2/gl2ext.h>

#include <QCAR/QCAR.h>
#include <QCAR/CameraDevice.h>
#include <QCAR/Renderer.h>
#include <QCAR/VideoBackgroundConfig.h>
#include <QCAR/Trackable.h>
#include <QCAR/TrackableResult.h>
#include <QCAR/Tool.h>
#include <QCAR/Tracker.h>
#include <QCAR/TrackerManager.h>
#include <QCAR/ImageTracker.h>
#include <QCAR/CameraCalibration.h>
#include <QCAR/UpdateCallback.h>
#include <QCAR/DataSet.h>

#include "SampleUtils.h"
#include "Texture.h"
#include "CubeShaders.h"
#include "Teapot.h"
#include "chessmen/BowlAndSpoonModel.h"

#include "chessmen/pawn.h"
#include "chessmen/rook.h"
#include "chessmen/knight.h"
#include "chessmen/bishop.h"
#include "chessmen/queen.h"
#include "chessmen/king.h"

#ifdef __cplusplus
extern "C" {
#endif

#define SQUARE_SIZE 93
#define N 32

#define LROOK 0
#define LKNIGHT 1
#define LBISHOP 2
#define QUEEN 3
#define KING 4
#define RBISHOP 5
#define RKNIGHT 6
#define RROOK 7
#define PAWN1 8
#define PAWN2 9
#define PAWN3 10
#define PAWN4 11
#define PAWN5 12
#define PAWN6 13
#define PAWN7 14
#define PAWN8 15

// Textures:
int textureCount = 0;
Texture** textures = 0;

// OpenGL ES 2.0 specific:
unsigned int shaderProgramID = 0;
GLint vertexHandle = 0;
GLint normalHandle = 0;
GLint textureCoordHandle = 0;
GLint mvpMatrixHandle = 0;
GLint texSampler2DHandle = 0;

// Screen dimensions:
unsigned int screenWidth = 0;
unsigned int screenHeight = 0;

// Indicates whether screen is in portrait (true) or landscape (false) mode
bool isActivityInPortraitMode = false;

// The projection matrix used for rendering virtual objects:
QCAR::Matrix44F projectionMatrix;

// Constants:
static const float kBowlScale = 18.0f;
static const float kTeapotScale = 3.0f;
static const float kPieceScale = 60.0f;

QCAR::DataSet* dataSetCheckerboard = 0;
QCAR::State state;

bool switchDataSetAsap = false;

struct point {
	float x;
	float y;
	float z;
};

struct point wPiecesCoords[N / 2];
struct point bPiecesCoords[N / 2];

void drawPiece(int, struct point *, float, float *, float *, float *, int, int);

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
	int x = -SQUARE_SIZE * 4 + SQUARE_SIZE / 2;
	int y = SQUARE_SIZE * 4 - SQUARE_SIZE / 2;
	int z = 0;
	for (int i = 0; i < N / 4; i++) {
		wPiecesCoords[i].x = x;
		wPiecesCoords[i].y = y;
		wPiecesCoords[i].z = z;

		bPiecesCoords[i].x = x;
		bPiecesCoords[i].y = -y;
		bPiecesCoords[i].z = z;
		x += SQUARE_SIZE;
	}

	x = -SQUARE_SIZE * 4 + SQUARE_SIZE / 2;
	y = SQUARE_SIZE * 3 - SQUARE_SIZE / 2;
	for (int i = 8; i < N / 2; i++) {
		wPiecesCoords[i].x = x;
		wPiecesCoords[i].y = y;
		wPiecesCoords[i].z = z;

		bPiecesCoords[i].x = x;
		bPiecesCoords[i].y = -y;
		bPiecesCoords[i].z = z;
		x += SQUARE_SIZE;
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
	state = QCAR::Renderer::getInstance().begin();

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
	for(int tIdx = 0; tIdx < state.getNumTrackableResults(); tIdx++)
	{
		int textureId = 1;
		glUseProgram(shaderProgramID);
		glActiveTexture(GL_TEXTURE0);
		glUniform1i(texSampler2DHandle, 0 /*GL_TEXTURE0*/);

		// Pawns
		for (int i = 8; i < N / 2; i++) {
			drawPiece(tIdx, &wPiecesCoords[i], kPieceScale, pawnVertices, pawnNormals, pawnTexCoords, pawnNumVertices, textureId);
			drawPiece(tIdx, &bPiecesCoords[i], kPieceScale, pawnVertices, pawnNormals, pawnTexCoords, pawnNumVertices, textureId);
		}

		// Rooks
		drawPiece(tIdx, &wPiecesCoords[LROOK], kPieceScale, rookVertices, rookNormals, rookTexCoords, rookNumVertices, textureId);
		drawPiece(tIdx, &bPiecesCoords[LROOK], kPieceScale, rookVertices, rookNormals, rookTexCoords, rookNumVertices, textureId);
		drawPiece(tIdx, &wPiecesCoords[RROOK], kPieceScale, rookVertices, rookNormals, rookTexCoords, rookNumVertices, textureId);
		drawPiece(tIdx, &bPiecesCoords[RROOK], kPieceScale, rookVertices, rookNormals, rookTexCoords, rookNumVertices, textureId);

		// Knights
		drawPiece(tIdx, &wPiecesCoords[LKNIGHT], kPieceScale, knightVertices, knightNormals, knightTexCoords, knightNumVertices, textureId);
		drawPiece(tIdx, &bPiecesCoords[LKNIGHT], kPieceScale, knightVertices, knightNormals, knightTexCoords, knightNumVertices, textureId);
		drawPiece(tIdx, &wPiecesCoords[RKNIGHT], kPieceScale, knightVertices, knightNormals, knightTexCoords, knightNumVertices, textureId);
		drawPiece(tIdx, &bPiecesCoords[RKNIGHT], kPieceScale, knightVertices, knightNormals, knightTexCoords, knightNumVertices, textureId);

		// Bishops
		drawPiece(tIdx, &wPiecesCoords[LBISHOP], kPieceScale, bishopVertices, bishopNormals, bishopTexCoords, bishopNumVertices, textureId);
		drawPiece(tIdx, &bPiecesCoords[LBISHOP], kPieceScale, bishopVertices, bishopNormals, bishopTexCoords, bishopNumVertices, textureId);
		drawPiece(tIdx, &wPiecesCoords[RBISHOP], kPieceScale, bishopVertices, bishopNormals, bishopTexCoords, bishopNumVertices, textureId);
		drawPiece(tIdx, &bPiecesCoords[RBISHOP], kPieceScale, bishopVertices, bishopNormals, bishopTexCoords, bishopNumVertices, textureId);

		// Queens
		drawPiece(tIdx, &wPiecesCoords[QUEEN], kPieceScale, queenVertices, queenNormals, queenTexCoords, queenNumVertices, textureId);
		drawPiece(tIdx, &bPiecesCoords[QUEEN], kPieceScale, queenVertices, queenNormals, queenTexCoords, queenNumVertices, textureId);

		// Kings
		drawPiece(tIdx, &wPiecesCoords[KING], kPieceScale, kingVertices, kingNormals, kingTexCoords, kingNumVertices, textureId);
		drawPiece(tIdx, &bPiecesCoords[KING], kPieceScale, kingVertices, kingNormals, kingTexCoords, kingNumVertices, textureId);

		glDisableVertexAttribArray(vertexHandle);
		glDisableVertexAttribArray(normalHandle);
		glDisableVertexAttribArray(textureCoordHandle);

		SampleUtils::checkGlError("ImageTargets renderFrame");
	}

	glDisable(GL_DEPTH_TEST);

	QCAR::Renderer::getInstance().end();
}

void drawPiece(int tIdx, struct point *coord, float scale, float *vertices,
		float *normals, float *texCoords, int numVertices, int textureId) {
	// Get the trackable:
	const QCAR::TrackableResult* result = state.getTrackableResult(tIdx);
	const QCAR::Trackable& trackable = result->getTrackable();

	QCAR::Matrix44F modelViewProjection;
	QCAR::Matrix44F modelViewMatrix = QCAR::Tool::convertPose2GLMatrix(
			result->getPose());

	SampleUtils::translatePoseMatrix(coord->x, coord->y, coord->z,
			&modelViewMatrix.data[0]);
	SampleUtils::scalePoseMatrix(scale, scale, scale, &modelViewMatrix.data[0]);
	SampleUtils::multiplyMatrix(&projectionMatrix.data[0],
			&modelViewMatrix.data[0], &modelViewProjection.data[0]);

	glVertexAttribPointer(vertexHandle, 3, GL_FLOAT, GL_FALSE, 0,
			(const GLvoid*) &vertices[0]);
	glVertexAttribPointer(normalHandle, 3, GL_FLOAT, GL_FALSE, 0,
			(const GLvoid*) &normals[0]);
	glVertexAttribPointer(textureCoordHandle, 2, GL_FLOAT, GL_FALSE, 0,
			(const GLvoid*) &texCoords[0]);
	glEnableVertexAttribArray(vertexHandle);
	glEnableVertexAttribArray(normalHandle);
	glEnableVertexAttribArray(textureCoordHandle);

	glBindTexture(GL_TEXTURE_2D, textures[textureId]->mTextureID);
	glUniformMatrix4fv(mvpMatrixHandle, 1, GL_FALSE,
			(GLfloat*) &modelViewProjection.data[0]);
	glDrawArrays(GL_TRIANGLES, 0, numVertices);
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
