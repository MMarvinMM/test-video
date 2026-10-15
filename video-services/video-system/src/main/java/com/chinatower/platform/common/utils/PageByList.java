package com.chinatower.platform.common.utils;   /**
 * @title: PageByList
 * @projectName video-platform
 * @功能说明：XXXXXX
 * @author 王梦迪
 * @date 2022/5/512:58
 * @requestID 需求单号及需求名称
 */

import java.util.ArrayList;
import java.util.List;

/**
 *
 *@className: PageByList
 *@description: TODO
 *@author: wmd
 *@time: 2022/5/5 12:58
 */
public class PageByList {

    /**
     * 功能说明:对list结果进行分页
     * @param:
     * @return:
     * @author: 王梦迪
     * @date: 2022/5/5 13:16
     * @requestID  需求单号及需求名称
     */
    public static List startPage(List list, Integer pageNum,Integer pageSize){
        if(list ==null){
            return null;
        }
        if(list.size()==0){
            return null;
        }
        //记录总数
        Integer count = list.size();
        //页数
        Integer pageCount =0;
        if(count % pageSize ==0){
            pageCount = count / pageSize;
        }else{
            pageCount = count / pageSize +1;
        }
        //开始索引
        int fromIndex =0;
        //结束索引
        int toIndex =0;
        if(!pageNum.equals(pageCount)){
            fromIndex =(pageNum -1)* pageSize;
            toIndex = fromIndex + pageSize;
        }else{
            fromIndex =(pageNum -1)* pageSize;
            toIndex = count;
        }
        if(fromIndex >= list.size()){
            return new ArrayList();
        }
        if(toIndex >= list.size()){
            return list.subList(fromIndex, list.size());
        }
        List pageList = list.subList(fromIndex, toIndex);
        return pageList;
    }
}
