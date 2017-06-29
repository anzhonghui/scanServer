package test;

import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;

/**
 * Created by vaf71 on 2017/5/12.
 */
public class TestTcp {
    @Test
    public void client() {
        String str = "";
        Socket sk = null;
        OutputStream os = null;
        InputStream is = null;
        System.out.println("客户端开启！");
        try {
            //1.创建Socket对象，指明服务器的ip和程序端口号
            sk = new Socket(InetAddress.getByName("192.168.137.236"), 8010);
            //2.调用Socket对象的getOutputStream()方法
            os = sk.getOutputStream();


            os.write(str.getBytes());
            sk.shutdownOutput();
            //4.读取服务器返回的信息
            is = sk.getInputStream();
            byte[] b = new byte[20];
            int len;
            while ((len = is.read(b)) != -1) {
                System.out.println(new String(b, 0, len));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            //4.关闭相应的流和Socket
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (sk != null) {
                try {
                    sk.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Test
    public void createMsg(){

    }
}
