package com.quizcorona.ws.restapis;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.codehaus.jettison.json.JSONObject;

public class Utility {
	
	public static Collection<JSONObject> getPage(Collection<JSONObject> residents,int startValue, int len) {
		List<JSONObject> jsonObjects = new ArrayList<JSONObject>();
		int count =0;
		int endVal = startValue + len;
//		Sb91Service.getLogService(Sb91Constants.LOGIN).info(startValue + " : "+ endVal);
		for (JSONObject jsonObject : residents) {
			if(count >= startValue && count < endVal){
				jsonObjects.add(jsonObject);
			}
			count++;
			if(count == endVal)
				break;
		}
		return jsonObjects;
	}

}
