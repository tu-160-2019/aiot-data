package cc.iotkit.plugins.hydrovalve.utils;

/**
 * @Author：tfd
 * @Date：2024/1/8 15:15
 */
public class ByteUtils {
    /**
     * 将十六进制的字符串转换成字节数组
     *
     * @param hexString
     * @return
     */
    public static byte[] hexStrToBinaryStr(String hexString) {

        if (hexString==null) {
            return null;
        }
        try {
            hexString = hexString.replaceAll(" ", "");
            int len = hexString.length();
            int index = 0;
            byte[] bytes = new byte[len / 2];
            while (index < len) {
                String sub = hexString.substring(index, index + 2);
                bytes[index/2] = (byte)Integer.parseInt(sub,16);
                index += 2;
            }
            return bytes;
        }catch (Exception e){
            return null;
        }

    }

    /**
     * 将字节数组转换成十六进制的字符串
     *
     * @return
     */
    public static String BinaryToHexString(byte[] bytes,boolean isBalank) {
        String hexStr = "0123456789ABCDEF";
        String result = "";
        String hex = "";
        Boolean feStart=true;
        for (byte b : bytes) {
            hex = String.valueOf(hexStr.charAt((b & 0xF0) >> 4));
            hex += String.valueOf(hexStr.charAt(b & 0x0F));
            if("FE".equals(hex) && feStart){
                continue;
            }else {
                feStart=false;
            }
            result += hex + (isBalank?" ":"");
        }
        return result;
    }
}
