//--------------------------------------------------------------//
// MyClient.java 2011年04月15日
//--------------------------------------------------------------//

import java.io.*;
import java.net.*;
import java.util.*;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

//--------------------------------------------------------------//
public class MyClient extends JFrame implements ActionListener {

    //設定名子及ip

    String name, ip = "";

    BufferedReader reader;
    PrintStream writer;
    Socket sock;

    BufferedReader readerImage;
    PrintStream writerImage;
    DataInputStream dis;
    DataOutputStream out;
    Socket sock2;

    //顯示區域
    JTextArea incoming = new JTextArea(15, 50);
    //輸入區域
    JTextField outgoing = new JTextField(20);
    JLabel jlmane = new JLabel("你的名字：");
    JLabel jlip = new JLabel("輸入ip：");
    JTextField jfmane = new JTextField("無名", 10);
    JTextField jfip = new JTextField("127.0.0.1", 10);
    JLabel state = new JLabel("請輸入你的名字及你的名字");

    MenuBar mBar = new MenuBar();
    //File
    Menu mFile = new Menu("檔案");
    //Save 
    MenuItem mFileSave = new MenuItem("儲存檔案");
 //--------------------------------------------------------------//
    //-1-主程式進入點
    //--------------------------------------------------------------//

    public static void main(String[] args) {
        MyClient client = new MyClient();       //
    }
 //--------------------------------------------------------------//
    //-2-設定及宣告視窗物件
    //--------------------------------------------------------------//

    MyClient() {
        //設定及宣告視窗物件 
        //建立視窗JFrame
        super("多入連線Client端");
        //用來放mane及ip--設定區域
        JPanel maneipPanel = new JPanel();
        //建來設定按鍵 
        JButton setmaneip = new JButton("連線設定");
        //按下設定
        setmaneip.addActionListener(this);
        //加入到JPanel
        maneipPanel.add(jlmane);
        //名字
        maneipPanel.add(jfmane);
        maneipPanel.add(jlip);
        //位子
        maneipPanel.add(jfip);
        //設定
        maneipPanel.add(setmaneip);
        //排版BorderLayout設定區域在上方----  
        getContentPane().add(BorderLayout.NORTH, maneipPanel);
        //JButton("送出")
        JButton sendButton = new JButton("送出");
        //按下
        sendButton.addActionListener(this);
  //對話區域-----
        //設置為 true，則當行的長度大於所分派的寬度時，將換行
        incoming.setLineWrap(true);
        //設置為 true，則當行的長度大於所分派的寬度時，將在單詞邊界（空白）處換行
        incoming.setWrapStyleWord(true);
        //不可編輯的  
        incoming.setEditable(false);
        //JScrollPane  
        JScrollPane qScroller = new JScrollPane(incoming);
        //垂直滾動  
        qScroller.setVerticalScrollBarPolicy(
                ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        //水平滾動
        qScroller.setHorizontalScrollBarPolicy(
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        JPanel mainPanel = new JPanel();
        mainPanel.add(qScroller);
        mainPanel.add(outgoing);
        mainPanel.add(sendButton);
        //對話區域在中間------
        getContentPane().add(BorderLayout.CENTER, mainPanel);

        //Menu事件
        mFileSave.addActionListener(this);
        //加入MenuItem  
        mFile.add(mFileSave);
        //加入Menu
        mBar.add(mFile);
        //MenuBar
        setMenuBar(mBar);
        //狀態區域在下方----
        getContentPane().add(BorderLayout.SOUTH, state);

        JPanel document = new JPanel();
        //傳圖檔
        JButton getDocument = new JButton("選擇圖片");
        //按下設定
        getDocument.addActionListener(this);
        //加入到JPanel
        document.add(getDocument);

        getContentPane().add(BorderLayout.EAST, document);

        setSize(650, 450);
        setVisible(true);
        //離開 
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.out.println("離開聊天室");
                System.exit(0);
            }
        });
    }

 //--------------------------------------------------------------//
    //-3-建立連線
    //--------------------------------------------------------------//
    private void EstablishConnection() {
        try {
            //請求建立連線
            sock = new Socket(ip, 8888);
            //建立I/O資料流
            InputStreamReader streamReader
                    = //取得Socket的輸入資料流
                    new InputStreamReader(sock.getInputStream());
            //放入暫存區
            reader = new BufferedReader(streamReader);
   //取得Socket的輸出資料流

            writer = new PrintStream(sock.getOutputStream());
   //連線成功

            //請求建立連線
            sock2 = new Socket(ip, 3490);
            //建立I/O資料流
            dis = new DataInputStream(new BufferedInputStream(sock2.getInputStream()));
            out = new DataOutputStream(new BufferedOutputStream(sock2.getOutputStream()));

            //連線成功
            state.setText("網路建立-連線成功");
            System.out.println("網路建立-連線成功");

        } catch (IOException ex) {
            System.out.println("建立連線失敗");
        }
    }
 //--------------------------------------------------------------//
    //-4-接收資料
    //--------------------------------------------------------------//

    public class IncomingReader implements Runnable {

        public void run() {
            String message;

            try {
                while ((message = reader.readLine()) != null) {
                    incoming.append(message + '\n');

                    System.out.println("送出資料11");

                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public class IncomingReader2 implements Runnable {

        public void run() {
            String message;
            try {
                //讀取資料
                while (true) {
                    byte[] buffer = new byte[39923];
                    System.out.println("222222");

                    dis.read(buffer);

                    System.out.println("傳送");
                    tellApiece(buffer);

                }
            } catch (Exception ex) {
                System.out.println("有一個連接離開");
            }
        }
  //--------------------------------------------------------------//
        //-3.3-告訴每人
        //--------------------------------------------------------------//

        public void tellApiece(byte[] buffer) {
            try {
                FileOutputStream fos = new FileOutputStream("1.jpg");
                fos.write(buffer);

                dis.close();

            } catch (Exception ex) {
                System.out.println("傳送");

            }
        }

    }

 //--------------------------------------------------------------//
    //-5-按下之動作
    //--------------------------------------------------------------//
    public void actionPerformed(ActionEvent e) {
        String str = e.getActionCommand();
        //按下設定
        if (str.equals("連線設定")) {
            //設定名字
            name = jfmane.getText();
            //設定ip，此程式目前無作用
            ip = jfip.getText();
            //狀態
            state.setText("設定" + name + ":" + ip);
            //建立連線----
            EstablishConnection();
            //建立接收資料執行緒----
            Thread readerThread = new Thread(new IncomingReader());
            readerThread.start();

            Thread readerThread2 = new Thread(new IncomingReader2());
            readerThread2.start();

            //按下送出   
        } else if (str.equals("送出")) {
            //不可沒有ip及送出空白
            if ((ip != null) && (outgoing.getText() != "")) {
                try {//送出資料
                    writer.println((name + ":" + outgoing.getText()));
                    //刷新該串流的緩衝。
                    writer.flush();
                } catch (Exception ex) {
                    System.out.println("送出資料失敗");
                }
                //清完輸入欄位
                outgoing.setText("");
            }
        } else if (str.equals("選擇圖片")) {
            if (ip != null) {
                try {//送出圖片
                    System.out.println("送出資料123");

                    JFileChooser chooser = new JFileChooser();
                    int ret = chooser.showOpenDialog(null);
                    if (ret == JFileChooser.APPROVE_OPTION) {
                        System.out.println("您選擇開啟此檔: " + chooser.getSelectedFile().getPath());
                    }

                    FileInputStream fis = new FileInputStream(chooser.getSelectedFile().getPath()); // Size =
                    // 39923
                    byte buffer[] = new byte[fis.available()];

                    fis.read(buffer);

                    out.write(buffer);
                    out.flush();
                    out.close();
                    System.out.println(buffer);
                    out.close();
                    //刷新該串流的緩衝。
                    System.out.println("收到");

                } catch (Exception ex) {
                    System.out.println("送出資料失敗");
                }
            }
        }
    }
}
