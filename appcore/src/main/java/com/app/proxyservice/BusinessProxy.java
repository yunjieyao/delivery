package com.app.proxyservice;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.app.config.Settings;
import com.app.log.Log;
import com.app.netstatecontrol.NetLogHelper;
import com.app.util.JSONParser;
import com.app.util.MapUtils;
import com.app.util.NumberParser;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.ResponseHandlerInterface;

import org.apache.http.Header;
import org.apache.http.conn.ConnectTimeoutException;
import org.json.JSONException;

import java.io.UnsupportedEncodingException;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class BusinessProxy {

    private final static BusinessProxy BUSINESSPROXY = new BusinessProxy();
    //private static final String TAG = "BusinessProxy";
    private static final String TAG_NET = "BusinessProxy/NET";

    private static final boolean ENABLE_OKHTTP = false;

    /*-------------线程控制相关变量--------------*/
    private final int corePoolSize = 4; // 线程池维护线程的最少数量
    private final int maximumPoolSize = 64; // 线程池维护线程的最大数量
    private final long keepAliveTime = 30000L; // 线程池维护线程所允许的空闲时间、

    private ThreadPoolExecutor poolExecutor; // 自定义线程池子

    private HashMap<String, String> mReqHeaders = new HashMap<String, String>();
    private HashMap<String, Object> mBaseReqParams = new HashMap<>();

    /*-------------线程控制相关变量--------------*/

    // 业务代理生命周期和应用程序一致，无需释放资源
    private BusinessProxy() {
    }

    private Context mContext;

    public static BusinessProxy shareInstance() {
        return BUSINESSPROXY;
    }

    public void initBusinessProxy(Context context, String proxyCacheDir) {
        mContext = context;
        // 初始化线程池，用来执行异步图片下载任务
        poolExecutor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>());

        // 初始化代理缓存ProxyCache
        ProxyCache.getProxyCache().init(context, proxyCacheDir);
        Settings.getInstance().init(mContext);

        // UserAgent添加app版本号
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo info = pm.getPackageInfo(context.getPackageName(), 0);
            SyncHttpRequest.setUserAgent("appVersion/" + info.versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    // 设置请求头
    public void addRequestHeader(String header, String value) {
        if (header != null && header.length() != 0) {
            // 规避okHttp中header中value抛出的空指针异常
            if (value == null) {
                value = "";
            }
            SyncHttpRequest.addHeader(header, value);
            mReqHeaders.put(header, value);
        }
    }

    // 添加基础请求参数,如app版本号信息
    public void addBaseReqParams(String key, Object reqParams) {
        mBaseReqParams.put(key, reqParams);
    }

    public HashMap<String, Object> getBaseReqParams() {
        return mBaseReqParams;
    }

    public MessageResp syncAccessBusinessProxy(MessageReq req) {
        return accessBusinessProxy(req);
    }

    public void cancelRequests(Context context) {
       SyncHttpRequest.cancelRequests(context);
    }

    public void asyncAccessBusinessProxy(final MessageReq req, final MessageRespCallback callback) {
        poolExecutor.execute(new Runnable() {

            @Override
            public void run() {

                if (callback != null) {
                    callback.onRespCallback(accessBusinessProxy(req));
                }
            }
        });
    }

    private MessageResp accessBusinessProxy(MessageReq req) {
        // 实例化响应消息出来
        MessageResp msgResp = null;
        if (req.rspClass == null) {
            msgResp = new BaseResp();
        } else {
            try {
                msgResp = req.rspClass.newInstance();
            } catch (Exception e) {
                e.printStackTrace();
                msgResp = new BaseResp();
                msgResp.errorCode = ErrorCode.NEW_RSP_FAILED;
                msgResp.errMsg = e.getMessage();
                return msgResp;
            }
        }

        String url = req.url;
        String methodName = req.methodName;
        Log.error("accessBusinessProxy", url + methodName);
        String netUrl = String.format("%s%s", url, methodName);
        boolean shouldCache = req.shouldCache;
        int cacheExpiretime = req.cacheExpiretime;

        shouldCache = ProxyCache.getProxyCache().shouldReadCacheFromBackendServiceConfigWithURL(netUrl, req.params);
        if (shouldCache) {
            // cacheExpiretime 单位:毫秒
            Log.debug(TAG_NET, "Read Cache!  >>" + netUrl);
            cacheExpiretime = ProxyCache.getProxyCache().getCacheExpireTimeFromBackendServiceWithURL(netUrl, req.params) * 1000;
        }

        // 是否需要请求网络
        boolean shouldRequestNet = false;
        // 如果不存在缓存
        if (!shouldCache) {
            shouldRequestNet = true;
        }

        // 如果需要缓存，则携带url，业务参数，缓存时间调用网络缓存接口
        else {
            @SuppressWarnings("unchecked")
            Map<String, Object> cache = (Map<String, Object>) ProxyCache.getProxyCache().fetchCachedDataWithProxy(methodName, req.params,
                    cacheExpiretime);

            if (cache != null) {
                int eCode = (Integer) cache.get("errorCode");
                if (eCode == ErrorCode.SUCC) {
                    String data = JSONParser.toJSONString(cache);
                    try {
                        msgResp.parse(data);
                        msgResp.errorCode = ErrorCode.SUCC;
                    } catch (Exception e) {
                        // 解析失败了。。
                        shouldRequestNet = true;
                        msgResp.errorCode = ErrorCode.PARSE_RSP_JSON_FAILED;
                        msgResp.errMsg = e.getMessage();
                    }
                } else {
                    msgResp.errorCode = eCode;
                }
            }
            // 重新访问网络，并保存缓存
            else {
                shouldRequestNet = true;
            }
        }

        // 需要请求网络
        if (shouldRequestNet) {

            Log.debug(TAG_NET, "[REQ-RAW]-" + req.httpMethod + " " + netUrl + " >>>>>>" + req.getData());
            String logCacheKey = NetLogHelper.getInstance().putRequest(req.methodName, req.httpMethod.name(), req.getData());
            // 对请求参数加密
            boolean encryptSuccess = EncryptionManager.getInstance().encryptionWithRequest(req);
            String encryptedReqData = req.getData();
            if (encryptSuccess) {
                Log.debug(TAG_NET, "[REQ]-" + req.httpMethod + " " + netUrl + " >>>>>>" + encryptedReqData);
            }
            ServiceResp resp = null;
            if (ENABLE_OKHTTP) {
                resp = okSyncSendMessage(netUrl, encryptedReqData, req.httpMethod, (req.encryptionType != null));
            } else {
                resp = syncSendMessage(netUrl, encryptedReqData, req.httpMethod, (req.encryptionType != null));
            }

            if (resp.retCode == ErrorCode.SUCC) {
                // 对响应数据解密
                boolean success = EncryptionManager.getInstance().decryptionWithRequest(req, resp);
                if (success) {
                    if (ENABLE_OKHTTP) {
                        Log.debug(TAG_NET, "[RESPONSE-RAW](okHttp) " + netUrl + " >>>>>>" + resp.content);
                    } else {
                        Log.debug(TAG_NET, "[RESPONSE-RAW] " + netUrl + " >>>>>>" + resp.content);
                    }
                }
                try {
                    // 通道返回了成功，进一步解析消息内容。。
                    NetLogHelper.getInstance().putResponse(logCacheKey, resp.content);
                    msgResp.parse(resp.content);
                } catch (Exception e) {
                    // 解析失败了。。
                    Log.error(TAG_NET, "[ERROR] URL=" + url + " >>>>>>" + e.getMessage());
                    e.printStackTrace();
                    msgResp.errorCode = ErrorCode.PARSE_RSP_JSON_FAILED;
                    msgResp.errMsg = e.getMessage();
                    NetLogHelper.getInstance().putResponse(logCacheKey, e.getMessage());

                }
            } else {
                msgResp.errorCode = resp.retCode;
                msgResp.errMsg = resp.errMsg;
                msgResp.errorInfo = resp.errInfo;
                NetLogHelper.getInstance().putResponse(logCacheKey, resp.errMsg);

            }

            // 如果需要缓存并且网络请求成功保存网络缓存
//			if (shouldCache && msgResp.errorCode == ErrorCode.SUCC) {
            if (msgResp.errorCode == ErrorCode.SUCC) {
                try {
                    if (msgResp.cacheTime > 0) {
                        HashMap<String, Object> cachedData = (HashMap<String, Object>) JSONParser.parse(resp.content);
                        ProxyCache.getProxyCache().saveCacheWithData(cachedData, methodName, req.params, cacheExpiretime);
                        ProxyCache.getProxyCache().setCacheConfigFromBackendServiceWithURL(netUrl, req.params, msgResp.cacheTime);
                    }
                } catch (JSONException e) {
                    Log.error(TAG_NET, "[ERROR] URL=" + url + " >>>>>>" + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
        return msgResp;
    }

    private ServiceResp okSyncSendMessage(final String url, String reqJson, MessageReq.MyHttpMethod httpMethod,
                                          final boolean encrypted) {
        final ServiceResp resp = new ServiceResp();
        try {
            String respStr;
            switch (httpMethod) {
                case GET:
                    respStr = OkHttpRequest.getSync(url, reqJson, mReqHeaders);
                    break;
                case PUT:
                    respStr = OkHttpRequest.putSync(url, reqJson, mReqHeaders);
                    break;
                case DELETE:
                    respStr = OkHttpRequest.deleteSync(url, reqJson, mReqHeaders);
                    break;
                case POST:
                default:
                    respStr = OkHttpRequest.postSync(url, reqJson, mReqHeaders);
                    break;
            }
            if (!encrypted) {
                Log.debug(TAG_NET, "[RESPONSE](okHttp)-" + httpMethod + " " + url + " >>>>>>" + respStr);
            }
            Map<String, Object> results = JSONParser.parse(respStr);
            int eCode = (Integer) results.get("errorCode");
            if (eCode == ErrorCode.SUCC) {
                resp.content = respStr;
            } else {
                resp.errInfo = MapUtils.getMap(results, "errorInfo", null);
                Log.warn(TAG_NET, "errorCode: " + eCode + ", KEY="
                        + EncryptionManager.getInstance().getBusinessEncryptionKey());
            }
            resp.retCode = eCode;
        } catch (SocketTimeoutException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Log.error(TAG_NET, "[ERROR] URL=" + url + " >>>>>>" + e.getMessage());
            resp.retCode = ErrorCode.SERVER_TIMEOUT_ERROR;
            resp.errMsg = e.getMessage();
        } catch (Exception e) {
            Log.error(TAG_NET, "[ERROR] URL=" + url + " >>>>>>" + e.getMessage());
            resp.retCode = ErrorCode.SERVER_ERROR;
            resp.errMsg = e.getMessage();
            e.printStackTrace();
        }
        return resp;
    }

    private ServiceResp syncSendMessage(final String url, String reqJson, final MessageReq.MyHttpMethod httpMethod,
                                        final boolean encrypted) {
        final ServiceResp resp = new ServiceResp();
        ResponseHandlerInterface responseHandler = new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                // TODO Auto-generated method stub
                String respStr = new String(responseBody);
                if (!encrypted) {
                    Log.debug(TAG_NET, "[RESPONSE]-" + httpMethod + " " + url + " >>>>>>" + respStr);
                }
                try {
                    Map<String, Object> results = JSONParser.parse(respStr);
                    int eCode = NumberParser.parseInt(results.get("errorCode"));
                    resp.errMsg = (String) results.get("errorMsg");
                    if (eCode == ErrorCode.SUCC) {
                        resp.content = respStr;
                    } else {
                        resp.errInfo = MapUtils.getMap(results, "errorInfo", null);
                        Log.warn(TAG_NET, "errorCode: " + eCode + ", KEY="
                                + EncryptionManager.getInstance().getBusinessEncryptionKey());
                    }
                    resp.retCode = eCode;
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    Log.error(TAG_NET, "[ERROR] URL=" + url + " >>>>>>" + e.getMessage());
                    e.printStackTrace();
                    resp.retCode = ErrorCode.SERVER_ERROR;
                    resp.errMsg = e.getMessage();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable e) {
                Log.error(TAG_NET, "[ERROR] URL=" + url + " >>>>>>" + e.getMessage());
                e.printStackTrace();
                if (e instanceof ConnectTimeoutException || e instanceof SocketTimeoutException) {
                    resp.retCode = ErrorCode.SERVER_TIMEOUT_ERROR;
                    resp.errMsg = e.getMessage();
                } else {
                    resp.retCode = ErrorCode.SERVER_ERROR;
                    resp.errMsg = e.getMessage();
                }
            }
        };
        responseHandler.setTag(url);
        try {
            switch (httpMethod) {
                case GET:
                    SyncHttpRequest.get(mContext, url, reqJson, responseHandler);
                    break;
                case PUT:
                    SyncHttpRequest.put(mContext, url, reqJson, responseHandler);
                    break;
                case DELETE:
                    SyncHttpRequest.delete(mContext, url, reqJson, responseHandler);
                    break;
                case POST:
                    SyncHttpRequest.post(mContext, url, reqJson, responseHandler);
                    break;
                default:
                    break;
            }
        } catch (UnsupportedEncodingException e) {
            Log.error(TAG_NET, "[ERROR] URL=" + url + " >>>>>>" + e.getMessage());
            e.printStackTrace();
        }
        return resp;
    }
}
