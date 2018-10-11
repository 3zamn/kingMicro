package com.king.utils.cloud;

/**
 * OCR工具类-利用百度api
 * @author King chen
 * @emai 396885563@qq.com
 * @date 2018年8月2日
 */
public class OCRUtils {
	public static final String general_text="https://aip.baidubce.com/rest/2.0/ocr/v1/general_basic";//通用文字识别
	public static final String accurate_text="https://aip.baidubce.com/rest/2.0/ocr/v1/accurate_basic";//通用文字识别（高精度版）
	public static final String web_image_text="https://aip.baidubce.com/rest/2.0/ocr/v1/webimage";//网络图片文字识别
	public static final String idcard_text="https://aip.baidubce.com/rest/2.0/ocr/v1/idcard";//身份证识别
	public static final String bank_cark_text="https://aip.baidubce.com/rest/2.0/ocr/v1/bankcard";//银行卡识别
	public static final String license_plate="https://aip.baidubce.com/rest/2.0/ocr/v1/license_plate";//车牌识别
	public static final String driving_license="https://aip.baidubce.com/rest/2.0/ocr/v1/driving_license";//驾驶证识别
	public static final String vehicle_license="https://aip.baidubce.com/rest/2.0/ocr/v1/vehicle_license";//行驶证识别	
	public static final String passport="https://aip.baidubce.com/rest/2.0/ocr/v1/passport";//护照识别
	public static final String business_card="https://aip.baidubce.com/rest/2.0/ocr/v1/business_card";//营业执照识别/名片识别
	
	public static class Sample {
	    //设置APPID/AK/SK
	    public static final String APP_ID = "11624594";
	    public static final String API_KEY = "09eDHwgoMbg5fpOsrf05vlG8";
	    public static final String SECRET_KEY = "Gy0unRTEHsfwAQjtw42jfyeCus17xWEb";

	   
	}

}
