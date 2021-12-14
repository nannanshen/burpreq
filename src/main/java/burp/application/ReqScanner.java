package burp.application;

import burp.BurpExtender;
import burp.IHttpRequestResponse;
import burp.IScanIssue;
import burp.utils.CommonUtils;

import java.util.ArrayList;
import java.util.List;


public class ReqScanner {

    public ReqScanner() {

    }

    public List<IHttpRequestResponse> detect(IHttpRequestResponse baseRequestResponse) {
        List<IHttpRequestResponse> reqs = new ArrayList<>();
        for(int i=0;i<3;i++){
            reqs.add(baseRequestResponse);
        }
        return reqs;
    }
}
