package cc.iotkit.plugins.hydrovalve.analysis;

/**
 * @Author：tfd
 * @Date：2024/1/9 15:41
 */
public abstract  class ModBusAnalysis {
    /**
     * 编码：将实体打包成报文
     *
     * @param entity 实体
     * @return 数据报文
     */
    public abstract byte[] packCmd4Entity(ModBusEntity entity);

    /**
     * 解包：将报文解码成实体
     *
     * @param arrCmd 报文
     * @return 实体
     */
    public abstract ModBusEntity unPackCmd2Entity(byte[] arrCmd);
}
