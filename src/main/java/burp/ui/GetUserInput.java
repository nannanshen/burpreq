package burp.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class GetUserInput {

        private JFrame jFrame = new JFrame("输入参数");
        private Container c = jFrame.getContentPane();
        private JLabel a1 = new JLabel("参数名");
        private JTextField param = new JTextField();
        private JButton okbtn = new JButton("确定");
        private JButton cancelbtn = new JButton("取消");
        private String text = "";
        private boolean isOk = false;

        public void setText(String inputtext){
            this.text= inputtext;
        }

        public String getText(){
            return this.text;
        }

        public boolean getisOk(){
            return isOk;
        }

        public void setisOk(){
            this.isOk = true;
        }

        public GetUserInput() {
            //设置窗体的位置及大小
            jFrame.setBounds(600, 200, 300, 220);
            //设置一层相当于桌布的东西
            c.setLayout(new BorderLayout());//布局管理器
            //jFrame.setUndecorated(true);
            //设置按下右上角X号后关闭
            jFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            jFrame.addWindowListener(new WindowAdapter() {//关闭窗口事件
                public void windowClosing(WindowEvent e) {
                    setisOk();
                }
            });
            //初始化--往窗体里放其他控件
            init();
            //设置窗体可见
            jFrame.setVisible(true);
            listerner();
        }

        public void init() {
            /*标题部分--North*/
            JPanel titlePanel = new JPanel();
            titlePanel.setLayout(new FlowLayout());
            titlePanel.add(new JLabel("输入需要测试的参数名，为*则全部测试"));
            c.add(titlePanel, "North");

            /*输入部分--Center*/
            JPanel fieldPanel = new JPanel();
            fieldPanel.setLayout(null);
            a1.setBounds(50, 20, 50, 20);
            fieldPanel.add(a1);
            param.setBounds(110, 20, 120, 40);
            fieldPanel.add(param);
            c.add(fieldPanel, "Center");

            /*按钮部分--South*/
            JPanel buttonPanel = new JPanel();
            buttonPanel.setLayout(new FlowLayout());
            buttonPanel.add(okbtn);
            buttonPanel.add(cancelbtn);
            c.add(buttonPanel, "South");

        }





        public void listerner() {

            //确认按下去获取
            okbtn.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    setText(param.getText());
                    setisOk();
                    jFrame.dispose();
                }
            });
            //取消按下去清空
            cancelbtn.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    setisOk();
                    jFrame.dispose();
                }
            });
        }

}