package org.zframework.web.service.admin.sport;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.zframework.core.util.Constant;
import org.zframework.core.web.support.WebResult;
import org.zframework.sms.utils.DateUtil;
import org.zframework.web.controller.ConfigSport;
import org.zframework.web.entity.sport.Applicationflow;
import org.zframework.web.entity.sport.ApplicationflowAttach;
import org.zframework.web.entity.sport.ApplicationflowLog;
import org.zframework.web.entity.sport.ProcessDetial;
import org.zframework.web.entity.system.User;
import org.zframework.web.service.BaseService;
import org.zframework.web.service.admin.sport.project.appliction.ApplicationflowAttachService;
import org.zframework.web.service.admin.sport.project.appliction.ApplicationflowService;

/**
 * 项目审批服务类
 * @author zhumin
 *
 */
@Service
public class SportCompletionService extends BaseService<Applicationflow> {
	@Autowired
	private ApplicationflowService applicationflowService;
	@Autowired
	private SportProcessDetialService sportProcessDetialService;
	@Autowired 
	private ApplicationflowLogService applicationflowLogService;
	@Autowired
	private ApplicationflowAttachService applicationflowAttachService;
	
	@Autowired
	ConfigSport configSport;
	
	/**
	 * 审计提交
	 * @return
	 */
	public JSONObject updateProjectApply(CommonsMultipartFile[] files,HttpServletRequest request,MultipartFile file,User user)
	{
		int flowId =Integer.valueOf(request.getParameter("flowId"));
		Applicationflow applicationflow =applicationflowService.getById(flowId);
		ProcessDetial processDetial = sportProcessDetialService.getById(applicationflow.getNext_processstepid());
		String remark = request.getParameter("remark");
		//日志信息表
		ApplicationflowLog applicationflowLog = new ApplicationflowLog();
		applicationflowLog.setAudit_time(DateUtil.dateToStr(new Date(), 12));
		applicationflowLog.setAudit_userid(user.getId());
		applicationflowLog.setAf_id(Integer.valueOf(flowId));
		applicationflowLog.setProcessid(processDetial.getProcessid());
		applicationflowLog.setProcessstep_id(applicationflow.getCurrent_processstepid());
		applicationflowLog.setAuditstatus(Constant.PA_APPLICATIONFLOW_LOG_0);
		applicationflowLog.setReason("申请描述(项目验收审批):"+remark);
		applicationflowLogService.save(applicationflowLog);
		
		applicationflow.setStatus(Constant.PA_APPLICATIONFLOW_4);
		applicationflow.setCurrent_processstepid(processDetial.getProcessnodeid());
		applicationflow.setNext_processstepid(processDetial.getNextprocessdetialid());
		applicationflow.setProcessid(processDetial.getProcessid());
		applicationflowService.update(applicationflow);
	
		applicationflowAttachService.executeHQL("update ApplicationflowAttach set status='无效' where af_id="+flowId +" and type='1' ");
		
		for(MultipartFile filet:files){
			if(!filet.isEmpty()){
				storeFile(request,filet,Integer.valueOf(flowId),Constant.FILE_TYPE_1);
			}
		}
		if(!file.isEmpty()){
			storeFile(request,file,Integer.valueOf(flowId),Constant.FILE_TYPE_2);
		}
		
		return WebResult.success();
	}
	
	
	private void storeFile(HttpServletRequest request,MultipartFile file,int afId,String type){
			
		String uploadPath = request.getSession().getServletContext().getRealPath("/");
		int seat = uploadPath.indexOf(configSport.getPorjectName());
		String strPath = uploadPath.substring(0, seat);
		String filePath = strPath + "image/upload/" + afId;
		File dir = new File(filePath);
		if (!dir.exists())
			dir.mkdirs();
		String ext = file.getOriginalFilename().substring(file.getOriginalFilename().indexOf("."));
		String newFileName = new SimpleDateFormat("yyyyMMddHHmmsss").format(new Date());
		newFileName += Math.round(10 + (Math.random() * 9990));
		newFileName += ext;
		ApplicationflowAttach applicationflowAttach = new ApplicationflowAttach();
		applicationflowAttach.setAf_id(afId);
		applicationflowAttach.setType(Constant.ATTCH_TYPE_1);
		applicationflowAttach.setName(newFileName);
		applicationflowAttach.setPath(filePath);
		applicationflowAttach.setFile_type(type);
		applicationflowAttach.setStatus("有效");
		applicationflowAttach.setCreate_userid(getCurrentUser().getId());
		applicationflowAttach.setCreate_time(DateUtil.dateToStr(new Date(), 12));
		applicationflowAttachService.save(applicationflowAttach);

		File f = new File(filePath + "/" + newFileName);
		try {
			FileCopyUtils.copy(file.getBytes(), f);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
