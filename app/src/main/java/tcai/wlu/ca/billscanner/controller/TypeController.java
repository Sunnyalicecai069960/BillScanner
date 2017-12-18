package tcai.wlu.ca.billscanner.controller;

import android.content.Context;
import android.content.Intent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import tcai.wlu.ca.billscanner.bean.Type;
import tcai.wlu.ca.billscanner.common.Common;


/**
 * Created by Administrator on 2017/4/3.
 */

public class TypeController {
    final static String FILE_NAME = "type.json";
    final static String classify="textClassify.json";
    final static String CODE = "code";
    final static String COLOR="color";
    final static String TYPE = "type";
    public static Map<Integer,List<Type>> getKeys(Context context){
        String jsonText=Common.getJson(context,classify);
        Map<Integer,List<Type>> keys=new TreeMap<>();
        try {
            JSONArray jsonArray=new JSONArray(jsonText);
            for(int i=0;i<jsonArray.length();i++){
                List list=new ArrayList();
                JSONObject jsonObject=jsonArray.getJSONObject(i);
                JSONArray array=jsonObject.getJSONArray("key");
                for(int j=0;j<array.length();j++){
                    Type type=new Type();
                    type.setCode(Integer.parseInt(jsonObject.getString("code")));
                    type.setType(array.getJSONObject(j).getString("key"));
                    list.add(type);
                }
                keys.put(i,list);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return keys;
    }
    public static Map<Integer,Type> getTypes(Context context){
        String jsonText = Common.getJson(context,FILE_NAME);
        Map<Integer,Type> types = new TreeMap<>();
        try{
            JSONArray array = new JSONArray(jsonText);
            for(int i =0;i<array.length();i++) {
                JSONObject object = array.getJSONObject(i);
                Type type = new Type();
                type.setCode(object.getInt(CODE));
                type.setType(object.getString(TYPE));
                if(type.getCode()!=Common.TOTAL_CODE) {
                    String color = object.getString(COLOR);
                    type.setRed(Integer.valueOf(color.substring(1, 3), 16));
                    type.setGreen(Integer.valueOf(color.substring(3, 5), 16));
                    type.setBlue(Integer.valueOf(color.substring(5, 7), 16));
                }
                types.put(type.getCode(),type);
            }
        }catch (JSONException e){
            e.printStackTrace();
        }
        return types;
    }

}
