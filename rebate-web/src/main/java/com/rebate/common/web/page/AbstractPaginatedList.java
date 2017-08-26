package com.rebate.common.web.page;

/**
 * 分页抽象类
 * @author weishi
 * @version 1.0.0
 */
/*
 * =========================== 维护日志 ===========================
 * 2017/3/29 12:53  weishi 新建代码
 * =========================== 维护日志 ===========================
 */

import java.util.ArrayList;


public abstract class AbstractPaginatedList<T> extends ArrayList<T> implements PaginatedList<T> {
    public static final int PAGESIZE_DEFAULT = 20;
    protected int pageSize;
    protected int index;
    protected int totalItem;
    protected int totalPage;
    protected int startRow;
    protected int endRow;

    protected AbstractPaginatedList() {
        this.repaginate();
    }

    protected AbstractPaginatedList(int index, int pageSize) {
        this.index = index;
        this.pageSize = pageSize;
        this.repaginate();
    }

    public boolean isFirstPage() {
        return this.index <= 1;
    }

    public boolean isMiddlePage() {
        return !this.isFirstPage() && !this.isLastPage();
    }

    public boolean isLastPage() {
        return this.index >= this.totalPage;
    }

    public boolean isNextPageAvailable() {
        return !this.isLastPage();
    }

    public boolean isPreviousPageAvailable() {
        return !this.isFirstPage();
    }

    public int getNextPage() {
        return this.isLastPage()?this.totalItem:this.index + 1;
    }

    public int getPreviousPage() {
        return this.isFirstPage()?1:this.index - 1;
    }

    public int getPageSize() {
        return this.pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
        this.repaginate();
    }

    public int getIndex() {
        return this.index;
    }

    public void setIndex(int index) {
        this.index = index;
        this.repaginate();
    }

    public int getTotalItem() {
        return this.totalItem;
    }

    public void setTotalItem(int totalItem) {
        this.totalItem = totalItem;
        this.repaginate();
    }

    public int getTotalPage() {
        return this.totalPage;
    }

    public int getStartRow() {
        return this.startRow;
    }

    public int getEndRow() {
        return this.endRow;
    }

    protected abstract void repaginate();
}
