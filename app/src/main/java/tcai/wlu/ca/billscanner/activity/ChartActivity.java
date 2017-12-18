package tcai.wlu.ca.billscanner.activity;

import android.support.v4.view.PagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import tcai.wlu.ca.billscanner.R;
import tcai.wlu.ca.billscanner.bean.Record;
import tcai.wlu.ca.billscanner.common.Common;
import tcai.wlu.ca.billscanner.controller.MonthBillController;
import tcai.wlu.ca.billscanner.controller.RecordController;
import tcai.wlu.ca.billscanner.view.HomeAdapter;
import tcai.wlu.ca.billscanner.view.OnItemClickListener;
import tcai.wlu.ca.billscanner.view.PanelDonutChart;

public class ChartActivity extends AppCompatActivity {

    private PagerAdapter mMyPagerAdapter;
    private ViewPager mViewPager;
    public final int PAGE_SIZE = 3;
    public final int MAX_PAGE_SIZE = Integer.MAX_VALUE/2;
    public static int lCurrentPosition,lastPosition;

    private static List<View> chartViews = new ArrayList<>();
    public static MonthBillController controller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        controller = new MonthBillController(this.getBaseContext());
        Common.month = Common.getCurrentMonth();
        Common.year = Common.getCurrentYear();

        mMyPagerAdapter = new MyPagerAdapter();
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mMyPagerAdapter);

        if (chartViews.size() == 0) {
            for(int i =0;i<PAGE_SIZE;i++)
            addView(R.layout.fragment_chart);
        }
        lCurrentPosition = MAX_PAGE_SIZE;
        mViewPager.setCurrentItem(lCurrentPosition);
    }

    private void addView(int layout){
        View view = LayoutInflater.from(ChartActivity.this).inflate(layout, null);
        HomeAdapter mItemAdapter= new HomeAdapter(ChartActivity.this, new OnItemClickListener() {
            @Override
            public void onItemDismiss(int position) {

            }

            @Override
            public void onItemClick(View v, int position) {

            }
        }, Common.formatRecordBill(controller.getBillByMonth(Common.getCurrentMonth(), Common.getCurrentYear())),0);
        RecyclerView mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mRecyclerView.getContext());
        linearLayoutManager.setOrientation(OrientationHelper.VERTICAL);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(mItemAdapter);
        chartViews.add(view);
    }


    public class MyPagerAdapter extends PagerAdapter {
        @Override
        public int getCount() {
            return MAX_PAGE_SIZE+1;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }




        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            System.out.print(" \n----->month " + Common.month+"---------->"+position);
            int move = position - lCurrentPosition;
            lastPosition = lCurrentPosition;
            lCurrentPosition = position;
            position %= PAGE_SIZE;
            if (position < 0) {
                position = PAGE_SIZE + position;
            }
            if (move > 0) {
                Common.month += move;
                if (Common.month > 12) {
                    Common.month -= 12;
                    Common.year++;
                }
            }
            if (move < 0) {
                Common.month += move;
                if (Common.month < 1) {
                    Common.month = 12 - Common.month;
                    Common.year--;
                }
            }
            View view = chartViews.get(position);
            PanelDonutChart chart = (PanelDonutChart) view.findViewById(R.id.chart);
            chart.setYear(Common.year);
            chart.setMonth(Common.month);
            chart.invalidate();
            if(move==0 && (Common.getCurrentMonth() != Common.month || Common.getCurrentYear() != Common.year))
                return  view;
            TextView textView = (TextView) view.findViewById(R.id.month_text);
            textView.setText(Common.getMonthText(Common.year, Common.month));
            RecyclerView mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
            HomeAdapter mItemAdapter= (HomeAdapter) mRecyclerView.getAdapter();
            mItemAdapter.setmData(Common.formatRecordBill(controller.getBillByMonth(Common.month, Common.year)), HomeAdapter.INVISIBLE);
            mItemAdapter.setmData(new ArrayList<Record>(), HomeAdapter.VISIBLE);
            mItemAdapter.notifyDataSetChanged();
            ViewParent vp = view.getParent();
            if (vp != null) {
                ViewGroup parent = (ViewGroup) vp;
                parent.removeView(view);
            }
            container.addView(view);
            return view;
        }


    }
}
