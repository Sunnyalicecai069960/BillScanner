package tcai.wlu.ca.billscanner.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import tcai.wlu.ca.billscanner.R;
import tcai.wlu.ca.billscanner.activity.MainActivity;
import tcai.wlu.ca.billscanner.activity.RecordActivity;
import tcai.wlu.ca.billscanner.bean.Record;
import tcai.wlu.ca.billscanner.common.Common;
import tcai.wlu.ca.billscanner.controller.RecordController;
import tcai.wlu.ca.billscanner.view.HomeAdapter;
import tcai.wlu.ca.billscanner.view.OnItemClickListener;

/**
 * Created by 蔡婷婷 on 2017/4/3.
 */

public class RecordFragment extends Fragment {
    private List<Record> recordList = new ArrayList<>();
    private RecyclerView mRecyclerView;
    public HomeAdapter mItemAdapter;
    private RecordController controller;


    public RecordFragment() {
        recordList = Common.records;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.mItemAdapter = new HomeAdapter(this.getActivity(), new OnItemClickListener() {
            @Override
            public void onItemDismiss(int position) {
                Record r = recordList.get(position);
                if(r.getId()!=null){
                    controller.deleteRecord(r);
                    ((MainActivity)getActivity()).updateData();
                }
                recordList.remove(position);
                mItemAdapter.notifyItemRemoved(position);
                mItemAdapter.notifyItemRangeChanged(position, recordList.size() - position);
            }

            @Override
            public void onItemClick(View v, int position) {
                Intent i = new Intent(getActivity(), RecordActivity.class);
                i.putExtra("index", position);
                i.putExtra("ifVisible",1);
                Log.i("onclick->", "" + position);
                startActivity(i);
            }
        }, recordList, HomeAdapter.INVISIBLE);
        View v = inflater.inflate(R.layout.record_fragment, container, false);

        mRecyclerView = (RecyclerView) v.findViewById(R.id.recyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mRecyclerView.getContext());
        linearLayoutManager.setOrientation(OrientationHelper.VERTICAL);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        this.mRecyclerView.setAdapter(mItemAdapter);
        ItemTouchHelper.Callback callback=new SimpleItemTouchHelperCallback(mItemAdapter.getOnItemClickListener());
        ItemTouchHelper itemTouchHelper=new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(mRecyclerView);
        controller = new RecordController(this.getActivity().getBaseContext());
        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        mItemAdapter.notifyDataSetChanged();

    }

    public class SimpleItemTouchHelperCallback extends ItemTouchHelper.Callback {
        private OnItemClickListener mListener;
        public SimpleItemTouchHelperCallback(OnItemClickListener listener){
            mListener = listener;
        }
        /**这个方法是用来设置我们拖动的方向以及侧滑的方向的*/
        @Override
        public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            //设置拖拽方向为上下
            final int dragFlags = ItemTouchHelper.UP|ItemTouchHelper.DOWN;
            //设置侧滑方向为从左到右和从右到左都可以
            final int swipeFlags = ItemTouchHelper.START|ItemTouchHelper.END;
            //将方向参数设置进去
            return makeMovementFlags(dragFlags,swipeFlags);
        }
        /**当我们拖动item时会回调此方法*/
        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {

            return false;
        }
        /**当我们侧滑item时会回调此方法*/
        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
            mListener.onItemDismiss(viewHolder.getAdapterPosition());
        }
    }

    public void dataChange(){
        recordList=Common.records;
        mItemAdapter.setmData(Common.records,HomeAdapter.INVISIBLE);
        mItemAdapter.setmData(new ArrayList<Record>(),HomeAdapter.VISIBLE);
        mItemAdapter.notifyDataSetChanged();
    }

    @Override
    public void onResume() {
        dataChange();
        super.onResume();
        System.out.print("recordFragement---->resume");
    }
}
