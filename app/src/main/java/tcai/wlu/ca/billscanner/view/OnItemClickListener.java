package tcai.wlu.ca.billscanner.view;

import android.view.View;

/**
 * Created by 蔡婷婷 on 2017/2/15.
 */

public interface OnItemClickListener {
    void onItemClick(View v, int position);
    void onItemDismiss(int position);
}
