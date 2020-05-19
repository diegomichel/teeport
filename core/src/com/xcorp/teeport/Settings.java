package com.xcorp.teeport;

public class Settings {
    public static final int INITIAL_MAP = 1;

    //Screen settings
    public static final int SCREEN_WIDTH = 1280;
    public static final int SCREEN_HEIGHT = 800;

    public static final float WORLD_TO_BOX = 0.01f;
    public static final float BOX_TO_WORLD = 100f;

    public static final boolean warnings = true;
    public static final boolean debugMap = true;

    //Player Settings
    public static final float PLAYER_RADIUS = 16;
    public static final float PLAYER_HEALTH = 100f;


    //Times are in Milliseconds
    public static final float PORTAL_GUN_RATE = 1000;

    public static final float PORTAL_WIDTH = 50;
    public static final float PORTAL_HEIGHT = 128;

    public static final float MAX_OBJECT_SIZE_TO_TELEPORT = 64;

    public static final float PLAYER_MAX_SPEED = 26f;

    public static final long INTERMISSION_TIME = 5000;

    /*
    Special controls
     */
    public static final boolean CONTROLS_VISUAL_CONTROLS = true;
    public static final int CONTROLS_MILLISECONDS_TO_RELEASE = 120;

    /*
    Debug
     */
    public static final boolean DEBUG_DRAW_BBOX = false;
    public static boolean DEVELOPMENT_MODE = true;
}