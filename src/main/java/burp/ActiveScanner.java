package burp;

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

    @Override
    public List<IScanIssue> doActiveScan(IHttpRequestResponse httpRequestResponse, IScannerInsertionPoint insertionPoint) {
        URL httpRequestURL = BurpExtender.getHelpers().analyzeRequest(httpRequestResponse).getUrl();
        String requestUrl = CommonUtils.getUrlWithoutFilename(httpRequestURL);

        // 目前检测的查重是将 http://user:pass@host:port/deep/path/filename?query#fragment
        // 归一化为 http://host:port/deep/path 后检测是否扫描过, 如果未来有对 query 有相关检测需求, 可以在修改 Common.getUrlWithoutFilename


        List<IHttpRequestResponse> reqs = this.ReqScanner.detect(httpRequestResponse);
        ExtensionTab extensionTab = BurpExtender.getExtensionTab();
        for(IHttpRequestResponse req : reqs){
            ReqDocumentListTree ReqDocumentListTree = new ReqDocumentListTree(extensionTab);
            IExtensionHelpers helpers = BurpExtender.getHelpers();
            ExtensionTab.ReqTableData mainReqData = new ExtensionTab.ReqTableData(false, ReqDocumentListTree,requestUrl, String.valueOf(helpers.analyzeResponse(req.getResponse()).getStatusCode()),String.valueOf(helpers.analyzeRequest(req).getMethod()), req,null,null,null);
            ArrayList<ExtensionTab.ReqTableData> subReqData = new ArrayList<>();
            mainReqData.setTreeStatus(Constants.TREE_STATUS_COLLAPSE);
            ReqDocumentListTree.setMainReqData(mainReqData);
            ReqDocumentListTree.setSubReqData(subReqData);
        }

        return  null;
    }

    public List<IScanIssue> parseReqDocument(ArrayList<ReqType> ReqTypes) {
        List<IScanIssue> issues = new ArrayList<>();
        ExtensionTab extensionTab = BurpExtender.getExtensionTab();

        //遍历扫到的ReqType
        for (ReqType ReqType : ReqTypes) {
            Map<String, IHttpRequestResponse> ReqDocuments = ReqType.getReqDocuments();
            //遍历ReqType中的接口文档

            for (Map.Entry<String, IHttpRequestResponse> entry : ReqDocuments.entrySet()) {
                ReqDocumentListTree ReqDocumentListTree = new ReqDocumentListTree(extensionTab);

                ExtensionTab.ReqTableData mainReqData = new ExtensionTab.ReqTableData(false, ReqDocumentListTree, String.valueOf(this.scannedCount), entry.getKey(), String.valueOf(BurpExtender.getHelpers().analyzeResponse(entry.getValue().getResponse()).getStatusCode()), ReqType.getReqTypeName(), "true", entry.getValue(), CommonUtils.getCurrentDateTime());
                ArrayList<ExtensionTab.ReqTableData> subReqData = new ArrayList<>();

                mainReqData.setTreeStatus(Constants.TREE_STATUS_COLLAPSE);

                ReqDocumentListTree.setMainReqData(mainReqData);
                ReqDocumentListTree.setSubReqData(subReqData);

                // 排序
                List<ReqEndpoint> ReqEndpoints = ReqType.parseReqDocument(entry.getValue());
                ReqEndpoints.sort(Comparator.comparing(ReqEndpoint::getUrl));

                // 遍历接口文档中的接口
                for (ReqEndpoint ReqEndpoint : ReqEndpoints) {
                    IHttpRequestResponse ReqParseRequestResponse = ReqEndpoint.getHttpRequestResponse();
                    ExtensionTab.ReqTableData currentData = new ExtensionTab.ReqTableData(true,
                            ReqDocumentListTree,
                            "",
                            ReqEndpoint.getUrl(),
                            String.valueOf(BurpExtender.getHelpers().analyzeResponse(ReqParseRequestResponse.getResponse()).getStatusCode()),
                            ReqType.getReqTypeName(),
                            (BurpExtender.getHelpers().analyzeResponse(ReqParseRequestResponse.getResponse()).getStatusCode() != 200 && BurpExtender.getHelpers().analyzeResponse(ReqParseRequestResponse.getResponse()).getStatusCode() != 405 && BurpExtender.getHelpers().analyzeResponse(ReqParseRequestResponse.getResponse()).getStatusCode() != 500 ? "false" : "true"),
                            ReqParseRequestResponse,
                            CommonUtils.getCurrentDateTime());

                    subReqData.add(currentData);
                }

                extensionTab.add(ReqDocumentListTree);
                this.scannedCount++;
            }

            // Req 指纹检测 - 报告输出
            issues.addAll(ReqType.exportIssues());
            // Req 指纹检测 - 控制台报告输出
            BurpExtender.getStdout().print(ReqType.exportConsole());
        }
        return issues;
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
