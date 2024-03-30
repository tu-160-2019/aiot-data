package cc.iotkit.plugins.dlt645.analysis;

import cc.iotkit.plugins.dlt645.utils.ByteUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author：tfd
 * @Date：2023/12/13 17:56
 */
@Slf4j
@Data
public class DLT645DataFormat {
    // 数值格式
    public static final String FORMAT_X = "X";
    public static final String FORMAT_N = "N";

    // 时间格式
    public static final String FORMAT_YYMMDDWW = "YYMMDDWW";
    public static final String FORMAT_hhmmss = "hhmmss";
    public static final String FORMAT_YYMMDDhhmm = "YYMMDDhhmm";
    public static final String FORMAT_MMDDhhmm = "MMDDhhmm";
    public static final String FORMAT_DDhh = "DDhh";
    public static final String FORMAT_hhmm = "hhmm";
    public static final String FORMAT_mmmm = "mmmm";

    // 编码格式
    public static final String FORMAT_NN___NN = "NN...NN";
    public static final String FORMAT_XX___XX = "XX...XX";

    // 状态格式
    public static final String FORMAT_STATUS_WEEK = "周休日状态字";
    public static final String FORMAT_STATUS_METER = "电表运行状态字";
    public static final String FORMAT_STATUS_NETWORK = "电网状态字";


    /**
     * 格式类型
     */
    private String format = "";
    /**
     * 组合格式：第二个格式
     */
    private String format2nd = "";
    /**
     * 长度
     */
    private int length = 0;
    /**
     * 组合格式：第二个长度
     */
    private int length2nd = 0;
    /**
     * 缩小比例
     */
    private double ratio = 1.0;

    public Object decodeValue(byte[] data, String format, int start, int length) throws RuntimeException {
        // 前面4个字节是DI0～DI3
        if (data.length < length + start) {
            log.info("DATA LENGTH ERROR");
        }

        // 各种XX.XX格式
        if (format.equals(FORMAT_X)) {
            return this.getValue(data, start, length, this.ratio);
        }
        // 各种NN.NN格式
        if (format.equals(FORMAT_N)) {
            return this.getValue(data, start, length, this.ratio);
        }
        if (format.equals(FORMAT_NN___NN)) {
            return this.getString(data, start, length);
        }

        // 时间格式
        if (format.equals(FORMAT_hhmm) || format.equals(FORMAT_DDhh) || format.equals(FORMAT_YYMMDDWW) || format.equals(FORMAT_hhmmss) || format.equals(FORMAT_YYMMDDhhmm) || format.equals(FORMAT_MMDDhhmm)) {
            return this.getDataTime(data, format, start, length);
        }


        if (format.equals(FORMAT_XX___XX)) {
            this.format = FORMAT_XX___XX;
            this.ratio = 1.0;
            return true;
        }


        return false;
    }

    /**
     * 解码格式：固定长度格式和可变长度格式
     * 固定长度格式：根据XX.XX它格式本身长度进行判定
     *
     * @param format 格式名称
     * @param length 可变格式的长度
     * @return 是否成功
     */
    public boolean decodeFormat(String format, int length) {
        // 统计字符种类的数量
        Map<Character, Integer> charCount = charCount(format);

        // 组合格式：XX.XXXX|YYMMDDhhmm
        if (charCount.containsKey('|') && charCount.get('|').equals(1)) {
            String format1 = format.substring(0, format.indexOf("|"));
            String format2 = format.substring(format.indexOf("|") + 1);
            this.decodeFormat(format2, -1);
            this.format2nd = this.format;
            this.length2nd = this.length;
            this.decodeFormat(format1, -1);
            return true;
        }

        // 各种XX.XX格式
        if (charCount.containsKey('X') && charCount.containsKey('.') && charCount.get('.').equals(1)) {
            this.format = FORMAT_X;
            int point = format.length() - format.indexOf(".") - 1;
            for (int i = 0; i < point; i++) {
                this.ratio *= 10.0;
            }
            this.length = (format.length() - 1) / 2;
            return true;
        }
        // XXXX格式
        if (charCount.containsKey('X') && charCount.size() == 1) {
            this.format = FORMAT_X;
            this.ratio = 1.0;
            this.length = length;
            return true;
        }
        // 各种NN.NN格式
        if (charCount.containsKey('N') && charCount.containsKey('.') && charCount.get('.').equals(1)) {
            this.format = FORMAT_N;
            int point = format.length() - format.indexOf(".") - 1;
            for (int i = 0; i < point; i++) {
                this.ratio *= 10.0;
            }
            this.length = (format.length() - 1) / 2;
            return true;
        }
        // NNN格式
        if (charCount.containsKey('N') && charCount.size() == 1) {
            this.format = FORMAT_N;
            this.ratio = 1.0;
            this.length = length;
            return true;
        }


        // 固定长度
        if (this.isFixedLength(format)) {
            this.format = format;
            this.ratio = 1.0;
            this.length = format.length() / 2;
            return true;
        }

        // 可变长度
        if (this.isVariableLength(format)) {
            this.format = format;
            this.ratio = 1.0;
            this.length = length;
            return true;
        }

        return false;
    }

    /**
     * 是否为固定长度：它的长度是直接通过格式就能确定
     *
     * @param format
     * @return
     */
    private boolean isFixedLength(String format) {
        if (format.equalsIgnoreCase(FORMAT_hhmm)) {
            return true;
        }
        if (format.equalsIgnoreCase(FORMAT_DDhh)) {
            return true;
        }
        if (format.equalsIgnoreCase(FORMAT_MMDDhhmm)) {
            return true;
        }
        if (format.equalsIgnoreCase(FORMAT_YYMMDDhhmm)) {
            return true;
        }
        if (format.equalsIgnoreCase(FORMAT_hhmmss)) {
            return true;
        }
        if (format.equalsIgnoreCase(FORMAT_mmmm)) {
            return true;
        }

        return format.equalsIgnoreCase(FORMAT_YYMMDDWW);
    }

    /**
     * 是否为可变长度：它的长度是通过用户在CSV文件中告知
     *
     * @param format
     * @return
     */
    private boolean isVariableLength(String format) {
        if (format.equalsIgnoreCase(FORMAT_NN___NN)) {
            return true;
        }
        if (format.equalsIgnoreCase(FORMAT_XX___XX)) {
            return true;
        }
        if (format.equalsIgnoreCase(FORMAT_STATUS_METER)) {
            return true;
        }
        if (format.equalsIgnoreCase(FORMAT_STATUS_NETWORK)) {
            return true;
        }

        return format.equalsIgnoreCase(FORMAT_STATUS_WEEK);
    }

    /**
     * 统计字符数量，方面后面判定格式
     *
     * @param format DLT645规约中定义的XXX.XXX之类的各种数据格式文本
     * @return 各字符数量
     */
    private Map<Character, Integer> charCount(String format) {

        Map<Character, Integer> charSet = new HashMap<>();
        for (int i = 0; i < format.length(); i++) {
            Character ch = format.charAt(i);
            Integer count = charSet.get(ch);
            if (count == null) {
                count = 0;
            }

            count++;
            charSet.put(ch, count);
        }

        return charSet;
    }

    /**
     * 4字节长度的double型数值
     *
     * @param data  data数组
     * @param start 数据在数组中的起始位置
     * @param ratio 倍率，比如缩小100倍数，那么填0.01
     * @return 返回值
     */
    private Object getValue(byte[] data, int start, int length, double ratio) {
        long sum = 0;
        double rd = 1.0;
        for (int i = 0; i < length; i++) {
            long l = data[start + i] & 0x0f;
            long h = (data[start + i] & 0xf0) >> 4;

            l = (long) (l * rd);
            sum += l;
            rd = rd * 10.0;


            h = (long) (h * rd);
            sum += h;
            rd = rd * 10.0;

        }

        if (ratio < 1.1 && ratio > 0.0) {
            // 如果ratio==1
            return sum;
        } else {
            return sum / ratio;
        }
    }

    /**
     * 日期格式的解码
     *
     * @param data   data数组
     * @param format 日期格式
     * @param start  数据在数组中的起始位置
     * @param length 格式长度
     * @return 返回值
     */
    private String getDataTime(byte[] data, String format, int start, int length) {
        // 拆解成个位数列表
        List<String> list = new ArrayList<>();
        for (int i = 0; i < length; i++) {
            int l = data[start + i] & 0x0f;
            int h = (data[start + i] & 0xf0) >> 4;

            list.add(Integer.toString(l));
            list.add(Integer.toString(h));
        }

        // 格式1
        if (format.equals(FORMAT_YYMMDDhhmm)) {
            String result = "20" + list.get(9) + list.get(8) + "年" + list.get(7) + list.get(6) + "月" + list.get(5) + list.get(4) + "日";
            result += " " + list.get(3) + list.get(2) + "点" + list.get(1) + list.get(0) + "分";
            return result;
        }

        if (format.equals(FORMAT_YYMMDDWW)) {
            String result = "20" + list.get(7) + list.get(6) + "年" + list.get(5) + list.get(4) + "月" + list.get(3) + list.get(2) + "日";
            result += " 星期:" + list.get(1) + list.get(0);
            return result;
        }
        if (format.equals(FORMAT_hhmmss)) {
            return list.get(5) + list.get(4) + "点" + list.get(3) + list.get(2) + "分" + list.get(1) + list.get(0) + "秒";
        }
        if (format.equals(FORMAT_mmmm)) {
            return list.get(3) + list.get(2) + list.get(1) + list.get(0) + "分";
        }


        if (format.equals(FORMAT_MMDDhhmm)) {
            String result = list.get(7) + list.get(6) + "月" + list.get(5) + list.get(4) + "日 ";
            result += list.get(3) + list.get(2) + "点" + list.get(1) + list.get(0) + "分";
            return result;
        }
        if (format.equals(FORMAT_DDhh)) {
            return list.get(3) + list.get(2) + "号 " + list.get(1) + list.get(0) + "点";
        }
        if (format.equals(FORMAT_hhmm)) {
            return list.get(3) + list.get(2) + "点 " + list.get(1) + list.get(0) + "分";
        }


        return "";
    }

    private byte encodeBCD(byte a) {
        return (byte) ((a / 10) * 16 + (a % 10));
    }

    private byte decodeBCD(byte a) {
        return (byte) ((a / 16) * 10 + (a % 16));
    }

    private Object getString(byte[] data, int start, int length) {
        byte[] tmp = new byte[length];

        for (int i = 0; i < length; i++) {
            tmp[i] = data[start + i];
        }
        for (int i = 0; i < length / 2; i++) {
            byte by = tmp[i];
            tmp[i] = tmp[length - i - 1];
            tmp[length - i - 1] = by;
        }
        return ByteUtils.byteArrayToHexString(tmp, true).replace(" ", "");
    }
}
