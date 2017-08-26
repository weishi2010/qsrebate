package com.rebate.domain.query;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

@JsonIgnoreProperties({"index","pageSize","beginRowNumber","endRowNumber","sort"})
public class BaseQuery implements Serializable{

    private static final long serialVersionUID = -6781700108092396652L;

    private int startRow;

    private int index = 1;

    private int pageSize = 10;

    private int beginRowNumber;

    private int endRowNumber;

    private String sort;

    public int getStartRow() {
        return startRow;
    }

    public void setStartRow(int startRow) {
        this.startRow = startRow;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getBeginRowNumber() {
        return beginRowNumber;
    }

    public void setBeginRowNumber(int beginRowNumber) {
        this.beginRowNumber = beginRowNumber;
    }

    public int getEndRowNumber() {
        return endRowNumber;
    }

    public void setEndRowNumber(int endRowNumber) {
        this.endRowNumber = endRowNumber;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public String getSort() {
        return sort;
    }
}
