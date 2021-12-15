package burp.application;

import burp.BurpExtender;
import burp.IHttpRequestResponse;
import burp.IScanIssue;
import burp.utils.CommonUtils;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class ReqScanner {

    public ReqScanner() {

    }

    private final String[] payloads = new String[]{
            "",
            "'",
            "' and '1'='1",
            "' or '1'='1"
    };

    public List<Map.Entry<Map.Entry<Float,String>, IHttpRequestResponse>> detect(IHttpRequestResponse baseRequestResponse) {
        ArrayList<Map.Entry<Map.Entry<Float,String>, IHttpRequestResponse>> s = new ArrayList();
        for(String payload : payloads){
            long startTime = System.currentTimeMillis();
            IHttpRequestResponse newHttpRequestResponse = BurpExtender.getCallbacks().makeHttpRequest(baseRequestResponse.getHttpService(), baseRequestResponse.getRequest());
            long endTime = System.currentTimeMillis();
            s.add(new AbstractMap.SimpleImmutableEntry<Map.Entry<Float,String>, IHttpRequestResponse>(new AbstractMap.SimpleImmutableEntry<Float,String>((float)(endTime - startTime)/1000,payload), newHttpRequestResponse));
        }
        return s;
    }
}
