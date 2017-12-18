package tcai.wlu.ca.billscanner.view;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import tcai.wlu.ca.billscanner.R;


/**
 * Created by 蔡婷婷 on 2017/2/15.
 */

public class MyViewHolder2 extends RecyclerView.ViewHolder  {

    public TextView tv,typeView;
    public CheckBox checkBox;
    public LinearLayout linearLayout;
    public TextView pr;

    public MyViewHolder2(View arg0) {
        super(arg0);
        checkBox=(CheckBox)arg0.findViewById(R.id.checkbox);
        linearLayout=(LinearLayout)arg0.findViewById(R.id.texts);
        tv = (TextView)arg0.findViewById(R.id.record);
        pr=(TextView)arg0.findViewById(R.id.record_price);
        typeView=(TextView)arg0.findViewById(R.id.type);

    }
}

