package com.rebate.common.util;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;
import org.codehaus.jackson.type.TypeReference;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * json工具类
 */
@SuppressWarnings({ "unchecked", "deprecation" })
public class JsonUtil {

	private final static Logger LOG = Logger.getLogger(JsonUtil.class);

	private final static ObjectMapper OBJECT_MAPPER = new ObjectMapper(); // 新版 simleFactory 二进制，高性能

	private JsonUtil() {
	}

	static {
		//设置输出包含的属性
		OBJECT_MAPPER.getSerializationConfig().setSerializationInclusion(Inclusion.NON_NULL);
		//设置输入时忽略JSON字符串中存在而Java对象实际没有的属性
		OBJECT_MAPPER.getDeserializationConfig().set(
				org.codehaus.jackson.map.DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		OBJECT_MAPPER.getSerializationConfig().setDateFormat(df);
		OBJECT_MAPPER.getDeserializationConfig().setDateFormat(df);
	}

	/**
	 * 如果JSON字符串为Null或"null"字符串,返回Null.
	 * 如果JSON字符串为"[]",返回空集合.
	 * @param jsonString
	 * @param clazz
	 */
	public static <T> T fromJson(String jsonString, Class<T> clazz) {
		if (StringUtils.isBlank(jsonString)) {
			return null;
		}
		try {
			return (T) OBJECT_MAPPER.readValue(jsonString, clazz);
		} catch (IOException e) {
			LOG.error("parse json string error:" + jsonString, e);
		}
		return null;
	}

	/**
	 * 读取集合如List/Map,且不是List<String>时.
	 * @param jsonString
	 * @param typeReference
	 * @return
	 */
	public static <T> T fromJson(String jsonString, TypeReference<T> typeReference) {
		if (StringUtils.isBlank(jsonString)) {
			return null;
		}
		try {
			return (T) OBJECT_MAPPER.readValue(jsonString, typeReference);
		} catch (IOException e) {
			LOG.error("parse json string error:" + jsonString, e);
		}
		return null;
	}

	/**
	 * 如果对象为Null,返回"null".
	 * 如果集合为空集合,返回"[]".
	 */
	public static String toJson(Object object) {
		try {
			return OBJECT_MAPPER.writeValueAsString(object);
		} catch (IOException e) {
			LOG.warn("write to json string error:" + object, e);
		}
		return null;
	}

	/**
	 * 取出Mapper做进一步的设置或使用其他序列化API.
	 */
	public static ObjectMapper getMapper() {
		return OBJECT_MAPPER;
	}
}