package cn.qimate.test.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import cn.nostra13.universalimageloader.core.ImageLoader;
import cn.qimate.test.R;
import cn.qimate.test.base.BaseViewAdapter;
import cn.qimate.test.base.BaseViewHolder;
import cn.qimate.test.core.common.Urls;
import cn.qimate.test.core.widget.MLImageView;
import cn.qimate.test.model.HistoryRoadBean;
import cn.qimate.test.model.MyInviterListBean;

/**
 * Created by Administrator1 on 2017/2/13.
 */

public class MyInviterListAdapter extends BaseViewAdapter<MyInviterListBean> {

    private LayoutInflater inflater;

    public MyInviterListAdapter(Context context) {
        super(context);
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (null == convertView) {
            convertView = inflater.inflate(R.layout.item_inviter_list, null);
        }
        ImageView heading = BaseViewHolder.get(convertView,R.id.item_inviterList_headg);
        TextView titleText = BaseViewHolder.get(convertView,R.id.item_inviterList_titleText);
        TextView telText = BaseViewHolder.get(convertView,R.id.item_inviterList_telText);
        TextView addTimeText = BaseViewHolder.get(convertView,R.id.item_inviterList_addTimeText);
        TextView stateText = BaseViewHolder.get(convertView,R.id.item_inviterList_stateText);
        MyInviterListBean bean = getDatas().get(position);
        ImageLoader.getInstance().displayImage(Urls.host + bean.getHeadimgurl(),heading);
        titleText.setText(bean.getRealname());
        telText.setText(bean.getTelphone());
        addTimeText.setText("注册时间："+bean.getAdddate());
        stateText.setText(bean.getIscert());
        return convertView;
    }
}
