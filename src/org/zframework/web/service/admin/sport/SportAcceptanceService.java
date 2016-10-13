package org.zframework.web.service.admin.sport;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.zframework.core.util.Constant;
import org.zframework.core.web.support.WebResult;
import org.zframework.sms.utils.DateUtil;
import org.zframework.web.entity.sport.Applicationflow;
import org.zframework.web.entity.sport.ApplicationflowLog;
import org.zframework.web.entity.sport.ProcessDetial;
import org.zframework.web.entity.system.User;
import org.zframework.web.service.BaseService;

/**
 * 
 * @author mzhu
 *
 *         2016年2月7日 下午6:14:54
 */
@Service
public class SportAcceptanceService extends BaseService<Applicationflow> {

	@Autowired
	private ApplicationflowLogService applicationflowLogService;
	@Autowired
	private SportProcessDetialService sportProcessDetialService;
			
	/**
	 * 同意
	 * 
	 * @param request
	 * @param user
	 * @return
	 */
	public JSONObject updateAgree(HttpServletRequest request, User user) {
		String flowId = request.getParameter("flowId");
		String reason = request.getParameter("reason");
		Applicationflow applicationflow = this.getById(Integer.valueOf(flowId));

		// 日志表
		ApplicationflowLog applicationflowLog = new ApplicationflowLog();
		applicationflowLog.setAf_id(Integer.valueOf(flowId));
		applicationflowLog.setProcessid(applicationflow.getProcessid());
		applicationflowLog.setProcessstep_id(applicationflow
				.getCurrent_processstepid());
		applicationflowLog.setAudit_userid(user.getId());
		applicationflowLog.setAuditstatus("0");
		applicationflowLog.setAudit_time(DateUtil.dateToStr(new Date(), 12));
		applicationflowLog.setReason("竣工确认" + reason);
		
		
		
		applicationflow.setStatus(Constant.PA_APPLICATIONFLOW_5);
		this.update(applicationflow);
		
		applicationflowLogService.save(applicationflowLog);
		return WebResult.success();
	}

	/**
	 * 不同意
	 * 
	 * @param request
	 * @param user
	 * @return
	 */
	public JSONObject updateDisAgree(HttpServletRequest request, User user) {
		String flowId = request.getParameter("flowId");
		String reason = request.getParameter("reason");
		Applicationflow applicationflow = this.getById(Integer.valueOf(flowId));
		applicationflow.setStatus(Constant.PA_APPLICATIONFLOW_1);
		ProcessDetial pd = sportProcessDetialService.getByHql("FROM ProcessDetial pd where pd.nextprocessdetialid="+applicationflow.getCurrent_processstepid());
		
		// 日志表
		ApplicationflowLog applicationflowLog = new ApplicationflowLog();
		applicationflowLog.setAf_id(Integer.valueOf(flowId));
		applicationflowLog.setProcessid(applicationflow.getProcessid());
		applicationflowLog.setProcessstep_id(applicationflow
				.getCurrent_processstepid());
		applicationflowLog.setAudit_userid(user.getId());
		applicationflowLog.setAuditstatus("1");
		applicationflowLog.setAudit_time(DateUtil.dateToStr(new Date(), 12));
		applicationflowLog.setReason("竣工不同意" + reason);
		
		applicationflow.setCurrent_processstepid(pd.getProcessnodeid());
		applicationflow.setNext_processstepid(pd.getNextprocessdetialid());
		this.update(applicationflow);
		applicationflowLogService.save(applicationflowLog);
		return WebResult.success();
	}

}
