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

import java.util.Map;

/**
 * @AUTHOR Jream.Y
 * @CREATE 2019-07-15
 */
public class Test {

    public static void main(String[] args) {

        Gson gson = new GsonBuilder()
                .registerTypeAdapterFactory(MapKeyIntTypeAdapter.FACTORY)
                .create();
        System.out.println(gson.fromJson("{\"val\":1}", Map.class));
    }

}
