package tcai.wlu.ca.billscanner.view;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tcai.wlu.ca.billscanner.R;
import tcai.wlu.ca.billscanner.bean.Record;
import tcai.wlu.ca.billscanner.bean.Type;
import tcai.wlu.ca.billscanner.common.Common;

import static android.R.attr.visibility;


/**
 * Created by 蔡婷婷 on 2017/2/15.
 */

public class HomeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Map<Integer, List<Record>> mData = new HashMap<>();
    private LayoutInflater mInflater;
    private OnItemClickListener onItemClickListener;
    private Context context;
    private SparseBooleanArray mCheckStates = new SparseBooleanArray();

    public static final int VISIBLE =1,INVISIBLE=0;


    public SparseBooleanArray getmCheckStates() {
        return mCheckStates;
    }

    public HomeAdapter(Context context, OnItemClickListener onItemClickListener, List<Record> RecordList, int visibility) {
        this.context = context;
        mData.put(visibility, RecordList);
        this.mInflater = LayoutInflater.from(context);
        this.onItemClickListener = onItemClickListener;
    }

    public List<Record> getmData(int key) {
        return mData.get(key);
    }

    public void setmData(List<Record> mData, int key) {
        this.mData.put(key, mData);
    }

    public OnItemClickListener getOnItemClickListener() {
        return onItemClickListener;
    }

    @Override
    public int getItemCount() {
        Log.i("itemlist size:", "" + mData.size());
        return mData.get(VISIBLE).size()+mData.get(INVISIBLE).size();
    }

    public int getItemViewType(int position) {
        int size = mData.get(VISIBLE)==null?0:mData.get(VISIBLE).size();
        if (size>position) {
            return VISIBLE;
        }else{
            return INVISIBLE;
        }
    }


    //数据的绑定显示
    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holderV, final int position) {
        if (holderV instanceof MyViewHolder) {
            Record bean = mData.get(INVISIBLE).get(position);
            ((MyViewHolder) holderV).linearLayout.setTag(position);
            if (bean != null) {
                int type = bean.getType();
                Type type1 = Common.getTypeMap(context).get(type);
                ((MyViewHolder) holderV).im.setColorFilter(Color.argb(255, type1.getRed(), type1.getGreen(), type1.getBlue()));
                ((MyViewHolder) holderV).tv.setText(bean.getItem() + "");
                if (bean.getTime() == null) {
                    ((MyViewHolder) holderV).pr.setText(bean.getTotalPrice() + "%");
                } else {
                    ((MyViewHolder) holderV).pr.setText(bean.getTotalPrice() + "");
                }
                ((MyViewHolder) holderV).typeView.setText(type1.getType());
            }
        }else if(holderV instanceof MyViewHolder2){
            Record bean = mData.get(VISIBLE).get(position);
            ((MyViewHolder2) holderV).linearLayout.setTag(position);
            ((MyViewHolder2) holderV).checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    int pos = position;
                    if (isChecked == mCheckStates.get(pos))
                        return;
                    if (isChecked) {
                        mCheckStates.put(pos, true);
                        ((MyViewHolder2) holderV).checkBox.setBackgroundResource(R.drawable.select);
                        //do something
                    } else {
                        mCheckStates.delete(pos);
                        //do something else
                        ((MyViewHolder2) holderV).checkBox.setBackgroundResource(R.drawable.circle);

                    }
                }
            });

            if (bean != null) {
                int type = bean.getType();
                Type type1 = Common.getTypeMap(context).get(type);
                ((MyViewHolder2) holderV).tv.setText(bean.getItem() + "");
                if (bean.getTime()==null) {
                    ((MyViewHolder2) holderV).pr.setText(bean.getTotalPrice() + "%");
                } else {
                    ((MyViewHolder2) holderV).pr.setText(bean.getTotalPrice() + "");
                }
                ((MyViewHolder2) holderV).typeView.setText(type1.getType());
            }
        }
    }

    //item显示类型
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.record_item, parent, false);
        view.findViewById(R.id.texts).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null)
                    onItemClickListener.onItemClick(v, (int) v.getTag());
            }
        });
        if (viewType == INVISIBLE)
            return new MyViewHolder(view);
        Log.i("createViewholder", "again");
        View view2 = mInflater.inflate(R.layout.record_item2, parent, false);
        return new MyViewHolder2(view2);
    }
}
