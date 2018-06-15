package com.parkingwang.keyboard.neighbor;

import android.text.TextUtils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * @author 陈哈哈 (yoojiachen@gmail.com)
 */
public class NeighborManager {

    private final Set<Province> mProvinces = new HashSet<>(32);

    public NeighborManager() {
        final Province beijing = new Province("北京市", "京");
        final Province tianjin = new Province("天津市", "津");
        final Province shanxi = new Province("山西省", "晋");
        final Province hebei = new Province("河北省", "冀");
        final Province neimenggu = new Province("内蒙古自治区", "蒙");
        final Province liaoning = new Province("辽宁省", "辽");
        final Province jilin = new Province("吉林省", "吉");
        final Province heilongjiang = new Province("黑龙江省", "黑");
        final Province shanghai = new Province("上海市", "沪");
        final Province jiangsu = new Province("江苏省", "苏");
        final Province zhejiang = new Province("浙江省", "浙");
        final Province anhui = new Province("安徽省", "皖");
        final Province fujian = new Province("福建省", "闽");
        final Province jiangxi = new Province("江西省", "赣");
        final Province shandong = new Province("山东省", "鲁");
        final Province henan = new Province("河南省", "豫");
        final Province hubei = new Province("湖北省", "鄂");
        final Province hunan = new Province("湖南省", "湘");
        final Province guangdong = new Province("广东省", "粤");
        final Province guangxi = new Province("广西壮族自治区", "桂");
        final Province hainan = new Province("海南省", "琼");
        final Province chongqing = new Province("重庆市", "渝");
        final Province sichuan = new Province("四川省", "川");
        final Province guizhou = new Province("贵州省", "贵");
        final Province yunnan = new Province("云南省", "云");
        final Province xizang = new Province("西藏自治区", "藏");
        final Province shannxi = new Province("陕西省", "陕");
        final Province gansu = new Province("甘肃省", "甘");
        final Province qinghai = new Province("青海省", "青");
        final Province ningxia = new Province("宁夏回族自治区", "宁");
        final Province xinjiang = new Province("新疆维吾尔自治区", "新");
        final Province taiwan = new Province("台湾省", "台");

        xinjiang.link(xizang)
                .link(qinghai)
                .link(gansu)
                .link(neimenggu);

        xizang.link(qinghai)
                .link(sichuan)
                .link(yunnan);

        qinghai.link(gansu)
                .link(sichuan)
                .link(shanxi);

        gansu.link(neimenggu)
                .link(shannxi)
                .link(sichuan)
                .link(chongqing)
                .link(ningxia);

        ningxia.link(shannxi)
                .link(gansu);

        neimenggu.link(heilongjiang)
                .link(jilin)
                .link(liaoning)
                .link(hebei)
                .link(beijing)
                .link(tianjin)
                .link(shanxi)
                .link(shannxi)
                .link(ningxia);

        shannxi.link(shanxi)
                .link(henan)
                .link(hubei)
                .link(chongqing)
                .link(sichuan);

        sichuan.link(yunnan)
                .link(guizhou)
                .link(chongqing);

        yunnan.link(guizhou)
                .link(guangxi);

        guizhou.link(hunan)
                .link(guangxi)
                .link(chongqing)
                .link(hubei);

        chongqing.link(hubei)
                .link(hunan);

        hubei.link(hunan)
                .link(henan)
                .link(anhui)
                .link(jiangxi);

        hunan.link(jiangxi)
                .link(guangxi)
                .link(guangdong);

        guangxi.link(guangdong)
                .link(hainan);

        guangdong.link(hainan)
                .link(fujian)
                .link(jiangxi);

        jiangxi.link(fujian)
                .link(anhui)
                .link(zhejiang);

        fujian.link(zhejiang);
        //fujian.link(taiwan);

        zhejiang.link(shanghai)
                .link(anhui)
                .link(jiangsu);

        anhui.link(jiangsu)
                .link(shanghai)
                .link(shandong);

        jiangsu.link(shandong)
                .link(shanghai);

        shandong.link(hebei)
                .link(beijing)
                .link(tianjin);

        shanxi.link(hebei)
                .link(henan);

        hebei.link(beijing)
                .link(tianjin)
                .link(shandong)
                .link(liaoning);

        beijing.link(tianjin)
                .link(liaoning)
                .link(shandong);

        liaoning.link(jilin);

        jilin.link(liaoning)
                .link(heilongjiang);

        this.bind(beijing, tianjin, shanxi, hebei, neimenggu, liaoning, jilin, heilongjiang, shanghai,
                jiangsu, zhejiang, anhui, fujian, jiangxi, shandong, henan, hubei, hunan, guangdong,
                guangxi, hainan, chongqing, sichuan, guizhou, yunnan, xizang, shannxi, gansu, qinghai,
                ningxia, xinjiang);
    }

    /**
     * 获取省份名称对应的省份对象
     *
     * @param provinceName 省分名称
     * @return Province
     */
    public Province getLocation(String provinceName) {
        if (TextUtils.isEmpty(provinceName)) {
            return new Province("", "");
        }
        provinceName = provinceName.replace("省", "");
        for (Province p : mProvinces) {
            if (p.name.contains(provinceName)) {
                return p;
            }
        }
        return new Province("", "");
    }

    private void bind(Province... provinces) {
        this.mProvinces.addAll(Arrays.asList(provinces));
    }
}
