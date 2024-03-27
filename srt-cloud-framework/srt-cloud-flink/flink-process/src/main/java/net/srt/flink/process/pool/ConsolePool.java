/*
 *
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package net.srt.flink.process.pool;


import net.srt.flink.common.context.SpringContextUtils;
import net.srt.flink.common.pool.AbstractPool;
import net.srt.framework.common.cache.RedisKeys;
import net.srt.framework.security.cache.TokenStoreCache;

import java.util.Map;

/**
 * ConsolePool
 *
 * @author zrx
 * @since 2022/10/18 22:51
 */
public class ConsolePool extends AbstractPool<StringBuilder> {

	//private static final Map<String, StringBuilder> consoleEntityMap = new ConcurrentHashMap<>();

	private static final ConsolePool instance = new ConsolePool();

	public static ConsolePool getInstance() {
		return instance;
	}

	@Override
	public boolean exist(String key) {
		TokenStoreCache tokenStoreCache = SpringContextUtils.getBeanByClass(TokenStoreCache.class);
		return tokenStoreCache.containsKey(RedisKeys.getConsoleLogKey(key));
	}

	@Override
	public int push(String key, StringBuilder entity) {
		TokenStoreCache tokenStoreCache = SpringContextUtils.getBeanByClass(TokenStoreCache.class);
		tokenStoreCache.saveCconsoleLog(key, entity.toString());
		return 0;
	}

	@Override
	public int remove(String key) {
		TokenStoreCache tokenStoreCache = SpringContextUtils.getBeanByClass(TokenStoreCache.class);
		tokenStoreCache.deleteConsoleLog(key);
		return 0;
	}

	@Override
	public StringBuilder get(String key) {
		TokenStoreCache tokenStoreCache = SpringContextUtils.getBeanByClass(TokenStoreCache.class);
		return new StringBuilder(tokenStoreCache.getConsoleLog(key));
	}

	@Override
	public Map<String, StringBuilder> getMap() {
		return null;
	}

	@Override
	public void refresh(StringBuilder entity) {

	}

	public static void write(String str, Integer userId) {
		String user = String.valueOf(userId);
		TokenStoreCache tokenStoreCache = SpringContextUtils.getBeanByClass(TokenStoreCache.class);
		String consoleLog = tokenStoreCache.getConsoleLog(user);
		tokenStoreCache.saveCconsoleLog(user, consoleLog == null ? "Console log:" + str : consoleLog + str);
	}

	public static void write(String str, String accessToken) {
		TokenStoreCache tokenStoreCache = SpringContextUtils.getBeanByClass(TokenStoreCache.class);
		String consoleLog = tokenStoreCache.getConsoleLog(accessToken);
		tokenStoreCache.saveCconsoleLog(accessToken, consoleLog == null ? "Console log:" + str : consoleLog + str);
		//consoleEntityMap.computeIfAbsent(accessToken, k -> new StringBuilder("Console log:")).append(str);
	}

	/*public static void clear() {
		consoleEntityMap.clear();
	}*/
}
