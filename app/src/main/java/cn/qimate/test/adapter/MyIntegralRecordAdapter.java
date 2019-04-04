package cn.qimate.test.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import cn.qimate.test.R;
import cn.qimate.test.base.BaseViewAdapter;
import cn.qimate.test.base.BaseViewHolder;
import cn.qimate.test.model.MyIntegralRecordBean;

/**
 * Created by Administrator1 on 2017/2/14.
 */

public class MyIntegralRecordAdapter extends BaseViewAdapter<MyIntegralRecordBean> {

    private LayoutInflater inflater;

    public MyIntegralRecordAdapter(Context context) {
        super(context);
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (null == convertView) {
            convertView = inflater.inflate(R.layout.item_my_integral_rule, null);
        }
        TextView time = BaseViewHolder.get(convertView,R.id.item_myIntegral_rule_time);
        TextView times = BaseViewHolder.get(convertView,R.id.item_myIntegral_rule_times);
        MyIntegralRecordBean bean = getDatas().get(position);
        time.setText(bean.getAddtime());
        times.setText(bean.getReason());
        return convertView;
    }
}
