package burp.utils;

import java.util.Date;
import java.util.List;
import java.util.Random;

public class CustomHelpers {
    /**
     * 随机取若干个字符
     *
     * @param number
     * @return String
     */
    public static String randomStr(int number) {
        StringBuffer s = new StringBuffer();
        char[] stringArray = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i',
                'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u',
                'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6',
                '7', '8', '9'};
        Random random = new Random();
        for (int i = 0; i < number; i++) {
            char num = stringArray[random.nextInt(stringArray.length)];
            s.append(num);
        }
        return s.toString();
    }

    /**
     * 获取精确到秒的时间戳
     *
     * @param date
     * @return Integer
     */
    public static Integer getSecondTimestamp(Date date) {
        if (null == date) {
            return 0;
        }
        String timestamp = String.valueOf(date.getTime() / 1000);
        return Integer.valueOf(timestamp);
    }

    /**
     * 判断某个List中是否存在指定的key
     * 注意: 大小写不区分
     * 如果该 key 存在, 则返回 true, 否则返回 false。
     *
     * @param val1 规定要查找的字符串
     * @param l1   规定要搜索的List
     * @return
     */
    public static Boolean listKeyExists(String val1, List<String> l1) {
        for (String s : l1) {
            if (s.toLowerCase().equals(val1.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断某个List中是否搜索的到指定的key
     * 注意: 大小写不区分
     * 如果该 key 存在, 则返回 true, 否则返回 false。
     *
     * @param val1 规定要查找的字符串
     * @param l1   规定要搜索的List
     * @return
     */
    public static Boolean listKeySearch(String val1, List<String> l1) {
        for (String s : l1) {
            if (s.toLowerCase().contains(val1.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取参数数据
     * 例如:
     * getParam("token=xx;Identifier=xxx;", "token"); 返回: xx
     *
     * @param d         被查找的数据
     * @param paramName 要查找的字段
     * @return
     */
    public static String getParam(final String d, final String paramName) {
        if (d == null || d.length() == 0)
            return null;

        String value = "test=test;" + d;

        final int length = value.length();
        int start = value.indexOf(';') + 1;
        if (start == 0 || start == length)
            return null;

        int end = value.indexOf(';', start);
        if (end == -1)
            end = length;

        while (start < end) {
            int nameEnd = value.indexOf('=', start);
            if (nameEnd != -1 && nameEnd < end
                    && paramName.equals(value.substring(start, nameEnd).trim())) {
                String paramValue = value.substring(nameEnd + 1, end).trim();
                int valueLength = paramValue.length();
                if (valueLength != 0)
                    if (valueLength > 2 && '"' == paramValue.charAt(0)
                            && '"' == paramValue.charAt(valueLength - 1))
                        return paramValue.substring(1, valueLength - 1);
                    else
                        return paramValue;
            }

            start = end + 1;
            end = value.indexOf(';', start);
            if (end == -1)
                end = length;
        }

        return null;
    }
}