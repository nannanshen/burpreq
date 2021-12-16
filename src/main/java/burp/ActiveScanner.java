package burp;
import burp.ui.GetUserInput;
import burp.application.ReqScanner;
import burp.ui.ReqDocumentListTree;
import burp.ui.ExtensionTab;
import burp.utils.CommonUtils;
import burp.utils.Constants;

import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class ActiveScanner implements IScannerCheck {
    private final ReqScanner ReqScanner;

    public ActiveScanner() {
        this.ReqScanner = new ReqScanner();
    }

    public ReqScanner getReqScanner() {
        return ReqScanner;
    }



    public IExtensionHelpers helpers = BurpExtender.getHelpers();

    @Override
    public List<IScanIssue> doActiveScan(IHttpRequestResponse httpRequestResponse, IScannerInsertionPoint insertionPoint) {
        GetUserInput GetUserInput = new GetUserInput();
        while (true){
            if (GetUserInput.getisOk()){
                break;
            }
        }
        String paramer = GetUserInput.getText();
        if(paramer == ""){
            return null;
        }
        URL httpRequestURL = BurpExtender.getHelpers().analyzeRequest(httpRequestResponse).getUrl();
        List<Map.Entry<Map.Entry<Float,String>, IHttpRequestResponse>> reqs = this.ReqScanner.detect(httpRequestResponse);
        ExtensionTab extensionTab = BurpExtender.getExtensionTab();
        ReqDocumentListTree ReqDocumentListTree = new ReqDocumentListTree(extensionTab);
        ExtensionTab.ReqTableData mainReqData = new ExtensionTab.ReqTableData(false,
                ReqDocumentListTree,
                httpRequestURL.toString(),
                String.valueOf(helpers.analyzeResponse(httpRequestResponse.getResponse()).getStatusCode()),
                String.valueOf(helpers.analyzeRequest(httpRequestResponse).getMethod()),
                httpRequestResponse,
                paramer,
                "none",
                String.valueOf(httpRequestResponse.getResponse().length-helpers.analyzeResponse(httpRequestResponse.getResponse()).getBodyOffset()),
                "none");
        ArrayList<ExtensionTab.ReqTableData> subReqData = new ArrayList<>();
        mainReqData.setTreeStatus(Constants.TREE_STATUS_COLLAPSE);
        ReqDocumentListTree.setMainReqData(mainReqData);
        ReqDocumentListTree.setSubReqData(subReqData);
        for(Map.Entry<Map.Entry<Float,String>, IHttpRequestResponse> re : reqs){
            IHttpRequestResponse req = re.getValue();
            String scantime = String.valueOf(re.getKey().getKey());
            String payload = re.getKey().getValue();
            ExtensionTab.ReqTableData currentData = new ExtensionTab.ReqTableData(true,
                    ReqDocumentListTree,
                    httpRequestURL.toString(),
                    String.valueOf(helpers.analyzeResponse(req.getResponse()).getStatusCode()),
                    String.valueOf(helpers.analyzeRequest(req).getMethod()),
                    httpRequestResponse,
                    paramer,
                    payload,
                    String.valueOf(req.getResponse().length-helpers.analyzeResponse(req.getResponse()).getBodyOffset()),
                    scantime
                    );

            subReqData.add(currentData);
        }
        extensionTab.add(ReqDocumentListTree);

        return  null;
    }

    @Override
    public List<IScanIssue> doPassiveScan(IHttpRequestResponse httpRequestResponse) {
        return null;
    }

    @Override
    public int consolidateDuplicateIssues(IScanIssue existingIssue, IScanIssue newIssue) {
        if (existingIssue.getIssueName().equals(newIssue.getIssueName())) {
            return -1;
        } else {
            return 0;
        }
    }
}
