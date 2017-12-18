package tcai.wlu.ca.billscanner.bean;

import java.util.Date;

/**
 * Created by Administrator on 2017/4/3.
 */

public class Record {
    public static final String TABLE = "record";
    public final static String ID ="id";
    public final static String ITEM ="item";
    public final static String TYPE="type";
    public final static String PRICE = "price";
    public final static String TAX="tax";
    public final static String DISCOUNT = "discount";
    public final static String TOTAL_PRICE="totalPrice";
    public final static String TIME="time";
    public final static String MONTH="month";
    public final static String YEAR="year";


    private Integer id;
    private  String item;
    private  int type;
    private  float price;
    private  float tax;
    private  float discount;
    private  float totalPrice;
    private int month;
    private int year;
    private String time;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public float getTax() {
        return tax;
    }

    public void setTax(float tax) {
        this.tax = tax;
    }

    public float getDiscount() {
        return discount;
    }

    public void setDiscount(float discount) {
        this.discount = discount;
    }

    public float getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(float totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }
}
