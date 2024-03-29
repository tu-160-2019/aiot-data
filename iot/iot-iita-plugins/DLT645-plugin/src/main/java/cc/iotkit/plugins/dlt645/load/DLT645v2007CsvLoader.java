package cc.iotkit.plugins.dlt645.load;

import cc.iotkit.plugins.dlt645.analysis.DLT645Data;
import cc.iotkit.plugins.dlt645.analysis.DLT645DataFormat;
import cc.iotkit.plugins.dlt645.analysis.DLT645V2007Data;
import cn.hutool.core.text.csv.CsvReader;
import cn.hutool.core.text.csv.CsvUtil;
import cn.hutool.core.util.CharsetUtil;
import io.vertx.core.AbstractVerticle;
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
public class DLT645v2007CsvLoader extends AbstractVerticle {

    /**
     * 从CSV文件中装载映射表
     *
     */
    public List<DLT645Data> loadCsvFile() {
        CsvReader csvReader = CsvUtil.getReader();
        InputStreamReader dataReader=new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream("DLT645-2007.csv"),CharsetUtil.CHARSET_GBK);
        List<JDecoderValueParam> rows = csvReader.read(dataReader, JDecoderValueParam.class);
        List<DLT645Data> list = new ArrayList<>();
        for (JDecoderValueParam jDecoderValueParam : rows) {
            try {
                DLT645V2007Data entity = new DLT645V2007Data();
                entity.setName(jDecoderValueParam.getName());
                entity.setDi0((byte) Integer.parseInt(jDecoderValueParam.di0, 16));
                entity.setDi1((byte) Integer.parseInt(jDecoderValueParam.di1, 16));
                entity.setDi2((byte) Integer.parseInt(jDecoderValueParam.di2, 16));
                entity.setDi3((byte) Integer.parseInt(jDecoderValueParam.di3, 16));
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
        private String di0;
        private String di1;
        private String di2;
        private String di3;
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
