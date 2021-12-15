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

    public Map.Entry<Long, IHttpRequestResponse> detect(IHttpRequestResponse baseRequestResponse) {
        List<IHttpRequestResponse> reqs = new ArrayList<>();
        List<Long> scantimes = new ArrayList<>();
        for(int i=0;i<3;i++){
            long startTime = System.nanoTime();
            IHttpRequestResponse newres = baseRequestResponse.
            reqs.add(newres);

        }
        return new AbstractMap.SimpleImmutableEntry<Long, IHttpRequestResponse>(
                System.nanoTime() - startTime, response);
    }
}
