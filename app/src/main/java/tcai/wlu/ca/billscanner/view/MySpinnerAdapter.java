package tcai.wlu.ca.billscanner.view;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import tcai.wlu.ca.billscanner.R;
import tcai.wlu.ca.billscanner.activity.ChartActivity;
import tcai.wlu.ca.billscanner.common.Common;

/**
 * Created by Administrator on 2017/4/5.
 */

public class MySpinnerAdapter extends BaseAdapter {

    private Context mContext;

    public MySpinnerAdapter(Context pContext) {
       this.mContext=pContext;
        if(Common.keys ==null) {
            Common.keys = new ArrayList<>();
            for (Integer i : Common.getTypeMap(mContext).keySet()) {
                if (i == Common.TOTAL_CODE)
                    continue;
                Common.keys.add(i);
            }
        }
    }

    @Override
    public int getCount() {
        return Common.keys.size();
    }

    @Override
    public Object getItem(int position) {
        return Common.getTypeMap(mContext).get(Common.keys.get(position));
    }

    @Override
    public long getItemId(int position) {
        return Common.getTypeMap(mContext).get(Common.keys.get(position)).getCode();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater _LayoutInflater=LayoutInflater.from(mContext);
        convertView=_LayoutInflater.inflate(R.layout.spinner_item, null);
        if(convertView!=null)
        {
            TextView _TextView1=(TextView)convertView.findViewById(R.id.text1);
            _TextView1.setText(Common.getTypeMap(mContext).get(Common.keys.get(position)).getType());
        }
        return convertView;
    }
}
