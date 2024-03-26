package net.srt.framework.common.utils;

import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * @ClassName BeanUtil
 * @author jrl
 * @Date 2022/3/21 14:47
 */
public class BeanUtil {

	/**
	 * 普通对象复制
	 *
	 * @param source
	 * @param target
	 */
	public static void copyProperties(Object source, Object target) {
		BeanUtils.copyProperties(source, target);
	}

	/**
	 * 普通对象复制
	 *
	 * @param source
	 * @param target
	 */
	public static <T> T copyProperties(Object source, Supplier<T> target) {
		if (source == null) {
			return null;
		}
		T t = target.get();
		BeanUtils.copyProperties(source, t);
		return t;
	}

	/**
	 * 集合数据的拷贝
	 *
	 * @param sources: 数据源类
	 * @param target:  目标类::new(eg: UserVO::new)
	 * @return
	 */
	public static <S, T> List<T> copyListProperties(List<S> sources, Supplier<T> target) {
		return copyListProperties(sources, target, null);
	}


	/**
	 * 带回调函数的集合数据的拷贝（可自定义字段拷贝规则）
	 *
	 * @param sources:  数据源类
	 * @param target:   目标类::new(eg: UserVO::new)
	 * @param callBack: 回调函数
	 * @return
	 */
	public static <S, T> List<T> copyListProperties(List<S> sources, Supplier<T> target, BeanCopyUtilCallBack<S, T> callBack) {
		List<T> list = new ArrayList<>(sources.size());
		for (S source : sources) {
			T t = target.get();
			copyProperties(source, t);
			list.add(t);
			if (callBack != null) {
				// 回调
				callBack.callBack(source, t);
			}
		}
		return list;
	}
}
