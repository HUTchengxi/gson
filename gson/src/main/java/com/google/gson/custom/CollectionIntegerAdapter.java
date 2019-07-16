/*
 * Copyright (C) 2011-2019 ShenZhen iBOXCHAIN Information Technology Co.,Ltd.
 *
 * All right reserved.
 *
 * This software is the confidential and proprietary
 * information of iBOXCHAIN Company of China.
 * ("Confidential Information"). You shall not disclose
 * such Confidential Information and shall use it only
 * in accordance with the terms of the contract agreement
 * you entered into with iBOXCHAIN inc.
 */
package com.google.gson.custom;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 集合类型Integer元素会自动转换成Double类型问题
 *
 * @AUTHOR Jream.Y
 * @CREATE 2019-07-16
 */
public class CollectionIntegerAdapter {

    /**
     * Map TypeAdapterFactory
     */
    public final static TypeAdapterFactory MAP_ADAPTER_FACTORY = new TypeAdapterFactory() {
        @Override
        public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
            return type.getRawType() == Map.class ? (TypeAdapter<T>) MAP_ADAPTER : null;
        }
    };

    /**
     * Map TypeAdapter
     */
    public final static TypeAdapter<Map> MAP_ADAPTER = new TypeAdapter<Map>() {

        public Object readObject(JsonReader in) throws IOException {
            return readInteger(in);
        }

        @SuppressWarnings("unchecked")
        @Override
        public void write(JsonWriter out, Map value) throws IOException {
            if (value == null) {
                out.nullValue();
                return;
            }
            out.beginObject();
            out.endObject();
            return;
        }

        @Override
        public Map read(JsonReader in) throws IOException {
            Map<String, Object> resultMap = (Map<String, Object>) readObject(in);
            return resultMap;
        }
    };

    /**
     * List TypeAdapterFactory
     */
    public final static TypeAdapterFactory LIST_ADAPTER_FACTORY = new TypeAdapterFactory() {
        @Override
        public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
            return type.getRawType() == List.class ? (TypeAdapter<T>) LIST_ADAPTER : null;
        }
    };

    /**
     * List TypeAdapter
     */
    public final static TypeAdapter<List> LIST_ADAPTER = new TypeAdapter<List>() {

        public Object readObject(JsonReader in) throws IOException {
            return readInteger(in);
        }

        @SuppressWarnings("unchecked")
        @Override
        public void write(JsonWriter out, List value) throws IOException {
            if (value == null) {
                out.nullValue();
                return;
            }
            out.beginObject();
            out.endObject();
            return;
        }

        @Override
        public List read(JsonReader in) throws IOException {
            List resultList = (List) readObject(in);
            return resultList;
        }
    };

    private static Object readInteger(JsonReader in) throws IOException {
        JsonToken token = in.peek();
        switch (token) {
            case BEGIN_ARRAY:
                List<Object> list = new ArrayList<Object>();
                in.beginArray();
                while (in.hasNext()) {
                    list.add(readInteger(in));
                }
                in.endArray();
                return list;

            case BEGIN_OBJECT:
                Map<String, Object> map = new LinkedTreeMap<String, Object>();
                in.beginObject();
                while (in.hasNext()) {
                    map.put(in.nextName(), readInteger(in));
                }
                in.endObject();
                return map;

            case STRING:
                return in.nextString();

            case NUMBER:
                String numStr = in.nextString();
                if (numStr.contains(".") || numStr.contains("e") || numStr.contains("E")) {
                    return Double.parseDouble(numStr);
                }
                if (Long.valueOf(numStr) <= Integer.MAX_VALUE) {
                    return Integer.parseInt(numStr);
                }
                return Long.parseLong(numStr);

            case BOOLEAN:
                return in.nextBoolean();

            case NULL:
                in.nextNull();
                return null;

            default:
                throw new IllegalStateException();
        }
    }

    public static void main(String[] args) {

        // 示例代码
        Gson gson = new GsonBuilder()
                // 下面这两个对应两种类型集合的Integer自动转换成了Double问题
                .registerTypeAdapterFactory(CollectionIntegerAdapter.MAP_ADAPTER_FACTORY)
                .registerTypeAdapterFactory(CollectionIntegerAdapter.LIST_ADAPTER_FACTORY)
                .create();
        System.out.println(gson.fromJson("{\"key\":12, \"key2\": f11}", Map.class));
        System.out.println(gson.fromJson("[1,2,3]", List.class));
    }
}
