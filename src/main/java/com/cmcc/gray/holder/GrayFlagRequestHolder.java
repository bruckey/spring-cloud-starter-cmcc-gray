package com.cmcc.gray.holder;


public class GrayFlagRequestHolder {
    /**
     * 标记使用的灰度版本
     */
    private static final ThreadLocal<String> grayFlag = new ThreadLocal<>();

    public static void setGrayTag(final String tag) {
        grayFlag.set(tag);
    }

    public static String getGrayTag() {
        return grayFlag.get();
    }

    public static void remove() {
        grayFlag.remove();
    }

}