package cc.iotkit.common.constant;

/**
 * 通用常量信息
 *
 * @author ruoyi
 */
public interface Constants {

    /**
     * UTF-8 字符集
     */
    String UTF8 = "UTF-8";

    /**
     * GBK 字符集
     */
    String GBK = "GBK";

    /**
     * www主域
     */
    String WWW = "www.";

    /**
     * http请求
     */
    String HTTP = "http://";

    /**
     * https请求
     */
    String HTTPS = "https://";

    /**
     * 通用成功标识
     */
    String SUCCESS = "0";

    /**
     * 通用失败标识
     */
    String FAIL = "1";

    /**
     * 登录成功
     */
    String LOGIN_SUCCESS = "Success";

    /**
     * 注销
     */
    String LOGOUT = "Logout";

    /**
     * 注册
     */
    String REGISTER = "Register";

    /**
     * 登录失败
     */
    String LOGIN_FAIL = "Error";

    /**
     * 验证码有效期（分钟）
     */
    Integer CAPTCHA_EXPIRATION = 2;

    String ACCOUNT_SECRET = "3n1z33kzvpgz1foijpkepyd3e8tw84us";

    String CACHE_PRODUCT = "product_cache";

    String CACHE_DEVICE_INFO = "device_info_cache";

    String CACHE_DEVICE_STATS = "device_stats_cache";

    String CACHE_CATEGORY = "category_cache";

    String CACHE_SPACE = "space_cache";

    String CACHE_THING_MODEL = "thing_model_cache";

    String CACHE_USER_INFO = "user_info_cache";

    String CACHE_OAUTH_CLIENT = "oauth_client_cache";

    String CACHE_PRODUCT_SCRIPT = "product_script_cache";

    /**
     * 管理员角色
     */
    String ROLE_ADMIN = "iot_admin";

    /**
     * 可写角色
     */
    String ROLE_WRITE = "iot_write";

    /**
     * 管理系统用户角色
     */
    String ROLE_SYSTEM = "iot_system";

    /**
     * C端用户角色
     */
    String ROLE_CLIENT = "iot_client";

    /**
     * C端用户默认密码
     */
    String PWD_CLIENT_USER = "c123456";

    /**
     * 系统用户默认密码
     */
    String PWD_SYSTEM_USER = "s123456";

    /**
     * 设备物模型消息的topic
     */
    String THING_MODEL_MESSAGE_TOPIC = "device_thing";

    /**
     * 设备属性上报消息的topic
     */
    String DEVICE_PROPERTY_REPORT_TOPIC = "device_property_report";

    /**
     * 设备配置消息topic
     */
    String DEVICE_CONFIG_TOPIC = "device_config";

    /**
     * http消费设备信息的topic
     */
    String HTTP_CONSUMER_DEVICE_INFO_TOPIC = "device_info:";

    /**
     * 写权限
     */
    String PERMISSION_WRITE = "write";

    /**
     * 设备属性缓存key
     */
    String PROPERTY_CACHE_KEY = "str:iotkit:device:property:%s";

    /**
     * 三方平台类型
     */
    enum ThirdPlatform {
        dueros("小度"),
        aligenie("天猫精灵"),
        miiot("小爱");

        public String desc;

        ThirdPlatform(String desc) {
            this.desc = desc;
        }
    }

    /**
     * 三方平台openUid名称
     */
    enum ThirdOpenUid {
        duerosOpenUid("小度OpenUid"),
        aligenieOpenUid("天猫精灵OpenUid"),
        miiotOpenUid("小爱OpenUid");

        public String desc;

        ThirdOpenUid(String desc) {
            this.desc = desc;
        }
    }

    interface API_DEVICE {

        /**
         * 设备-基路径
         */
        String BASE = "/device";

        /**
         * 设备-设备列表
         */
        String LIST = "/list/{size}/{page}";

        /**
         * 设备-设备详情
         */
        String DETAIL = "/{deviceId}/detail";

        /**
         * 设备-属性设置
         */
        String SET_PROPERTIES = "/{deviceId}/service/property/set";

        /**
         * 设备-服务调用
         */
        String INVOKE_SERVICE = "/{deviceId}/service/{service}/invoke";
        /**
         * 设备-属性获取
         */
        String INVOKE_SERVICE_PROPERTY_GET = "/service/property/get";

        /**
         * OTA升级
         */
        String OTA_UPGRADE_PACKAGE = "{deviceId}/ota/upgrade/package/";

        /**
         * OTA升级进度上报
         */
        String OTA_UPGRADE_INFORM = "{deviceId}/ota/upgrade/inform/";

    }

    interface API_SPACE {

        /**
         * 空间-基路径
         */
        String BASE = "/space";

        /**
         * 最近使用设备列表
         */
        String RECENT_DEVICES = "/myRecentDevices";

        /**
         * 获取用户当前收藏设备
         */
        String GET_COLLECT_DEVICES = "/getCollectDevices";

        /**
         * 我的空间设备列表
         */
        String SPACE_DEVICES = "/myDevices/{spaceId}";

        /**
         * 查找设备
         */
        String FIND_DEVICE = "/findDevice";

        /**
         * 空间添加设备
         */
        String ADD_DEVICE = "/addDevice";

        /**
         * 空间删除设备
         */
        String REMOVE_DEVICE = "/removeDevice";

        /**
         * 空间修改设备
         */
        String SAVE_DEVICE = "/saveDevice";

        /**
         * 收藏/取消收藏设备
         */
        String COLLECT_DEVICE = "/collectDevice";

        /**
         * 获取空间设备信息
         */
        String GET_DEVICE = "/device/{deviceId}";

        /**
         * 设置第三方平台openUid
         */
        String SET_OPEN_UID = "/setOpenUid";
    }

}

