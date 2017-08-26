package com.rebate.common.web.page;

public class PaginatedArrayList<T> extends AbstractPaginatedList<T> {

    /**
     * 默认构造方法
     */
    public PaginatedArrayList() {
        super();
    }

    /**
     * 带当前页和页大小的构造方法
     *
     * @param index    当前页
     * @param pageSize 页大小
     */
    public PaginatedArrayList(int index, int pageSize) {
        super(index, pageSize);
    }

    @Override
    protected void repaginate() {
        if (pageSize < 1) { //防止程序偷懒，list和分页的混合使用
            pageSize = PAGESIZE_DEFAULT;
        }
        if (index < 1) {
            index = 1;//恢复到第一页
        }
        if (totalItem > 0) {
            totalPage = totalItem / pageSize + (totalItem % pageSize > 0 ? 1 : 0);
            if (index > totalPage) {
                index = totalPage; //最大页
            }

            endRow = index * pageSize;
            startRow = endRow - pageSize;
            endRow = pageSize;

            if (endRow > totalItem) {
                endRow = totalItem;
            }
        }
    }

    @Override
    public int getNextPage() {
        if (index >= totalPage) {
            return totalPage;
        } else {
            return index++;
        }
    }
}
