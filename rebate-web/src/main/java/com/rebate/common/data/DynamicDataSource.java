package com.rebate.common.data;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import java.util.Map;

/**
 * 动态数据源.
 * @author zhoumin@360buy.com
 * 2011-9-13 下午6:12:18
 */
@SuppressWarnings( { "unused", "unchecked" })
public class DynamicDataSource extends AbstractRoutingDataSource {
	private int readCount = 1;
	private void setCustomDataSources(Map customDataSources) {

	}

	@Override
	public void setTargetDataSources(Map targetDataSources) {
		super.setTargetDataSources(targetDataSources);
		readCount = targetDataSources.size();
	}

	@Override
	protected Object determineCurrentLookupKey() {
		
		String lookupKey = DataSourceSwitcher.getDataSource();
		if(DataSourceSwitcher.READ.equals(lookupKey)){
			lookupKey = DataSourceSwitcher.READ + (DataSourceSwitcher.RANDOM.nextInt(readCount) + 1);
		}
		return lookupKey;
	}

}
