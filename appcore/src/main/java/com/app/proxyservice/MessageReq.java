package com.app.proxyservice;

import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.app.util.JSONParser;
import com.loopj.android.http.RequestParams;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.String.valueOf;

public abstract class MessageReq {

    public static final String VERSION = "v1";

    // 请求的url
    public String url;
    // 请求的接口名
    public String methodName;
    // 请求url的前缀，不包含
    public String parseUrl;
    // 请求超时时间
    public String timeout;
    // 响应类名
    public Class<? extends MessageResp> rspClass;
    // get参数
    public LinkedHashMap<String, Object> postParams = new LinkedHashMap<>();
    // post参数
    public Map<String, Object> params = new HashMap<>();
    // url参数
    public LinkedHashMap<String, Object> urlParams = new LinkedHashMap<>();
    // 是否需要缓存
    public boolean shouldCache;
    // 缓存时间
    public int cacheExpiretime;

    /**
     * 加密方式，见{@link EncryptionType}
     */
    public EncryptionType encryptionType = null;

    /**
     * http请求方式，见{@link MyHttpMethod}
     */
    public MyHttpMethod httpMethod = MyHttpMethod.POST;

    public enum EncryptionType {
        /**
         * 平台密钥
         */
        TYPE_PLATFORM,
        /**
         * 会话密钥（用户密钥）
         */
        TYPE_BUSINESS
    }

    /**
     * Http请求方式，目前支持四种
     */
    public enum MyHttpMethod {
        POST,
        GET,
        PUT,
        DELETE
    }

    public void setParam(String name, Object value) {
        this.params.put(name, value);
    }

    /**
     * 设置有序的参数
     *
     * @param name
     * @param value
     */
    public void setOrderlyParam(String name, String value) {
        this.postParams.put(name, TextUtils.isEmpty(value) ? "-1" : value.replaceAll(" ", ""));
    }

    /**
     * 设置url参数
     *
     * @param name
     * @param value
     */
    public void setUrlParams(String name, String value) {
        this.urlParams.put(name, value);
    }

    public void setParam(String name, Object value, boolean fitJSON) {
        if (fitJSON && value instanceof String) {
            // 需要进行转义符的处理
            value = JSONParser.fitJSON((String) value);
        }
        this.params.put(name, value);
    }

    public void removeParam(String name) {
        this.params.remove(name);
    }

    public Map<String, Object> getParams() {
        return params;
    }

    public String getData() {
        HashMap<String, Object> baseReqParams = BusinessProxy.shareInstance().getBaseReqParams();
        for (String key : baseReqParams.keySet()) {
            if (!params.containsKey(key)) {
                params.put(key, baseReqParams.get(key));
            }
        }
        return JSON.toJSONString(params, false);
    }

    public void setEncryptData(String encrypted, String userToken) {
        params.clear();
        params.put("data", encrypted);
        if (userToken != null && userToken.length() != 0) {
            params.put("userToken", userToken);
        }
    }

    /**
     * 将url切分成四段，重新组合,如http://113.108.40.12:10010/jsse/miUserAuthService/login，切分后为<br>
     * http<br>
     * ://113.108.40.12:<br>
     * 10010<br>
     * /jsse/miUserAuthService/login<br>
     * 再重组得到https://113.108.40.12:10011/jsse/miUserAuthService/login
     */
    public void buildUrlToHttps() {
        if (url != null) {
            Pattern pattern = Pattern.compile("^(http[s]?)(.+?:)([0-9]+)(/.+)$");
            Matcher m = pattern.matcher(url);
            if (m.find()) {
                int port = Integer.parseInt(m.group(3)) + 1;
                StringBuilder sb = new StringBuilder();
                sb.append("https").append(m.group(2)).append(port).append(m.group(4));
                url = sb.toString();
            }
        }
    }

    public String verifyRestfulUrlParam(String urlParam, String defaultValue) {
        if (TextUtils.isEmpty(urlParam)) {
            return defaultValue;
        }
        return urlParam;
    }

    public RequestParams getRequestData() {
        // reqParams对应的键值对必须是 <String, String>
        RequestParams reqParams = new RequestParams();
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            reqParams.put(entry.getKey(), valueOf(entry.getValue()));
        }
        return reqParams;
    }

    private String getBaseData(Object data) {
        StringBuffer sb = new StringBuffer();
        if (data instanceof String) {
            sb.append('\'');
            sb.append(data);
            sb.append('\'');
        } else if (data instanceof Integer) {
            sb.append(data);
        } else if (data instanceof Double) {
            sb.append(data);
        } else if (data instanceof Boolean) {
            sb.append(data);
        }
        return sb.toString();
    }

    @SuppressWarnings("unchecked")
    private String getArrayObjectData(Object[] datas) {
        StringBuffer sb = new StringBuffer();
        sb.append('[');
        boolean hasContent = false;
        for (Object data : datas) {
            if (hasContent) {
                sb.append(',');
            }

            if (data instanceof Map<?, ?>) {
                sb.append(getObjectData((HashMap<String, Object>) data));
            } else if (data instanceof Object[]) {
                sb.append(getArrayObjectData((Object[]) data));
            } else {
                sb.append(getBaseData(data));
            }
            hasContent = true;
        }
        sb.append(']');
        return sb.toString();
    }

    // Map转Json格式
    private String getObjectData(Map<?, ?> data) {
        @SuppressWarnings("unchecked")
        HashMap<String, Object> dataMap = (HashMap<String, Object>) data;
        StringBuffer sb = new StringBuffer();
        sb.append('{');
        boolean hasContent = false;
        for (String key : dataMap.keySet()) {
            Object v = dataMap.get(key);
            if (hasContent) {
                sb.append(',');
            }
            sb.append(key);
            sb.append(':');
            if (v instanceof Map<?, ?>) {
                sb.append(getObjectData((Map<?, ?>) v));
            } else if (v instanceof Object[]) {
                sb.append(getArrayObjectData((Object[]) v));
            } else {
                sb.append(getBaseData(v));
            }
            hasContent = true;
        }
        sb.append('}');

        return sb.toString();
    }

    /**
     * 拼接parseurl和参数
     *
     * @return
     */
    public void setRestfulUrlParams() {
        StringBuffer sb = new StringBuffer();
        // 添加url参数
        if (postParams != null) {
            Iterator<String> it = postParams.keySet().iterator();
            while (it.hasNext()) {
                String key = it.next();
                String value = valueOf(postParams.get(key));
                sb.append("/");
                // 參數值为空，则传-1
                sb.append(value);
            }
        }
        methodName = String.format("%s%s%s", parseUrl, sb.toString(), buildUrlParams());
    }

    public String buildUrlParams() {
        StringBuffer sb = new StringBuffer();
        if (urlParams != null) {
            Iterator<String> iterator = urlParams.keySet().iterator();
            while (iterator.hasNext()) {
                String key = iterator.next();
                String value = String.valueOf(urlParams.get(key));
                sb.append(String.format("?%s=%s", key, value));
            }
        }

        return sb.toString();

    }

}
