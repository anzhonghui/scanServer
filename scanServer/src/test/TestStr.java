package test;

import handler.PortHandler;
import org.junit.Test;

/**
 * Created by vaf71 on 2017/5/17.
 */
public class TestStr {
    @Test
    public void addSp(){
        String str = "26110115e711060c0c00118d3c000002303000020001000000010000000102000000000000000aa00000000000003b032100280000032a00af001fc7009f000f049c005f00010203000000080004002e000cb0000000000000000c130000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000002a7a";
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < str.length(); i += 2){
            stringBuffer.append(str.substring(i,i+2));
            stringBuffer.append(" ");
        }
        System.out.println(stringBuffer);
    }

    @Test
    public void testDePack(){
        String str = "2020#14982#1#0#0$00000001000000010000000600000061626364&";
        new PortHandler().depacketize(str);
    }
}
