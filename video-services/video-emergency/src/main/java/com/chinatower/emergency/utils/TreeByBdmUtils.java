package com.chinatower.emergency.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.chinatower.bdm.api.entity.TreeSelectByBdm;

import java.util.Iterator;
import java.util.List;

public class TreeByBdmUtils {
    private static final String CHILDREN = "children";

    public TreeByBdmUtils() {
        //Do nothing because of constructed function
    }

    public static JSONArray listToTree(List list, String id, String pid, String childrenName) {
        JSONArray arr = JSON.parseArray(JSON.toJSONString(list));
        JSONArray res = new JSONArray();
        JSONObject hash = new JSONObject();
        Iterator var7 = arr.iterator();

        Object anArr;
        JSONObject aVal;
        while(var7.hasNext()) {
            anArr = var7.next();
            aVal = (JSONObject)anArr;
            hash.put(aVal.getString(id), aVal);
        }

        var7 = arr.iterator();

        while(var7.hasNext()) {
            anArr = var7.next();
            aVal = (JSONObject)anArr;
            JSONObject hashVP = (JSONObject)hash.get(aVal.get(pid).toString());
            if (hashVP != null) {
                JSONArray ch;
                if (hashVP.get(childrenName) != null) {
                    ch = (JSONArray)hashVP.get(childrenName);
                    ch.add(aVal);
                    hashVP.put(childrenName, ch);
                } else {
                    ch = new JSONArray();
                    ch.add(aVal);
                    hashVP.put(childrenName, ch);
                }
            } else {
                res.add(aVal);
            }
        }

        return res;
    }

    public static JSONArray listToTree(List list, String id, String pid) {
        JSONArray arr = JSON.parseArray(JSON.toJSONString(list));
        JSONArray res = new JSONArray();
        JSONObject hash = new JSONObject();
        Iterator var6 = arr.iterator();

        Object anArr;
        JSONObject aVal;
        while(var6.hasNext()) {
            anArr = var6.next();
            aVal = (JSONObject)anArr;
            hash.put(aVal.getString(id), aVal);
        }

        var6 = arr.iterator();

        while(var6.hasNext()) {
            anArr = var6.next();
            aVal = (JSONObject)anArr;
            JSONObject hashVP = (JSONObject)hash.get(aVal.get(pid).toString());
            if (hashVP != null) {
                JSONArray ch;
                if (hashVP.get(CHILDREN) != null) {
                    ch = (JSONArray)hashVP.get(CHILDREN);
                    ch.add(aVal);
                    hashVP.put(CHILDREN, ch);
                } else {
                    ch = new JSONArray();
                    ch.add(aVal);
                    hashVP.put(CHILDREN, ch);
                }
            } else {
                res.add(aVal);
            }
        }

        return res;
    }

    public static List<TreeSelectByBdm> listToTreeSelect(List list) {
        JSONArray arr = JSON.parseArray(JSON.toJSONString(list));
        JSONArray res = new JSONArray();
        JSONObject hash = new JSONObject();
        String id = "id";
        String pid = "pid";
        Iterator var6 = arr.iterator();

        Object anArr;
        JSONObject aVal;
        while(var6.hasNext()) {
            anArr = var6.next();
            aVal = (JSONObject)anArr;
            hash.put(aVal.getString(id), aVal);
        }

        var6 = arr.iterator();

        while(var6.hasNext()) {
            anArr = var6.next();
            aVal = (JSONObject)anArr;
            JSONObject hashVP = (JSONObject)hash.get(aVal.get(pid).toString());
            if (hashVP != null) {
                JSONArray ch;
                if (hashVP.get(CHILDREN) != null) {
                    ch = (JSONArray)hashVP.get(CHILDREN);
                    ch.add(aVal);
                    hashVP.put(CHILDREN, ch);
                } else {
                    ch = new JSONArray();
                    ch.add(aVal);
                    hashVP.put(CHILDREN, ch);
                }
            } else {
                res.add(aVal);
            }
        }

        return JSON.parseArray(JSON.toJSONString(res), TreeSelectByBdm.class);
    }
}
