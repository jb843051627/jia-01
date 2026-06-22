package com.fc.v2.controller.admin;

import java.util.List;

import com.fc.v2.common.base.BaseController;
import com.fc.v2.common.domain.AjaxResult;
import com.fc.v2.model.custom.DailyTrendVO;
import com.fc.v2.model.custom.DashboardOverviewVO;
import com.fc.v2.model.custom.ServiceTypeStatsVO;
import com.fc.v2.service.ITDashboardService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Api(value = "数据看板", tags = "数据看板")
@Controller
@RequestMapping("/DashboardController")
public class DashboardController extends BaseController {

    private final String prefix = "admin/dashboard";

    @Autowired
    private ITDashboardService dashboardService;

    @ApiOperation(value = "数据看板页面", notes = "数据看板页面")
    @GetMapping("/view")
    public String view(ModelMap model) {
        return prefix + "/dashboard";
    }

    @ApiOperation(value = "获取概览数据", notes = "获取今日预约数、本周预约数、本月订单数、本月收入、待派单数、待回访数")
    @GetMapping("/overview")
    @ResponseBody
    public AjaxResult overview() {
        DashboardOverviewVO overview = dashboardService.getOverview();
        return AjaxResult.successData(200, overview);
    }

    @ApiOperation(value = "获取近30天趋势数据", notes = "获取近30天每天的预约数和收入")
    @GetMapping("/trend")
    @ResponseBody
    public AjaxResult trend() {
        List<DailyTrendVO> trend = dashboardService.getTrend();
        return AjaxResult.successData(200, trend);
    }

    @ApiOperation(value = "获取服务类型统计", notes = "获取各服务类型订单占比")
    @GetMapping("/service-type-stats")
    @ResponseBody
    public AjaxResult serviceTypeStats() {
        List<ServiceTypeStatsVO> stats = dashboardService.getServiceTypeStats();
        return AjaxResult.successData(200, stats);
    }
}
