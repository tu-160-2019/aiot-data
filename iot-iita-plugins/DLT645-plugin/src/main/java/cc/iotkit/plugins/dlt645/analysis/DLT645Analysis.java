package cc.iotkit.plugins.dlt645.analysis;

import cc.iotkit.plugins.dlt645.constants.DLT645Constant;
import cc.iotkit.plugins.dlt645.load.DLT645v1997CsvLoader;
import cc.iotkit.plugins.dlt645.load.DLT645v2007CsvLoader;
import cc.iotkit.plugins.dlt645.utils.ByteRef;
import cc.iotkit.plugins.dlt645.utils.BytesRef;
import cc.iotkit.plugins.dlt645.utils.ContainerUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author：tfd
 * @Date：2023/12/13 17:51
 * DL/T 645-1997 通讯规约通信规约
 * 帧起始符 地址域 帧起始符 控制码 数据长度域 数据域 校验码 结束符
 *    1      6      1       1        1      N      1      1
 */
public class DLT645Analysis {
    /**
     * 静态实例
     */
    private static final DLT645Analysis DLT645Analysis = new DLT645Analysis();

    public static DLT645Analysis inst() {
        return DLT645Analysis;
    }
    /**
     * 设备地址:4字节的byte[]
     */
    public static final String ADR = "ADR";
    /**
     * 功能码：1字节的byte
     */
    public static final String FUN = "FUN";
    /**
     * 数据报：不定长的byte[]
     */
    public static final String DAT = "DAT";

    /**
     * 索引表
     */
    private Map<String, DLT645Data> name2entity;
    public static Map<String, DLT645Data> din2entity;


    /**
     * 验证码
     *
     * @param arrCmd
     * @param iOffSet
     * @return
     */
    private static int GetVfy(byte[] arrCmd, int iOffSet) {
        int iSize = arrCmd.length - 2 - iOffSet;
        if (iSize < 0) {
            return 0;
        }

        int bySum = 0x00;

        int index = iOffSet;
        for (int i = 0; i < iSize; i++) {
            bySum += arrCmd[index++];
            bySum &= 0xff;
        }

        return bySum & 0xff;
    }

    /**
     * 打包
     *
     * @param arrAddr 6字节的地址码
     * @param byCmd   命令字
     * @param arrData 数据段
     * @return 是否成功
     */
    public static byte[] packCmd(byte[] arrAddr, byte byCmd, byte[] arrData) {
        // 检查:数据块的大小
        int iDataSize = arrData.length;
        if (iDataSize > 255) {
            return new byte[0];
        }
        if (arrAddr.length != 6) {
            return new byte[0];
        }

        // 初始化数组大小
        byte[] arrCmd = new byte[iDataSize + 13];


        int index = 0;


        // 前导字符(在发送帧信息之前,先发送1个或多个字节FEH,以唤醒接收方)
        arrCmd[index++] = (byte) 0xFE;

        // 帧起始符
        arrCmd[index++] = (byte) 0x68;

        // 地址码
        System.arraycopy(arrAddr, 0, arrCmd, index, arrAddr.length);
        index += arrAddr.length;

        // 帧起始符
        arrCmd[index++] = (byte) 0x68;

        // 控制码
        arrCmd[index++] = byCmd;

        // 帧长度
        arrCmd[index++] = (byte) iDataSize;

        // 数据域
        System.arraycopy(arrData, 0, arrCmd, index, iDataSize);
        // 每个字节加上0x33
        for (int i = 0; i < arrData.length; i++) {
            arrCmd[index + i] = (byte) ((arrCmd[index + i] & 0xff) + 0x33);
        }
        index += iDataSize;


        // 校验码
        arrCmd[index++] = (byte) GetVfy(arrCmd, 1);

        // 结束符
        arrCmd[index++] = 0x16;

        return arrCmd;
    }

    /**
     * 默认打包
     *
     * @param byCmd
     * @param arrData
     * @return
     */
    public static byte[] packCmd(byte byCmd, byte[] arrData) {
        byte[] arrAddr = new byte[6];

        arrAddr[0] = 0x01;
        arrAddr[1] = 0x00;
        arrAddr[2] = 0x00;
        arrAddr[3] = 0x00;
        arrAddr[4] = 0x00;
        arrAddr[5] = 0x00;

        return packCmd(arrAddr, byCmd, arrData);
    }

    /**
     * 解包
     *
     * @param arrCmd     报文，前面有不确定的唤醒字符
     * @param arrAddrRef 地址码
     * @param byCmd      命令字
     * @param arrDataRef 数据
     * @return 是否成功
     */
    private static boolean unPackCmd2Map(byte[] arrCmd, BytesRef arrAddrRef, ByteRef byCmd, BytesRef arrDataRef) {
        int iSize = arrCmd.length;

        // 查找偏移量:DLT645电表前面会被塞入不定长的乱码数据，被用来激活电表，直到0x68字符出现
        int iOffSet = 0;
        int index = 0;
        for (iOffSet = 0; iOffSet < iSize; iOffSet++) {
            if ((arrCmd[index++] & 0xff) == 0x68) {
                break;
            }
        }
        if (iOffSet == iSize) {
            return false;
        }

        // 检查:数据包大小
        if (iSize < 12 + iOffSet) {
            return false;
        }

//==============================================================================
// 中国电力总局的DL/T 645-1997 多功能电能表通信规约
// 引导码 起始符 地址码 起始符 功能码 帧长度 数据域 校验和 结束符
//   N      1      6      1      1      1      N       1      1
//==============================================================================

        // 检查:起始符1
        if (arrCmd[iOffSet + 0] != 0x68) {
            return false;
        }
        // 检查:起始符2
        if (arrCmd[iOffSet + 7] != 0x68) {
            return false;
        }
        // 检查:结束符
        if (arrCmd[iSize - 1] != 0x16) {
            return false;
        }

        // 地址码
        byte[] arrAddr = new byte[6];
        System.arraycopy(arrCmd, iOffSet + 1, arrAddr, 0, 6);
        arrAddrRef.setValue(arrAddr);


        // 功能码
        byCmd.setValue(arrCmd[iOffSet + 8]);


        // 检查:帧长度
        int iDataSize = arrCmd[iOffSet + 9];
        if ((iDataSize + 12 + iOffSet) != iSize) {
            return false;
        }

        // 数据域
        byte[] arrData = new byte[iDataSize];
        System.arraycopy(arrCmd, iOffSet + 10, arrData, 0, iDataSize);
        // 每个字节先减去0x33
        for (int i = 0; i < arrData.length; i++) {
            arrData[i] = (byte) ((arrData[i] & 0xff) - 0x33);
        }
        arrDataRef.setValue(arrData);


        // 检查:校验码
        byte byVfyOK = (byte) (GetVfy(arrCmd, iOffSet) & 0xff);
        return byVfyOK == arrCmd[iSize - 2];
    }

    public static boolean unPackCmd2Map(byte[] arrCmd, ByteRef byCmd, BytesRef arrData) {
        BytesRef arrAddr = new BytesRef();
        return unPackCmd2Map(arrCmd, arrAddr, byCmd, arrData);
    }

    /**
     * 只有数据标识的DI0和DI1的请求命令
     *
     * @param DI0 数据标识
     * @param DI1 数据标识
     * @return
     */
    public static byte[] packCmdGetData(int DI0, int DI1) {
        byte[] arrData = new byte[2];
        arrData[0] = (byte) DI0;
        arrData[1] = (byte) DI1;

        return packCmd((byte) 0x01, arrData);
    }

    public static boolean unPackCmdGetData(byte[] arrCmd, BytesRef arrData) {
        ByteRef byCmd = new ByteRef();
        if (!unPackCmd2Map(arrCmd, byCmd, arrData)) {
            return false;
        }

        return byCmd.getValue() == 0x81;
    }

    /**
     * 包装成另一种格式
     *
     * @param arrCmd
     * @return
     */
    public static Map<String, Object> unPackCmd2Map(byte[] arrCmd) {
        ByteRef byFun = new ByteRef();
        BytesRef byAddr = new BytesRef();
        BytesRef arrData = new BytesRef();
        if (!unPackCmd2Map(arrCmd, byAddr, byFun, arrData)) {
            return Collections.emptyMap();
        }

        Map<String, Object> value = new HashMap<>();
        value.put(ADR, byAddr.getValue());
        value.put(FUN, byFun.getValue());
        value.put(DAT, arrData.getValue());
        return value;
    }

    /**
     * 获得对象信息
     *
     * @return 对象副本
     */
    public synchronized Map<String, DLT645Data> getTemplateByName() {
        if (this.name2entity == null) {
            DLT645v1997CsvLoader loader = new DLT645v1997CsvLoader();
            List<DLT645Data> entityList = loader.loadCsvFile();
            Map<String, DLT645Data> nameMap = ContainerUtils.buildMapByKey(entityList, DLT645V1997Data::getName);
            this.name2entity = new ConcurrentHashMap<>();
            this.name2entity.putAll(nameMap);
        }

        return this.name2entity;
    }

    public synchronized Map<String, DLT645Data> getTemplateByDIn(String ver) {
        if (this.din2entity == null) {
            if(DLT645Constant.PRO_VER_1997.equals(ver)){
                DLT645v1997CsvLoader loader = new DLT645v1997CsvLoader();
                List<DLT645Data> entityList = loader.loadCsvFile();
                Map<String, DLT645Data> keyMap = ContainerUtils.buildMapByKey(entityList, DLT645V1997Data::getKey);
                this.din2entity = new ConcurrentHashMap<>();
                this.din2entity.putAll(keyMap);
            }else{
                DLT645v2007CsvLoader loader = new DLT645v2007CsvLoader();
                List<DLT645Data> entityList = loader.loadCsvFile();
                Map<String, DLT645Data> keyMap = ContainerUtils.buildMapByKey(entityList, DLT645V2007Data::getKey);
                this.din2entity = new ConcurrentHashMap<>();
                this.din2entity.putAll(keyMap);
            }
        }

        return this.din2entity;
    }
}
