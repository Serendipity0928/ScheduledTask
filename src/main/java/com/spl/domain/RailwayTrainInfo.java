package com.spl.domain;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Data
public class RailwayTrainInfo {

    private String trainNumber; // 车次名称
    private String startStation; // 始发站
    private String endStation; // 终点站
    private String departureTime; // 出发时间
    private String arrivalTime; // 到达时间
    private String duration; // 历时

    private Map<RailwaySeatType, String> seatTypeNumMaps; // 座位类型及其数量

    public static RailwayTrainInfo buildFromLeftTicketDataResult(String leftTicketData, Map<String, String> aliasMap) {
        if(StringUtils.isBlank(leftTicketData)) {
            return null;
        }

        String[] ticketDataArr = leftTicketData.split("\\|");
        if(ticketDataArr.length != 53) {
            System.out.println("错误!! 解析剩余车票数据长度异常");
            return null;
        }

        RailwayTrainInfo trainInfo = new RailwayTrainInfo();
        trainInfo.setTrainNumber(ticketDataArr[3]);
        trainInfo.setStartStation(aliasMap.get(ticketDataArr[6]));
        trainInfo.setEndStation(aliasMap.get(ticketDataArr[7]));
        trainInfo.setDepartureTime(ticketDataArr[8]);
        trainInfo.setArrivalTime(ticketDataArr[9]);
        trainInfo.setDuration(ticketDataArr[10]);


        Map<RailwaySeatType, String> seatTypeNumMaps = new HashMap<>();
        Arrays.stream(RailwaySeatType.values())
                .filter(seatType -> seatType.getIndex() != -1)
                .forEach(seatType -> {
                    String curTypeSeatNum = ticketDataArr[seatType.getIndex()];
                    if(StringUtils.isNotBlank(curTypeSeatNum)) {
                        if(StringUtils.equals("有", curTypeSeatNum)) {
                            seatTypeNumMaps.put(seatType, "余票充足");
                        }
                        if(StringUtils.isNumeric(curTypeSeatNum) && Integer.parseInt(curTypeSeatNum) < 2) {
                            // 过滤小于2的，后续优化
                            seatTypeNumMaps.put(seatType, curTypeSeatNum);
                        }
                    }
                });
        trainInfo.setSeatTypeNumMaps(seatTypeNumMaps);

        return trainInfo;
    }
}
