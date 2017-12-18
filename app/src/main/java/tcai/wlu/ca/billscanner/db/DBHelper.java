package tcai.wlu.ca.billscanner.db;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import tcai.wlu.ca.billscanner.bean.MonthBill;
import tcai.wlu.ca.billscanner.bean.Record;

/**
 * Created by Administrator on 2017/4/3.
 */
public class DBHelper extends SQLiteOpenHelper {

    private static final String TAG = "DBHelper";
    private static final String DATABASE = "billScanner.db";
    private static final int VERSION = 2;

    public DBHelper(Context context) {
        super(context, DATABASE, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "create table "+ MonthBill.TABLE+"(" +
                MonthBill.YEAR+" int not null,"+
                MonthBill.MONTH+" int not null," +
                MonthBill.TYPE+" int not null," +
                MonthBill.AMOUNT+" real," +
                "primary key ("+MonthBill.YEAR+","+MonthBill.MONTH+","+MonthBill.TYPE+"))";
        System.out.println(sql);
        Log.i(TAG, "create table------------->"+MonthBill.TABLE);
        db.execSQL(sql);
        sql = "create table "+Record.TABLE+"(" +
                Record.ID+" Integer primary key autoincrement," +
                Record.ITEM+" text not null," +
                Record.TYPE+" int not null," +
                Record.PRICE+" real not null," +
                Record.TAX+" real default 0," +
                Record.DISCOUNT+" real default 0," +
                Record.TOTAL_PRICE+" real not null," +
                Record.YEAR+" int not null,"+
                Record.MONTH+" int not null,"+
                Record.TIME+" char not null)";
        Log.i(TAG, "create table ------------->"+Record.TABLE);
        System.out.println(sql);
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if(oldVersion != newVersion) {
            Log.i(TAG, "update Database------------->");
            db.execSQL("DROP TABLE IF EXISTS " + MonthBill.TABLE);
            db.execSQL("DROP TABLE IF EXISTS " + Record.TABLE);
            onCreate(db);
        }
    }

}
