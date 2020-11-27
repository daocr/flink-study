package com.huilong.mock;

/**
 * @author daocr
 * @date 2020/11/25
 */
public class ProjectContext {


    /**
     * 获取 checkpoint 保存路径
     *
     * @param cls
     * @return
     */
    public static String getCheckPoint(Class cls) {
        return cls.getResource("/") + "resources/checkpoint";
    }

    /**
     * 获取 savepoint 保存路径
     *
     * @param cls
     * @return
     */
    public static String getSavePoint(Class cls) {
        return cls.getResource("/") + "resources/savepoint";
    }

}
