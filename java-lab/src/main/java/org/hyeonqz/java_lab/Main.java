package org.hyeonqz.java_lab;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Main {
	public static void main (String[] args) {
		// 기존 방식
		Map<String, Integer> map = new HashMap<>();
		// 타입추론 사용
		var map2 = new HashMap<String,Integer>();

		var list = new ArrayList<>();
		list.add("a");
		list.add(1);
		System.out.println(list);

		if( list.get(1) instanceof String) {
			System.out.println("String Type");
		} else if (list.get(1) instanceof Object) {
			System.out.println("뭐지ㄱㄱ");
		}

		Map<String, String> map3 = Map.ofEntries(
			Map.entry("1","2")
		);

		Map<String, String> map4 = Map.of(
			"map4","map4"
		);



	}
}
