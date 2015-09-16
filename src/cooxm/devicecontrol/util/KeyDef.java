package cooxm.devicecontrol.util;

/**
 * Created by XWH on 2015/4/24.
 */
public interface KeyDef {

    public static final int DEV_TYPE_TV = 501;
    public static final int DEV_TYPE_AC = 541;
    public static final int DEV_TYPE_ACL = 591;
    public static final int DEV_TYPE_FAN = 601;
    public static final int DEV_TYPE_DVD = 621;
    public static final int DEV_TYPE_STB = 511;
    public static final int DEV_TYPE_IPTV = 521;


    /**
     * 开关遥控
     */
    public static final int KEY_ON = 501;
    public static final int KEY_OFF = 502;
    public static final int KEY_STOP = 503;


    /**
     * 电视遥控
     */
    /*public static final int KEY_TV_ON = 101;
    public static final int KEY_TV_OFF = 102;*/         //power 0
    public static final int KEY_TV_MENU = 103;          //菜单 29
    public static final int KEY_TV_NOSOUND = 104;       //静音 37
    public static final int KEY_TV_HOME = 105;          //
    public static final int KEY_TV_SOUND_ADD = 106;     //33
    public static final int KEY_TV_SOUND_SUB = 107;     //34
    public static final int KEY_TV_CHANNEL_ADD = 108;   //35
    public static final int KEY_TV_CHANNEL_SUB = 109;   //36
    public static final int KEY_TV_UP = 110;            //25
    public static final int KEY_TV_DOWN = 111;          //26
    public static final int KEY_TV_LEFT = 112;          //27
    public static final int KEY_TV_RIGHT = 113;         //28
    public static final int KEY_TV_OK = 114;            //32
    public static final int KEY_TV_SIGNAL = 115;            //31
    public static final int KEY_TV_RETURN = 116;            //32

    /**
     * 空调遥控
     */
    /*public static final int KEY_AIR_ON = 201;
    public static final int KEY_AIR_OFF = 202;*/
    public static final int KEY_AIR_AUTO = 203;
    public static final int KEY_AIR_COLD = 204;
    public static final int KEY_AIR_HOT = 205;
    public static final int KEY_AIR_CHANGE_AIR = 206;
    public static final int KEY_AIR_WET = 207;
    public static final int KEY_AIR_LEFT_RIGHT = 208;
    public static final int KEY_AIR_UP_DOWN = 209;
    public static final int KEY_AIR_TMP_ADD = 210;
    public static final int KEY_AIR_TMP_SUB = 211;
    public static final int KEY_AIR_SLEEP = 212;
    public static final int KEY_AIR_WIND_AUTO = 213;
    public static final int KEY_AIR_WIND_STRONG = 214;
    public static final int KEY_AIR_WIND_MID = 215;
    public static final int KEY_AIR_WIND_SMALL = 216;
    public static final int KEY_AIR_MODE = 217;
    public static final int KEY_AIR_STABLE_ON = 218;
    public static final int KEY_AIR_STABLE_OFF = 219;



    /**
     * 音响遥控
     */
    /*public static final int KEY_SOUND_ON = 301;
    public static final int KEY_SOUND_OFF = 302;*/
    public static final int KEY_SOUND_PLAY = 303;
    public static final int KEY_SOUND_STOP = 304;
    public static final int KEY_NO_SOUND = 305;
    public static final int KEY_SOUND_ADD = 306;
    public static final int KEY_SOUND_SUB = 307;
    public static final int KEY_SOUND_PRE = 308;
    public static final int KEY_SOUND_NEXT = 309;


    /**
     * 热水器遥控
     */
    /*public static final int KEY_HOT_ON = 401;
    public static final int KEY_HOT_OFF = 402;*/
    public static final int KEY_HOT_ADD = 403;
    public static final int KEY_HOT_SUB = 404;


    /**
     * 2个按键遥控模板
     */
    public static final int KEY_2_1 = 601;
    public static final int KEY_2_2 = 602;


    /**
     * 3个按键遥控模板
     */
    public static final int KEY_3_1 = KEY_ON;
    public static final int KEY_3_2 = KEY_STOP;
    public static final int KEY_3_3 = KEY_OFF;

    /**
     * 4个按键遥控模板
     */
    public static final int KEY_4_1 = 801;
    public static final int KEY_4_2 = 802;
    public static final int KEY_4_3 = 803;
    public static final int KEY_4_4 = 804;


    /**
     * 风扇
     * */
    //public static final int KEY_FAN_ON = 901;
    //public static final int KEY_FAN_OFF = 902;
    public static final int KEY_FAN_MODE = 903;
    public static final int KEY_FAN_SHAKE_HEAD = 904;
    public static final int KEY_FAN_SPEED = 905;
    public static final int KEY_FAN_COLD = 906;
    public static final int KEY_FAN_HEALTH = 907;
    public static final int KEY_FAN_SAVING = 908;
    public static final int KEY_FAN_TIMEOUT = 909;

    /**
     * DVD
     * */
    public static final int KEY_DVD_ON = 901;


}
