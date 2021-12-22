package burp.ui;


import burp.utils.Constants;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ReqDocumentListTree {
    private ExtensionTab parent;
    private ExtensionTab.ReqTableData mainReqData;
    private ArrayList<ExtensionTab.ReqTableData> subReqData;
    private Boolean expandStatus = false; // true = 展开, false = 收起
    private int size = 0;

    public ReqDocumentListTree(ExtensionTab parent) {
        this.parent = parent;
    }

    public void setSubReqData(ArrayList<ExtensionTab.ReqTableData> subReqData) {
        this.subReqData = subReqData;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getSize(){
        return this.size;
    }

    public ExtensionTab.ReqTableData getMainReqData() {
        return this.mainReqData;
    }

    public ArrayList<ExtensionTab.ReqTableData> getSubReqData() {
        return this.subReqData;
    }

    public void setMainReqData(ExtensionTab.ReqTableData mainReqData) {
        this.mainReqData = mainReqData;
    }

    public Boolean getExpandStatus() {
        return this.expandStatus;
    }

    public void expand() {
        if (!this.expandStatus) {
            this.mainReqData.setTreeStatus(Constants.TREE_STATUS_EXPAND);

            List<ExtensionTab.ReqTableData> ReqTableData = this.parent.getReqTable().getTableData();
            int selfIndex = ReqTableData.indexOf(this.mainReqData);
            int mysize = subReqData.size();
            subReqData.sort(Comparator.comparing(Bean->Bean.id));
            setSize(mysize);
            for (int i = 0; i < mysize; i++) {
                ExtensionTab.ReqTableData data = subReqData.get(i);
                /*
                if (i != subReqData.size() - 1) {
                    data.setTreeStatus("┠");
                } else {
                    data.setTreeStatus("┗");
                }
                 */
                ReqTableData.add(selfIndex + 1 + i, data);
            }
            int _id = ReqTableData.size();
            parent.fireTableRowsInserted(selfIndex, _id);
        }
        this.expandStatus = true;
    }

    public void collapse() {
        if (this.expandStatus) {
            this.mainReqData.setTreeStatus(Constants.TREE_STATUS_COLLAPSE);
            List<ExtensionTab.ReqTableData> ReqTableData = this.parent.getReqTable().getTableData();
            int selfIndex = ReqTableData.indexOf(this.mainReqData);
            int mysize = getSize();
            for (int i = 0; i < mysize; i++) {
                ReqTableData.remove(selfIndex + 1);
            }
            int _id = ReqTableData.size();
            parent.fireTableRowsInserted(selfIndex, _id);
        }
        this.expandStatus = false;
    }
}
