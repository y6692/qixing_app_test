package cn.qimate.test.ble.utils;

import java.util.Comparator;

import cn.qimate.test.ble.bean.BleDevice;

/**
 * 排序
 * Created by sunshine on 2017/2/21.
 */

public class SortComparator implements Comparator {
    @Override
    public int compare(Object o, Object t1) {
        BleDevice a = (BleDevice) o;
        BleDevice b = (BleDevice) t1;

        return (b.getRiss()- a.getRiss());
    }
}
