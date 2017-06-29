package timer;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Timer;

public class TimerManager {

	// 时间间隔(一天)
	private static final long PERIOD_DAY = 24 * 60 * 60 * 1000;
	
	//用于存放对比的连续ID
	public static List<Integer> list= new ArrayList<Integer>();

	public TimerManager() {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, 13); // 凌晨1点
		calendar.set(Calendar.MINUTE, 32);
		calendar.set(Calendar.SECOND, 0);
		Date date = calendar.getTime(); // 第一次执行定时任务的时间
		// 如果第一次执行定时任务的时间 小于当前的时间
		// 此时要在 第一次执行定时任务的时间加一天，以便此任务在下个时间点执行。如果不加一天，任务会立即执行。
		if (date.before(new Date())) {
			date = this.addDay(date, 1);
		}
		Timer timer = new Timer();
		ReceiveMsgTask delMsgTask = new ReceiveMsgTask();
		// 安排指定的任务在指定的时间开始进行重复的固定延迟执行。
		timer.schedule(delMsgTask, date, PERIOD_DAY);
		
		//初始化连续ID
		for (int i = 1; i <=1000000; i++) {
			list.add(i);
		}
	}

	// 增加或减少天数
	public Date addDay(Date date, int num) {
		Calendar startDT = Calendar.getInstance();
		startDT.setTime(date);
		startDT.add(Calendar.DAY_OF_MONTH, num);
		return startDT.getTime();
	}
	
//	public static void main(String[] args) {
//		new TimerManager();
//	}
}
