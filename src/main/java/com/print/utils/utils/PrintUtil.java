package com.print.utils.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.print.utils.PrintTicket;
import com.print.utils.analysis.PrintBook;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

/**
 * <Description> <br>
 *
 * @author hanqy<br>
 * @version 1.0<br>
 * @CreateDate 2020/6/28 9:19 上午<br>
 */
@Slf4j
public class PrintUtil {

    public static void print(String jsonData) {
        log.info(">>>>>>>>>>>>>>>>开始打印<<<<<<<<<<<<<<<<<<<");
        // 模板解析
        String jsonStr = "";
        try {
            jsonStr = new ResourcesUtils().getResource(PropertiesUtils.getString("ext.template"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (StringUtils.isBlank(jsonStr)) {
            System.exit(0);
            return;
        }
        PrintBook printBook = JSON.parseObject(jsonStr, PrintBook.class);
        Map<String, Object> dataMap = getResult(getResult(jsonData).get("resData").toString());
        JSONObject order = JSONObject.parseObject(dataMap.get("order").toString());
        dataMap.put("id", order.get("id"));
        dataMap.put("num", order.get("num"));
        dataMap.put("orderTime", order.get("orderTime"));
        dataMap.put("wxName", order.get("wxName"));
        dataMap.put("price", order.get("price"));
        // 输出打印
        PrintTicket printTicket = new PrintTicket(printBook, dataMap);
        printTicket.printer();
        log.info(">>>>>>>>>>>>>>>>结束打印<<<<<<<<<<<<<<<<<<<");
    }

    private static Map<String, Object> getResult(String msg) {
        Map result = (Map) JSON.parse(msg);
        return result;
    }

    public static void main(String[] args) {
        log.info(">>>>>>>>>>>>>>>>开始打印<<<<<<<<<<<<<<<<<<<");
        // 模板解析
        String jsonStr = "";
        try {
            jsonStr = new ResourcesUtils().getResource(PropertiesUtils.getString("ext.template"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (StringUtils.isBlank(jsonStr)) {
            System.exit(0);
            return;
        }
        PrintBook printBook = JSON.parseObject(jsonStr, PrintBook.class);
        // 接口传的数据
        String jsonData = "{\"resCode\":1,\"resData\":{\"discount\":\"0\",\"details\":[{\"adultNum\":1,\"childNum\":0,\"createTime\":1591929724000,\"creator\":null,\"creatorId\":null,\"departureDate\":1592582400000,\"detailId\":19,\"id\":306,\"isDelete\":\"0\",\"modifier\":null,\"modifierId\":null,\"name\":\"测试旅游\",\"num\":1,\"orderId\":343,\"price\":0.01,\"remark\":null,\"sum\":0.01,\"updateTime\":1591929724000}],\"order\":{\"createTime\":1591929724000,\"id\":343,\"name\":\"测试旅游\",\"num\":1,\"orderTime\":\"2020-06-12 10:42:04\",\"picture\":\"http://121.37.191.177:7777/file/down/group1/M00/00/10/wKgAQF7OIcCAHpPNAAFjvH6ixZ0845.jpg\",\"price\":0.01,\"status\":\"2\",\"typeId\":3,\"typeName\":\"测试旅游\",\"unitPrice\":0.01,\"userId\":1040,\"wxName\":\"执着\uD83E\uDD18 ⃢\uD83D\uDC41-\uD83D\uDC41⃢\"}},\"resMsg\":[{\"msgCode\":\"10001\",\"msgText\":\"10001\"}]}";

        Map<String, Object> dataMap = getResult(getResult(jsonData).get("resData").toString());
        JSONObject order = JSONObject.parseObject(dataMap.get("order").toString());
        dataMap.put("id", order.get("id"));
        dataMap.put("num", order.get("num"));
        dataMap.put("orderTime", order.get("orderTime"));
        dataMap.put("wxName", order.get("wxName"));
        dataMap.put("price", order.get("price"));
        // 输出打印
        PrintTicket printTicket = new PrintTicket(printBook, dataMap);
        printTicket.printer();
        log.info(">>>>>>>>>>>>>>>>结束打印<<<<<<<<<<<<<<<<<<<");
    }
}
