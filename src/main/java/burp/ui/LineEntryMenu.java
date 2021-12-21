package burp.ui;



import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.awt.event.ActionEvent;


public class LineEntryMenu extends JPopupMenu {

    private static ExtensionTab.ReqTable reqTable;


    public LineEntryMenu(final ExtensionTab.ReqTable reqTable, final int[] modleRows, final int columnIndex) {
        this.reqTable = reqTable;

        JMenuItem itemNumber = new JMenuItem(new AbstractAction(modleRows.length + " Items Selected") {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
            }
        });

        JMenuItem removeItem = new JMenuItem(new AbstractAction("Delete This Entry") {//need to show dialog to confirm
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                int result = JOptionPane.showConfirmDialog(null,"Are you sure to DELETE these items ?");
                if (result == JOptionPane.YES_OPTION) {
                    int num = 0;
                    for(int i : modleRows){
                        reqTable.removeRow(i-num);
                        num++;
                    }
                }else {
                    return;
                }
            }
        });

        LineEntryMenu.this.add(itemNumber);
        LineEntryMenu.this.add(removeItem);
    }
}