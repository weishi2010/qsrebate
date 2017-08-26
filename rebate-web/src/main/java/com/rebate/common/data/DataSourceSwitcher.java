package com.rebate.common.data;

import org.springframework.util.Assert;

import java.util.Random;

public class DataSourceSwitcher {

	// 读库数量
	public static final String READ = "slave";

	public static final Random RANDOM = new Random(17);

	private static final ThreadLocal CONTEX_THOLDER = new ThreadLocal();

	public static void setDataSource(String dataSource) {
		Assert.notNull(dataSource, "dataSource cannot be null");
		CONTEX_THOLDER.set(dataSource);
	}

	/**
	 * 设置写库.
	 */
	public static void setMaster() {
		clearDataSource();
	}

	/**
	 * 设置主从库.
	 */
	public static void setSlave() {
		setDataSource(READ + "1");
	}

	/**
	 * 设置从库.
	 * @param slaveIndex
	 */
	public static void setSlave(int slaveIndex) {
		setDataSource(READ + slaveIndex);
	}

	/**
	 * 随机取读库.
	 *
	 */
	public static void setEitherSlave() {
		setDataSource(READ);
	}

	public static String getDataSource() {
		return (String) CONTEX_THOLDER.get();
	}

	public static void clearDataSource() {
		CONTEX_THOLDER.remove();
	}

}
