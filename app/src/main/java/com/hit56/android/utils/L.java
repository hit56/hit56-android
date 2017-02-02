/**
 * @Project Name:WeChat
 * @File Name:L.java
 * @Package Name:com.wuxainedu.util
 * @Date:2016年7月6日下午2:43:13
 * @Copyright(c)2016 www.wuxianedu.com Inc. All rights reserved.
*/

package com.hit56.android.utils;

import android.util.Log;

/**
 * @ClassName:L 
 * @Function: 日志打印类
 * @Date:     2016年7月6日 下午2:43:13 
 * @author   yifeng.Zhang
 * @Copyright(c)2016 www.wuxianedu.com Inc. All rights reserved.
 */
public class L {
	
	//是否为debug状态，非debug状态不打印日志
	public static final boolean IS_DEBUG=true; 
	
	//标签

	public static void i(String tag, Object obj){
		if(IS_DEBUG){
			Log.i(tag,obj.toString());
		}
	}
	public static void w(String tag, Object obj){
		if(IS_DEBUG){
			Log.w(tag,obj.toString());
		}
	}
	public static void e(String tag, Object obj){
		if(IS_DEBUG){
			Log.e(tag,obj.toString());
		}
	}
}

