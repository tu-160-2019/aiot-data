package cc.iotkit.temporal.iotdb.model;


/**
 * @author sjg
 */
public interface Record {

    /**
     * 设备Id
     *
     * @return string
     */
    String getDeviceId();

    /**
     * 时间
     *
     * @return long
     */
    Long getTime();

}
