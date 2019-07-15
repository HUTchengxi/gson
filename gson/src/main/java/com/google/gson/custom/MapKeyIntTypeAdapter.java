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
 * Map中Integer元素会当成Double处理问题解决
 *
 * @AUTHOR Jream.Y
 * @CREATE 2019-07-15
 */
public class MapKeyIntTypeAdapter extends TypeAdapter<Map> {

    public static final TypeAdapterFactory FACTORY = new TypeAdapterFactory() {
        @SuppressWarnings("unchecked")
        @Override
        public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
            if (type.getRawType() == Map.class) {
                return (TypeAdapter<T>) new MapKeyIntTypeAdapter(gson);
            }
            return null;
        }
    };

    private final Gson gson;

    MapKeyIntTypeAdapter(Gson gson) {
        this.gson = gson;
    }

    public Object readObject(JsonReader in) throws IOException {
        JsonToken token = in.peek();
        switch (token) {
            case BEGIN_ARRAY:
                List<Object> list = new ArrayList<Object>();
                in.beginArray();
                while (in.hasNext()) {
                    list.add(read(in));
                }
                in.endArray();
                return list;

            case BEGIN_OBJECT:
                Map<String, Object> map = new LinkedTreeMap<String, Object>();
                in.beginObject();
                while (in.hasNext()) {
                    map.put(in.nextName(), read(in));
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

    @SuppressWarnings("unchecked")
    @Override
    public void write(JsonWriter out, Map value) throws IOException {
        if (value == null) {
            out.nullValue();
            return;
        }

        TypeAdapter<Map> typeAdapter = (TypeAdapter<Map>) gson.getAdapter(value.getClass());
        if (typeAdapter instanceof MapKeyIntTypeAdapter) {
            out.beginObject();
            out.endObject();
            return;
        }

        typeAdapter.write(out, value);
    }

    @Override
    public Map read(JsonReader in) throws IOException {
        Map<String, Object> resultMap = (Map<String, Object>) readObject(in);
        return resultMap;
    }
}
