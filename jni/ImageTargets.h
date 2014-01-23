#include <cstdlib>
#include <time.h>

#include <jni.h>
#include <android/log.h>
#include <stdio.h>
#include <string.h>
#include <assert.h>
#include <math.h>

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
#include "SampleMath.h"
#include "Texture.h"
#include "CubeShaders.h"

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

#define MAX_TAP_TIMER 200
#define MAX_TAP_DISTANCE2 400

enum ActionType {
    ACTION_DOWN,
    ACTION_MOVE,
    ACTION_UP,
    ACTION_CANCEL
};

typedef struct _TouchEvent {
    bool isActive;
    int actionType;
    int pointerId;
    float x;
    float y;
    float lastX;
    float lastY;
    float startX;
    float startY;
    float tapX;
    float tapY;
    unsigned long startTime;
    unsigned long dt;
    float dist2;
    bool didTap;
} TouchEvent;


typedef struct _Piece {
    int id;
    int state;

    QCAR::Vec2F position;
    QCAR::Matrix44F transform;
    float *vertices;
    float *normals;
    float *texCoords;
    int numVertices;
    int textureId;

    int restingFrameCount;
} Piece;

float* vertices[N / 4] = {rookVertices, knightVertices, bishopVertices, queenVertices, kingVertices, bishopVertices, knightVertices, rookVertices};
float* normals[N / 4] = {rookNormals, knightNormals, bishopNormals, queenNormals, kingNormals, bishopNormals, knightNormals, rookNormals};
float* texCoords[N / 4] = {rookTexCoords, knightTexCoords, bishopTexCoords, queenTexCoords, kingTexCoords, bishopTexCoords, knightTexCoords, rookTexCoords};
int numVertices[N / 4] = {rookNumVertices, knightNumVertices, bishopNumVertices, queenNumVertices, kingNumVertices, bishopNumVertices, knightNumVertices, rookNumVertices};

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
QCAR::Matrix44F projectionMatrix, modelViewMatrix;
QCAR::Matrix44F inverseProjMatrix;

// Constants:
static const float kPieceScale = 60.0f;

QCAR::DataSet* dataSetCheckerboard = 0;

bool switchDataSetAsap = false;

unsigned long lastTapTime;

TouchEvent touch1, touch2;

Piece wPieces[N / 2], bPieces[N / 2];

Piece* selectedPiece;

void drawPiece(Piece *);

void updatePieceTransform(Piece *);

void handleTouches();

void projectScreenPointToPlane(QCAR::Vec2F point, QCAR::Vec3F planeCenter, QCAR::Vec3F planeNormal,
                               QCAR::Vec3F &intersection, QCAR::Vec3F &lineStart, QCAR::Vec3F &lineEnd);

bool linePlaneIntersection(QCAR::Vec3F lineStart, QCAR::Vec3F lineEnd, QCAR::Vec3F pointOnPlane,
                           QCAR::Vec3F planeNormal, QCAR::Vec3F &intersection);

unsigned long getCurrentTimeMS();
