package ani.cide;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

public class Compile {
    private JTextArea textArea1;
    private JPanel panel;
    private JButton okButton;
    File f;
    public Compile(File file,final JFrame parent){
        f=file;
        okButton.addActionListener(e -> {
            try {
                ProcessBuilder pb=new ProcessBuilder();
                String commands=fout+";echo \" \";echo \" \";echo \"-----End of Program-----\";read -p 'Press any key to exit'";
                pb.command("xterm","-e",commands);
                System.out.println(commands);
                pb.start();
                parent.dispose();
            } catch (IOException e1) {
                e1.printStackTrace();
            }

        });
    }
    String fout="";
    public void start(File fexp){
        try {
            fout=fexp==null?f.getAbsolutePath()+".out":fexp.getAbsolutePath();
            textArea1.append(f.getAbsolutePath()+"\n");
            final Process p=Runtime.getRuntime().exec("cc -o "+fout+" "+f.getAbsolutePath());

            Thread t1=new Thread(()->{
                int b=-1;
                InputStreamReader es=new InputStreamReader(p.getErrorStream(),Charset.forName("utf-8"));

                try {
                    while(p.isAlive()||(b=es.read())!=-1){
                        if(b!=-1){
                            System.out.print((char)b);

                            textArea1.append(""+(char)b);
                        }
                    }
                    System.out.println("tHREAD IS DEAD!");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }),t2=
            new Thread(()->{
                int b=-1;
                InputStreamReader es=new InputStreamReader(p.getInputStream(),Charset.forName("utf-8"));
                try {
                    while(p.isAlive()||(b=es.read())!=-1){
                        if(b!=-1){
                            System.out.print((char)b);
                            textArea1.append(""+(char)b);
                        }
                    }
                    System.out.println("tHREAD IS DEAD!");

                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            t1.start();
            t2.start();
            t1.join();
            t2.join();
            new Thread(()-> {
                try {
                    p.waitFor();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (p.exitValue() == 0) {
                    okButton.setEnabled(true);
                    textArea1.append("\n\nWould you like to run the program?");
                } else {
                    textArea1.append("\n\nPlease check the errors mentioned above.");

                }
            }).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void start(File f,boolean export) {
        JFrame.setDefaultLookAndFeelDecorated(true);
        JFrame jdialog=new JFrame("Compilation");
        Compile dialog = new Compile(f,jdialog);
        jdialog.add(dialog.panel);
        jdialog.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        jdialog.setSize(500,300);
        if(export){
            JFileChooser jfc=new JFileChooser();
            int result=jfc.showSaveDialog(null);
            if(result==JFileChooser.APPROVE_OPTION){
                File exp=jfc.getSelectedFile();
                jdialog.setVisible(true);
                dialog.start(exp);
                jdialog.dispose();
                JOptionPane.showMessageDialog(null,"The executable was created at: "+exp.getAbsolutePath());
            }

        }
        else {
            jdialog.setVisible(true);
            dialog.start(null);
        }
    }
}
