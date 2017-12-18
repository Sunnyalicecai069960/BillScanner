package tcai.wlu.ca.billscanner.bean;

/**
 * Created by Administrator on 2017/4/3.
 */

public class MonthBill {
    public static final String TABLE = "monthBill";
    public static final String MONTH = "month";
    public static final String TYPE = "type";
    public static final String YEAR = "year";
    public static final String AMOUNT = "amount";

    private int month;
    private int type;
    private float amount;
    private int year;

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }
}
