package cn.qimate.bike.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import cn.qimate.bike.R;
import cn.qimate.bike.base.BaseViewAdapter;
import cn.qimate.bike.base.BaseViewHolder;
import cn.qimate.bike.model.MyMessageBean;

/**
 * Created by Administrator1 on 2017/2/14.
 */

public class MyMessageAdapter extends BaseViewAdapter<MyMessageBean>{

    private LayoutInflater inflater;

    public MyMessageAdapter(Context context) {
        super(context);
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (null == convertView) {
            convertView = inflater.inflate(R.layout.item_my_message, null);
        }
        TextView dates = BaseViewHolder.get(convertView,R.id.item_message_dates);
        TextView info = BaseViewHolder.get(convertView,R.id.item_message_info);
        MyMessageBean bean = getDatas().get(position);
        dates.setText(bean.getMes_addtime());
        info.setText(bean.getMes_content());
        return convertView;
    }
}
