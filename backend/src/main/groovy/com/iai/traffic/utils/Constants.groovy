package com.iai.traffic.utils

class Constants {
    public static final int DATA_TRAFFIC_FLOW  = 1
    public static final int DATA_TRAVEL_TIME   = 2
    public static final int DATA_WAZE_ALERTS   = 3
    public static final int DATA_RESTRICTION   = 4
    public static final int DATA_WEATHER       = 5
    public static final int DATA_TRAFFIC_ALERT = 6
    public static final int DATA_TRAFFIC_VIDEO = 7

    public static final int DATA_QUALITY_MISSING_DATA          = 1
    public static final int DATA_QUALITY_FLOW_STATUS_ZERO      = 2
    public static final int DATA_QUALITY_EXCEEDING_THRESHOLD   = 3
    public static final int DATA_QUALITY_VOS_CONSTANT_VALUE    = 4
    public static final int DATA_QUALITY_VOS_INCONSISTENCY     = 5
    public static final int DATA_QUALITY_VOLUME_AGGREGATION    = 6
    public static final int DATA_QUALITY_TIMEZONE_ERROR        = 7

    public static final int REGION_URBAN_FREEWAY = 1
    public static final int REGION_URBAN_SYSTEM = 2
    public static final int REGION_CAV_FREEWAY = 3
    public static final int REGION_CAV_SYSTEM = 4

    public static final String VISION_CAMERA = 'camera'
    public static final String JETSON_CAMERA = 'jetson'
}
