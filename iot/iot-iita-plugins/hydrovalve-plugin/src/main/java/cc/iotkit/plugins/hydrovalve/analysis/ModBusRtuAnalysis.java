package cc.iotkit.plugins.hydrovalve.analysis;

import cc.iotkit.common.utils.StringUtils;
import cc.iotkit.plugins.hydrovalve.utils.ByteUtils;
import lombok.extern.slf4j.Slf4j;

/**
 * @Author：tfd
 * @Date：2024/1/9 15:53
 */
@Slf4j
public class ModBusRtuAnalysis extends ModBusAnalysis  {
    /**
     * 校验CRC16
     *
     * @param arrCmd
     * @return
     */
    public static int getCRC16(byte[] arrCmd) {
        int iSize = arrCmd.length - 2;

        // 检查:帧长度
        if (iSize < 2) {
            return 0;
        }

        int wCrcMathematics = 0xA001;

        int usCrc16 = 0x00;

        //16位的CRC寄存器
        int byteCrc16Lo = 0xFF;
        int byteCrc16Hi = 0xFF;
        //临时变量
        int byteSaveHi = 0x00;
        int byteSaveLo = 0x00;

        //CRC多项式码的寄存器
        int byteCl = wCrcMathematics % 0x100;
        int byteCh = wCrcMathematics / 0x100;

        for (int i = 0; i < iSize; i++) {
            byteCrc16Lo &= 0xFF;
            byteCrc16Hi &= 0xFF;
            byteSaveHi &= 0xFF;
            byteSaveLo &= 0xFF;

            byteCrc16Lo ^= arrCmd[i];                    //每一个数据与CRC寄存器进行异或
            for (int k = 0; k < 8; k++) {
                byteCrc16Lo &= 0xFF;
                byteCrc16Hi &= 0xFF;

                byteSaveHi = byteCrc16Hi;
                byteSaveLo = byteCrc16Lo;
                byteCrc16Hi /= 2;                         //高位右移一位
                byteCrc16Lo /= 2;                         //低位右移一位
                if ((byteSaveHi & 0x01) == 0x01)         //如果高位字节最后一位为1
                {
                    byteCrc16Lo |= 0x80;                 //则低位字节右移后前面补1
                }                                         //否则自动补0
                if ((byteSaveLo & 0x01) == 0x01)         //如果高位字节最后一位为1，则与多项式码进行异或
                {
                    byteCrc16Hi ^= byteCh;
                    byteCrc16Lo ^= byteCl;
                }
            }
        }


        usCrc16 = (byteCrc16Hi & 0xff) * 0x100 + (byteCrc16Lo & 0xff);

        return usCrc16;
    }

    /**
     * 解码
     *
     * @param arrCmd 报文
     * @return 是否成功
     */
    @Override
    public ModBusEntity unPackCmd2Entity(byte[] arrCmd) {
        ModBusEntity entity = new ModBusEntity();

        int iSize = arrCmd.length;
        if (iSize < 4) {
            return null;
        }

        // 地址码
        byte byAddr = arrCmd[0];
        entity.setDevAddr(byAddr);

        // 功能码
        byte byFun = arrCmd[1];
        entity.setFunc(byFun);

        // 数据域
        int iDataSize = iSize - 4;
        entity.setData(new byte[iDataSize]);
        byte[] arrData = entity.getData();
        System.arraycopy(arrCmd, 2, arrData, 0, iDataSize);

        // 校验CRC
        int wCrc16OK = getCRC16(arrCmd);
        byte crcH = (byte) (wCrc16OK & 0xff);
        byte crcL = (byte) ((wCrc16OK & 0xff00) >> 8);
        if (arrCmd[arrCmd.length - 1] == crcL && arrCmd[arrCmd.length - 2] == crcH) {
            return entity;
        }

        return null;
    }

    /**
     * 编码
     *
     * @return 编码是否成功
     */
    @Override
    public byte[] packCmd4Entity(ModBusEntity entity) {
        int iSize = entity.getData().length;

        byte[] arrCmd = new byte[iSize + 4];

        // 地址码
        arrCmd[0] = entity.getDevAddr();

        // 功能码
        arrCmd[1] = entity.getFunc();

        // 数据域
        System.arraycopy(entity.getData(), 0, arrCmd, 2, iSize);

        // 校验CRC
        int wCrc16 = getCRC16(arrCmd);
        arrCmd[arrCmd.length - 2] = (byte) (wCrc16 % 0x100);
        arrCmd[arrCmd.length - 1] = (byte) (wCrc16 / 0x100);

        return arrCmd;
    }

    public static void main(String[] args) {
//        String hexString = "0103020457FB7A";
        String hexString = "01060001000119CA";
        ModBusRtuAnalysis a=new ModBusRtuAnalysis();
        ModBusEntity b=a.unPackCmd2Entity(ByteUtils.hexStrToBinaryStr(hexString));
        int lenth=b.getData()[0];
        byte[] val = new byte[lenth];
        System.arraycopy(b.getData(), 1, val, 0, lenth);
        log.info("ret:"+Integer.parseInt(ByteUtils.BinaryToHexString(val,false), 16));
        ModBusEntity c=new ModBusEntity();
        c.setDevAddr((byte) 1);
        c.setFunc(ModBusConstants.FUN_CODE3);
        Integer dz=0;
        Integer dzsl=10;
        String a1=StringUtils.leftPad(dz.toHexString(dz),4,'0')+StringUtils.leftPad(dz.toHexString(dzsl),4,'0');
        c.setData(ByteUtils.hexStrToBinaryStr(a1));
        byte[] d=a.packCmd4Entity(c);
        log.info("ret1:"+ByteUtils.BinaryToHexString(d,false));
        ModBusEntity e=new ModBusEntity();
        e.setDevAddr((byte) 1);
        e.setFunc(ModBusConstants.FUN_CODE6);
        Integer dze=0;
        Integer dzsle=1;
        String a1e=StringUtils.leftPad(dz.toHexString(dze),4,'0')+StringUtils.leftPad(dz.toHexString(dzsle),4,'0');
        e.setData(ByteUtils.hexStrToBinaryStr(a1e));
        byte[] f=a.packCmd4Entity(e);
        log.info("rete:"+ByteUtils.BinaryToHexString(f,false));
    }
}
