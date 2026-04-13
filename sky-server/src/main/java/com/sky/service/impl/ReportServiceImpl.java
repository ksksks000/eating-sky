package com.sky.service.impl;

import com.github.pagehelper.util.StringUtil;
import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.service.WorkspaceService;
import com.sky.vo.*;
import io.swagger.models.auth.In;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Service;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class ReportServiceImpl implements ReportService {


    @Autowired
    public  OrderMapper orderMapper;

    @Autowired
    public UserMapper userMapper;

    @Autowired
    public WorkspaceService workspaceService;

    //统计指定时区内的营业额数据

    @Override
    public TurnoverReportVO getTurnoverStatistics(LocalDate begin, LocalDate end) {

        //当前集合用于存放从begin到end范围内的每天的日期
        List<LocalDate> dateList = new ArrayList<>();

        dateList.add(begin);

        while (!begin.equals(end)){
            //每次begin+1，且压进datelist ，直到end那一天
            begin = begin.plusDays(1);
            dateList.add(begin);
        }

        List<Double> turnoverList = new ArrayList<>();
        //根据datelist里面记录的日期，获取当天最开始和最后的时间，
        // 通过这个时间差来计算时间差内的“已完成”的总和金额
        for (LocalDate date : dateList) {
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);

            Map map = new HashMap();
            map.put("begin",beginTime);
            map.put("end",endTime);
            map.put("status", Orders.COMPLETED);
            Double turnover = orderMapper.sumByMap(map);

            //如果为空，相当于没人下单，那就返回0.0
            turnover = turnover== null ?0.0 :turnover;
            turnoverList.add(turnover);

        }

        return TurnoverReportVO
                .builder()
                .dateList(StringUtils.join(dateList,","))
                .turnoverList(StringUtils.join(turnoverList,","))
                .build();
    }

    /*
    * 业务逻辑：
    * 前端传进来一个begin，一个end，两个开始，结束日期
    * 后端根据两个日期来返回一个实体类对象，包含两个string字符串
    * 字符串1要求能够以逗号分隔开始到结束日期的 具体日期，格式为yyyy-MM-dd
    * 字符串2要求能够以                    每日的销售额
    * controller层反正只需要传参，
    * serverimpl实现类反正处理逻辑实现
    * mapper层就帮着serveriml拿数据就是了
    *
    * serverimpl实现类 实现思路如下，
    * 完成字符串1，
    * 你要求中间段日期以都展示出来并且以逗号分隔，
    * 那肯定是要根据begin和end把中间日期都想办法遍历一遍
    * 以逗号分隔，
    * 那肯定是原来有个东西装着，那就list吧
    * 所以，就for循环一下，找个begin = begin.plusDays(1);方法加一
    * 然后拿
    * TurnoverReportVO
                .builder()
                .dateList(StringUtils.join(dateList,","))
                .turnoverList(StringUtils.join(turnoverList,","))
                .build();
    *分隔
    * 完成字符串2
    * 你上面已经有对应每天的日期了，
    * 所以你根据每天的订单直接加起来就好了
    * 一样的，还是装到list里面到后面分隔一下*/


    //统计指定时区内的 用户数据
    @Override
    public UserReportVO getUserStatistics(LocalDate begin, LocalDate end) {
        //当前集合用于存放从begin到end范围内的每天的日期
        List<LocalDate> dateList = new ArrayList<>();

        dateList.add(begin);

        while (!begin.equals(end)){
            //每次begin+1，且压进datelist ，直到end那一天
            begin = begin.plusDays(1);
            dateList.add(begin);
        }

        List<Integer> newUserList = new ArrayList<>();

        List<Integer> totalUserList = new ArrayList<>();

        for (LocalDate date : dateList) {
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endime = LocalDateTime.of(date, LocalTime.MAX);

            Map map = new HashMap<>();
            map.put("end",endime);
            //总用户数量
            Integer totalUser = userMapper.countByMap(map);
            //每日新增用户数量
            map.put("begin",beginTime);
            Integer newUser = userMapper.countByMap(map);
            newUserList.add(newUser);
            totalUserList.add(totalUser);
        }


        return UserReportVO.builder()
                .dateList(StringUtils.join(dateList,","))
                .totalUserList(StringUtils.join(totalUserList,","))
                .newUserList(StringUtils.join(newUserList,","))
                .build();
    }

    //统计指定时区内的 订单数据
    @Override
    public OrderReportVO getOrderStatistics(LocalDate begin, LocalDate end) {
        //当前集合用于存放从begin到end范围内的每天的日期
        List<LocalDate> dateList = new ArrayList<>();

        dateList.add(begin);

        while (!begin.equals(end)){
            //每次begin+1，且压进datelist ，直到end那一天
            begin = begin.plusDays(1);
            dateList.add(begin);
        }

        List<Integer> orderCountList = new ArrayList<>();
        List<Integer> vaildOrderCountList = new ArrayList<>();

        for (LocalDate date : dateList) {
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endime = LocalDateTime.of(date, LocalTime.MAX);
            Integer orderCount = getOrderCount(beginTime,endime,null);

            Integer vaildOrderCount = getOrderCount(beginTime, endime,Orders.COMPLETED);

            orderCountList.add(orderCount);
            vaildOrderCountList.add(vaildOrderCount);

        }

        Integer totalOrderCount = orderCountList.stream().reduce(Integer::sum).get();
        Integer vaildOrderCount = vaildOrderCountList.stream().reduce(Integer::sum).get();

        Double orderCompletionRate = 0.0;
        if (totalOrderCount != 0){
             orderCompletionRate = vaildOrderCount.doubleValue() / totalOrderCount;
        }


        return OrderReportVO.builder()
                .dateList(StringUtils.join(dateList,","))
                .orderCountList(StringUtils.join(orderCountList,","))
                .validOrderCountList(StringUtils.join(vaildOrderCountList,","))
                .totalOrderCount(totalOrderCount)
                .validOrderCount(vaildOrderCount)
                .orderCompletionRate(orderCompletionRate)
                .build();
    }

    private Integer getOrderCount(LocalDateTime begin,LocalDateTime end,Integer status){
        Map map = new HashMap<>();
        map.put("begin",begin);
        map.put("end",end);
        map.put("status",status);
        return orderMapper.getOrderByMap(map);
    }


    /*
    * 统计指定时区内的top10销量*/
    @Override
    public SalesTop10ReportVO getSalesTop10(LocalDate begin, LocalDate end) {
        // 1. 调用Mapper查询数据库，返回 List<Map<String, Object>>
        Map<String, Object> map = new HashMap<>();
        map.put("begin", begin);
        map.put("end", end);

        List<Map<String, Object>> resultList = orderMapper.getSalesTop10(map);

        // 2. 数据清洗与转换
        List<String> nameList = new ArrayList<>();
        List<Integer> numberList = new ArrayList<>();

        if (resultList != null && !resultList.isEmpty()) {
            for (Map<String, Object> item : resultList) {
                // 假设SQL返回的key是 "name" 和 "number"
                nameList.add((String) item.get("name"));
                // 如果数据库返回的是Long类型（count统计），需要转为Integer
                Number number = (Number) item.get("number");
                numberList.add(number.intValue());
            }
        }


        // 3. 封装VO返回
        return SalesTop10ReportVO.builder()
                .nameList(nameList.toString())
                .numberList(numberList.toString())
                .build();
    }


    /**
     * 导出运营数据报表
     * @param response
     */
    @Override
    public void exportBusinessData(HttpServletResponse response) {

        //查询数据库，获取营业数据--最近30天的运营数据
        //30天前（begintime取00.00时刻，所以取30）
        LocalDate dateBegin = LocalDate.now().minusDays(30);
        //1天前（endtime取 23.59时刻，所以取1）
        LocalDate dateEnd = LocalDate.now().minusDays(1);

        //然后通过获得的begintime和endtime从获取数据那里调数据
        //查询概览数据
        BusinessDataVO businessDataVO = workspaceService.getBusinessData(
                LocalDateTime.of(dateBegin, LocalTime.MIN),
                LocalDateTime.of(dateBegin, LocalTime.MAX));

        //通过poi将数据写入到Excel文件中

        InputStream in = this.getClass().getClassLoader().getResourceAsStream("template/运营数据报表模板.xlsx");


        try {
            //基于模板文件创建一个新的Excel文件
            XSSFWorkbook excel = new XSSFWorkbook(in);

            //获取表格文件的sheet页
            XSSFSheet sheet = excel.getSheet("Sheet1");

            //填充数据--时间
            sheet.getRow(1).getCell(1).setCellValue("时间" + dateBegin + "至" + dateEnd);

            //获得第四行
            XSSFRow row = sheet.getRow(3);
            row.getCell(2).setCellValue(businessDataVO.getTurnover());
            row.getCell(4).setCellValue(businessDataVO.getOrderCompletionRate());
            row.getCell(6).setCellValue(businessDataVO.getNewUsers());

            //获得第五行
            row = sheet.getRow(4);
            row.getCell(2).setCellValue(businessDataVO.getValidOrderCount());
            row.getCell(4).setCellValue(businessDataVO.getUnitPrice());

            //填充明细数据
            for (int i = 0; i < 30; i++) {
                LocalDate date = dateBegin.plusDays(i);

                //获得某一行
                BusinessDataVO businessData = workspaceService.getBusinessData(
                        LocalDateTime.of(date, LocalTime.MIN),
                        LocalDateTime.of(date, LocalTime.MAX));
                row = sheet.getRow(7+i);
                row.getCell(1).setCellValue(date.toString());
                row.getCell(2).setCellValue(businessData.getTurnover());
                row.getCell(3).setCellValue(businessData.getValidOrderCount());
                row.getCell(4).setCellValue(businessData.getOrderCompletionRate());
                row.getCell(5).setCellValue(businessData.getUnitPrice());
                row.getCell(6).setCellValue(businessData.getNewUsers());
            }

            //通过输出流对象将Excel文件下载到客户端浏览器
            ServletOutputStream out = response.getOutputStream();
            excel.write(out);

            //关闭资源
            out.close();;
            excel.close();

        } catch (IOException e) {
            e.printStackTrace();
        }



    }

}
