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
    private  boolean hasParam = false;

    private final String[] payloads = new String[]{
            "",
            "'",
            "' and '1'='1",
            "' or '1'='1"
    };

    public void SetParam(){
        hasParam = true;
    }

    public List<Map.Entry<Map.Entry<Float,String>, IHttpRequestResponse>> detect(IHttpRequestResponse baseRequestResponse,String paramer) {
        IParameter pa = null;
        for (IParameter p : BurpExtender.getHelpers().analyzeRequest(baseRequestResponse.getRequest()).getParameters()) {
            if(p.getType() == 2){
                continue;
            }
            if(paramer.equals(p.getName())){
                SetParam();
                pa = p;
                break;
            }
        }
        if(hasParam){
            ArrayList<Map.Entry<Map.Entry<Float,String>, IHttpRequestResponse>> s = new ArrayList();
            BurpAnalyzedRequest burpAnalyzedRequest = new BurpAnalyzedRequest(BurpExtender.getCallbacks(),baseRequestResponse);
            for(String payload : payloads){
                long startTime = System.currentTimeMillis();
                IHttpRequestResponse newHttpRequestResponse = burpAnalyzedRequest.makeHttpRequest(pa, payload);
                long endTime = System.currentTimeMillis();
                s.add(new AbstractMap.SimpleImmutableEntry<Map.Entry<Float,String>, IHttpRequestResponse>(new AbstractMap.SimpleImmutableEntry<Float,String>((float)(endTime - startTime)/1000,payload), newHttpRequestResponse));
            }
            return s;
        }
        return null;

    }
}
