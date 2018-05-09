package com.app.proxyservice;

import android.text.TextUtils;

import com.app.config.Settings;
import com.app.log.Log;
import com.app.proxyservice.MessageReq.EncryptionType;
import com.app.util.AESUtils;
import com.app.util.JSONParser;
import com.app.util.MapUtils;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EncryptionManager {

	private static final String TAG = "EncryptionManager";
	private static final String PLATFORM_KEY = "pagodabaiguoyuan";
	private static EncryptionManager sInstance = null;

	private String mBusinessEncryptionKey;
	private String mPlatformEncryptionKey;

	private EncryptionManager() {
		mPlatformEncryptionKey = PLATFORM_KEY;
		mBusinessEncryptionKey = Settings.getInstance().get(Settings.KEY_USERTOKEN_FOR_DECRYPTION, null);
	}

	public static EncryptionManager getInstance() {
		if (sInstance == null) {
			sInstance = new EncryptionManager();
		}
		return sInstance;
	}

	public String getBusinessEncryptionKey() {
		return mBusinessEncryptionKey;
	}

	public void setBusinessEncryptionKey(String businessEncryptionKey) {
		this.mBusinessEncryptionKey = businessEncryptionKey;
	}

	public boolean encryptionWithRequest(MessageReq req) {
		if (req == null || req.encryptionType == null) {
			return false;
		}
		try {
			if (req.encryptionType == EncryptionType.TYPE_PLATFORM) {
				String encrypted = AESUtils.encrypt(mPlatformEncryptionKey, req.getData());
				req.setEncryptData(encrypted, null);

			} else if (req.encryptionType == EncryptionType.TYPE_BUSINESS) {
				String userToken = MapUtils.getString(req.params, "userToken");
				String encrypted = AESUtils.encrypt(mBusinessEncryptionKey, req.getData());
				req.setEncryptData(encrypted, userToken);
			} else {
				// 未加密
			}
			return true;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Log.error(TAG, "加密失败，ERROR: " + req.url + req.methodName);
			e.printStackTrace();
		}
		return false;
	}

	public boolean decryptionWithRequest(MessageReq req, ServiceResp resp) {
		if (req == null || req.encryptionType == null || 
				resp == null || TextUtils.isEmpty(resp.content)) {
			return false;
		}
		String encryptedContent = resp.content;
		try {
			Pattern pattern = Pattern.compile("\"data\":\"([0-9a-zA-Z].+?)\"");
			Matcher m = pattern.matcher(encryptedContent);
			String encryptedStr = null; 
			if(m.find()){
				encryptedStr = m.group(1);
			} else {
				Log.debug(TAG, "无需解密，未找到data字段");
				return false;
			}
			if (req.encryptionType == EncryptionType.TYPE_PLATFORM) {
				String decrypted = AESUtils.decrypt(mPlatformEncryptionKey, encryptedStr);
				int end = Math.max(decrypted.lastIndexOf("]"), decrypted.lastIndexOf("}"));
				decrypted = decrypted.substring(0, end+1);
				
				generateBusinessKey(decrypted);
				if (decrypted.startsWith("[") || decrypted.startsWith("{")) {
					resp.content = encryptedContent.replace("\"" + encryptedStr + "\"", decrypted);
				} else {
					resp.content = encryptedContent.replace(encryptedStr, decrypted);
				}
				
			} else if (req.encryptionType == EncryptionType.TYPE_BUSINESS) {
				String decrypted = AESUtils.decrypt(mBusinessEncryptionKey, encryptedStr);
				int end = Math.max(decrypted.lastIndexOf("]"), decrypted.lastIndexOf("}"));
				decrypted = decrypted.substring(0, end + 1);
				
				if (decrypted.startsWith("[") || decrypted.startsWith("{")) {
					resp.content = encryptedContent.replace("\"" + encryptedStr + "\"", decrypted);
				} else {
					resp.content = encryptedContent.replace(encryptedStr, decrypted);
				}
				
			} else { 
				// 未加密
			}
			return true;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.error(TAG, "解密失败，ERROR: " + req.url + req.methodName);
		}
		return false;
	}

	// 将token利用平台密钥解密，得到用户密钥
	private void generateBusinessKey(String decrypted) throws Exception {
		// TODO Auto-generated method stub
		HashMap<String, Object> dataMap = (HashMap<String, Object>) JSONParser.parse(decrypted);
		if(!dataMap.containsKey("userToken")) {
			return;
		}
		String token = MapUtils.getString(dataMap, "userToken");

		// 解密后内容为如下结构 "9|9151dd8a7fd7|NaN" ，需要拆分取得真正的用户密钥
		String businessKeyContent = AESUtils.decrypt(mPlatformEncryptionKey, token);
		if (businessKeyContent.matches(".+?|.+?|.+?")) {
			mBusinessEncryptionKey = businessKeyContent.split("\\|")[1];
			Settings.getInstance().put(Settings.KEY_USERTOKEN_FOR_DECRYPTION, mBusinessEncryptionKey);
		}
		Log.debug(TAG, "generateBusinessKey: " + mBusinessEncryptionKey);
	}
	
	

}
