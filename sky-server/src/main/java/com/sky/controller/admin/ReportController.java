package com.sky.controller.admin;

import com.sky.result.Result;
import com.sky.service.ReportService;
import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/admin/report")
@Slf4j
public class ReportController {

    @Autowired
    private ReportService reportService;
    //营业额统计
    @GetMapping("/turnoverStatistics")
    public Result<TurnoverReportVO> turnoverStatistics(
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end
            ){
        log.info("统计指定时区内的营业额数据,{},{}",begin,end);
        return Result.success(reportService.getTurnoverStatistics(begin,end));
    }


    //用户统计
    @GetMapping("/userStatistics")
    public Result<UserReportVO> userStatistics(
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end
    ){
        log.info("统计指定时区内的营业额数据,{},{}",begin,end);
        return Result.success(reportService.getUserStatistics(begin,end));
    }

    //订单统计
    @GetMapping("/ordersStatistics")
    public Result<OrderReportVO> ordersStatistics(
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end
    ){
        log.info("统计指定时区内的营业额数据,{},{}",begin,end);
        return Result.success(reportService.getOrderStatistics(begin,end));
    }

    /**
     * 销量排名Top10
     */
    @GetMapping("/top10")
    @ApiOperation("销量排名Top10")
    public Result<SalesTop10ReportVO> top10(
            @DateTimeFormat(pattern = "yyyy-MM-dd HH") LocalDate beginTime,
            @DateTimeFormat(pattern = "yyyy-MM-dd HH") LocalDate endTime) {

        log.info("销量排名Top10：{}, {}", beginTime, endTime);
        SalesTop10ReportVO vo = reportService.getSalesTop10(beginTime, endTime);
        return Result.success(vo);
    }
}
