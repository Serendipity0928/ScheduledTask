package com.spl.util;

import org.apache.commons.lang3.StringUtils;

public class OsascriptUtil {

    private static final String osascriptTemplate = "display notification \"{infoContent}\" with title \"{infoTitle}\" subtitle \"{subTitle}\"";

    /**
     * MAC弹窗提醒
     * @param title 标题
     * @param subTitle 副标题
     * @param content 弹窗显示内容
     * example: OsascriptUtil.triggerInfo("今日美好", "2022-10-27", "这是一首好听的歌曲");
     */
    public static void triggerInfo(String title, String subTitle, String content) {
        if(StringUtils.isBlank(content) || StringUtils.isBlank(title) || StringUtils.isBlank(subTitle)) {
            return;
        }

        String osascriptInfo = osascriptTemplate.replace("{infoContent}", content)
                .replace("{infoTitle}", title).replace("{subTitle}", subTitle);
        System.out.println("MAC弹窗，title=" + title + ",content=" + content);

        try {
            Process process = Runtime.getRuntime().exec(new String[]{"osascript", "-e", osascriptInfo});
            int status = process.waitFor();

            if(status != 0) {
                System.out.println("MAC弹窗执行失败！status=" + status);
            }
        } catch (Exception e) {
            System.out.println("MAC弹窗执行异常！");
        }

    }
}
