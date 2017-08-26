package com.rebate.common.dao;

import com.rebate.common.data.DataSourceSwitcher;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.orm.ibatis.SqlMapClientTemplate;

import java.util.List;


public class BaseDao extends SqlMapClientTemplate {
	protected final static Logger log = LogManager.getLogger(BaseDao.class);

	@Override
	public Object insert(String statementName, Object parameterObject) throws DataAccessException {
		//可在此根据配置选择数据源,修改语句分表.(需要解析sql语句并进行替换)
		
		return super.insert(statementName, parameterObject);
	}
	@Override
	public int delete(String statementName, Object parameterObject) throws DataAccessException {
		
		return super.delete(statementName, parameterObject);
	}
	@Override
	public int update(String statementName, Object parameterObject) throws DataAccessException {
		DataSourceSwitcher.setMaster();
		return super.update(statementName, parameterObject);
	}
	@Override
	public Object insert(String statementName) throws DataAccessException {
		return super.insert(statementName);
	}
	@Override
	public int delete(String statementName) throws DataAccessException {
		return super.delete(statementName);
	}
	@Override
	public int update(String statementName) throws DataAccessException {
		return super.update(statementName);
	}
	@Override
	public List queryForList(String statementName) throws DataAccessException {
		return super.queryForList(statementName);
	}
	@Override
	public List queryForList(String statementName, Object parameterObject) throws DataAccessException {
		return super.queryForList(statementName, parameterObject);
	}
	@Override
	public Object queryForObject(String statementName) throws DataAccessException {
		return super.queryForObject(statementName);
	}
	@Override
	public Object queryForObject(String statementName, Object parameterObject) throws DataAccessException {
		return super.queryForObject(statementName, parameterObject);
	}
}
