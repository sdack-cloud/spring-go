package cn.sdack.go.auth.utils;


import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;

import java.io.Serializable;
import java.util.List;

/**
 * User: sdake
 * Date: 2019/2/1
 */
public class JpaPageUtils<T> implements Serializable {

    //    public HttpServletRequest httpServletRequest;
//    //是否简单分页
//    public boolean simple = false;
//
//    public StringBuffer stringBuffer = new StringBuffer();
//    public StringBuffer pageStr = new StringBuffer();
//    public StringBuffer html = new StringBuffer();
//    public StringBuffer html2 = new StringBuffer();
//    public StringBuffer replace = new StringBuffer(); //替换字符串
//
    protected static Logger logger = LoggerFactory.getLogger(JpaPageUtils.class);

    //当前页
    public int page;
    //每页的数量
    public int pageSize;
    //当前页的数量
    public int contentSize;
    //总记录数
    public long totalSize;
    //总页数
    public int totalPages;
    //结果集
    public List<T> listData;

    //    //前一页
//    public int prePage;
//    //下一页
//    public int nextPage;
//
    //是否为第一页
    public boolean isFirstPage = false;
    //是否为最后一页
    public boolean isLastPage = false;
    //是否有前一页
    public boolean hasPreviousPage = false;
    //是否有下一页
    public boolean hasNextPage = false;

    public static JpaPageUtils getInstance() {
        return Holder.INSTANCE;
    }

    public static class Holder {
        public static final JpaPageUtils INSTANCE = new JpaPageUtils();
    }

    public JpaPageUtils<T> setData(Page<T> pageData) {
        this.page = pageData.getNumber() + 1;//当前页
        this.pageSize = pageData.getNumberOfElements(); //每页的数量
        this.contentSize = pageData.getNumberOfElements();//当前页的数量
        this.totalSize = pageData.getTotalElements();//总记录数
        this.totalPages = pageData.getTotalPages();//总页数
        this.listData = pageData.getContent();//结果集
        this.isFirstPage = (1 == (pageData.getNumber() + 1));//是否为第一页
        this.isLastPage = pageData.hasNext();//是否为最后一页
        this.hasPreviousPage = pageData.hasPrevious();//是否有前一页
        this.hasNextPage = (pageData.getTotalPages() == (pageData.getNumber() + 1));//是否有下一页
        logger.info("JpaPageUtils单例---" + this);
        return this;
    }


    public String simplePage(HttpServletRequest httpServletRequest, StringBuffer pageBuffer) {
        if (totalSize != 0) {
//            StringBuffer pageBuffer = new StringBuffer();  // 不清楚 使用一个分页Buffer 会不会产生数据污染
            pageBuffer.delete(0, (pageBuffer.length()));
//            StringBuffer paramBuffer = new StringBuffer();

            String requestURI = httpServletRequest.getRequestURI();
            String queryString = httpServletRequest.getQueryString();
            /****   处理参数  code=&name=&isStock=1&isLock=&way=&supplierType.id=&shopId=&page=2&page=1 */
            if (queryString != null) { // 处理参数
                int pageIndex = queryString.indexOf("&page=");
                if (pageIndex != -1) { //说明有 page 值,过滤掉page值
                    int pageNumIndex = queryString.indexOf("&", pageIndex + 1);
                    if (pageNumIndex == -1) { // 当他后面没参数了
                        //说明page 在最后
                        queryString = queryString.substring(0, pageIndex);
                    } else {//说明page 在中间
                        String haedStr = queryString.substring(0, pageIndex);
                        String footStr = queryString.substring(pageNumIndex);
                        queryString = haedStr+footStr;
                    }
                }
            }

            pageBuffer.append("<nav aria-label=\"...\" ><ul class=\"pagination\">\n");
            /*** 上一页 拼接 ****/
            if (isFirstPage) { // 上一页  禁用
                pageBuffer.append("<li class=\"page-item disabled\" ><a class=\"page-link\" href=\"#\" aria-label=\"Previous\"><span aria-hidden=\"true\">&laquo;</span><span class=\"sr-only\">Previous</span></a></li>\n");
            } else { // 上一页  启用
                pageBuffer.append("<li class=\"page-item \" ><a class=\"page-link\" href=\"")
                        .append(requestURI).append("?").append(queryString != null ? queryString : "").append("&page=").append((page - 1))
                        .append("\" aria-label=\"Previous\"><span aria-hidden=\"true\">&laquo;</span><span class=\"sr-only\">Previous</span></a></li>\n");
            }

            /*** 页码 拼接 ***/
            if (totalPages < 10) {  // 页码少全显示
                for (int i = 1; i <= totalPages; i++) {
                    pageNumber(pageBuffer, requestURI, queryString, i);
                }
            } else { // 页码多，忽略显示
                for (int i = 1; i <= 5; i++) {
                    pageNumber(pageBuffer, requestURI, queryString, i);
                }
                // 省略号页码
                pageBuffer.append("<li class=\"page-item disabled\"><a class=\"page-link\" href=\"#\" aria-label=\"Next\"><span aria-hidden=\"true\">&sdot;&sdot;&sdot;</span><span class=\"sr-only\">...</span></a></li>\n");
                // 省略号页码
                for (int i = (totalPages-5); i <= (totalPages); i++) {
                    pageNumber(pageBuffer,  requestURI, queryString, i);
                }
            }

            /*** 下一页 拼接 ***/
            if (!isLastPage) { //下一页  禁用
                pageBuffer.append("<li class=\"page-item disabled\"><a class=\"page-link\" href=\"#\" aria-label=\"Next\"><span aria-hidden=\"true\">&raquo;</span><span class=\"sr-only\">Next</span></a></li>\n");
            } else {// 下一页  启用
                pageBuffer.append("<li class=\"page-item\"><a class=\"page-link\" href=\"")
                        .append(requestURI).append("?").append(queryString != null ? queryString : "").append("&page=").append((page + 1)).
                        append("\" aria-label=\"Next\"><span aria-hidden=\"true\">&raquo;</span><span class=\"sr-only\">Next</span></a></li>\n");
            }
            pageBuffer.append("</ul></nav>");
//            paramBuffer = null;
        }
        return pageBuffer.toString();
    }

    /**
     *
     * @param pageBuffer
     * @param requestURI
     * @param queryString
     * @param i
     */
    private void pageNumber(StringBuffer pageBuffer, String requestURI, String queryString, int i) {
        if (i == page) {
            pageBuffer.append("<li class=\"page-item active\"> <span class=\"page-link\">").append(i).append(" </span></li>\n");
        } else {
            pageBuffer.append("<li class=\"page-item\"> <a class=\"page-link\" href=\"")
                    .append(requestURI).append("?").append(queryString != null ? queryString : "").append("&page=").append(i).append("\">").append(i)
                    .append("</a></li>\n");
        }
    }


    public int getPageNum() {
        return page;
    }

    public void setPageNum(int pageNum) {
        this.page = pageNum;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getContentSize() {
        return contentSize;
    }

    public void setContentSize(int contentSize) {
        this.contentSize = contentSize;
    }

    public long getTotalSize() {
        return totalSize;
    }

    public void setTotalSize(long totalSize) {
        this.totalSize = totalSize;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public List<T> getListData() {
        return listData;
    }

    public void setListData(List<T> listData) {
        this.listData = listData;
    }

    public boolean isFirstPage() {
        return isFirstPage;
    }

    public void setFirstPage(boolean firstPage) {
        isFirstPage = firstPage;
    }

    public boolean isLastPage() {
        return isLastPage;
    }

    public void setLastPage(boolean lastPage) {
        isLastPage = lastPage;
    }

    public boolean isHasPreviousPage() {
        return hasPreviousPage;
    }

    public void setHasPreviousPage(boolean hasPreviousPage) {
        this.hasPreviousPage = hasPreviousPage;
    }

    public boolean isHasNextPage() {
        return hasNextPage;
    }

    public void setHasNextPage(boolean hasNextPage) {
        this.hasNextPage = hasNextPage;
    }
}
