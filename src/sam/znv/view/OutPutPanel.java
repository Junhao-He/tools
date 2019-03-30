package sam.znv.view;

import sam.znv.TestPro;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * 数据配置信息
 * 
 * @author YDL
 *
 */
public class OutPutPanel extends JPanel {

    private static final long serialVersionUID = 1L;
    private JLabel outputIpLabel;
    private JButton sendBtn;
	//定义图片地址
    public static String filepath;

    public OutPutPanel() {
        super();
        this.setBackground(Color.white);
        this.setLayout(null);

        outputIpLabel = new JLabel("选择图片");
        outputIpLabel.setBounds(40, 20, 100, 30);
        this.add(outputIpLabel);
        
        JButton selectFileBtn = new JButton("选择文件");
        selectFileBtn.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent arg0) {
        		JFileChooser chooser = new JFileChooser("./");             //设置选择器 
        		chooser.setMultiSelectionEnabled(true);   //设为多选
        		int returnVal = chooser.showOpenDialog(null);        //是否打开文件选择框
        		System.out.println("returnVal="+returnVal); 
        		if (returnVal == JFileChooser.APPROVE_OPTION) {          //如果符合文件类型 
        			filepath = chooser.getSelectedFile().getAbsolutePath();      //获取绝对路径
        			System.out.println(filepath); 
        			System.out.println("You chose to open this file: "+ chooser.getSelectedFile().getName());  //输出相对路径 }
        		}
        	}
        });
        selectFileBtn.setBounds(160, 20, 100, 30);
        this.add(selectFileBtn);
        
        sendBtn = new JButton("发送");
        sendBtn.setBounds(280, 20, 100, 30);
        this.add(sendBtn);
        sendBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
                TestPro.sendIdx1(filepath);
                System.out.println("发送：" + filepath);
			}
		});
    }


}
