package com.rebate.common.web.result;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 结果集对象
 * @author weishi
 * @time 2014-9-29上午10:45:18
 */
public class Result implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	* 是否成功
	*/
	private boolean success;

	/**
	 * service返回的对象
	 */
	private Map<String, Object> result = new HashMap<String, Object>();

	/**
	 * 当前的key
	 */
	private String modelKey;

	/**
	 * 返回码
	 */
	private String resultCode;
	
	/**
	 * 返回状态码信息
	 */
	private String message;
	
	private String[] resultCodeParams;

	/**
	 * 带是否成功的构造方法
	 * @param success
	 */
	public Result(boolean success) {
		this.success = success;
	}

	/**
	 * 默认构造方法
	 */
	public Result() {
	}

	/**
	 * 新增一个带key的返回结果
	 * @param key
	 * @param obj
	 * @return
	 */
	public Object addDefaultModel(String key, Object obj) {
		modelKey = key;
		return result.put(key, obj);
	}

	/**
	 * 取出所有的key
	 * @return
	 */
	public Set<String> keySet() {
		return result.keySet();
	}

	/**
	 * 取出默认的值
	 * @return
	 */
	public Object get() {
		return result.get(modelKey);
	}

	/**
	 * 取出值
	 * @param key
	 * @return
	 */
	public Object get(String key) {
		return result.get(key);
	}

	/**
	 * 取出值集合
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Collection values() {
		return result.values();
	}

	/**
	 * 返回是否成功
	 * @return
	 */
	public boolean isSuccess() {
		return success;
	}

	/**
	 * 设置返回是否成功
	 * @param success
	 */
	public void setSuccess(boolean success) {
		this.success = success;
	}

	public String getResultCode() {
		return resultCode;
	}

	public void setResultCode(String resultCode) {
		this.resultCode = resultCode;
	}

	public void setResultCode(String resultCode, String... args) {
		this.resultCode = resultCode;
		this.resultCodeParams = args;
	}

	public String[] getResultCodeParams() {
		return resultCodeParams;
	}

	public void setResultCodeParams(String[] resultCodeParams) {
		this.resultCodeParams = resultCodeParams;
	}

	public Map<String, Object> getResult() {
		return result;
	}

	public void setResult(Map<String, Object> result) {
		this.result = result;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}
