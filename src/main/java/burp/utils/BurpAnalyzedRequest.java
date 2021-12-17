package burp.utils;

import burp.*;

import java.util.ArrayList;
import java.util.List;

public class BurpAnalyzedRequest {
    private IBurpExtenderCallbacks callbacks;

    private IExtensionHelpers helpers;

    private CustomBurpHelpers customBurpHelpers;

    private IHttpRequestResponse requestResponse;

    private List<IParameter> eligibleParameters = new ArrayList<>();


    public BurpAnalyzedRequest(IBurpExtenderCallbacks callbacks, IHttpRequestResponse requestResponse) {
        this.callbacks = callbacks;
        this.helpers = this.callbacks.getHelpers();


        this.customBurpHelpers = new CustomBurpHelpers(callbacks);

        this.requestResponse = requestResponse;

        initEligibleParameters();
    }

    public IHttpRequestResponse requestResponse() {
        return this.requestResponse;
    }

    public IRequestInfo analyzeRequest() {
        return this.helpers.analyzeRequest(this.requestResponse.getRequest());
    }

    /**
     * 初始化所有符合条件的参数
     */
    private void initEligibleParameters() {


        if (analyzeRequest().getParameters().isEmpty()) {
            return;
        }

        for (IParameter p : analyzeRequest().getParameters()) {

            this.eligibleParameters.add(p);
        }

    }

    /**
     * 获取所有符合条件的json参数
     *
     * @return List<IParameter>
     */
    public List<IParameter> getEligibleParameters() {
        return this.eligibleParameters;
    }

    /**
     * 判断站点是否有符合条件的参数
     *
     * @return
     */
    public Boolean isSiteEligibleParameters() {
        if (this.getEligibleParameters().size() > 0) {
            return true;
        }

        return false;
    }

    /**
     * 会根据程序类型自动组装请求的 请求发送接口
     *
     * @param p
     * @param payload
     * @return
     */
    public IHttpRequestResponse makeHttpRequest(IParameter p, String payload) {
        byte[] newRequest;

        List<String> newHeaders = new ArrayList<>();
        newHeaders = this.analyzeRequest().getHeaders();


        // 数据处理
        if (this.analyzeRequest().getContentType() == 4) {
            // POST请求包提交的数据为json时的处理
            newRequest = this.buildHttpMessage(p, payload);
        } else {
            // 普通数据格式的处理
            newRequest = this.buildParameter(p, payload, null, newHeaders);
        }

        IHttpRequestResponse newHttpRequestResponse = this.callbacks.makeHttpRequest(this.requestResponse().getHttpService(), newRequest);
        return newHttpRequestResponse;
    }

    /**
     * json数据格式请求处理方法
     *
     * @param payload
     * @return
     */
    private byte[] buildHttpMessage(IParameter p, String payload) {
        String requestBody = this.customBurpHelpers.getHttpRequestBody(this.requestResponse().getRequest());
        String pl;
        String pj1;
        String pj2;
        if(p.getValue().equals("")){
            pl = "\"" + p.getName() + "\"" + ":" + "\"" + payload + "\"";
            pj1 = "\"" + p.getName() + "\"" + ":" + "\"\"";
            pj2 = pj1;
        }else {
            pl = "\"" + p.getName() + "\"" + ":" + "\"" + p.getValue() +payload + "\"";
            pj1 = "\"" + p.getName() + "\"" + ":" + "\"" + p.getValue() + "\"";
            pj2 = "\"" + p.getName() + "\"" + ":" + p.getValue();
        }



        requestBody = requestBody.replace(pj1, pl);
        requestBody = requestBody.replace(pj2, pl);

        byte[] newRequest = this.helpers.buildHttpMessage(
                this.analyzeRequest().getHeaders(),
                this.helpers.stringToBytes(requestBody));
        return newRequest;
    }

    /**
     * 普通数据格式的参数构造方法
     *
     * @param p
     * @param payload
     * @return
     */
    private byte[] buildParameter(IParameter p, String payload, byte[] request, List<String> headers) {
        byte[] newRequest;

        if (request == null) {
            newRequest = this.requestResponse().getRequest();
        } else {
            newRequest = request;
        }

        // 添加header头
        newRequest = this.helpers.buildHttpMessage(
                headers,
                this.customBurpHelpers.getHttpRequestBody(newRequest).getBytes());

        IParameter newParameter = this.helpers.buildParameter(
                p.getName(),
                p.getValue()+payload,
                p.getType()
        );

        return this.helpers.updateParameter(newRequest, newParameter);
    }
}