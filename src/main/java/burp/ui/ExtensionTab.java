package burp.ui;

import burp.*;
import burp.utils.Constants;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ExtensionTab extends AbstractTableModel implements ITab, IMessageEditorController {
    private final String tagName;
    private JSplitPane mainSplitPane;
    private IMessageEditor requestTextEditor;
    private IMessageEditor responseTextEditor;
    private IHttpRequestResponse currentlyDisplayedItem;
    private JScrollPane upScrollPane;
    private JSplitPane downSplitPane;
    private JTabbedPane requestPanel;
    private JTabbedPane responsePanel;
    private JSplitPane upSplitPane;

    private ReqTable ReqTable;

    public ExtensionTab(String name) {
        IBurpExtenderCallbacks callbacks = BurpExtender.getCallbacks();

        this.tagName = name;

        // 创建用户界面
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                // 主分隔面板
                mainSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);

                //上方面板
                upSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
                upSplitPane.setEnabled(false); // 禁止拖动



                // 任务栏面板
                ReqTable = new ReqTable(ExtensionTab.this);
                upScrollPane = new JScrollPane(ReqTable);

                // 前两列设置宽度 30px
                for (int i = 0; i < 1; i++) {
                    ReqTable.getColumnModel().getColumn(i).setMaxWidth(30);
                }

                ReqTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
                    @Override
                    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                        final Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                        if (isSelected) {
                            c.setBackground(Color.decode(Constants.TAB_COLOR_SELECTED));
                        } else {
                            ExtensionTab ReqTable = (ExtensionTab) table.getModel();
                            ReqTableData ReqTableData = ReqTable.getReqTable().getTableData().get(row);
                            if (ReqTableData.isSubData) {
                                c.setBackground(Color.decode(Constants.TAB_COLOR_SUB_DATA));
                            } else {
                                c.setBackground(Color.decode(Constants.TAB_COLOR_MAIN_DATA));
                            }
                        }
                        return c;
                    }
                });

                ReqTable.registerListeners();

                // 请求与响应界面的分隔面板规则
                downSplitPane = new JSplitPane();
                downSplitPane.setResizeWeight(0.5D);

                // 请求的面板
                requestPanel = new JTabbedPane();
                requestTextEditor = callbacks.createMessageEditor(ExtensionTab.this, false);
                requestPanel.addTab("Request", requestTextEditor.getComponent());

                // 响应的面板
                responsePanel = new JTabbedPane();
                responseTextEditor = callbacks.createMessageEditor(ExtensionTab.this, false);
                responsePanel.addTab("Response", responseTextEditor.getComponent());

                // 自定义程序UI组件
                downSplitPane.add(requestPanel, "left");
                downSplitPane.add(responsePanel, "right");

                upSplitPane.add(upScrollPane, "right");

                mainSplitPane.add(upSplitPane, "left");
                mainSplitPane.add(downSplitPane, "right");

                callbacks.customizeUiComponent(mainSplitPane);

                // 将自定义选项卡添加到Burp的UI
                callbacks.addSuiteTab(ExtensionTab.this);
            }
        });
    }

    @Override
    public String getTabCaption() {
        return this.tagName;
    }

    @Override
    public Component getUiComponent() {
        return mainSplitPane;
    }

    @Override
    public int getRowCount() {
        return this.getReqTable().getTableData().size();
    }

    @Override
    public int getColumnCount() {
        return 9;
    }

    @Override
    public String getColumnName(int columnIndex) {
        switch (columnIndex) {
            case 0:
                return " ";
            case 1:
                return "id";
            case 2:
                return "URL";
            case 3:
                return "statusCode";
            case 4:
                return "reqType";
            case 5:
                return "paramer";
            case 6:
                return "payloadStr";
            case 7:
                return "resLen";
            case 8:
                return "scanTime";
        }
        return null;
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return String.class;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        ReqTableData data = this.getReqTable().getTableData().get(rowIndex);
        switch (columnIndex) {
            case 0:
                return data.treeStatus;
            case 1:
                return data.id;
            case 2:
                return data.Url;
            case 3:
                return data.statusCode;
            case 4:
                return data.reqType;
            case 5:
                return data.paramerStr;
            case 6:
                return data.payloadStr;
            case 7:
                return data.resLen;
            case 8:
                return data.scanTime;
        }
        return null;
    }

    @Override
    public byte[] getRequest() {
        return currentlyDisplayedItem.getRequest();
    }

    @Override
    public byte[] getResponse() {
        return currentlyDisplayedItem.getResponse();
    }

    @Override
    public IHttpService getHttpService() {
        return currentlyDisplayedItem.getHttpService();
    }

    /**
     * 新增任务至任务栏面板
     */
    public void add(ReqDocumentListTree ReqDocumentListTree) {
        synchronized (this.ReqTable) {
            this.ReqTable.getTableData().add(ReqDocumentListTree.getMainReqData());
            int _id = this.ReqTable.getTableData().size();
            fireTableRowsInserted(_id, _id);
        }
    }

    public ReqTable getReqTable() {
        return this.ReqTable;
    }

    /**
     * 界面显示数据存储模块
     */
    public static class ReqTableData {
        final String id;
        final String Url;
        final String statusCode;
        final String reqType;
        final IHttpRequestResponse requestResponse;
        final String paramerStr;
        final String payloadStr;
        final String resLen;
        final String scanTime;
        final Boolean isSubData;
        final ReqDocumentListTree parentListTree;
        private String treeStatus = "";

        public ReqTableData(Boolean isSubData, ReqDocumentListTree parentListTree,String id, String Url, String statusCode, String reqType, IHttpRequestResponse requestResponse,String paramerStr,String payloadStr, String resLen,String scanTime) {
            this.isSubData = isSubData;
            this.parentListTree = parentListTree;
            this.id = id;
            this.Url = Url;
            this.statusCode = statusCode;
            this.reqType = reqType;
            this.requestResponse = requestResponse;
            this.paramerStr = paramerStr;
            this.payloadStr = payloadStr;
            this.resLen = resLen;
            this.scanTime = scanTime;
        }

        public void setTreeStatus(String treeStatus) {
            this.treeStatus = treeStatus;
        }
    }

    /**
     * 自定义Table
     */
    public class ReqTable extends JTable {
        private final List<ReqTableData> tableData = new ArrayList<ReqTableData>();

        public ReqTable(TableModel tableModel) {
            super(tableModel);
        }

        public List<ReqTableData> getTableData() {
            return this.tableData;
        }

        public void removeRow(int row) {
            synchronized (ReqTable.this) {
                ReqTableData dataEntry = ExtensionTab.ReqTable.this.tableData.get(convertRowIndexToModel(row));
                int selfIndex = ExtensionTab.ReqTable.this.tableData.indexOf(dataEntry.parentListTree.getMainReqData());
                if (dataEntry.isSubData) {
                    dataEntry.parentListTree.getSubReqData().remove(dataEntry);
                    ExtensionTab.ReqTable.this.tableData.remove(row);
                    dataEntry.parentListTree.setSize(dataEntry.parentListTree.getSize()-1);
                    int _id = ExtensionTab.ReqTable.this.tableData.size();
                    fireTableRowsDeleted(selfIndex, _id);
                }else {
                    int _id = ExtensionTab.ReqTable.this.tableData.size();
                    if(dataEntry.parentListTree.getExpandStatus()){
                        for (int i = 0; i < dataEntry.parentListTree.getSubReqData().size(); i++) {
                            ExtensionTab.ReqTable.this.tableData.remove(selfIndex + 1);
                        }
                    }
                    ExtensionTab.ReqTable.this.tableData.remove(selfIndex);
                    dataEntry.parentListTree.setMainReqData(null);
                    dataEntry.parentListTree.getSubReqData().clear();
                    fireTableRowsDeleted(selfIndex,_id);

                }

            }
        }

        public void changeSelection(int row, int col, boolean toggle, boolean extend) {
            ReqTableData dataEntry = ExtensionTab.ReqTable.this.tableData.get(convertRowIndexToModel(row));

            if (!dataEntry.isSubData) { // 切换状态
                if (dataEntry.parentListTree.getExpandStatus()) {
                    dataEntry.parentListTree.collapse();
                } else {
                    dataEntry.parentListTree.expand();
                }
            }

            requestTextEditor.setMessage(dataEntry.requestResponse.getRequest(), true);
            responseTextEditor.setMessage(dataEntry.requestResponse.getResponse(), false);
            currentlyDisplayedItem = dataEntry.requestResponse;
            super.changeSelection(row, col, toggle, extend);
        }
        public void registerListeners() {
            ReqTable.this.setRowSelectionAllowed(true);
            this.addMouseListener(new MouseAdapter() {

                @Override
                public void mouseReleased(MouseEvent e) {//在windows中触发,因为isPopupTrigger在windows中是在鼠标释放是触发的，而在mac中，是鼠标点击时触发的。
                    //https://stackoverflow.com/questions/5736872/java-popup-trigger-in-linux
                    if (SwingUtilities.isRightMouseButton(e)) {
                        if (e.isPopupTrigger() && e.getComponent() instanceof JTable) {
                            //getSelectionModel().setSelectionInterval(rows[0], rows[1]);
                            int[] rows = getSelectedRows();
                            int col = ((ReqTable) e.getSource()).columnAtPoint(e.getPoint()); // 获得列位置
                            int modelCol = ReqTable.this.convertColumnIndexToModel(col);
                            if (rows.length > 0) {
                                int[] modelRows = ReqTable.getSelectedRows();
                                new LineEntryMenu(ReqTable.this, modelRows, modelCol).show(e.getComponent(), e.getX(), e.getY());
                            } else {
                            }
                        }
                    }
                }
            });
        }
    }
}
