package burp;
import burp.application.ReqScanner;
import burp.ui.ReqDocumentListTree;
import burp.ui.ExtensionTab;
import burp.utils.Constants;
import burp.utils.Executor;

import javax.swing.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class ActiveScanner implements IScannerCheck {
    private final ReqScanner ReqScanner;
    private IParameter pa = null;

    public ActiveScanner() {
        this.ReqScanner = new ReqScanner();
    }

    public ReqScanner getReqScanner() {
        return ReqScanner;
    }



    public IExtensionHelpers helpers = BurpExtender.getHelpers();

    @Override
    public List<IScanIssue> doActiveScan(IHttpRequestResponse httpRequestResponse, IScannerInsertionPoint insertionPoint) {
        ArrayList paraList = new ArrayList();
        for (IParameter p : BurpExtender.getHelpers().analyzeRequest(httpRequestResponse.getRequest()).getParameters()) {
            if(p.getType() == 2){
                continue;
            }
            paraList.add(p.getName());
        }
        if(paraList.size()<1){
            return null;
        }
        Object[] options= new Object[paraList.size()];
        for(int i=0;i<paraList.size();i++){
            options[i] = paraList.get(i);
        }
        Object myoption = JOptionPane.showInputDialog(null,"选择要测试的参数","参数",JOptionPane.QUESTION_MESSAGE,
                null,options,options[0]);
        String paramer = String.valueOf(myoption);
        //String paramer = JOptionPane.showInputDialog(null, "请输入要测试的参数：\n", "参数", JOptionPane.PLAIN_MESSAGE);
        if(paramer==null || paramer.equals("null") || paramer.equals("")){
            return null;
        }
        URL httpRequestURL = BurpExtender.getHelpers().analyzeRequest(httpRequestResponse).getUrl();
        for (IParameter p : BurpExtender.getHelpers().analyzeRequest(httpRequestResponse.getRequest()).getParameters()) {
            if(p.getType() == 2){
                continue;
            }
            if(paramer.equals(p.getName())){
                pa = p;
                break;
            }
        }
        if(pa==null){
            return null;
        }


        ExtensionTab extensionTab = BurpExtender.getExtensionTab();
        ReqDocumentListTree ReqDocumentListTree = new ReqDocumentListTree(extensionTab);
        ExtensionTab.ReqTableData mainReqData = new ExtensionTab.ReqTableData(false,
                ReqDocumentListTree,
                "none",
                httpRequestURL.toString(),
                "unknow",
                String.valueOf(helpers.analyzeRequest(httpRequestResponse).getMethod()),
                httpRequestResponse,
                paramer,
                "none",
                "unknow",
                "unknow");
        ArrayList<ExtensionTab.ReqTableData> subReqData = new ArrayList<>();
        mainReqData.setTreeStatus(Constants.TREE_STATUS_COLLAPSE);
        ReqDocumentListTree.setMainReqData(mainReqData);
        ReqDocumentListTree.setSubReqData(subReqData);
        extensionTab.add(ReqDocumentListTree);


        for(Map.Entry<String, String> payloadMap : Constants.payloadsMap.entrySet()){
            CompletableFuture.supplyAsync(() -> {
                String payload = payloadMap.getValue();
                String id = payloadMap.getKey();
                Map.Entry<Map.Entry<Float,String>, IHttpRequestResponse> re = this.ReqScanner.detect(pa,httpRequestResponse,paramer,payload);
                if(re == null){
                    return null;
                }
                IHttpRequestResponse req = re.getValue();
                String scantime = String.valueOf(re.getKey().getKey());
                String mypayload = re.getKey().getValue();
                IResponseInfo responseInfo = this.helpers.analyzeResponse(req.getResponse());
                int httpResponseBodyOffset = responseInfo.getBodyOffset();
                int httpResponseBodyLength = req.getResponse().length - httpResponseBodyOffset;
                ExtensionTab.ReqTableData currentData = new ExtensionTab.ReqTableData(true,
                        ReqDocumentListTree,
                        id,
                        httpRequestURL.toString(),
                        String.valueOf(helpers.analyzeResponse(req.getResponse()).getStatusCode()),
                        String.valueOf(helpers.analyzeRequest(req).getMethod()),
                        req,
                        paramer,
                        mypayload,
                        String.valueOf(httpResponseBodyLength),
                        scantime
                );

                subReqData.add(currentData);
                return null;
            }, Executor.getExecutor());
        }


        //extensionTab.add(ReqDocumentListTree);

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
