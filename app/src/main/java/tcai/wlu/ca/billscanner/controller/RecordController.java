package tcai.wlu.ca.billscanner.controller;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tcai.wlu.ca.billscanner.bean.MonthBill;
import tcai.wlu.ca.billscanner.bean.Record;
import tcai.wlu.ca.billscanner.common.Common;
import tcai.wlu.ca.billscanner.db.DBHelper;


/**
 * Created by Administrator on 2017/4/4.
 */

public class RecordController {

    private DBHelper dbHelper;
    SQLiteDatabase db;
    Context context;

    private MonthBillController controller;

    public RecordController(Context context) {
        this.context = context;
        controller = new MonthBillController(context);
        dbHelper = new DBHelper(context);
    }

    public void insertRecord(Record bill) {
        bill.setId(null);
        db = dbHelper.getWritableDatabase();
        long index= db.insert(Record.TABLE, null, getCV(bill));
        System.out.println("index--------------->"+index);
        bill.setId((int)index);
        db.close();
        updateMB(bill);
    }

    public void updateRecord(Record bill) {
        Record initBill = getRecordById(bill.getId());
        db = dbHelper.getWritableDatabase();
        String whereClause = Record.ID + "=?";
        String[] whereArgs = {String.valueOf(bill.getId())};
        db.update(Record.TABLE, getCV(bill), whereClause, whereArgs);
        db.close();
        updateMB(bill, initBill);
    }

    private void updateMB(Record bill, Record beforeBill) {
        MonthBill mb = controller.getBillByKey(beforeBill.getMonth(), beforeBill.getYear(),beforeBill.getType());
        if(mb==null)
            return;
        mb.setAmount(mb.getAmount() - beforeBill.getTotalPrice());
        controller.insertOrUpdateMonthBill(mb);
        MonthBill nowmb = controller.getBillByKey(bill.getMonth(), bill.getYear(),bill.getType());
        if(nowmb==null){
            nowmb = new MonthBill();
            nowmb.setMonth(bill.getMonth());
            nowmb.setYear(bill.getYear());
            nowmb.setType(bill.getType());
            nowmb.setAmount(0);
        }
        nowmb.setAmount(nowmb.getAmount() + bill.getTotalPrice());
        controller.insertOrUpdateMonthBill(nowmb);
    }

    private void updateMB(Record bill) {
        Map<Integer, MonthBill> bills = controller.getBillByMonth(bill.getMonth(), bill.getYear());
        MonthBill mb = bills.get(bill.getType());
        if (mb == null) {
            mb=new MonthBill();
            mb.setMonth(bill.getMonth());
            mb.setAmount(bill.getTotalPrice());
            mb.setYear(bill.getYear());
            mb.setType(bill.getType());
        } else {
            mb.setAmount(mb.getAmount() + bill.getTotalPrice());
        }
        controller.insertOrUpdateMonthBill(mb);
    }


    public void deleteRecord(Record bill) {
        db = dbHelper.getWritableDatabase();
        String whereClause = Record.ID + "= ?";
        String[] whereArgs = {String.valueOf(bill.getId())};
        int index = db.delete(Record.TABLE, whereClause, whereArgs);
        db.close();
        if(index == 0)
            return;
        Map<Integer, MonthBill> bills = controller.getBillByMonth(bill.getMonth(), bill.getYear());
        MonthBill mb = bills.get(bill.getType());
        mb.setAmount(mb.getAmount() - bill.getTotalPrice());
        controller.insertOrUpdateMonthBill(mb);
    }

    private Record getRecordById(int id) {
        db = dbHelper.getReadableDatabase();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Cursor cursor = db.query(Record.TABLE,
                new String[]{Record.YEAR, Record.MONTH, Record.TYPE, Record.ID, Record.TOTAL_PRICE, Record.TAX, Record.ITEM, Record.TIME, Record.DISCOUNT, Record.PRICE},
                Record.ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null);
        if (cursor.moveToNext()) {
            Record record = new Record();
            record.setMonth(cursor.getInt(cursor.getColumnIndex(Record.MONTH)));
            record.setYear(cursor.getInt(cursor.getColumnIndex(Record.YEAR)));
            record.setType(cursor.getInt(cursor.getColumnIndex(Record.TYPE)));
            record.setId(cursor.getInt(cursor.getColumnIndex(Record.ID)));
            record.setTotalPrice(cursor.getFloat(cursor.getColumnIndex(Record.TOTAL_PRICE)));
            record.setTax(cursor.getFloat(cursor.getColumnIndex(Record.TAX)));
            record.setItem(cursor.getString(cursor.getColumnIndex(Record.ITEM)));
            record.setDiscount(cursor.getFloat(cursor.getColumnIndex(Record.DISCOUNT)));
            record.setPrice(cursor.getFloat(cursor.getColumnIndex(Record.PRICE)));
            record.setTime(cursor.getString(cursor.getColumnIndex(Record.TIME)));
            db.close();
            return record;
        }
        return null;
    }

    public List<Record> getRecordsByMonth(Integer month, Integer year) {
        List<Record> records = new ArrayList<>();
        db = dbHelper.getReadableDatabase();
//        db.execSQL("delete from record");
//        db.execSQL("delete from monthBill");

        Cursor cursor;
        if (year != null && month != null) {
            cursor = db.query(Record.TABLE,
                    new String[]{Record.MONTH, Record.YEAR, Record.TYPE, Record.ID, Record.TOTAL_PRICE, Record.TAX, Record.ITEM, Record.TIME, Record.DISCOUNT, Record.PRICE},
                    Record.MONTH + "=? and " + Record.YEAR + "=?",
                    new String[]{String.valueOf(month), String.valueOf(year)}, null, null, Record.TIME+" desc");
        } else {
            cursor = db.query(Record.TABLE,
                    new String[]{Record.MONTH, Record.YEAR, Record.TYPE, Record.ID, Record.TOTAL_PRICE, Record.TAX, Record.ITEM, Record.TIME, Record.DISCOUNT, Record.PRICE},
                    null, null, null, null, null);
        }
        while (cursor.moveToNext()) {
            Record record = new Record();
            record.setMonth(cursor.getInt(cursor.getColumnIndex(Record.MONTH)));
            record.setYear(cursor.getInt(cursor.getColumnIndex(Record.YEAR)));
            record.setType(cursor.getInt(cursor.getColumnIndex(Record.TYPE)));
            record.setId(cursor.getInt(cursor.getColumnIndex(Record.ID)));
            record.setTotalPrice(cursor.getFloat(cursor.getColumnIndex(Record.TOTAL_PRICE)));
            record.setTax(cursor.getFloat(cursor.getColumnIndex(Record.TAX)));
            record.setItem(cursor.getString(cursor.getColumnIndex(Record.ITEM)));
            record.setDiscount(cursor.getFloat(cursor.getColumnIndex(Record.DISCOUNT)));
            record.setPrice(cursor.getFloat(cursor.getColumnIndex(Record.PRICE)));
            try {
                record.setTime(cursor.getString(cursor.getColumnIndex(Record.TIME)));
            } catch (Exception e) {
                e.printStackTrace();
            }
            records.add(record);
        }
        db.close();
        return records;
    }

    private ContentValues getCV(Record bill) {
        ContentValues cv = new ContentValues();
        cv.put(Record.TYPE, bill.getType());
        cv.put(Record.TOTAL_PRICE, bill.getTotalPrice());
        cv.put(Record.DISCOUNT, bill.getDiscount());
        cv.put(Record.ID, bill.getId());
        cv.put(Record.ITEM, bill.getItem());
        cv.put(Record.PRICE, bill.getPrice());
        cv.put(Record.TAX, bill.getTax());
        cv.put(Record.MONTH, bill.getMonth());
        cv.put(Record.YEAR, bill.getYear());
        if(bill.getTime() != null)
        cv.put(Record.TIME,bill.getTime());
        return cv;
    }
}
