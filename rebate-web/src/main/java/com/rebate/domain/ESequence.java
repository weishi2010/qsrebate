package com.rebate.domain;

/**
 * 自增序列枚举
 */
public enum ESequence {

	/**
	 * 子联盟ID生成序列
	 */
	SUB_UNION_ID("sub_union_id");

	private String sequenceName;

	ESequence(String sequenceName) {
		this.sequenceName = sequenceName;
	}

	public String getSequenceName() {
		return sequenceName;
	}
}
