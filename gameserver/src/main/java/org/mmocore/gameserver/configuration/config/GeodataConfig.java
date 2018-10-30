package org.mmocore.gameserver.configuration.config;

import org.mmocore.commons.configuration.annotations.Configuration;
import org.mmocore.commons.configuration.annotations.Setting;

/**
 * Create by Mangol on 28.11.2015.
 */
@Configuration("geodata.json")
public class GeodataConfig {
    @Setting(name = "GeoFirstX")
    public static int GEO_X_FIRST;

    @Setting(name = "GeoFirstY")
    public static int GEO_Y_FIRST;

    @Setting(name = "GeoLastX")
    public static int GEO_X_LAST;

    @Setting(name = "GeoLastY")
    public static int GEO_Y_LAST;

    @Setting(name = "AllowGeodata")
    public static boolean ALLOW_GEODATA;

    @Setting(name = "AllowFallFromWalls")
    public static boolean ALLOW_FALL_FROM_WALLS;

    @Setting(name = "ClientZShift")
    public static int CLIENT_Z_SHIFT;

    @Setting(name = "CompactGeoData")
    public static boolean COMPACT_GEO;

    @Setting(name = "MinLayerHeight")
    public static int MIN_LAYER_HEIGHT;

    @Setting(name = "MaxZDiff")
    public static int MAX_Z_DIFF;

    @Setting(name = "RegionEdgeMaxZDiff")
    public static int REGION_EDGE_MAX_Z_DIFF;

    @Setting(name = "PathFindDiagonal")
    public static boolean PATHFIND_DIAGONAL;

    @Setting(name = "PathClean")
    public static boolean PATH_CLEAN;

    @Setting(name = "PathFindBoost")
    public static int PATHFIND_BOOST;

    @Setting(name = "PathFindMaxZDiff")
    public static int PATHFIND_MAX_Z_DIFF;

    @Setting(name = "PathFindMapMul")
    public static int PATHFIND_MAP_MUL;

    @Setting(name = "PathFindMaxTime")
    public static long PATHFIND_MAX_TIME;

    @Setting(name = "PathFindBuffers")
    public static final String PATHFIND_BUFFERS = "8x96;8x128;8x160;8x192;4x224;4x256;4x288;2x320;2x384;2x352;1x512";

    public static int maxAsyncCoordDiff;
    public static int maxAsyncCoordDiffBeforeAttack;
    public static int correctType;
}
