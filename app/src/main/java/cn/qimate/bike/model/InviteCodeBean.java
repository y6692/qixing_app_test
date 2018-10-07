package cn.qimate.bike.model;

/**
 * Created by 123 on 2018/3/14.
 */

public class InviteCodeBean {

    private String telphone;
    private String commission;
    private String inviter_num;
    private String share_title;
    private String share_desc;
    private String share_url;
    private String share_thumb;
    private String senddays;

    public String getCommission() {
        return commission;
    }

    public void setCommission(String commission) {
        this.commission = commission;
    }

    public String getInviter_num() {
        return inviter_num;
    }

    public void setInviter_num(String inviter_num) {
        this.inviter_num = inviter_num;
    }

    public String getShare_title() {
        return share_title;
    }

    public void setShare_title(String share_title) {
        this.share_title = share_title;
    }

    public String getShare_desc() {
        return share_desc;
    }

    public void setShare_desc(String share_desc) {
        this.share_desc = share_desc;
    }

    public String getShare_url() {
        return share_url;
    }

    public void setShare_url(String share_url) {
        this.share_url = share_url;
    }

    public String getShare_thumb() {
        return share_thumb;
    }

    public void setShare_thumb(String share_thumb) {
        this.share_thumb = share_thumb;
    }

    public String getTelphone() {
        return telphone;
    }

    public void setTelphone(String telphone) {
        this.telphone = telphone;
    }

    public String getSenddays() {
        return senddays;
    }

    public void setSenddays(String senddays) {
        this.senddays = senddays;
    }
}
