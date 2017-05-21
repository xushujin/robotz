package com.hatim.common.utils;

import com.taobao.api.ApiException;
import com.taobao.api.DefaultTaobaoClient;
import com.taobao.api.TaobaoClient;
import com.taobao.api.request.TbkJuTqgGetRequest;
import com.taobao.api.response.TbkJuTqgGetResponse;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by Hatim on 2017/5/15.
 */
public class TaobaoSignUtil {
    public static void main(String[] args) {
        String url = "http://gw.api.taobao.com/router/rest";
//        String appkey = "23822761";
//        String secret = "c40a95c9da0a9506abb163793b60ca9c";
        String appkey = "23731931";
        String secret = "9d1a6b3c1e30e1a42aa914d08a936190";

        // 微尘提供的key
//        23803810
//        e1a7cad0e82b4f518e66528570e46a93

        TaobaoClient client = new DefaultTaobaoClient(url, appkey, secret);
//        TbkItemGetRequest req = new TbkItemGetRequest();
//        req.setFields("click_url,num_iid,title,pict_url,small_images,reserve_price,zk_final_price,user_type,provcity,item_url,seller_id,volume,nick");
//        req.setQ("孔雀鱼");
//        try {
//            TbkItemGetResponse response = client.execute(req);
//            System.out.println(response.getBody());
//        } catch (ApiException e) {
//            e.printStackTrace();
//        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DAY_OF_MONTH, -3);
        Date dateStart = calendar.getTime();

        calendar.setTime(new Date());
        calendar.add(Calendar.DAY_OF_MONTH, 3);
        Date dateEnd = calendar.getTime();

        TbkJuTqgGetRequest req2 = new TbkJuTqgGetRequest();
        req2.setAdzoneId(76976820L);
        req2.setFields("click_url,pic_url,reserve_price,zk_final_price,total_amount,sold_num,title,category_name,start_time,end_time");
        req2.setStartTime(dateStart);
        req2.setEndTime(dateEnd);
        req2.setPageNo(1L);
        req2.setPageSize(10L);
        try {
            TbkJuTqgGetResponse response = client.execute(req2);
            System.out.println(response.getBody());
        } catch (ApiException e) {
            e.printStackTrace();
        }
    }

}
