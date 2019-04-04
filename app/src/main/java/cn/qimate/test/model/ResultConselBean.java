package cn.qimate.test.model;

import java.util.List;

/**
 * Created by Administrator on 2017/3/4 0004.
 */

public class ResultConselBean {

    private String flag;
    private String errcode;
    private String msg;
    private List<CurRoadBikingBean> data;

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public String getErrcode() {
        return errcode;
    }

    public void setErrcode(String errcode) {
        this.errcode = errcode;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public List<CurRoadBikingBean> getData() {
        return data;
    }

    public void setData(List<CurRoadBikingBean> data) {
        this.data = data;
    }
}
