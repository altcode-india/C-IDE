package ani.cide;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.charset.Charset;

public class MainForm {
    private JPanel mainPanel;
    private JTextPane textPane1;
    private JButton saveButton;
    private JButton openButton;
    private JButton runButton;
    private JButton exportButton;
    private File f=null;

    public MainForm() {
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                save();
            }
        });
        openButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                open();
            }
        });
        runButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                save();
                Compile.start(f);
            }
        });
    }
    private void open(){
        try {
            if(f!=null)
                if(JOptionPane.showConfirmDialog(null,"Save existing file?")==JOptionPane.YES_OPTION)save();
            JFileChooser jfc=new JFileChooser();
            jfc.setFileFilter(new FileFilter() {
                @Override
                public boolean accept(File f) {
                    return f.getAbsolutePath().endsWith(".c")||f.isDirectory();
                }

                @Override
                public String getDescription() {
                    return "C Source Files";
                }
            });
            jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
            jfc.showOpenDialog(null);
            f=jfc.getSelectedFile();
            FileInputStream fos= new FileInputStream(f);
            StringBuilder sb=new StringBuilder();
            int b;
            while((b=fos.read())!=-1)
                sb.append((char)b);
            fos.close();
            textPane1.setText(sb.toString());
        } catch (Exception e1) {
            e1.printStackTrace();
            JOptionPane.showMessageDialog(null,"Reading was incomplete! Please report.");
        }
    }
    private void save() {
        try {
            if(f==null) {
                JFileChooser jfc = new JFileChooser();
                jfc.setFileFilter(new FileFilter() {
                    @Override
                    public boolean accept(File f) {
                        return f.getAbsolutePath().endsWith(".c") || f.isDirectory();
                    }

                    @Override
                    public String getDescription() {
                        return "C Source Files";
                    }
                });
                jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
                jfc.showSaveDialog(null);
                f = jfc.getSelectedFile();
            }
            FileOutputStream fos=new FileOutputStream(f);
            fos.write(textPane1.getText().getBytes(Charset.forName("utf-8")));
            fos.close();
        } catch (Exception e1) {
            e1.printStackTrace();
            JOptionPane.showMessageDialog(null,"Saving was incomplete! Please report.");
        }
    }

    public static void main(String args[]){
        StyleContext styleContext = new StyleContext();
        Style defaultStyle = styleContext.getStyle(StyleContext.DEFAULT_STYLE);
        Style cwStyle = styleContext.addStyle("ConstantWidth", null);
        StyleConstants.setForeground(cwStyle, Color.BLUE);
        StyleConstants.setBold(cwStyle, true);
        Style cwStyle2 = styleContext.addStyle("ConstantWidth", null);
        StyleConstants.setBackground(cwStyle2,Color.CYAN);
        StyleConstants.setForeground(cwStyle2, Color.WHITE);
        StyleConstants.setBold(cwStyle2, true);
        Style cwStyle3 = styleContext.addStyle("ConstantWidth", null);
        StyleConstants.setForeground(cwStyle3, Color.MAGENTA);
        //StyleConstants.setBold(cwStyle3, true);
        MainForm mf=new MainForm();
        mf.textPane1.setStyledDocument(new KeywordStyledDocument(defaultStyle, cwStyle, cwStyle2, cwStyle3));
        JFrame mainFrame=new JFrame("C IDE");
        mainFrame.setSize(500,300);
        mainFrame.add(mf.mainPanel);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setVisible(true);
    }

}
