package ani.cide;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class Compile {
    private JTextArea textArea1;
    private JPanel panel;
    private JButton okButton;
    File f;
    public Compile(File file){
        f=file;
        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    textArea1.append("\n"+fout);
                    ProcessBuilder pb=new ProcessBuilder();
                    pb.command("xterm","-e",fout+";read -p 'Press any key to exit'");
                    pb.start();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }

            }
        });
    }
    String fout="";
    public void start(){
        try {
            fout=f.getAbsolutePath()+".out";
            textArea1.append(f.getAbsolutePath()+"\n");
            final Process p=Runtime.getRuntime().exec("cc -o "+fout+" "+f.getAbsolutePath());

            Thread t1=new Thread(()->{
                int b=-1;
                InputStream es=p.getErrorStream();
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
                InputStream es=p.getInputStream();
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
            p.waitFor();
            if(p.exitValue()==0){
                okButton.setEnabled(true);
                textArea1.append("\n\nWould you like to run the program?");
            }
            else{
                textArea1.append("\n\nPlease check the errors mentioned above.");

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void start(File f) {
        Compile dialog = new Compile(f);
        JFrame jdialog=new JFrame("Compilation");
        jdialog.add(dialog.panel);
        jdialog.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        jdialog.setSize(500,300);
        jdialog.setVisible(true);
        dialog.start();
    }
}
