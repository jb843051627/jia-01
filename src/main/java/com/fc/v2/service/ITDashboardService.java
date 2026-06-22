package com.fc.v2.service;

import java.util.List;
import com.fc.v2.model.custom.DailyTrendVO;
import com.fc.v2.model.custom.DashboardOverviewVO;
import com.fc.v2.model.custom.ServiceTypeStatsVO;

public interface ITDashboardService {

    public DashboardOverviewVO getOverview();

    public List<DailyTrendVO> getTrend();

    public List<ServiceTypeStatsVO> getServiceTypeStats();
}
