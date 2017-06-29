package test;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import handler.PortHandler;

public class ExcelTest {
	
	@Test
	public void fun1() {
		
		List<Integer> list=new ArrayList<Integer>();
		for (int i = 1; i <=100000; i++) {
			list.add(i);
		}
		//list中有0-10000
		//list2位假定的没有漏扫的
		List<Integer> list2=new ArrayList<Integer>();
		list2.add(10);
		list2.add(12);
		list2.add(15);
		list2.add(19);
		list2.add(20);
		list2.add(25);
		
//		int[] array = new int[];
//		List<Integer> list2 = PortHandler.getReceiveIDs();
		
		
		int max = list2.get(list2.size()-1);
		
		list.removeAll(list2);
		
		for (int i = 0; i < max-list2.size(); i++) {
			System.out.println(list.get(i));
		}
	}
}
