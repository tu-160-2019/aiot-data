package net.srt.framework.common.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @ClassName SqlUtils
 * @Author zrx
 * @Date 2022/11/12 15:20
 */
public class SqlUtils {
	public static Map<String, String> convertColumns(List<String> columns) {
		if (null == columns || columns.isEmpty()) {
			return new HashMap<>();
		}
		Map<String, String> result = new LinkedHashMap<>(6);
		for (String column : columns) {
			result.put(column, column);
		}
		return result;
	}

	public static List<Map<String, Object>> convertRows(List<String> columns, List<List<Object>> rows) {
		if (null == rows || rows.isEmpty()) {
			return Collections.emptyList();
		}
		List<Map<String, Object>> result = new ArrayList<>(rows.size());
		for (List<Object> row : rows) {
			Map<String, Object> map = new HashMap<>();
			for (int i = 0; i < row.size(); ++i) {
				map.put(columns.get(i), row.get(i));
			}
			result.add(map);
		}
		return result;
	}

}
