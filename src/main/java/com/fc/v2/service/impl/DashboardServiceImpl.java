package com.fc.v2.service.impl;

import java.math.BigDecimal;
import java.util.*;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fc.v2.mapper.auto.AppointmentMapper;
import com.fc.v2.mapper.auto.ServiceTypeMapper;
import com.fc.v2.mapper.auto.TOrderMapper;
import com.fc.v2.model.auto.Appointment;
import com.fc.v2.model.auto.AppointmentStatus;
import com.fc.v2.model.custom.DailyTrendVO;
import com.fc.v2.model.custom.DashboardOverviewVO;
import com.fc.v2.model.custom.ServiceTypeStatsVO;
import com.fc.v2.service.ITDashboardService;
import com.fc.v2.util.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DashboardServiceImpl extends ServiceImpl<AppointmentMapper, Appointment> implements ITDashboardService {

    @Autowired
    private AppointmentMapper appointmentMapper;

    @Autowired
    private TOrderMapper tOrderMapper;

    @Autowired
    private ServiceTypeMapper serviceTypeMapper;

    @Override
    public DashboardOverviewVO getOverview() {
        DashboardOverviewVO vo = new DashboardOverviewVO();
        Date now = new Date();

        Date todayStart = DateUtils.truncate(now, Calendar.DAY_OF_MONTH);
        Date todayEnd = todayStart;

        Calendar weekStartCal = Calendar.getInstance();
        weekStartCal.setTime(now);
        weekStartCal.setFirstDayOfWeek(Calendar.MONDAY);
        weekStartCal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        Date weekStart = DateUtils.truncate(weekStartCal.getTime(), Calendar.DAY_OF_MONTH);
        Date weekEnd = todayStart;

        Calendar monthStartCal = Calendar.getInstance();
        monthStartCal.setTime(now);
        monthStartCal.set(Calendar.DAY_OF_MONTH, 1);
        Date monthStart = DateUtils.truncate(monthStartCal.getTime(), Calendar.DAY_OF_MONTH);
        Date monthEnd = todayStart;

        Integer todayAppointments = appointmentMapper.countAppointmentsByDateRange(todayStart, todayEnd);
        vo.setTodayAppointments(todayAppointments != null ? todayAppointments : 0);

        Integer weekAppointments = appointmentMapper.countAppointmentsByDateRange(weekStart, weekEnd);
        vo.setWeekAppointments(weekAppointments != null ? weekAppointments : 0);

        Integer monthOrders = tOrderMapper.countOrdersByDateRange(monthStart, monthEnd);
        vo.setMonthOrders(monthOrders != null ? monthOrders : 0);

        BigDecimal monthRevenue = tOrderMapper.sumRevenueByDateRange(monthStart, monthEnd);
        vo.setMonthRevenue(monthRevenue != null ? monthRevenue : BigDecimal.ZERO);

        Integer pendingConfirm = appointmentMapper.countAppointmentsByStatus(AppointmentStatus.PENDING.getCode());
        Integer confirmed = appointmentMapper.countAppointmentsByStatus(AppointmentStatus.CONFIRMED.getCode());
        vo.setPendingDispatch((pendingConfirm != null ? pendingConfirm : 0) + (confirmed != null ? confirmed : 0));

        Integer completedAppointments = appointmentMapper.countAppointmentsByStatus(AppointmentStatus.COMPLETED.getCode());
        vo.setPendingFollowUp(completedAppointments != null ? completedAppointments : 0);

        return vo;
    }

    @Override
    public List<DailyTrendVO> getTrend() {
        Date now = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(now);
        cal.add(Calendar.DAY_OF_MONTH, -29);
        Date startDate = DateUtils.truncate(cal.getTime(), Calendar.DAY_OF_MONTH);
        Date endDate = DateUtils.truncate(now, Calendar.DAY_OF_MONTH);

        List<DailyTrendVO> appointmentTrend = appointmentMapper.countAppointmentsGroupByDate(startDate, endDate);
        List<DailyTrendVO> revenueTrend = tOrderMapper.sumRevenueGroupByDate(startDate, endDate);

        Map<String, DailyTrendVO> trendMap = new LinkedHashMap<>();

        Calendar dateCal = Calendar.getInstance();
        dateCal.setTime(startDate);
        for (int i = 0; i < 30; i++) {
            String dateStr = DateUtils.format(dateCal.getTime(), DateUtils.YYYY_MM_DD);
            DailyTrendVO vo = new DailyTrendVO();
            vo.setDate(dateStr);
            trendMap.put(dateStr, vo);
            dateCal.add(Calendar.DAY_OF_MONTH, 1);
        }

        for (DailyTrendVO vo : appointmentTrend) {
            if (trendMap.containsKey(vo.getDate())) {
                trendMap.get(vo.getDate()).setAppointmentCount(vo.getAppointmentCount());
            }
        }

        for (DailyTrendVO vo : revenueTrend) {
            if (trendMap.containsKey(vo.getDate())) {
                trendMap.get(vo.getDate()).setRevenue(vo.getRevenue());
            }
        }

        return new ArrayList<>(trendMap.values());
    }

    @Override
    public List<ServiceTypeStatsVO> getServiceTypeStats() {
        return serviceTypeMapper.countOrdersByServiceType();
    }
}
