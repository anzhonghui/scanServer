package listener;

import exception.ReadDataFromSerialPortFailure;
import exception.SerialPortInputStreamCloseFailure;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import handler.PortHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.ExceptionUtils;
import utils.SerialTool;

import java.io.UnsupportedEncodingException;

public class PortListener implements SerialPortEventListener {
    final Logger logger = LoggerFactory.getLogger("SCAN");
    private SerialPort serialPort;
    PortHandler portHandler = new PortHandler();

    public void setPortListener(SerialPort serialPort) {
        this.serialPort = serialPort;
    }

    @Override
    public void serialEvent(SerialPortEvent serialPortEvent) {

        switch (serialPortEvent.getEventType()) {

            case SerialPortEvent.BI: // 10 通讯中断
                logger.error("与串口设备通讯中断");
                break;

            case SerialPortEvent.OE: // 7 溢位（溢出）错误

            case SerialPortEvent.FE: // 9 帧错误

            case SerialPortEvent.PE: // 8 奇偶校验错误

            case SerialPortEvent.CD: // 6 载波检测

            case SerialPortEvent.CTS: // 3 清除待发送数据

            case SerialPortEvent.DSR: // 4 待发送数据准备好了

            case SerialPortEvent.RI: // 5 振铃指示

            case SerialPortEvent.OUTPUT_BUFFER_EMPTY: // 2 输出缓冲区已清空
                break;

            case SerialPortEvent.DATA_AVAILABLE: // 1 串口存在可用数据
                // 延时收到字符串0.5秒，足够接收512字节，以免出现字符串隔断
                try {
                    Thread.sleep(900);
                } catch (InterruptedException e) {
                    logger.error(ExceptionUtils.getTrace(e));
                }
                byte[] data = null;

                try {
                    if (serialPort == null) {
                        logger.error("\n串口对象为空！监听失败！");
                    } else {
                        data = SerialTool.readFromPort(serialPort); // 读取数据，存入字节数组
                        String receive = new String(data,"GBK").trim();

                        logger.debug("\n从串口读取到数据:{}", receive);

                        portHandler.depacketize(receive);//存到数据库
                    }
                } catch (ReadDataFromSerialPortFailure
                        | SerialPortInputStreamCloseFailure e) {
                    logger.error("串口读取错误");
                    System.exit(0); // 发生读取错误时显示错误信息后退出系统
                } catch (UnsupportedEncodingException e) {
                    logger.error(ExceptionUtils.getTrace(e));
                }
                break;
        }
    }
}
