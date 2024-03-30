package cc.iotkit.plugins.dlt645.load;

import cc.iotkit.plugins.dlt645.analysis.DLT645Data;
import cc.iotkit.plugins.dlt645.analysis.DLT645DataFormat;
import cc.iotkit.plugins.dlt645.analysis.DLT645V1997Data;
import cn.hutool.core.text.csv.CsvReader;
import cn.hutool.core.text.csv.CsvUtil;
import cn.hutool.core.util.CharsetUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author：tfd
 * @Date：2023/12/13 17:59
 */
@Slf4j
public class DLT645v1997CsvLoader {
    /**
     * 从CSV文件中装载映射表
     *
     */
    public List<DLT645Data> loadCsvFile() {
        CsvReader csvReader = CsvUtil.getReader();
        InputStreamReader dataReader=new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream("DLT645-1997.csv"),CharsetUtil.CHARSET_GBK);
        List<JDecoderValueParam> rows = csvReader.read(dataReader, JDecoderValueParam.class);
        List<DLT645Data> list = new ArrayList<>();
        for (JDecoderValueParam jDecoderValueParam : rows) {
            try {
                DLT645V1997Data entity = new DLT645V1997Data();
                entity.setName(jDecoderValueParam.getName());
                entity.setDi1h((byte) Integer.parseInt(jDecoderValueParam.di1h, 16));
                entity.setDi1l((byte) Integer.parseInt(jDecoderValueParam.di1l, 16));
                entity.setDi0h((byte) Integer.parseInt(jDecoderValueParam.di0h, 16));
                entity.setDi0l((byte) Integer.parseInt(jDecoderValueParam.di0l, 16));
                entity.setLength(jDecoderValueParam.length);
                entity.setUnit(jDecoderValueParam.unit);
                entity.setRead(Boolean.parseBoolean(jDecoderValueParam.read));
                entity.setWrite(Boolean.parseBoolean(jDecoderValueParam.write));

                DLT645DataFormat format = new DLT645DataFormat();
                if (format.decodeFormat(jDecoderValueParam.format, jDecoderValueParam.length)) {
                    entity.setFormat(format);
                } else {
                    log.info("DLT645 CSV记录的格式错误:" + jDecoderValueParam.getName() + ":" + jDecoderValueParam.getFormat());
                    continue;
                }
                list.add(entity);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return list;
    }


    @Data
    public static class JDecoderValueParam implements Serializable {
        private String di1h;
        private String di1l;
        private String di0h;
        private String di0l;
        /**
         * 编码格式
         */
        private String format;
        /**
         * 长度
         */
        private Integer length;
        /**
         * 单位
         */
        private String unit;

        /**
         * 是否可读
         */
        private String read;
        /**
         * 是否可写
         */
        private String write;
        /**
         * 名称
         */
        private String name;
    }
}
