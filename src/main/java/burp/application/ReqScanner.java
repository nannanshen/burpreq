package burp.application;

import burp.BurpExtender;
import burp.IHttpRequestResponse;
import burp.IParameter;
import burp.utils.BurpAnalyzedRequest;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class ReqScanner {

    public Map.Entry<Map.Entry<Float,String>, IHttpRequestResponse> detect(IParameter pa,IHttpRequestResponse baseRequestResponse,String paramer,String payload) {
        BurpAnalyzedRequest burpAnalyzedRequest = new BurpAnalyzedRequest(BurpExtender.getCallbacks(),baseRequestResponse);

        long startTime = System.currentTimeMillis();
        IHttpRequestResponse newHttpRequestResponse = burpAnalyzedRequest.makeHttpRequest(pa, payload);
        long endTime = System.currentTimeMillis();
        return new AbstractMap.SimpleImmutableEntry<Map.Entry<Float,String>, IHttpRequestResponse>(new AbstractMap.SimpleImmutableEntry<Float,String>((float)(endTime - startTime)/1000,payload), newHttpRequestResponse);


    }
}
