package srt.cloud.framework.dbswitch.common.util;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonParser.Feature;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

/**
 * 该类用于提供一些类似的单例的，无状态的对象
 *
 * @author jrl
 */
public class SingletonObject {

	public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

	public static final BigDecimal HUNDRED = new BigDecimal(100);

	static {
		//允许出现特殊字符和转义符
		OBJECT_MAPPER.configure(Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
		OBJECT_MAPPER.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
		OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, true);
		//OBJECT_MAPPER.setSerializationInclusion(Include.NON_ABSENT);
		OBJECT_MAPPER.registerModule(new JavaTimeModule());

		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		simpleDateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
		OBJECT_MAPPER.setDateFormat(simpleDateFormat);
	}

}
