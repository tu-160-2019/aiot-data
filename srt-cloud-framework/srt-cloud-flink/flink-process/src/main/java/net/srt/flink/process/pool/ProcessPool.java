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
import net.srt.flink.common.utils.JSONUtil;
import net.srt.flink.process.model.ProcessEntity;
import net.srt.framework.common.cache.RedisKeys;
import net.srt.framework.security.cache.TokenStoreCache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * ProcessPool
 *
 * @author zrx
 * @since 2022/10/16 17:00
 */
public class ProcessPool extends AbstractPool<ProcessEntity> {

	//private static final Map<String, ProcessEntity> processEntityMap = new ConcurrentHashMap<>();

	private static final ProcessPool instance = new ProcessPool();

	public static ProcessPool getInstance() {
		return instance;
	}

	@Override
	public Map<String, ProcessEntity> getMap() {
		return null;
	}

	public boolean exist(String key) {
		TokenStoreCache tokenStoreCache = SpringContextUtils.getBeanByClass(TokenStoreCache.class);
		return tokenStoreCache.containsKey(RedisKeys.getProcessKey(key));
		//return getMap().containsKey(key);
	}

	public int push(String key, ProcessEntity entity) {
		TokenStoreCache tokenStoreCache = SpringContextUtils.getBeanByClass(TokenStoreCache.class);
		tokenStoreCache.saveProcess(key, JSONUtil.toJsonString(entity));
		return 0;
		/*getMap().put(key, entity);
		return getMap().size();*/
	}

	public int remove(String key) {
		TokenStoreCache tokenStoreCache = SpringContextUtils.getBeanByClass(TokenStoreCache.class);
		tokenStoreCache.deleteProcess(key);
		return 0;
		/*getMap().remove(key);
		return getMap().size();*/
	}

	public ProcessEntity get(String key) {
		TokenStoreCache tokenStoreCache = SpringContextUtils.getBeanByClass(TokenStoreCache.class);
		return JSONUtil.parseObject(tokenStoreCache.getProcess(key), ProcessEntity.class);
		//return getMap().get(key);
	}

	/*public static void clear() {
		processEntityMap.clear();
	}*/

	@Override
	public void refresh(ProcessEntity entity) {

	}
}
