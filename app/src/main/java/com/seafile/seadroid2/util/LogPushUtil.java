package com.seafile.seadroid2.util;//package com.baojia.util;
//
//import android.content.Context;
//
//import com.baojia.oschina.base.MyApplication;
//
///**
// * 错误日志 提交至友盟服务器工具类
// *
// * @author Administrator
// *
// */
//public class LogPushUtil {
//
//	/**
//	 * 所需参数Context context , Throwable throwable
//	 *
//	 * @param context
//	 * @param throwable
//	 */
//	public static void commitCrashLog(Context context, Throwable throwable) {
////		if (context == null) {
////			context = MyApplication.getInstance().getApplicationContext();
////		}
////		StringBuffer stringBuffer = new StringBuffer("mobile:"
////				+ MyApplication.getPerferenceUtil().getNokeyString(
////						Constant.UERMOBILE, "") + ";\n");
////		printError(stringBuffer, throwable);
////		if (Constant.DEBUG) {
////			 LogUtil.e("Debug", stringBuffer.toString());
////			throwable.printStackTrace();
////		} else {
////			MobclickAgent.reportError(context, stringBuffer.toString());
////		}
//	}
//
//	private static void printError(StringBuffer stringBuffer,
//			Throwable throwable) {
//		StackTraceElement[] stackTraceElements = throwable.getStackTrace();
//		if (stackTraceElements != null && stackTraceElements.length > 1) {
//			int size = stackTraceElements.length;
//			for (int i = 0; i < size; i++) {
//				StackTraceElement stackTraceElement = stackTraceElements[i];
//				if (stackTraceElement != null) {
//					stringBuffer.append(stackTraceElement.toString() + ";\n");
//				}
//			}
//		}
//		stringBuffer.append(throwable.toString());
//	}
//
//	/**
//	 * 所需参数Context context , Exception throwable
//	 *
//	 * @param context
//	 * @param throwable
//	 */
//	public static void commitCrashLog(Context context, Exception throwable) {
//		if(throwable == null)
//			return;
//
//		if (context == null) {
//			context = MyApplication.getInstance().getApplicationContext();
//		}
//		StringBuffer stringBuffer = new StringBuffer("mobile:"
//				+ MyApplication.getPerferenceUtil().getNokeyString(
//						Constants.UERMOBILE, "") + ";\n");
//		printError(stringBuffer, throwable);
//		if (Constants.DEBUG) {
//			 LogUtil.e("Debug", stringBuffer.toString());
//			throwable.printStackTrace();
//		} else {
//			MobclickAgent.reportError(context, stringBuffer.toString());
//		}
//	}
//}
