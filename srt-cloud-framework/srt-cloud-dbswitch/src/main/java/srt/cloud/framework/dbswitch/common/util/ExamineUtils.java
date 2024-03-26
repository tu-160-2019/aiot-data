// Copyright tang.  All rights reserved.
// https://gitee.com/inrgihc/dbswitch
//
// Use of this source code is governed by a BSD-style license
//
// Author: tang (inrgihc@126.com)
// Date : 2020/1/2
// Location: beijing , china
/////////////////////////////////////////////////////////////
package srt.cloud.framework.dbswitch.common.util;

import com.google.common.base.Preconditions;

import java.util.Collection;

public final class ExamineUtils {

	public static void checkNotNull(Object object, String elem) {
		Preconditions.checkNotNull(object, "The '%s' can't be null", elem);
	}

	public static void checkNotNull(Object object, String elem, String owner) {
		Preconditions.checkNotNull(object,
				"The '%s' of '%s' can't be null",
				elem, owner);
	}

	public static void checkNotEmpty(Collection<?> collection, String elem) {
		Preconditions.checkArgument(!collection.isEmpty(),
				"The '%s' can't be empty", elem);
	}

	public static void checkNotEmpty(Collection<?> collection, String elem, String owner) {
		Preconditions.checkArgument(!collection.isEmpty(),
				"The '%s' of '%s' can't be empty",
				elem, owner);
	}

	public static void checkArgument(boolean expression,
									 String message,
									 Object... args) {
		Preconditions.checkArgument(expression, message, args);
	}

	public static void checkArgumentNotNull(Object object,
											String message,
											Object... args) {
		Preconditions.checkArgument(object != null, message, args);
	}

	public static void checkState(boolean expression,
								  String message,
								  Object... args) {
		Preconditions.checkState(expression, message, args);
	}

	public static void check(boolean expression, String message, Object... args) {
		if (!expression) {
			throw new RuntimeException(String.format(message, args));
		}
	}

}
