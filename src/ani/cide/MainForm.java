package ani.cide;

import com.sun.javaws.exceptions.ErrorCodeResponseException;

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
    private JButton saveAsButton;
    protected File f=null;
    boolean edited=false;
    public MainForm() {
        saveButton.addActionListener(e -> save(false));
        openButton.addActionListener(e -> open(null));
        runButton.addActionListener(e -> {
            if(save(false))
            Compile.start(f,false);
        });
        saveAsButton.addActionListener(e -> save(true));

        exportButton.addActionListener(e -> {
            if(save(false))
                Compile.start(f,true);

        });
    }
    private void open(File file){
        try {
            if(f!=null)
                if(JOptionPane.showConfirmDialog(null,"Save existing file?")==JOptionPane.YES_OPTION)save(false);
            int result=JFileChooser.APPROVE_OPTION;
            if(file==null) {
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
                result = jfc.showOpenDialog(null);
                if (result == JFileChooser.APPROVE_OPTION) {
                    f = jfc.getSelectedFile();
                }
            }
            else f=file;
            if(result==JFileChooser.APPROVE_OPTION&&f.exists()){
                FileInputStream fos = new FileInputStream(f);
                StringBuilder sb = new StringBuilder();
                int b;
                while ((b = fos.read()) != -1)
                    sb.append((char) b);
                fos.close();
                textPane1.setText(sb.toString());
            }
        } catch (Exception e1) {
            e1.printStackTrace();
            JOptionPane.showMessageDialog(null,"Reading was incomplete! Please report.");
        }
    }
    private boolean save(boolean as) {
        try {
            int result=JFileChooser.APPROVE_OPTION;
            if(f==null||as) {
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
                result=jfc.showSaveDialog(null);
                if(result==JFileChooser.APPROVE_OPTION) {
                    f = jfc.getSelectedFile();
                    if (!f.getAbsolutePath().endsWith(".c")) f = new File(f.getAbsolutePath() + ".c");
                }
            }
            if(result==JFileChooser.APPROVE_OPTION) {
                FileOutputStream fos = new FileOutputStream(f);
                fos.write(textPane1.getText().getBytes(Charset.forName("utf-8")));
                fos.close();
                return true;
            }
        } catch (Exception e1) {
            e1.printStackTrace();
            JOptionPane.showMessageDialog(null,"Saving was incomplete! Please report.");
        }
        return false;
    }

    public static void main(String args[]){
        JFrame.setDefaultLookAndFeelDecorated(true);
        StyleContext styleContext = new StyleContext();
        Style defaultStyle = styleContext.getStyle(StyleContext.DEFAULT_STYLE);

        Style cwStyle = styleContext.addStyle("ConstantWidth", null);
        StyleConstants.setForeground(cwStyle, Color.BLUE);
        StyleConstants.setBold(cwStyle, true);
        Style cwStyle2 = styleContext.addStyle("ConstantWidth", null);
        StyleConstants.setForeground(cwStyle2, Color.CYAN);
        StyleConstants.setBold(cwStyle2, true);
        Style cwStyle3 = styleContext.addStyle("ConstantWidth", null);
        StyleConstants.setForeground(cwStyle3, Color.MAGENTA);
        //StyleConstants.setBold(cwStyle3, true);
        MainForm mf=new MainForm();
        mf.textPane1.setStyledDocument(new KeywordStyledDocument(defaultStyle, cwStyle, cwStyle2, cwStyle3));
        mf.textPane1.setFont(mf.mainPanel.getFont().deriveFont(16.0f));
        JFrame mainFrame=new JFrame("C IDE");
        mainFrame.setSize(500,300);
        mainFrame.add(mf.mainPanel);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setVisible(true);
        if(args.length==1){
            File f=new File(args[0]);
            if(f.isDirectory()){
                System.out.println("Usage:\n" +
                        "cide [path to C source file]" +
                        "\n");
                System.err.println("Error: Your path is a directory");
                System.exit(1);
            }
            else mf.open(f);
        }
    }

}
