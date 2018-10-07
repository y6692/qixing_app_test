package cn.qimate.bike.lock.constant;

/**
 * 作者：王凯强 on 2017/1/06 11:07
 * <p>
 * 邮箱：317097478@qq.com
 */
public interface ConstantURL {


    //总路径
    String BASE_URL = "https://bike.nokelock.com/";
//    String BASE_URL = "http://192.168.0.131:11111/";
    //总路径
    String SERVER_URL = BASE_URL + "api/v1/";
    //总路径
    String IMG_URL = "http://oj6zjwfmk.bkt.clouddn.com/";
    /**
     * 登录
     */
    String USER_LOGIN = "user/login_v2";
    /**
     * 发送短信
     */
    String SEND_CODE = "user/getvcode_v2";
    /**
     * 获取用户信息
     */
    String USER_INFO = "user/info";
    /**
     * 修改用户信息 支持字段 nick_name 昵称 phone 手机 avatar 头像
     */
    String USER_UPDATE = "user/update";
    /**
     * 获取七牛上传令牌
     */
    String QINIU_TOKEN = "common/qntoken";
    /**
     * 搜索车辆信息
     */
    String CAR_SEARCH = "search/search";
    /**
     * 通过二维码获取设备信息
     */
    String COMMON_SCAN_QR_CODE = "user/scanqrcode";
    /**
     * 远程开锁
     */
    String COMMON_OPEN_LOCK = "common/openlock";
    /**
     * 蓝牙开关锁上报
     */
    String COMMON_ORDER_UPLOADPOSIONT = "order/uploadposition";

    /**
     * 上报故障
     */
    String COMMON_SEARCH = "common/uploadfault";

    /**
     * 实名认证
     */
    String USER_AUTHENTICATION = "common/certification";
    /**
     * 要充值的押金金额
     */
    String USER_RECHARGE_GET_DEPOSIT = "recharge/getdeposit";
    /**
     * 要充值的押金金额
     */
    String USER_RECHARGE_GET_RECHARGE = "recharge/echargeList";
    /**
     * 推出登陆
     */
    String USER_LOGOUT = "user/logout";

    /**
     * 押金充值
     */
    String USER_RECHARGE_DEPOSIT = "recharge/deposit";
    /**
     * 余额充值
     */
    String USER_RECHARGE_RECHARGE = "recharge/recharge";
    /**
     * 查询订单信息
     */
    String USER_ORDER_ORDER = "order/order";
    /**
     * 查询可用优惠券
     */
    String USER_COUPON_USABLE = "coupon/coupons";
    /**
     * 查询我的消息
     */
    String USER_MESSAGE = "user/message";
    /**
     * 查询我的消费明细
     */
    String USER_PAY_DETAIL = "user/consumptions";
    /**
     * 查询我的信用
     */
    String USER_GET_CREDITS = "user/credits";
    /**
     * 查询我的行程
     */
    String USER_GET_TRIPS = "user/mytrips";
    /**
     * 兑换优惠卷
     */
    String USER_GET_COUPON = "coupon/getcoupon";
    /**
     * 返还押金申请
     */
    String USER_MONEY_BACK = "order/moneyback";
    /**
     * 获取骑行卡种类列表
     */
    String CARD_CARDS = "card/cards";
    /**
     * 支付
     */
    String PAY = "pay/pay";
    /**
     * 购买骑行卡
     */
    String CARD_BUY = "card/buy";
    /**
     * 启动页图片
     */
    String WELCOME_IMG = "common/adverti";
    /**
     * 首页广告
     */
    String HOME_ADVERT = "common/homeadverti";
    /**
     * 检查版本
     */
    String CHECK_VERSION = "common/getappversion";

    /**
     * 微信分享
     */
    String WEB_SHARE = BASE_URL + "html/share.html";
    /**
     * 充值协议
     */
    String WEB_RECHARGE = BASE_URL + "html/chongzhi.html";
    /**
     * 用户指南
     */
    String WEB_HELP = BASE_URL + "html/help.html";
    /**
     * 用户协议
     */
    String WEB_USER = BASE_URL + "html/user.html";
    /**
     * 信用规则
     */
    String WEB_CREDIT = BASE_URL + "html/xinyong.html";
    /**
     * 押金说明
     */
    String WEB_DEPOSIT = BASE_URL + "html/yajin.html";
}
