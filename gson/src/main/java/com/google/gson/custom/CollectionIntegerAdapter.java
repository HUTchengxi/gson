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
import java.util.HashMap;
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
                //.registerTypeAdapter(Map.class, CollectionIntegerAdapter.MAP_ADAPTER)
                //.registerTypeAdapter(List.class, CollectionIntegerAdapter.LIST_ADAPTER)
                .create();
        //System.out.println(gson.fromJson("{\"key\":12, \"key2\": f11}", Map.class));
        //System.out.println(gson.fromJson("[1,2,3]", List.class));

        Cust cust = new Cust();
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("key1", 11);
        paramMap.put("key2", "key2");
        List<Object> paramList = new ArrayList<>();
        paramList.add("111");
        paramList.add("222");
        Map<String, Object> listMap = new HashMap<>();
        listMap.put("kk", "vv");
        Map<String, Object> tempMap = new HashMap<>();
        tempMap.put("ad", "Ad");
        paramList.add(tempMap);
        listMap.put("list", paramList);
        paramMap.put("map", listMap);
        cust.setData(paramMap);
        System.out.println(new HashMap<>() instanceof Map);
        System.out.println(gson.toJson(gson.fromJson(getJson(), Cust.class)));
        //Cust<Map> cust1 = test(gson, cust, json, Map.class);
        //System.out.println(cust1.getData().get("key1"));
        //System.out.println(((Map) test(gson, cust, json).getData()).get("key1"));
    }

    //static <T> Cust<T> test(Gson gson, Cust<T> cust, String json, Class<T> clazz) {
    //    // rawType: Cust    ownerType: CollectionIntegerAdapter
    //    //Type type = new TypeToken<Cust<Map>>() {}.getType();
    //    //System.out.println(gson.toJson(cust));
    //    return gson.fromJson(json, TypeToken.getParameterized(Cust.class, clazz).getType());
    //}

    static class Cust {

        private String orderWebSite;
        private Map<String, Object> data;

        public String getOrderWebSite() {
            return orderWebSite;
        }

        public void setOrderWebSite(String orderWebSite) {
            this.orderWebSite = orderWebSite;
        }

        public Map<String, Object> getData() {
            return data;
        }

        public void setData(Map<String, Object> data) {
            this.data = data;
        }
    }

    private static String getJson() {
        return "{\"orderWebSite\":\"https://ums.iboxpay.com\",\"data\":{\"total\":153,\"dayList\":[{\"sumTradeAmt\":99,\"sumTradeNum\":1,\"tradeDate\":\"20190524\"},{\"sumTradeAmt\":119,\"sumTradeNum\":6,\"tradeDate\":\"20190508\"},{\"sumTradeAmt\":5695,\"sumTradeNum\":7,\"tradeDate\":\"20190429\"},{\"sumTradeAmt\":230,\"sumTradeNum\":1,\"tradeDate\":\"20190319\"},{\"sumTradeAmt\":5039.2,\"sumTradeNum\":20,\"tradeDate\":\"20190314\"}],\"list\":[{\"tradeAmt\":99,\"tradeSubCode\":\"31\",\"traceNo\":\"000013869161\",\"latitude\":\"22.523903\",\"snNo\":\"018330104953\",\"errorCode\":\"00\",\"remark\":\"cashbox\",\"tradeDesc\":\"交易成功\",\"cardNo\":\"622588******0619\",\"sysRefNo\":\"000524304550\",\"channelTerminalNo\":\"31580861\",\"tradeName\":\"快捷收款\",\"channelMchtNo\":\"831584456990224\",\"outTradeNo\":\"0183301049531558686246452\",\"expireDate\":\"4912\",\"id\":\"20190524000013869161\",\"longitude\":\"113.941045\",\"bankCode\":\"308584000013\",\"terminalNo\":\"68001547\",\"batchNo\":\"000001\",\"orderNo\":\"202132928516254096\",\"mchtName\":\"娣卞湷甯傞緳宀楀尯瀹囨澃鏈嶈\uE5CA鍔犲伐搴?\",\"mchtNo\":\"015440301207672\",\"cardType\":1,\"tradeCode\":\"00\",\"tradeDate\":\"20190524\",\"brfNo\":\"1535333269793413\",\"userId\":886020729,\"voucherNo\":\"869161\",\"tradeTime\":\"162428\",\"acquirerNo\":\"04062410\",\"tradeStatus\":\"0\",\"settleType\":1},{\"tradeAmt\":23,\"tradeSubCode\":\"31\",\"traceNo\":\"000013864457\",\"latitude\":\"22.523914\",\"snNo\":\"018330104953\",\"errorCode\":\"00\",\"remark\":\"cashbox\",\"tradeDesc\":\"交易成功\",\"cardNo\":\"622588******0619\",\"sysRefNo\":\"000508301283\",\"channelTerminalNo\":\"31580861\",\"tradeName\":\"快捷收款\",\"channelMchtNo\":\"831584456990224\",\"outTradeNo\":\"0183301049531557319605435\",\"expireDate\":\"4912\",\"id\":\"20190508000013864457\",\"longitude\":\"113.94104\",\"bankCode\":\"308584000013\",\"terminalNo\":\"68001547\",\"batchNo\":\"000001\",\"orderNo\":\"188466295076070809\",\"mchtName\":\"娣卞湷甯傞緳宀楀尯瀹囨澃鏈嶈\uE5CA鍔犲伐搴?\",\"mchtNo\":\"015440301207672\",\"cardType\":1,\"tradeCode\":\"00\",\"tradeDate\":\"20190508\",\"brfNo\":\"1535333269793413\",\"userId\":886020729,\"voucherNo\":\"864457\",\"tradeTime\":\"204715\",\"acquirerNo\":\"04062410\",\"tradeStatus\":\"0\",\"settleType\":1},{\"tradeAmt\":28,\"tradeSubCode\":\"31\",\"traceNo\":\"000013864441\",\"latitude\":\"22.523914\",\"snNo\":\"018330104953\",\"errorCode\":\"00\",\"remark\":\"cashbox\",\"tradeDesc\":\"交易成功\",\"cardNo\":\"622166******1471\",\"sysRefNo\":\"000508301281\",\"channelTerminalNo\":\"31580861\",\"tradeName\":\"快捷收款\",\"channelMchtNo\":\"831584456990224\",\"outTradeNo\":\"UT021557315966923018330104953277\",\"expireDate\":\"1809\",\"id\":\"20190508000013864441\",\"longitude\":\"113.941041\",\"bankCode\":\"105100000017\",\"terminalNo\":\"68001547\",\"batchNo\":\"000001\",\"orderNo\":\"188429869618540510\",\"mchtName\":\"娣卞湷甯傞緳宀楀尯瀹囨澃鏈嶈\uE5CA鍔犲伐搴?\",\"mchtNo\":\"015440301207672\",\"cardType\":2,\"tradeCode\":\"00\",\"tradeDate\":\"20190508\",\"brfNo\":\"1535333269793413\",\"userId\":886020729,\"voucherNo\":\"864441\",\"tradeTime\":\"194630\",\"acquirerNo\":\"04062410\",\"tradeStatus\":\"0\",\"settleType\":1},{\"tradeAmt\":23,\"tradeSubCode\":\"31\",\"traceNo\":\"000013864438\",\"latitude\":\"22.523908\",\"snNo\":\"018330104953\",\"errorCode\":\"00\",\"remark\":\"cashbox\",\"tradeDesc\":\"交易成功\",\"cardNo\":\"622166******1471\",\"sysRefNo\":\"000508301278\",\"channelTerminalNo\":\"31580861\",\"tradeName\":\"快捷收款\",\"channelMchtNo\":\"831584456990224\",\"outTradeNo\":\"0183301049531557315739558\",\"expireDate\":\"1809\",\"id\":\"20190508000013864438\",\"longitude\":\"113.941035\",\"bankCode\":\"105100000017\",\"terminalNo\":\"68001547\",\"batchNo\":\"000001\",\"orderNo\":\"188427573868367817\",\"mchtName\":\"娣卞湷甯傞緳宀楀尯瀹囨澃鏈嶈\uE5CA鍔犲伐搴?\",\"mchtNo\":\"015440301207672\",\"cardType\":2,\"tradeCode\":\"00\",\"tradeDate\":\"20190508\",\"brfNo\":\"1535333269793413\",\"userId\":886020729,\"voucherNo\":\"864438\",\"tradeTime\":\"194240\",\"acquirerNo\":\"04062410\",\"tradeStatus\":\"0\",\"settleType\":1},{\"tradeAmt\":10,\"tradeSubCode\":\"31\",\"traceNo\":\"000013864189\",\"latitude\":\"22.523908\",\"snNo\":\"018330104953\",\"errorCode\":\"00\",\"remark\":\"cashbox\",\"tradeDesc\":\"交易成功\",\"cardNo\":\"622588******0619\",\"sysRefNo\":\"000508301089\",\"channelTerminalNo\":\"31580861\",\"tradeName\":\"快捷收款\",\"channelMchtNo\":\"831584456990224\",\"outTradeNo\":\"0183301049531557299181998\",\"expireDate\":\"4912\",\"id\":\"20190508000013864189\",\"longitude\":\"113.941034\",\"bankCode\":\"308584000013\",\"terminalNo\":\"68001547\",\"batchNo\":\"000001\",\"orderNo\":\"188261984047609161\",\"mchtName\":\"娣卞湷甯傞緳宀楀尯瀹囨澃鏈嶈\uE5CA鍔犲伐搴?\",\"mchtNo\":\"015440301207672\",\"cardType\":1,\"tradeCode\":\"00\",\"tradeDate\":\"20190508\",\"brfNo\":\"1535333269793413\",\"userId\":886020729,\"voucherNo\":\"864189\",\"tradeTime\":\"150644\",\"acquirerNo\":\"04062410\",\"tradeStatus\":\"0\",\"settleType\":1},{\"tradeAmt\":12,\"tradeSubCode\":\"31\",\"traceNo\":\"000013864181\",\"latitude\":\"22.523908\",\"snNo\":\"018330104953\",\"errorCode\":\"00\",\"remark\":\"cashbox\",\"tradeDesc\":\"交易成功\",\"cardNo\":\"622588******0619\",\"sysRefNo\":\"000508301083\",\"channelTerminalNo\":\"31580861\",\"tradeName\":\"快捷收款\",\"channelMchtNo\":\"831584456990224\",\"outTradeNo\":\"0183301049531557298806032\",\"expireDate\":\"4912\",\"id\":\"20190508000013864181\",\"longitude\":\"113.941034\",\"bankCode\":\"308584000013\",\"terminalNo\":\"68001547\",\"batchNo\":\"000001\",\"orderNo\":\"188258234048547547\",\"mchtName\":\"娣卞湷甯傞緳宀楀尯瀹囨澃鏈嶈\uE5CA鍔犲伐搴?\",\"mchtNo\":\"015440301207672\",\"cardType\":1,\"tradeCode\":\"00\",\"tradeDate\":\"20190508\",\"brfNo\":\"1535333269793413\",\"userId\":886020729,\"voucherNo\":\"864181\",\"tradeTime\":\"150036\",\"acquirerNo\":\"04062410\",\"tradeStatus\":\"0\",\"settleType\":1},{\"tradeAmt\":23,\"tradeSubCode\":\"31\",\"traceNo\":\"000013864180\",\"latitude\":\"22.523913\",\"snNo\":\"018330104953\",\"errorCode\":\"00\",\"remark\":\"cashbox\",\"tradeDesc\":\"交易成功\",\"cardNo\":\"622588******0619\",\"sysRefNo\":\"000508301082\",\"channelTerminalNo\":\"31580861\",\"tradeName\":\"快捷收款\",\"channelMchtNo\":\"831584456990224\",\"outTradeNo\":\"0183301049531557298744732\",\"expireDate\":\"4912\",\"id\":\"20190508000013864180\",\"longitude\":\"113.941033\",\"bankCode\":\"308584000013\",\"terminalNo\":\"68001547\",\"batchNo\":\"000001\",\"orderNo\":\"188257618705289645\",\"mchtName\":\"娣卞湷甯傞緳宀楀尯瀹囨澃鏈嶈\uE5CA鍔犲伐搴?\",\"mchtNo\":\"015440301207672\",\"cardType\":1,\"tradeCode\":\"00\",\"tradeDate\":\"20190508\",\"brfNo\":\"1535333269793413\",\"userId\":886020729,\"voucherNo\":\"864180\",\"tradeTime\":\"145943\",\"acquirerNo\":\"04062410\",\"tradeStatus\":\"0\",\"settleType\":1},{\"tradeAmt\":5312,\"tradeSubCode\":\"31\",\"traceNo\":\"000013861511\",\"latitude\":\"22.523891\",\"snNo\":\"018330104953\",\"errorCode\":\"00\",\"remark\":\"cashbox\",\"tradeDesc\":\"交易成功\",\"cardNo\":\"622166******1471\",\"sysRefNo\":\"000429298660\",\"channelTerminalNo\":\"31580861\",\"tradeName\":\"快捷收款\",\"channelMchtNo\":\"831584456990224\",\"outTradeNo\":\"0183301049531556550580523\",\"expireDate\":\"1809\",\"id\":\"20190429000013861511\",\"longitude\":\"113.941034\",\"bankCode\":\"105100000017\",\"terminalNo\":\"68001547\",\"batchNo\":\"000001\",\"orderNo\":\"180775810237899421\",\"mchtName\":\"娣卞湷甯傞緳宀楀尯瀹囨澃鏈嶈\uE5CA鍔犲伐搴?\",\"mchtNo\":\"015440301207672\",\"cardType\":2,\"tradeCode\":\"00\",\"tradeDate\":\"20190429\",\"brfNo\":\"1535333269793413\",\"userId\":886020729,\"voucherNo\":\"861511\",\"tradeTime\":\"231006\",\"acquirerNo\":\"04062410\",\"tradeStatus\":\"0\",\"settleType\":1},{\"tradeAmt\":253,\"tradeSubCode\":\"31\",\"traceNo\":\"000013861480\",\"latitude\":\"22.5239\",\"snNo\":\"018330104953\",\"errorCode\":\"00\",\"remark\":\"cashbox\",\"tradeDesc\":\"交易成功\",\"cardNo\":\"622166******1471\",\"sysRefNo\":\"000429298631\",\"channelTerminalNo\":\"31580861\",\"tradeName\":\"快捷收款\",\"channelMchtNo\":\"831584456990224\",\"outTradeNo\":\"UT021556541422072018330104953308\",\"expireDate\":\"1809\",\"id\":\"20190429000013861480\",\"longitude\":\"113.941029\",\"bankCode\":\"105100000017\",\"terminalNo\":\"68001547\",\"batchNo\":\"000001\",\"orderNo\":\"180684230058841437\",\"mchtName\":\"娣卞湷甯傞緳宀楀尯瀹囨澃鏈嶈\uE5CA鍔犲伐搴?\",\"mchtNo\":\"015440301207672\",\"cardType\":2,\"tradeCode\":\"00\",\"tradeDate\":\"20190429\",\"brfNo\":\"1535333269793413\",\"userId\":886020729,\"voucherNo\":\"861480\",\"tradeTime\":\"203726\",\"acquirerNo\":\"04062410\",\"tradeStatus\":\"0\",\"settleType\":1},{\"tradeAmt\":36,\"tradeSubCode\":\"51\",\"doubleSign\":\"001\",\"traceNo\":\"000013861478\",\"latitude\":\"22.523886\",\"snNo\":\"018330104953\",\"errorCode\":\"00\",\"remark\":\"cashbox\",\"tradeDesc\":\"交易成功\",\"cardNo\":\"622827*********7471\",\"sysRefNo\":\"000429298630\",\"channelTerminalNo\":\"31580861\",\"tradeName\":\"快捷收款\",\"ICCardPrivateDomain\":\"82027C008408A000000333010101950500000000009A031904299C01005F2A0201569F02060000000036009F03060000000000009F090200309F101307120103A00000010A010000000000AD9097589F1A0201569F2608FB9379CF6FC0B2039F2701809F3303E0C0C89F360204A79F37049AF6709A\",\"channelMchtNo\":\"831584456990224\",\"outTradeNo\":\"0183301049531556540603460\",\"expireDate\":\"2409\",\"id\":\"20190429000013861478\",\"longitude\":\"113.94103\",\"bankCode\":\"103100000026\",\"terminalNo\":\"68001547\",\"batchNo\":\"000001\",\"orderNo\":\"180676028992166642\",\"mchtName\":\"娣卞湷甯傞緳宀楀尯瀹囨澃鏈嶈\uE5CA鍔犲伐搴?\",\"mchtNo\":\"015440301207672\",\"cardType\":1,\"tradeCode\":\"00\",\"tradeDate\":\"20190429\",\"brfNo\":\"1535333269793413\",\"userId\":886020729,\"voucherNo\":\"861478\",\"tradeTime\":\"202346\",\"acquirerNo\":\"04062410\",\"cardSn\":\"00\",\"tradeStatus\":\"0\",\"settleType\":1},{\"tradeAmt\":36,\"tradeSubCode\":\"51\",\"doubleSign\":\"001\",\"traceNo\":\"000013861473\",\"latitude\":\"22.5239\",\"snNo\":\"018330104953\",\"errorCode\":\"00\",\"remark\":\"cashbox\",\"tradeDesc\":\"交易成功\",\"cardNo\":\"622827*********7471\",\"sysRefNo\":\"000429298626\",\"channelTerminalNo\":\"31580861\",\"tradeName\":\"快捷收款\",\"ICCardPrivateDomain\":\"82027C008408A000000333010101950500000000009A031904299C01005F2A0201569F02060000000036009F03060000000000009F090200309F101307120103A00000010A01000000000020DFB1BE9F1A0201569F26083D17DD56094226129F2701809F3303E0C0C89F360204A59F3704DF528867\",\"channelMchtNo\":\"831584456990224\",\"outTradeNo\":\"0183301049531556538736985\",\"expireDate\":\"2409\",\"id\":\"20190429000013861473\",\"longitude\":\"113.941021\",\"bankCode\":\"103100000026\",\"terminalNo\":\"68001547\",\"batchNo\":\"000001\",\"orderNo\":\"180657356438062338\",\"mchtName\":\"娣卞湷甯傞緳宀楀尯瀹囨澃鏈嶈\uE5CA鍔犲伐搴?\",\"mchtNo\":\"015440301207672\",\"cardType\":1,\"tradeCode\":\"00\",\"tradeDate\":\"20190429\",\"brfNo\":\"1535333269793413\",\"userId\":886020729,\"voucherNo\":\"861473\",\"tradeTime\":\"195244\",\"acquirerNo\":\"04062410\",\"cardSn\":\"00\",\"tradeStatus\":\"0\",\"settleType\":1},{\"tradeAmt\":23,\"tradeSubCode\":\"31\",\"traceNo\":\"000013861472\",\"latitude\":\"22.523896\",\"snNo\":\"018330104953\",\"errorCode\":\"00\",\"remark\":\"cashbox\",\"tradeDesc\":\"交易成功\",\"cardNo\":\"622166******1471\",\"sysRefNo\":\"000429298625\",\"channelTerminalNo\":\"31580861\",\"tradeName\":\"快捷收款\",\"channelMchtNo\":\"831584456990224\",\"outTradeNo\":\"0183301049531556538706111\",\"expireDate\":\"1809\",\"id\":\"20190429000013861472\",\"longitude\":\"113.94102\",\"bankCode\":\"105100000017\",\"terminalNo\":\"68001547\",\"batchNo\":\"000001\",\"orderNo\":\"180657048806592075\",\"mchtName\":\"娣卞湷甯傞緳宀楀尯瀹囨澃鏈嶈\uE5CA鍔犲伐搴?\",\"mchtNo\":\"015440301207672\",\"cardType\":2,\"tradeCode\":\"00\",\"tradeDate\":\"20190429\",\"brfNo\":\"1535333269793413\",\"userId\":886020729,\"voucherNo\":\"861472\",\"tradeTime\":\"195208\",\"acquirerNo\":\"04062410\",\"tradeStatus\":\"0\",\"settleType\":1},{\"tradeAmt\":23,\"tradeSubCode\":\"31\",\"traceNo\":\"000013861463\",\"latitude\":\"22.52389\",\"snNo\":\"018330104953\",\"errorCode\":\"00\",\"remark\":\"cashbox\",\"tradeDesc\":\"交易成功\",\"cardNo\":\"622166******1471\",\"sysRefNo\":\"000429298618\",\"channelTerminalNo\":\"31580861\",\"tradeName\":\"快捷收款\",\"channelMchtNo\":\"831584456990224\",\"outTradeNo\":\"0183301049531556538031059\",\"expireDate\":\"1809\",\"id\":\"20190429000013861463\",\"longitude\":\"113.941025\",\"bankCode\":\"105100000017\",\"terminalNo\":\"68001547\",\"batchNo\":\"000001\",\"orderNo\":\"180650309501771500\",\"mchtName\":\"娣卞湷甯傞緳宀楀尯瀹囨澃鏈嶈\uE5CA鍔犲伐搴?\",\"mchtNo\":\"015440301207672\",\"cardType\":2,\"tradeCode\":\"00\",\"tradeDate\":\"20190429\",\"brfNo\":\"1535333269793413\",\"userId\":886020729,\"voucherNo\":\"861463\",\"tradeTime\":\"194057\",\"acquirerNo\":\"04062410\",\"tradeStatus\":\"0\",\"settleType\":1},{\"tradeAmt\":12,\"tradeSubCode\":\"31\",\"traceNo\":\"000013861454\",\"latitude\":\"22.523891\",\"snNo\":\"018330104953\",\"errorCode\":\"00\",\"remark\":\"cashbox\",\"tradeDesc\":\"交易成功\",\"cardNo\":\"622166******1471\",\"sysRefNo\":\"000429298611\",\"channelTerminalNo\":\"31580861\",\"tradeName\":\"快捷收款\",\"channelMchtNo\":\"831584456990224\",\"outTradeNo\":\"0183301049531556537273601\",\"expireDate\":\"1809\",\"id\":\"20190429000013861454\",\"longitude\":\"113.941021\",\"bankCode\":\"105100000017\",\"terminalNo\":\"68001547\",\"batchNo\":\"000001\",\"orderNo\":\"180642729349969224\",\"mchtName\":\"娣卞湷甯傞緳宀楀尯瀹囨澃鏈嶈\uE5CA鍔犲伐搴?\",\"mchtNo\":\"015440301207672\",\"cardType\":2,\"tradeCode\":\"00\",\"tradeDate\":\"20190429\",\"brfNo\":\"1535333269793413\",\"userId\":886020729,\"voucherNo\":\"861454\",\"tradeTime\":\"192819\",\"acquirerNo\":\"04062410\",\"tradeStatus\":\"0\",\"settleType\":1},{\"tradeAmt\":230,\"tradeSubCode\":\"31\",\"traceNo\":\"000013847627\",\"latitude\":\"22.523865\",\"snNo\":\"018330104953\",\"errorCode\":\"00\",\"remark\":\"cashbox\",\"tradeDesc\":\"交易成功\",\"cardNo\":\"622588******0619\",\"sysRefNo\":\"000319287425\",\"channelTerminalNo\":\"31580861\",\"tradeName\":\"快捷收款\",\"channelMchtNo\":\"831584456990224\",\"outTradeNo\":\"0183301049531552962200995\",\"expireDate\":\"4912\",\"id\":\"20190319000013847627\",\"longitude\":\"113.940888\",\"bankCode\":\"308584000013\",\"terminalNo\":\"68001547\",\"batchNo\":\"000001\",\"orderNo\":\"144891214286026687\",\"mchtName\":\"娣卞湷甯傞緳宀楀尯瀹囨澃鏈嶈\uE5CA鍔犲伐搴?\",\"mchtNo\":\"015440301207672\",\"cardType\":1,\"tradeCode\":\"00\",\"tradeDate\":\"20190319\",\"brfNo\":\"1535333269793413\",\"userId\":886020729,\"voucherNo\":\"847627\",\"tradeTime\":\"102325\",\"acquirerNo\":\"04062410\",\"tradeStatus\":\"0\",\"settleType\":0},{\"tradeAmt\":2,\"tradeSubCode\":\"51\",\"traceNo\":\"000013847149\",\"latitude\":\"22.523851\",\"snNo\":\"018330104953\",\"errorCode\":\"00\",\"remark\":\"cashbox\",\"tradeDesc\":\"交易成功\",\"cardNo\":\"625906******5259\",\"sysRefNo\":\"000314287213\",\"channelTerminalNo\":\"31580861\",\"tradeName\":\"快捷收款\",\"ICCardPrivateDomain\":\"82027C008408A000000333010103950500000000009A031903149C01005F2A0201569F02060000000002009F03060000000000009F090200309F101307090103A00000010A0100000000003B3EDDEF9F1A0201569F2608450C35B5D20AA2D69F2701809F3303E0C0C89F360202B29F37047EB7B856\",\"channelMchtNo\":\"831584456990224\",\"outTradeNo\":\"0183301049531552554462675\",\"expireDate\":\"2206\",\"id\":\"20190314000013847149\",\"longitude\":\"113.940914\",\"bankCode\":\"104100000004\",\"terminalNo\":\"68001547\",\"batchNo\":\"000001\",\"orderNo\":\"140813753654568964\",\"mchtName\":\"娣卞湷甯傞緳宀楀尯瀹囨澃鏈嶈\uE5CA鍔犲伐搴?\",\"mchtNo\":\"015440301207672\",\"cardType\":3,\"tradeCode\":\"00\",\"tradeDate\":\"20190314\",\"brfNo\":\"1535333269793413\",\"userId\":886020729,\"voucherNo\":\"847149\",\"tradeTime\":\"170749\",\"acquirerNo\":\"04062410\",\"cardSn\":\"00\",\"tradeStatus\":\"0\",\"settleType\":0},{\"tradeAmt\":50,\"tradeSubCode\":\"51\",\"traceNo\":\"000013847146\",\"latitude\":\"22.523853\",\"snNo\":\"018330104953\",\"errorCode\":\"00\",\"remark\":\"cashbox\",\"tradeDesc\":\"交易成功\",\"cardNo\":\"625906******5259\",\"sysRefNo\":\"000314287210\",\"channelTerminalNo\":\"31580861\",\"tradeName\":\"快捷收款\",\"ICCardPrivateDomain\":\"82027C008408A000000333010103950500000000009A031903149C01005F2A0201569F02060000000050009F03060000000000009F090200309F101307090103A00000010A010000000000498463319F1A0201569F2608CE5831D4A7C25C5D9F2701809F3303E0C0C89F360202B19F3704CD72811E\",\"channelMchtNo\":\"831584456990224\",\"outTradeNo\":\"UT021552554260332018330104953268\",\"expireDate\":\"2206\",\"id\":\"20190314000013847146\",\"longitude\":\"113.94091\",\"bankCode\":\"104100000004\",\"terminalNo\":\"68001547\",\"batchNo\":\"000001\",\"orderNo\":\"140811746645888858\",\"mchtName\":\"娣卞湷甯傞緳宀楀尯瀹囨澃鏈嶈\uE5CA鍔犲伐搴?\",\"mchtNo\":\"015440301207672\",\"cardType\":3,\"tradeCode\":\"00\",\"tradeDate\":\"20190314\",\"brfNo\":\"1535333269793413\",\"userId\":886020729,\"voucherNo\":\"847146\",\"tradeTime\":\"170426\",\"acquirerNo\":\"04062410\",\"cardSn\":\"00\",\"tradeStatus\":\"0\",\"settleType\":0},{\"tradeAmt\":2300,\"tradeSubCode\":\"51\",\"traceNo\":\"000013847140\",\"latitude\":\"22.523854\",\"snNo\":\"018330104953\",\"errorCode\":\"00\",\"remark\":\"cashbox\",\"tradeDesc\":\"交易成功\",\"cardNo\":\"625906******5259\",\"sysRefNo\":\"000314287207\",\"channelTerminalNo\":\"31580861\",\"tradeName\":\"快捷收款\",\"ICCardPrivateDomain\":\"82027C008408A0000003330101039505088804E8809A031903149C01005F2A0201569F02060000002300009F03060000000000009F090200309F101307090103A0B000010A010000000000B5B94DF79F1A0201569F2608217411FE78B09BD89F2701809F3303E0C0C89F34030203009F360202B09F370466BD23CC\",\"channelMchtNo\":\"831584456990224\",\"outTradeNo\":\"0183301049531552554121830\",\"expireDate\":\"2206\",\"id\":\"20190314000013847140\",\"longitude\":\"113.940903\",\"bankCode\":\"104100000004\",\"terminalNo\":\"68001547\",\"batchNo\":\"000001\",\"orderNo\":\"140810344748712045\",\"mchtName\":\"娣卞湷甯傞緳宀楀尯瀹囨澃鏈嶈\uE5CA鍔犲伐搴?\",\"mchtNo\":\"015440301207672\",\"cardType\":3,\"tradeCode\":\"00\",\"tradeDate\":\"20190314\",\"brfNo\":\"1535333269793413\",\"userId\":886020729,\"voucherNo\":\"847140\",\"tradeTime\":\"170206\",\"acquirerNo\":\"04062410\",\"cardSn\":\"00\",\"tradeStatus\":\"0\",\"settleType\":0},{\"tradeAmt\":2300,\"tradeSubCode\":\"51\",\"traceNo\":\"000013847138\",\"latitude\":\"22.523852\",\"snNo\":\"018330104953\",\"errorCode\":\"00\",\"remark\":\"cashbox\",\"tradeDesc\":\"交易成功\",\"cardNo\":\"625906******5259\",\"sysRefNo\":\"000314287206\",\"channelTerminalNo\":\"31580861\",\"tradeName\":\"快捷收款\",\"ICCardPrivateDomain\":\"82027C008408A0000003330101039505088804E8809A031903149C01005F2A0201569F02060000002300009F03060000000000009F090200309F101307090103A0B000010A01000000000022867F669F1A0201569F26084BD3B8FFCEE992E79F2701809F3303E0C0C89F34030203009F360202AF9F3704F764A44E\",\"channelMchtNo\":\"831584456990224\",\"outTradeNo\":\"0183301049531552554087595\",\"expireDate\":\"2206\",\"id\":\"20190314000013847138\",\"longitude\":\"113.940909\",\"bankCode\":\"104100000004\",\"terminalNo\":\"68001547\",\"batchNo\":\"000001\",\"orderNo\":\"140810002380215274\",\"mchtName\":\"娣卞湷甯傞緳宀楀尯瀹囨澃鏈嶈\uE5CA鍔犲伐搴?\",\"mchtNo\":\"015440301207672\",\"cardType\":3,\"tradeCode\":\"00\",\"tradeDate\":\"20190314\",\"brfNo\":\"1535333269793413\",\"userId\":886020729,\"voucherNo\":\"847138\",\"tradeTime\":\"170133\",\"acquirerNo\":\"04062410\",\"cardSn\":\"00\",\"tradeStatus\":\"0\",\"settleType\":1},{\"tradeAmt\":12,\"tradeSubCode\":\"31\",\"traceNo\":\"000013847136\",\"latitude\":\"22.523853\",\"snNo\":\"018330104953\",\"errorCode\":\"00\",\"remark\":\"cashbox\",\"tradeDesc\":\"交易成功\",\"cardNo\":\"622166******1471\",\"sysRefNo\":\"000314287204\",\"channelTerminalNo\":\"31580861\",\"tradeName\":\"快捷收款\",\"channelMchtNo\":\"831584456990224\",\"outTradeNo\":\"0183301049531552554064197\",\"expireDate\":\"1809\",\"id\":\"20190314000013847136\",\"longitude\":\"113.940903\",\"bankCode\":\"105100000017\",\"terminalNo\":\"68001547\",\"batchNo\":\"000001\",\"orderNo\":\"140809768865572002\",\"mchtName\":\"娣卞湷甯傞緳宀楀尯瀹囨澃鏈嶈\uE5CA鍔犲伐搴?\",\"mchtNo\":\"015440301207672\",\"cardType\":2,\"tradeCode\":\"00\",\"tradeDate\":\"20190314\",\"brfNo\":\"1535333269793413\",\"userId\":886020729,\"voucherNo\":\"847136\",\"tradeTime\":\"170108\",\"acquirerNo\":\"04062410\",\"tradeStatus\":\"0\",\"settleType\":1}]},\"resultCode\":\"1\"}";
    }
}
