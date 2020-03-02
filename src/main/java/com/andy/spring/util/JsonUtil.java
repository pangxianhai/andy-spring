package com.andy.spring.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.util.ParameterizedTypeImpl;
import java.lang.reflect.Type;
import java.util.List;

/**
 * Json工具
 *
 * @author 庞先海 2019-11-17
 */
public class JsonUtil {

    public static String toJson(Object object) {
        return JSON.toJSONString(object, SerializerFeature.DisableCircularReferenceDetect);
    }

    public static <T> T fromJson(String jsonText, Class<T> clazz) {
        JSONObject jsonObject = JSON.parseObject(jsonText);
        return jsonObject.toJavaObject(clazz);
    }

    public static <T> List<T> fromJsonList(String jsonText, Class<T> clazz) {
        JSONArray array = JSONArray.parseArray(jsonText);
        return array.toJavaList(clazz);
    }

    public static <T> T fromJson(String jsonText, Type type) {
        return JSONObject.parseObject(jsonText, type);
    }

    public static <T> T fromJson(String jsonText, Type... types) {
        return JSONObject.parseObject(jsonText, buildType(types));
    }

    private static Type buildType(Type... types) {
        ParameterizedTypeImpl beforeType = null;
        if (types != null && types.length > 0) {
            for (int i = types.length - 1; i > 0; i--) {
                beforeType = new ParameterizedTypeImpl(new Type[] {beforeType == null ? types[i] : beforeType}, null,
                    types[i - 1]);
            }
        }
        return beforeType;
    }
}
