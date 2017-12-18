package tcai.wlu.ca.billscanner.controller;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tcai.wlu.ca.billscanner.bean.MonthBill;
import tcai.wlu.ca.billscanner.common.Common;
import tcai.wlu.ca.billscanner.db.DBHelper;

/**
 * Created by Administrator on 2017/4/3.
 */

public class MonthBillController {

    private DBHelper dbHelper;
    SQLiteDatabase db;
    Context context;

    public MonthBillController(Context context) {
        this.context = context;
        dbHelper = new DBHelper(context);
    }

    public void insertOrUpdateMonthBill(MonthBill bill) {
        MonthBill initBill = getBillByKey(bill.getMonth(), bill.getYear(), bill.getType());
        MonthBill totalBill = getBillByKey(bill.getMonth(), bill.getYear(), Common.TOTAL_CODE);
        db = dbHelper.getWritableDatabase();
        if (initBill == null) {
            insertBill(db, bill);
            if (totalBill == null) {
                totalBill = formatTotalBill(bill);
                insertBill(db, totalBill);
            } else {
                totalBill.setAmount(Common.formatMoney(totalBill.getAmount() + bill.getAmount()));
                updateMonthBill(db, totalBill);
            }
            db.close();
            return;
        } else {
            updateMonthBill(db, bill);
            totalBill.setAmount(Common.formatMoney(totalBill.getAmount()-initBill.getAmount()+bill.getAmount()));
            updateMonthBill(db,totalBill);
            db.close();
        }
    }

    private MonthBill formatTotalBill(MonthBill bill) {
        MonthBill totalBill = new MonthBill();
        totalBill.setType(Common.TOTAL_CODE);
        totalBill.setAmount(bill.getAmount());
        totalBill.setMonth(bill.getMonth());
        totalBill.setYear(bill.getYear());
        return totalBill;
    }

    private ContentValues getCV(MonthBill bill) {
        ContentValues cv = new ContentValues();
        cv.put(MonthBill.AMOUNT, bill.getAmount());
        cv.put(MonthBill.MONTH, bill.getMonth());
        cv.put(MonthBill.TYPE, bill.getType());
        cv.put(MonthBill.YEAR, bill.getYear());
        return cv;
    }

    private long insertBill(SQLiteDatabase db, MonthBill bill) {
        return db.insert(MonthBill.TABLE, null, getCV(bill));
    }

    private void updateMonthBill(SQLiteDatabase db, MonthBill bill) {
        String whereClause = MonthBill.YEAR + "=? and " + MonthBill.MONTH + "=? and " + MonthBill.TYPE + "=?";
        String[] whereArgs = {String.valueOf(bill.getYear()), String.valueOf(bill.getMonth()), String.valueOf(bill.getType())};
        db.update(MonthBill.TABLE, getCV(bill), whereClause, whereArgs);
    }


    public MonthBill getBillByKey(int month, int year, int type) {
        MonthBill bill = null;
        db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(MonthBill.TABLE,
                new String[]{MonthBill.TYPE, MonthBill.AMOUNT},
                MonthBill.MONTH + "=? and " + MonthBill.YEAR + "=? and " + MonthBill.TYPE + "=?",
                new String[]{String.valueOf(month), String.valueOf(year), String.valueOf(type)}, null, null, null);
        if (cursor.moveToNext()) {
            bill = new MonthBill();
            bill.setAmount(cursor.getFloat(cursor.getColumnIndex(MonthBill.AMOUNT)));
            bill.setMonth(month);
            bill.setYear(year);
            bill.setType(cursor.getInt(cursor.getColumnIndex(MonthBill.TYPE)));
        }
        db.close();
        return bill;
    }

    public Map<Integer, MonthBill> getBillByMonth(int month, int year) {
        Map<Integer, MonthBill> bills = new HashMap<>();
        db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(MonthBill.TABLE, new String[]{MonthBill.TYPE, MonthBill.AMOUNT}, MonthBill.MONTH + "=? and " + MonthBill.YEAR + "=?",
                new String[]{String.valueOf(month), String.valueOf(year)}, null, null, null);
        while (cursor.moveToNext()) {
            MonthBill bill = new MonthBill();
            bill.setAmount(cursor.getFloat(cursor.getColumnIndex(MonthBill.AMOUNT)));
            bill.setMonth(month);
            bill.setYear(year);
            bill.setType(cursor.getInt(cursor.getColumnIndex(MonthBill.TYPE)));
            bills.put(bill.getType(), bill);
        }
        db.close();

//        db = dbHelper.getWritableDatabase();
//        db.execSQL("delete from monthBill where month = 0");
//        db.close();
        return bills;
    }

    public void deleteBill(MonthBill bill) {
        db = dbHelper.getWritableDatabase();
        String whereClause = MonthBill.YEAR + "=? and " + MonthBill.MONTH + "=? and " + MonthBill.TYPE + "=?";
        String[] whereArgs = {String.valueOf(bill.getYear()), String.valueOf(bill.getMonth()), String.valueOf(bill.getType())};
        db.delete(MonthBill.TABLE, whereClause, whereArgs);
        db.close();
    }
}
