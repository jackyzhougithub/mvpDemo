package com.z.jk.linkagelistview.DialogFragment;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

/**
 * Created by $ zhoudeheng on 2016/3/3.
 * Email zhoudeheng@qccr.com
 */
public class  JsonUtil {
    private static Gson gson = null;

    static {
        if (gson == null) {
            gson = new Gson();
        }
    }

    /**
     * 将json转换成bean对象
     *
     * @param jsonStr jsonString
     * @return obj
     */
    @SuppressWarnings("unchecked")
    public static <T> T jsonToBean(String jsonStr, Class<?> cl) {
        T t = null;
        if (gson != null) {
            try {
                t = (T) gson.fromJson(jsonStr, cl);
            }catch (JsonSyntaxException e) {
                Log.e("json转换异常-->", e.toString());
            }catch (JsonParseException e) {
                Log.e("数据不是json格式-->", e.toString());
            }

        }
        return t;
    }

    /**
     * 对象转换成json字符串
     *
     * @param obj obj
     * @return jsonString
     */
    public static String objectToJson(Object obj) {
        String jsonStr = null;
        if (gson != null) {
            jsonStr = gson.toJson(obj);
        }
        return jsonStr;
    }

    /**
     * 判断是否是json
     */
    public static boolean isGoodJson(String json) {
        try {
            new JsonParser().parse(json);
            return true;
        } catch (JsonParseException e) {
            return false;
        }
    }
}
