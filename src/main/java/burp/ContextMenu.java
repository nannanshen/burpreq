package burp;

import burp.utils.Executor;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class ContextMenu implements IContextMenuFactory {
    private static final HashSet<Byte> availableToolFlag = new HashSet<>();

    static {
        availableToolFlag.add(IContextMenuInvocation.CONTEXT_PROXY_HISTORY);
        availableToolFlag.add(IContextMenuInvocation.CONTEXT_MESSAGE_EDITOR_REQUEST);
        availableToolFlag.add(IContextMenuInvocation.CONTEXT_MESSAGE_VIEWER_REQUEST);
    }

    @Override
    public List<JMenuItem> createMenuItems(IContextMenuInvocation invocation) {
        if (availableToolFlag.contains(invocation.getInvocationContext())) {
            ArrayList<JMenuItem> menuItemList = new ArrayList<>();
            JMenuItem menuItem = new JMenuItem("Do Req scan");
            menuItem.addActionListener(new ContextMenuActionListener(invocation));
            menuItemList.add(menuItem);
            return menuItemList;
        } else {
            return null;
        }
    }

    static class ContextMenuActionListener implements ActionListener {
        IContextMenuInvocation invocation;

        public ContextMenuActionListener(IContextMenuInvocation invocation) {
            this.invocation = invocation;
        }

        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            CompletableFuture.supplyAsync(() -> {
                ActiveScanner activeScanner = BurpExtender.getActiveScanner();

                IHttpRequestResponse[] httpRequestResponses = invocation.getSelectedMessages();

                for (IHttpRequestResponse httpRequestResponse : httpRequestResponses) {
                    activeScanner.doActiveScan(httpRequestResponse,null);
                }
                return null;
            }, Executor.getExecutor());
        }
    }
}
