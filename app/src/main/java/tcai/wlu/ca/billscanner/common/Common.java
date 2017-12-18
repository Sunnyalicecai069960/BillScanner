package tcai.wlu.ca.billscanner.common;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Color;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import tcai.wlu.ca.billscanner.bean.MonthBill;
import tcai.wlu.ca.billscanner.bean.Record;
import tcai.wlu.ca.billscanner.bean.Type;
import tcai.wlu.ca.billscanner.controller.TypeController;


/**
 * Created by Administrator on 2017/4/3.
 */

public class Common {

    public final static int TOTAL_CODE = 0;
    private static Map<Integer,Type> typeMap = null;

    public static List<Record> records= new ArrayList<>();
    public static List<Integer> keys = null;
    public static Map<Integer,List<Type>> textClassify=null;
    public static  final SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
    public static int month,year;

    public static Map<Integer,Type> getTypeMap(Context context){
        if(typeMap==null)
            typeMap= TypeController.getTypes(context);
        return typeMap;
    }
    public static Map<Integer,List<Type>>getKeysMap(Context context){
        if(textClassify==null)
            textClassify=TypeController.getKeys(context);
        return textClassify;
    }
    public static String getJson(Context context, String fileName) {
        StringBuilder stringBuilder = new StringBuilder();
        BufferedReader bf = null;
        try {
            AssetManager assetManager = context.getAssets();
            bf = new BufferedReader(new InputStreamReader(
                    assetManager.open(fileName)));
            String line;
            while ((line = bf.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                if(bf!=null)
                    bf.close();
            }catch (IOException e){

            }
        }
        return stringBuilder.toString();
    }

    public static int getCurrentMonth(){
        Calendar calendar=Calendar.getInstance();
        return calendar.get(calendar.MONTH)+1;
    }

    public static int getCurrentYear(){
        Calendar calendar=Calendar.getInstance();
        return calendar.get(calendar.YEAR);
    }

    public static int getCurrentDay(){
        Calendar calendar=Calendar.getInstance();
        return calendar.get(calendar.DAY_OF_MONTH);
    }

    private static String PRE = "/";

    public static String getDayText(int year,int month,int day){
        Format f = new DecimalFormat("00");
        return year + PRE + f.format(month)+PRE+f.format(day);
    }

    public static String getDayText(Date date){
        Format f = new DecimalFormat("00");
        return getYear(date) + PRE + f.format(getMonth(date))+PRE+f.format(getDay(date));
    }

    public static String getMonthText(int year,int month) {
        Format f = new DecimalFormat("00");
        return year + PRE + f.format(month);
    }


    public static int getMonth(Date date){
        if(date == null)
            return 0;
        Calendar calendar=Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(calendar.MONTH)+1;
    }

    public static int getYear(Date date){
        if(date == null)
            return 0;
        Calendar calendar=Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(calendar.YEAR);
    }

    public static int getDay(Date date){
        if(date == null)
            return 0;
        Calendar calendar=Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(calendar.DAY_OF_MONTH);
    }


    public static List<Record> formatRecordBill(Map<Integer,MonthBill> bills){
        List<Record> records=new ArrayList<>();
        MonthBill totalBill = bills.get(TOTAL_CODE);
        if(totalBill==null)
            return records;
        int i = 0;
        for (Integer type : bills.keySet()) {
            MonthBill bill = bills.get(type);
            if (type == Common.TOTAL_CODE ||bill.getAmount()==0)
                continue;
            Record record=new Record();
            record.setTotalPrice(totalBill.getAmount()==0?0:Common.formatMoney(bill.getAmount()/totalBill.getAmount()*100));
            record.setType(type);
            record.setItem(bill.getAmount()+"");
            records.add(record);
            i++;
        }
        return records;
    }

    public static float formatMoney(float money){
        BigDecimal b  =   new BigDecimal(money);
        float   f1   =  b.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
        return f1;
    }

    public static void setRecordTotalPrice(Record record){
        record.setTotalPrice(Common.formatMoney(record.getPrice()*(1-record.getDiscount())*(1+record.getTax())));
    }

    public static String formatDate(Date date){
        if(date==null)
            return null;
        return format.format(date);
    }

    public static Date formatToDate(String str){
        try {
            return format.parse(str);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
}
