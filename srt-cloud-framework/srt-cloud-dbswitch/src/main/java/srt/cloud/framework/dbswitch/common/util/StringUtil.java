package srt.cloud.framework.dbswitch.common.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.util.Base64;
import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Pattern;

/**
 * 字符串工具类
 *
 * @author jrl
 */
public class StringUtil {

	public static final String NOT_BLANK = "^[\\s\\S]*.*[^\\s][\\s\\S]*$";

	public static final String REGEX_NUMBER = "^\\d+$";

	public static final String REGEX_DECIMAL = "^-?\\d?\\.?\\d+$";

	public static final String REGEX_EMAIL = "^([a-zA-Z0-9_-])+@([a-zA-Z0-9_-])+((\\.[a-zA-Z0-9_-]{2,3}){1,2})$";

	public static final String REGEX_PHONE = "^(\\d{11})|^((\\d{7,8})|(\\d{4}|\\d{3})-(\\d{7,8})|(\\d{4}|\\d{3})-(\\d{7,8})-(\\d{4}|\\d{3}|\\d{2}|\\d{1})|(\\d{7,8})-(\\d{4}|\\d{3}|\\d{2}|\\d{1}))$";

	public static final String REGEX_IDCARD = "(\\d{14}[0-9a-zA-Z])|(\\d{17}[0-9a-zA-Z])";

	public static final String DATE_FORAMT = "^((\\d{2}(([02468][048])|([13579][26]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])))))|(\\d{2}(([02468][1235679])|([13579][01345789]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|(1[0-9])|(2[0-8]))))))(\\s(((0?[0-9])|([1][0-9])|([2][0-4]))\\:([0-5]?[0-9])((\\s)|(\\:([0-5]?[0-9])))))?$";

	public static final String COMMA = ",";

	public static final String BLANK = " ";

	public static final String URI_PATH_SEPERATOR = "/";

	public static final String UNDERLINE = "_";

	public static boolean equal(String str1, String str2) {
		if (str1 == str2) {
			return true;
		}

		if (null == str1 || null == str2) {
			return false;
		}

		return str1.equals(str2);
	}

	public static boolean isNumber(String number) {
		return match(number, REGEX_NUMBER);
	}

	public static boolean isDecimal(String decimal) {
		return match(decimal, REGEX_DECIMAL);
	}

	public static boolean isEmail(String email) {
		return match(email, REGEX_EMAIL);
	}

	public static boolean isPhone(String phone) {
		return match(phone, REGEX_PHONE);
	}

	public static boolean isIdCard(String idCard) {
		return match(idCard, REGEX_IDCARD);
	}

	public static boolean match(String string, String regex) {
		return string != null ? Pattern.compile(regex).matcher(string).find() : false;
	}

	public static String getRandom(int len) {
		ThreadLocalRandom random = ThreadLocalRandom.current();
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < len; i++) {
			builder.append(random.nextInt(10));
		}

		return builder.toString();
	}

	public static String getRandom2(int len) {
		String source = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
		ThreadLocalRandom random = ThreadLocalRandom.current();
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < len; i++) {
			builder.append(source.charAt(random.nextInt(62)));
		}

		return builder.toString();
	}

	public static String convertToCommaSplitString(Collection<String> strs) {
		if (strs == null || strs.isEmpty()) {
			return null;
		}

		Iterator<String> iterator = strs.iterator();
		StringBuilder stringBuilder = new StringBuilder();
		while (iterator.hasNext()) {
			stringBuilder.append(iterator.next());
			if (iterator.hasNext()) {
				stringBuilder.append(",");
			}
		}
		return stringBuilder.toString();
	}

	public static boolean isJson(String value) {
		if (isBlank(value)) {
			throw new NullPointerException("StringUtil.isJson(null)");
		}

		boolean valid = false;
		try {
			JsonParser jsonParser = SingletonObject.OBJECT_MAPPER.getFactory().createParser(value);
			while (jsonParser.nextToken() != null) {
			}
			valid = true;
		} catch (Exception e) {
		}

		return valid;
	}

	public static String toJson(Object value) {
		if (null == value) {
			throw new NullPointerException("StringUtil.toJson(null)");
		}
		try {
			return SingletonObject.OBJECT_MAPPER.writeValueAsString(value);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
	}

	public static <T> T fromJson(String value, Class<T> clazz) {
		if (isBlank(value)) {
			throw new NullPointerException("StringUtil.fromJson(null, clazz)");
		}

		try {
			return SingletonObject.OBJECT_MAPPER.readValue(value, clazz);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static <T> T fromJson(String value, TypeReference<T> clazz) {
		if (isBlank(value)) {
			throw new NullPointerException("StringUtil.fromJson(null, clazz)");
		}

		try {
			return SingletonObject.OBJECT_MAPPER.readValue(value, clazz);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static String getDefaultNullString(String value) {
		if (null == value || value.length() == 0) {
			return null;
		}

		return value;
	}

	public static String replaceURLParameter(String url, String parameterName, String value) {
		if (StringUtils.isBlank(url) || StringUtils.isBlank(parameterName)) {
			return url;
		}

		String numberSign = "#";
		String ampersand = "&";
		String questionMark = "?";
		String equalsSign = "=";
		if (-1 != url.indexOf(numberSign)) {
			url = url.substring(0, url.indexOf(numberSign));
		}
		url = removeMatchCharOnTail(url, ampersand);
		url = removeMatchCharOnTail(url, questionMark);

		if (StringUtils.isBlank(url) || StringUtils.isBlank(parameterName)) {
			return url;
		}

		if (-1 == url.indexOf(questionMark)) {
			return url + questionMark + parameterName + equalsSign + value;
		}

		int beginIndex = url.indexOf(parameterName + equalsSign);
		if (-1 != beginIndex) {
			int nextParameterIndex = url.indexOf(ampersand, beginIndex);
			if (-1 == nextParameterIndex) {
				url = url.substring(0, beginIndex - 1);
			} else {
				url = url.substring(0, beginIndex) + url.substring(nextParameterIndex + 1);
			}
		}

		if (-1 == url.indexOf(questionMark)) {
			return url + questionMark + parameterName + equalsSign + value;
		}
		return url + ampersand + parameterName + equalsSign + value;
	}

	public static String removeMatchCharOnTail(String string, String ch) {
		int chLen = ch.length();
		if (string.lastIndexOf(ch) == string.length() - chLen) {
			string = string.substring(0, string.length() - chLen);
			string = removeMatchCharOnTail(string, ch);
		}

		return string;
	}


	public static String ltrim(String raw) {
		if (StringUtils.isBlank(raw)) {
			return raw;
		}

		if (raw.startsWith(BLANK)) {
			return ltrim(raw.substring(1));
		}

		return raw;
	}

	public static String rtrim(String raw) {
		if (StringUtils.isBlank(raw)) {
			return raw;
		}

		if (raw.endsWith(BLANK)) {
			return rtrim(raw.substring(0, raw.length() - 1));
		}

		return raw;
	}

	public static String trim(String raw) {
		if (StringUtils.isBlank(raw)) {
			return raw;
		}

		return rtrim(ltrim(raw));
	}

	public static boolean isBlank(String value) {
		return null == value || value.trim().length() == 0;
	}

	public static boolean isNotBlank(String value) {
		return null != value && value.trim().length() > 0;
	}

	public static String getYuanFromFen(Integer fen) {
		if (null == fen) {
			return "0.00";
		}

		if (fen < 0) {
			return "-" + getYuanFromFen(0 - fen);
		}

		int ten = 10;
		int hundred = 100;
		if (fen < ten && fen >= 0) {
			return "0.0" + fen;
		}
		if (fen < hundred && fen >= ten) {
			return "0." + fen;
		}

		String temp = String.valueOf(fen);
		int len = temp.length() - 2;
		return temp.substring(0, len) + "." + temp.substring(len);
	}

	public static String getYuanFromFen(Long fen) {
		if (null == fen) {
			return "0.00";
		}

		if (fen < 0) {
			return "-" + getYuanFromFen(0 - fen);
		}

		int ten = 10;
		int hundred = 100;
		if (fen < ten && fen >= 0) {
			return "0.0" + fen;
		}
		if (fen < hundred && fen >= ten) {
			return "0." + fen;
		}

		String temp = String.valueOf(fen);
		int len = temp.length() - 2;
		return temp.substring(0, len) + "." + temp.substring(len);
	}

	public static long getFenFromYuan(String yuan) {
		if (StringUtils.isBlank(yuan)) {
			return 0L;
		}

		return getFenFromYuan(new BigDecimal(yuan));
	}

	public static long getFenFromYuan(BigDecimal yuan) {
		if (null == yuan) {
			return 0L;
		}
		if (yuan.compareTo(BigDecimal.ZERO) == 0) {
			return 0L;
		}

		if (yuan.compareTo(BigDecimal.ZERO) < 0) {
			return -getFenFromYuan(yuan.multiply(new BigDecimal(-1)));
		}

		return yuan.multiply(new BigDecimal(100)).longValue();
	}

	public static String join(String[] sources, String sep) {
		if (null == sources || sources.length == 0) {
			return "";
		}
		StringBuilder builder = new StringBuilder();
		builder.append(sources[0]);
		for (int i = 1; i < sources.length; i++) {
			builder.append(sep).append(sources[i]);
		}
		return builder.toString();
	}

	public static byte[] decoder2Byte(String img) {
		Base64.Decoder decoder = Base64.getDecoder();
		return decoder.decode(img);
	}
}
