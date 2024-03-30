package cc.iotkit.plugins.dlt645.utils;

/**
 * @Author：tfd
 * @Date：2023/12/13 17:48
 */
public class ByteUtils {
    /**
     * 根据十六进制生成byte
     *
     * @param hex 16进制的字符串 比如"FF"
     * @return byte数字比如-1
     */
    public static byte hex2byte(String hex) {
        return Integer.valueOf(hex, 16).byteValue();
    }

    /**
     * 16进制的字符串表示转成字节数组
     *
     * @param hexString 16进制格式的字符串
     * @return 转换后的字节数组
     **/
    public static byte[] hexStringToByteArray(String hexString) {
        String string = hexString.replaceAll(" ", "");
        final byte[] byteArray = new byte[string.length() / 2];
        int pos = 0;
        for (int i = 0; i < byteArray.length; i++) {
            // 因为是16进制，最多只会占用4位，转换成字节需要两个16进制的字符，高位在先
            byte high = (byte) (Character.digit(string.charAt(pos), 16) & 0xff);
            byte low = (byte) (Character.digit(string.charAt(pos + 1), 16) & 0xff);
            byteArray[i] = (byte) (high << 4 | low);
            pos += 2;
        }

        return byteArray;
    }

    /**
     * 字节数组转成16进制表示格式的字符串
     *
     * @param byteArray 要转换的字节数组
     * @return 16进制表示格式的字符串
     **/
    public static String byteArrayToHexString(byte[] byteArray) {
        return byteArrayToHexString(byteArray, true);
    }

    public static String byteArrayToHexString(byte[] byteArray, boolean blankz) {
        final StringBuilder hexString = new StringBuilder();
        for (int i = 0; i < byteArray.length; i++) {
            if ((byteArray[i] & 0xff) < 0x10) {
                // 0~F前面不零
                hexString.append("0");
            }

            hexString.append(Integer.toHexString(0xFF & byteArray[i]));

            if (blankz) {
                hexString.append(" ");
            }
        }
        return hexString.toString();
    }

    /**
     * 字节逆序
     *
     **/
    public static void byteInvertedOrder(byte[] tmp,byte[] retData) {
        System.arraycopy(tmp, 0, retData, 0, Math.min(tmp.length, retData.length));
        for (int i = 0; i < retData.length / 2; i++) {
            byte by = retData[i];
            retData[i] = retData[5 - i];
            retData[5 - i] = by;
        }
    }
}
