package cn.qimate.bike.lock.utils;

import java.math.BigDecimal;

public class ArithUtil {
	private static final int DEF_DIV_SCALE = 10;

	/** 两数相加 */
	public static double add(double d1, double d2) {
		BigDecimal b1 = new BigDecimal(Double.toString(d1));
		BigDecimal b2 = new BigDecimal(Double.toString(d2));
		return b1.add(b2).doubleValue();

	}
	/** 两数相加 */
	public static float add(float d1, float d2) {
		BigDecimal b1 = new BigDecimal(Float.toString(d1));
		BigDecimal b2 = new BigDecimal(Float.toString(d2));
		return b1.add(b2).floatValue();

	}

	/** 两数相減 */
	public static double sub(double d1, double d2) {
		BigDecimal b1 = new BigDecimal(Double.toString(d1));
		BigDecimal b2 = new BigDecimal(Double.toString(d2));
		return b1.subtract(b2).doubleValue();

	}

	/** 两数相減 */
	public static float sub(float d1, float d2) {
		BigDecimal b1 = new BigDecimal(Float.toString(d1));
		BigDecimal b2 = new BigDecimal(Float.toString(d2));
		return b1.subtract(b2).floatValue();

	}
	/** 两数相乘 */
	public static float mul(float f1, int i2) {
		BigDecimal b1 = new BigDecimal(Float.toString(f1));
		BigDecimal b2 = new BigDecimal(Float.toString(i2));
		return b1.multiply(b2).floatValue();

	}
	/** 两数相乘 */
	public static double mul(double d1, double d2) {
		BigDecimal b1 = new BigDecimal(Double.toString(d1));
		BigDecimal b2 = new BigDecimal(Double.toString(d2));
		return b1.multiply(b2).doubleValue();

	}



	/** 两数相除 */
	public static double div(double d1, double d2) {
		return div(d1, d2, DEF_DIV_SCALE);

	}
	/** 两数相除 */
	public static float div(float d1, float d2) {
		return div(d1, d2, DEF_DIV_SCALE);

	}

	public static double div(double d1, double d2, int scale) {
		if (scale < 0) {
			throw new IllegalArgumentException("The scale must be a positive integer or zero");
		}
		BigDecimal b1 = new BigDecimal(Double.toString(d1));
		BigDecimal b2 = new BigDecimal(Double.toString(d2));
		return b1.divide(b2, scale, BigDecimal.ROUND_UP).doubleValue();
	}
	public static float div(float d1, float d2, int scale) {
		if (scale < 0) {
			throw new IllegalArgumentException("The scale must be a positive integer or zero");
		}
		BigDecimal b1 = new BigDecimal(Double.toString(d1));
		BigDecimal b2 = new BigDecimal(Double.toString(d2));
		return b1.divide(b2, scale, BigDecimal.ROUND_UP).floatValue();
	}


	/**
	 * 去除小数点后面的无效零
	 *
	 * @param numStr
	 * @return
	 */
	public static String remove0(String numStr) {
		if (numStr.lastIndexOf('.') != -1) {
			if (numStr.charAt(numStr.length() - 1) == '0') {
				numStr = numStr.substring(0, numStr.length() - 1);
				numStr = remove0(numStr);
			}
			if ('.' == numStr.charAt(numStr.length() - 1)) {
				numStr = numStr.substring(0, numStr.length() - 1);
			}
		}
		return numStr;
	}
}
