package cn.qimate.bike.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import cn.qimate.bike.R;
import cn.qimate.bike.base.BaseViewAdapter;
import cn.qimate.bike.base.BaseViewHolder;
import cn.qimate.bike.model.MyCommissionBean;
import cn.qimate.bike.model.MyIntegralRecordBean;

/**
 * Created by Administrator1 on 2017/2/14.
 */

public class MyCommissionAdapter extends BaseViewAdapter<MyCommissionBean> {

    private LayoutInflater inflater;

    public MyCommissionAdapter(Context context) {
        super(context);
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (null == convertView) {
            convertView = inflater.inflate(R.layout.item_my_commission, null);
        }
        TextView addTimeText = BaseViewHolder.get(convertView,R.id.item_myCommission_addTimeText);
        TextView moneyext = BaseViewHolder.get(convertView,R.id.item_myCommission_moneyext);
        TextView descText = BaseViewHolder.get(convertView,R.id.item_myCommission_descText);
        MyCommissionBean bean = getDatas().get(position);
        addTimeText.setText(bean.getAdddate());
        moneyext.setText(bean.getMoney());
        descText.setText(bean.getRemark());
        return convertView;
    }
}
