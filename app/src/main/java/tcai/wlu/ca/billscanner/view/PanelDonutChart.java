package tcai.wlu.ca.billscanner.view;

/**
 * Created by Administrator on 2017/4/3.
 */

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;

import java.util.Map;
import java.util.Set;

import tcai.wlu.ca.billscanner.bean.MonthBill;
import tcai.wlu.ca.billscanner.bean.Type;
import tcai.wlu.ca.billscanner.common.Common;
import tcai.wlu.ca.billscanner.controller.MonthBillController;


public class PanelDonutChart extends View {

    private int ScrWidth, ScrHeight;
    private Context context;
    private float totalAmount;
    private float arrPer[];
    private int arrColorRgb[][];
    private final int lTextSize = 60;
    private final int sTextSize = 40;
    private final int dark = 0xFFeeeeee;
    private Map<Integer, MonthBill> bills;
    MonthBillController billController;

    private int month,year;

    private void initView() {
        DisplayMetrics dm = getResources().getDisplayMetrics();
        ScrHeight = dm.heightPixels;
        ScrWidth = dm.widthPixels;

        ViewGroup.LayoutParams para = this.getLayoutParams();//获取按钮的布局
        para.height = ScrWidth/3*2;
        para.width=ScrWidth;
        this.setLayoutParams(para);

        bills = billController.getBillByMonth(month,year);
        MonthBill totalBill = bills.get(Common.TOTAL_CODE);
        if (totalBill == null) {
            arrPer = new float[0];
            totalAmount = 0;
            return;
        }
        totalAmount = totalBill.getAmount();
        if (totalAmount == 0) {
            arrPer = new float[0];
            return;
        }
        arrPer = new float[bills.size() - 1];
        arrColorRgb = new int[bills.size() - 1][3];
        Set<Integer> types = bills.keySet();

        int i = 0;
        for (Integer type : types) {
            if (type == Common.TOTAL_CODE)
                continue;
            MonthBill bill = bills.get(type);
            arrPer[i] = bill.getAmount() / totalAmount * 100.0f;
            Type tp = Common.getTypeMap(context).get(bill.getType());
            arrColorRgb[i][0] = tp.getRed();
            arrColorRgb[i][1] = tp.getGreen();
            arrColorRgb[i][2] = tp.getBlue();
            i++;
        }
    }

    public PanelDonutChart(Context context) {
        super(context);
        this.context = context;
        if (billController == null) {
            billController = new MonthBillController(context);
        }
        month = Common.getCurrentMonth();
        year=Common.getCurrentYear();
    }

    public PanelDonutChart(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        if (billController == null) {
            billController = new MonthBillController(context);
        }
        month = Common.getCurrentMonth();
        year=Common.getCurrentYear();
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

    public void onDraw(Canvas canvas) {
        initView();
        canvas.drawColor(dark);
        float cirX = ScrWidth / 2;
        float cirY = ScrWidth / 3;
        float radius = ScrWidth / 4;


        float arcLeft = cirX - radius;
        float arcTop = cirY - radius;
        float arcRight = cirX + radius;
        float arcBottom = cirY + radius;
        RectF arcRF0 = new RectF(arcLeft, arcTop, arcRight, arcBottom);


        Paint paintArc = new Paint();
        paintArc.setAntiAlias(true);

        XChartCalc xcalc = new XChartCalc();

        float percentage = 0.0f;
        float currPer = 0.0f;
        int i = 0;
        for (i = 0; i < arrPer.length; i++) {
            if(arrPer[i] ==0)
                continue;
            percentage = 360 * (arrPer[i] / 100);
            percentage = (float) (Math.round(percentage * 100)) / 100;
            paintArc.setARGB(255, arrColorRgb[i][0], arrColorRgb[i][1], arrColorRgb[i][2]);
            canvas.drawArc(arcRF0, currPer, percentage, true, paintArc);
            xcalc.CalcArcEndPointXY(cirX, cirY, radius - radius / 2 / 2, currPer + percentage / 2);
            currPer += percentage;
        }
        if(arrPer.length == 0){
            paintArc.setARGB(255, 230, 230, 230);
            canvas.drawArc(arcRF0, currPer, 360, true, paintArc);

        }

        paintArc.setColor(Color.WHITE);
        canvas.drawCircle(cirX, cirY, radius / 2, paintArc);
        Paint paintLabel = new Paint();
        paintLabel.setColor(Color.BLACK);
        paintLabel.setTextSize(lTextSize);
        canvas.drawText(Common.formatMoney(totalAmount) + "", cirX - 2 * lTextSize, cirY, paintLabel);
        paintLabel.setColor(Color.BLUE);
        paintLabel.setTextSize(sTextSize);
        canvas.drawText("totalAmount", cirX - lTextSize, cirY + lTextSize / 3 * 2, paintLabel);

    }


}
