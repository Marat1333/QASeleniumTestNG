package com.leroy.core.configuration;

@Deprecated
public class DeprecatedCommonUtil {

    /**
     * Filter out all the non-UTF8 chars. Newline is okay
     *
     * @param msg
     * @return
     */
    public static String filterInvalidChars(String msg) {
        if (TextUtil.hasValue(msg)) {
            msg = msg.replaceAll("[^\\n\\x20-\\x7e]", "");
        }

        return msg;
    }
}
