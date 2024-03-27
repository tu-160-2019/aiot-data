package net.srt.flink.common.utils;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName ThreadUtil
 * @Author zrx
 * @Date 2023/1/9 16:51
 */
public class ThreadUtil {


	public static ThreadPoolExecutor threadPool = new ThreadPoolExecutor(
			10,
			100,
			60,
			TimeUnit.SECONDS, new ArrayBlockingQueue<>(10)
			, new ThreadPoolExecutor.AbortPolicy()
	);

	public static void sleep(Integer sleepMills) {
		try {
			Thread.sleep(sleepMills);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
