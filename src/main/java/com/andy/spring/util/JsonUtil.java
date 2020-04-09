package com.andy.spring.util;

import com.andy.spring.base.Result;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

/**
 * 基础模块-COMMON-工具
 *
 * <p>Json转换工具</p>
 *
 * @author 庞先海 2018-07-05
 */
public class JsonUtil {

    private static Gson gson = createGson();

    /**
     * json字符串转换为对象
     *
     * @param json  json字符串
     * @param clazz class类型
     * @return 对应对象
     */
    public static <T> T fromJson(String json, Class<T> clazz) {
        return createGson().fromJson(json, clazz);
    }

    /**
     * json字符串转换为对象列表
     *
     * @param json  json字符串
     * @param clazz 类型
     * @return 对应对象列表
     */
    public static <T> T fromJson(String json, final Class... clazz) {
        Object object = createGson().fromJson(json, new ParameterizedType() {
            @Override
            public Type[] getActualTypeArguments() {
                if (clazz.length > 2) {
                    return new Type[] {new ParameterizedType() {
                        @Override
                        public Type[] getActualTypeArguments() {
                            Type[] types = new Type[clazz.length - 2];
                            System.arraycopy(clazz, 2, types, 0, clazz.length - 2);
                            return types;
                        }

                        @Override
                        public Type getRawType() {
                            return clazz[1];
                        }

                        @Override
                        public Type getOwnerType() {
                            return null;
                        }
                    }};
                } else {
                    return new Type[] {clazz[1]};
                }
            }

            @Override
            public Type getRawType() {
                return clazz[0];
            }

            @Override
            public Type getOwnerType() {
                return null;
            }
        });
        return (T)object;
    }

    /**
     * json字符串转换为Result对象
     *
     * @param json  json字符串
     * @param clazz 类型
     * @return 对应对象列表
     */
    public static <T> Result<T> fromJsonResult(String json, final Class... clazz) {
        return createGson().fromJson(json, new ParameterizedType() {
            @Override
            public Type[] getActualTypeArguments() {
                return JsonUtil.getActualTypeArguments(clazz);
            }

            @Override
            public Type getRawType() {
                return Result.class;
            }

            @Override
            public Type getOwnerType() {
                return null;
            }
        });
    }

    /**
     * json字符串转换为List对象
     *
     * @param json  json字符串
     * @param clazz 类型
     * @return 对应对象列表
     */
    public static <T> List<T> fromJsonList(String json, final Class... clazz) {
        return createGson().fromJson(json, new ParameterizedType() {
            @Override
            public Type[] getActualTypeArguments() {
                return JsonUtil.getActualTypeArguments(clazz);
            }

            @Override
            public Type getRawType() {
                return List.class;
            }

            @Override
            public Type getOwnerType() {
                return null;
            }
        });
    }

    private static Type[] getActualTypeArguments(Class... clazz) {
        if (clazz.length > 1) {
            return new Type[] {new ParameterizedType() {
                @Override
                public Type[] getActualTypeArguments() {
                    Type[] types = new Type[clazz.length - 1];
                    System.arraycopy(clazz, 1, types, 0, clazz.length - 1);
                    return types;
                }

                @Override
                public Type getRawType() {
                    return clazz[0];
                }

                @Override
                public Type getOwnerType() {
                    return null;
                }
            }};
        } else {
            return clazz;
        }
    }


    /**
     * 对象转换为json字符串
     *
     * @param object 对象数据
     * @return json字符串
     */
    public static String toJson(Object object) {
        return createGson().toJson(object);
    }

    public synchronized static Gson createGson() {
        if (null == gson) {
            reCreateGson("yyyy-MM-dd HH:mm:ss");
        }
        return gson;
    }


    public synchronized static Gson reCreateGson(String pattern) {
        gson = new GsonBuilder().enableComplexMapKeySerialization().registerTypeAdapterFactory(
            new MyAdapterFactory()).setDateFormat(pattern).disableHtmlEscaping().create();
        return gson;
    }

    public static class MyAdapterFactory<T> implements TypeAdapterFactory {

        @Override
        public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
            Class<T> rawType = (Class<T>)type.getRawType();
            if (rawType == Long.class || rawType == long.class) {
                return (TypeAdapter<T>)new JsonUtil.LongAdapter();
            } else if (rawType == Integer.class || rawType == int.class) {
                return (TypeAdapter<T>)new JsonUtil.IntegerAdapter();
            } else if (rawType == Boolean.class || rawType == boolean.class) {
                return (TypeAdapter<T>)new JsonUtil.BooleanAdapter();
            } else if (rawType == Double.class || rawType == double.class) {
                return (TypeAdapter<T>)new DoubleAdapter();
            }
            return null;
        }
    }

    public static class LongAdapter extends TypeAdapter<Long> {

        @Override
        public Long read(JsonReader reader) throws IOException {

            if (reader.peek() == JsonToken.NULL) {
                reader.nextNull();
                return null;
            } else if (reader.peek() == JsonToken.STRING) {
                String nextStr = reader.nextString();
                if (StringUtils.isEmpty(nextStr)) {
                    //仅处理空白字符串
                    return null;
                }
                //其他字符串如果转换失败仍然报错
                return Long.parseLong(nextStr);
            } else {
                return reader.nextLong();
            }
        }

        @Override
        public void write(JsonWriter writer, Long value) throws IOException {
            writer.value(value);
        }
    }

    public static class IntegerAdapter extends TypeAdapter<Integer> {

        @Override
        public Integer read(JsonReader reader) throws IOException {
            if (reader.peek() == JsonToken.NULL) {
                reader.nextNull();
                return null;
            } else if (reader.peek() == JsonToken.STRING) {
                String nextStr = reader.nextString();
                if (StringUtils.isEmpty(nextStr)) {
                    //仅处理空白字符串
                    return null;
                }
                //其他字符串如果转换失败仍然报错
                return Integer.parseInt(nextStr);
            } else {
                return reader.nextInt();
            }
        }

        @Override
        public void write(JsonWriter writer, Integer value) throws IOException {
            writer.value(value);
        }
    }

    public static class BooleanAdapter extends TypeAdapter<Boolean> {

        @Override
        public Boolean read(JsonReader reader) throws IOException {
            if (reader.peek() == JsonToken.NULL) {
                reader.nextNull();
                return null;
            } else if (reader.peek() == JsonToken.STRING) {
                String nextStr = reader.nextString();
                if (StringUtils.isEmpty(nextStr)) {
                    //仅处理空白字符串
                    return null;
                }
                //其他字符串如果转换失败仍然报错
                return Boolean.parseBoolean(nextStr);
            } else if (reader.peek() == JsonToken.NUMBER) {
                int nextInt = reader.nextInt();
                return nextInt == 1;
            } else {
                return reader.nextBoolean();
            }
        }

        @Override
        public void write(JsonWriter writer, Boolean value) throws IOException {
            writer.value(value);
        }
    }

    public static class DoubleAdapter extends TypeAdapter<Double> {

        @Override
        public void write(JsonWriter writer, Double value) throws IOException {
            if (null != value && value == value.longValue()) {
                writer.value(value.longValue());
            } else {
                writer.value(value);
            }
        }

        @Override
        public Double read(JsonReader reader) throws IOException {
            return reader.nextDouble();
        }
    }

}
