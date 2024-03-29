package cc.iotkit.common.enums;

/**
 * @author ：tfd
 * 异常枚举类
 */
public enum ErrCode implements IEnum {
    /**
     * 系统通用异常段
     */
    PARAMS_EXCEPTION(10000001, "参数异常"),
    SYSTEM_EXCEPTION(10000002, "系统异常"),
    UNKNOWN_EXCEPTION(10000003, "未知异常"),
    SYSTEM_ERROR(10000004, "服务器内部错误"),
    METHOD_NOT_ALLOWED(10000005, "请求方法不支持"),
    NOT_FOUND(10000006, "请求资源不存在"),
    FORBIDDEN(10000007, "请求被拒绝"),
    UNAUTHORIZED_EXCEPTION(10000008, "未授权访问"),
    UNSUPPORTED_OPERATION_EXCEPTION(10000009, "方法未实现"),
    DATA_NOT_EXIST(10000010, "数据不存在"),

    /**
     * openapi通用异常段
     */
    IDENTIFIER_ERROR(20000000, "签名验证失败"),
    API_LOGIN_ERROR(20000000, "登录验证失败"),


    /**
     * 组件通用异常段
     */
    GET_COMPONENT_INSTANCE_ERROR(30000001, "获取通讯组件实例失败"),
    GET_COMPONENT_SCRIPT_ERROR(30000002, "获取通讯组件脚本失败"),
    GET_CONVERT_ERROR(30000003, "获取转换器失败"),
    GET_SPI_COMPONENT_ERROR(30000004, "获取组件CLASS失败"),
    GET_SPI_CONVERT_ERROR(30000005, "获取转换器CLASS失败"),
    COMPONENT_NOT_FOUND(30000006, "通讯组件不存在"),
    SEND_DESTINATION_NOT_FOUND(30000007, "发送目标不存在"),
    MSG_CONVERT_ERROR(30000008, "消息转换失败"),
    DEVICE_REGISTER_ERROR(30000009, "设备注册失败"),
    COMPONENT_ID_BLANK(30000010, "通讯组件ID为空"),
    COMPONENT_JAR_NOT_FOUND(30000011, "通讯组件JAR包为空"),
    COMPONENT_ALREADY(30000012, "通讯组件已经存在"),
    SAVE_COMPONENT_SCRIPT_ERROR(30000013, "保存通讯组件脚本失败"),
    SAVE_CONVERT_SCRIPT_ERROR(30000014, "保存转换器脚本失败"),
    ADD_COMPONENT_ERROR(30000015, "添加通讯组件失败"),
    ADD_CONVERT_ERROR(30000016, "添加转换器失败"),
    CONVERT_NOT_FOUND(30000017, "转换器不存在"),
    DELETE_CONVERT_ERROR(30000018, "删除转换器失败"),
    DELETE_COMPONENT_ERROR(30000019, "删除通讯组件失败"),
    PRODUCT_SECRET_ERROR(30000020, "产品密钥错误"),
    COMPONENT_START_ERROR(30000021, "通讯组件启动失败"),
    INIT_PRODUCER_ERROR(30000022, "初始化MQ生产者失败"),
    SEND_MSG_ERROR(30000023, "发送消息失败"),
    PLUGIN_ROUTER_NOT_FOUND(30000100, "未找到插件路由"),
    PLUGIN_INSTANCE_NOT_FOUND(30000101, "插件实例未找到"),
    PLUGIN_SERVICE_NOT_FOUND(30000102, "插件设备服务未找到"),
    PLUGIN_INSTALL_FAILED(30000103, "插件安装失败"),
    DEVICE_ACTION_FAILED(30000200, "设备动作执行失败"),


    /**
     * 大屏通用异常段
     */
    RESOURCE_FILE_NOT_FOUND(40000000, "资源包为空"),
    BIG_SCREEN_NOT_FOUND(40000001, "大屏不存在"),
    BIG_SCREEN_ALREADY(40000002, "大屏已存在"),
    ADD_BIG_SCREEN_ERROR(40000003, "保存大屏失败"),
    DELETE_BIG_SCREEN_ERROR(40000004, "删除大屏资源失败"),
    SCREEN_API_NOT_FOUND(40000005, "大屏接口不存在"),
    ADD_SCREEN_API_ERROR(40000006, "添加大屏接口失败"),
    SCREEN_PUBLISH_ERROR(40000007, "大屏发布失败"),
    API_LIST_BLANK(40000008, "接口列表为空"),

    /**
     * 业务通用异常段
     */
    ID_BLANK(50000001, "ID为空"),
    TASK_NOT_SUPPORT_RENEW(50000002, "任务不支持续订"),
    GROUP_ALREADY(50000003, "分组已经存在"),
    GROUP_NOT_FOUND(50000004, "分组不存在"),
    PRODUCT_NOT_FOUND(50000005, "产品不存在"),
    DEVICE_NOT_FOUND(50000006, "设备不存在"),
    DEVICE_OFFLINE(50000007, "设备已离线"),
    DEVICE_ALREADY(50000008, "设备已存在"),
    MODEL_DEVICE_ALREADY(50000009, "设备DN已存在"),

    DEVICE_HAS_ASSOCIATED(50000010, "设备已关联"),
    MODEL_ALREADY(50000011, "型号已存在"),
    MODEL_SCRIPT_NOT_FOUND(50000012, "产品型号脚本不存在"),
    PRODUCT_MODEL_NOT_FOUND(50000013, "产品型号不存在"),
    FILE_NOT_FOUND(50000014, "文件不存在"),
    RULE_NOT_FOUND(50000015, "规则不存在"),
    RULE_ALREADY_RUNNING(50000016, "规则已运行"),
    SEND_REQUEST_ERROR(50000017, "发送请求失败"),
    TASK_NOT_FOUND(50000018, "任务不存在"),
    RENEW_TASK_ERROR(50000019, "重启任务失败"),
    HOME_NOT_FOUND(50000020, "家庭不存在"),
    CURRENT_HOME_NOT_FOUND(50000021, "当前家庭不存在"),
    SPACE_NOT_FOUND(50000022, "空间不存在"),
    SPACE_DEVICE_NOT_FOUND(50000023, "空间设备不存在"),
    DATA_BLANK(50000024, "数据为空"),
    DATA_LENGTH_ERROR(50000025, "数据长度错误"),
    DATA_FORMAT_ERROR(50000026, "数据格式错误"),
    USER_NOT_FOUND(50000027, "用户不存在"),
    RESET_PWD_ERROR(50000028, "重置密码失败"),
    UPDATE_PWD_ERROR(50000029, "修改密码失败"),
    PWD_ERROR(50000030, "密码错误"),
    STATE_ERROR(50000031, "状态错误"),
    RECORD_NOT_FOUND(50000032, "记录不存在"),
    ADD_PLATFORM_USER_ERROR(50000033, "添加平台用户失败"),
    UPLOAD_FILE_ERROR(50000034, "上传文件失败"),
    FILE_NAME_IS_NULL(50000035, "文件名为空，获取文件名失败"),
    PRODUCT_KEY_EXIST(50000036, "ProductKey已存在"),
    DATA_ALREADY(50000037, "记录已存在"),

    TENANT_NOT_FOUND(50001001, "租户不存在"),
    TENANT_DISABLE(50001002, "租户已禁用"),
    TENANT_EXPIRE(50001003, "租户已过期"),
    UNAUTHORIZED_TENANT(50001004, "不允许操作管理租户"),
    PACKAGE_NOT_FOUND(50001005, "套餐不存在"),
    ;

    private int code;
    private String message;

    ErrCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public Integer getKey() {
        return this.code;
    }

    @Override
    public String getValue() {
        return this.message;
    }
}
