package timer;

import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import handler.PortHandler;

public class ReceiveMsgTask extends TimerTask {
	
	final Logger logger = LoggerFactory.getLogger("TIMER");
	
	@Override
	public void run() {
		
		//list2记录的是，如果不释放，第二次还会去掉所有的
		List<Integer> list2 = PortHandler.getReceiveIDs();
		
		//加逻辑，已进入找最小值
		
		int max = list2.get(list2.size()-1);
		List<Integer> idList = new ArrayList<>();
		
		TimerManager.list.removeAll(PortHandler.getReceiveIDs());
		int i = 0;
		while(TimerManager.list.get(i) <= max){
			idList.add(TimerManager.list.get(i));
			i++;
		}
		
		if(idList.size() == 0 || idList.isEmpty()){
			PortHandler.getReceiveIDs().clear();
			logger.debug("该范围内没有漏扫，清空集合");
		}else{
			logger.debug("漏扫的ID为：{}",idList.toString());
			WriteExcel.writeExcel(idList);
		}
	}

}
