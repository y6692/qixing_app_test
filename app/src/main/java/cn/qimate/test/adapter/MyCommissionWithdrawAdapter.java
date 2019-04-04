package cn.qimate.test.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import cn.qimate.test.R;
import cn.qimate.test.base.BaseViewAdapter;
import cn.qimate.test.base.BaseViewHolder;
import cn.qimate.test.model.MyCommissionBean;
import cn.qimate.test.model.MyCommissionWithdrawBean;

/**
 * Created by Administrator1 on 2017/2/14.
 */

public class MyCommissionWithdrawAdapter extends BaseViewAdapter<MyCommissionWithdrawBean> {

    private LayoutInflater inflater;

    public MyCommissionWithdrawAdapter(Context context) {
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
        MyCommissionWithdrawBean bean = getDatas().get(position);
        addTimeText.setText(bean.getAdddate());
        moneyext.setText(bean.getStatus());
        descText.setText(bean.getRemark());
        return convertView;
    }
}
