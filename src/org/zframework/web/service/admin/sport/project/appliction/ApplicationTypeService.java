package org.zframework.web.service.admin.sport.project.appliction;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;

import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;
import org.zframework.core.util.Constant;
import org.zframework.core.util.ObjectUtil;
import org.zframework.core.util.SMSUtils;
import org.zframework.core.util.SequenceFactoryBean;
import org.zframework.core.util.StringUtil;
import org.zframework.core.web.support.WebResult;
import org.zframework.sms.utils.DateUtil;
import org.zframework.web.controller.ConfigSport;
import org.zframework.web.entity.sport.Applicationflow;
import org.zframework.web.entity.sport.ApplicationflowAttach;
import org.zframework.web.entity.sport.ApplicationflowDetail;
import org.zframework.web.entity.sport.ApplicationflowLog;
import org.zframework.web.entity.sport.Applicationtype;
import org.zframework.web.entity.sport.JSQCForm;
import org.zframework.web.entity.sport.ProcessDetial;
import org.zframework.web.entity.sport.Signuserrole;
import org.zframework.web.entity.sport.Userarea;
import org.zframework.web.entity.system.User;
import org.zframework.web.service.BaseService;
import org.zframework.web.service.admin.sport.SportProcessDetialService;
import org.zframework.web.service.admin.sport.SportSignUserRoleService;
import org.zframework.web.service.admin.sport.SportUserAreaService;
import org.zframework.web.service.admin.system.LogService;

/**
 * 项目申请
 * 
 * @author zhumin
 *
 */
@Service
public class ApplicationTypeService extends BaseService<Applicationtype> {

	private static Logger logger = Logger.getLogger(ApplicationTypeService.class);

	@Autowired
	SequenceFactoryBean sequenceFactoryBean;
	@Autowired
	ApplicationflowDetailService applicationflowDetailService;
	@Autowired
	SportSignUserRoleService sportSignUserRoleService;
	@Autowired
	LogService logService;
	@Autowired
	SMSUtils sMSUtils;
	@Autowired
	SportUserAreaService sportUserAreaService;
	@Autowired
	SportProcessDetialService sportProcessDetialService;
	@Autowired
	ApplicationflowAttachService applicationflowAttachService;
	@Autowired
	ConfigSport configSport;
	@Autowired
	ApplicationflowService applicationflowService;

	/**
	 * 更新申请表单信息
	 * 
	 * @param request
	 * @param applicationflowDetail
	 * @param user
	 * @param file
	 * @return
	 */
	public JSONObject updateProject(HttpServletRequest request, ApplicationflowDetail applicationflowDetail, User user, MultipartFile file) {
		// 判断用户是否有角色
		List<Signuserrole> listSignuserrole = sportSignUserRoleService.listForEntity("SELECT sur From Signuserrole sur where sur.user.id=?", new Object[] { user.getId() });
		if (listSignuserrole == null || listSignuserrole.size() <= 0) {
			return WebResult.error("您还没有授予角色");
		}
		Signuserrole singUserRole = listSignuserrole.get(0);
		// 根据角色查询当前的节点和下个节点
		ProcessDetial processDetial = baseDao.getByHQL(ProcessDetial.class, "FROM ProcessDetial pd where pd.signroleid = ?", new Object[] { singUserRole.getSignrole().getId() });
		if (null == processDetial) {
			return WebResult.error("角色明细为空");
		}
		String completed_date_str = request.getParameter("completed_date_str");
		String start_date_str = request.getParameter("start_date_str");
		if (!StringUtil.isEmpty(start_date_str)) {
			applicationflowDetail.setStart_date(start_date_str);
		}
		if (!StringUtil.isEmail(completed_date_str)) {
			applicationflowDetail.setCompleted_date(completed_date_str);
		}

		// 流程表Id
		String flowId = request.getParameter("flowId");
		ProcessDetial processDetialNext = sportProcessDetialService.getById(processDetial.getNextprocessdetialid());
		Applicationflow applicationflow = applicationflowService.getById(Integer.valueOf(flowId));
		String remark = request.getParameter("remark");
		applicationflow.setRemark(remark);
		// 当前节点
		applicationflow.setCurrent_processstepid(processDetialNext.getId());
		// 下个节点
		applicationflow.setNext_processstepid(processDetialNext.getNextprocessdetialid());
		// 更新主表
		applicationflowService.update(applicationflow);

		// 更新附属表
		String flowDetailId = request.getParameter("flowDetailId");
		applicationflowDetail.setId(Integer.valueOf(flowDetailId));
		applicationflowDetail.setAf_id(applicationflow.getId());
//		BeanUtils.copyProperties(source, applicationflowDetail);
		applicationflowDetailService.update(applicationflowDetail);

		// 插入新增流水记录（日志）
		ApplicationflowLog applicationflowLog = new ApplicationflowLog();
		applicationflowLog.setAf_id(applicationflow.getId());
		applicationflowLog.setProcessstep_id(processDetial.getId());
		applicationflowLog.setAudit_time(DateUtil.dateToStr(new Date(), 12));
		applicationflowLog.setAudit_userid(user.getId());
		applicationflowLog.setAuditstatus(Constant.PA_APPLICATIONFLOW_0);
		applicationflowLog.setReason("项目再申请");
		baseDao.save(applicationflowLog);

		if (!file.isEmpty()) {
			String uploadPath = request.getSession().getServletContext().getRealPath("/");
			int seat = uploadPath.indexOf(configSport.getPorjectName());
			String strPath = uploadPath.substring(0, seat);
			String filePath = strPath + "image/upload/" + applicationflow.getId();
			File dir = new File(filePath);
			if (!dir.exists())
				dir.mkdirs();
			String ext = file.getOriginalFilename().substring(file.getOriginalFilename().indexOf("."));
			String newFileName = new SimpleDateFormat("yyyyMMddHHmmsss").format(new Date());
			newFileName += Math.round(10 + (Math.random() * 9990));
			newFileName += ext;
			File f = new File(filePath + "/" + newFileName);
			try {
				FileCopyUtils.copy(file.getBytes(), f);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			ApplicationflowAttach applicationflowAttach = applicationflowAttachService.getByHql("FROM ApplicationflowAttach WHERE af_id=" + applicationflow.getId());
			if(ObjectUtil.isNull(applicationflowAttach)){
				applicationflowAttach = new ApplicationflowAttach();
				applicationflowAttach.setAf_id(applicationflow.getId());
				applicationflowAttach.setName(newFileName);
				applicationflowAttach.setPath(filePath);
				applicationflowAttach.setCreate_userid(getCurrentUser().getId());
				applicationflowAttach.setCreate_time(DateUtil.dateToStr(new Date(), 12));
				applicationflowAttachService.save(applicationflowAttach);
			}else{
				//不为空
				applicationflowAttach.setName(newFileName);
				applicationflowAttach.setPath(filePath);
				applicationflowAttach.setCreate_userid(getCurrentUser().getId());
				applicationflowAttach.setCreate_time(DateUtil.dateToStr(new Date(), 12));
				applicationflowAttachService.update(applicationflowAttach);
			}
		}

		// 发送短信
		Applicationtype applicationtype = this.getById(applicationflow.getAt_id());
		sMSUtils.sendSMSGroup(getCurrentUser(), Constant.SMS_TEMP_62695, new String[] { applicationflow.getForm_num(), applicationtype.getName(), Constant.WEB_URL });

		return WebResult.success();

	}

	/**
	 * 添加项目申请
	 * 
	 * @return
	 */
	public JSONObject addProject(HttpServletRequest request, ApplicationflowDetail applicationflowDetail, User user, MultipartFile file) {
		// 判断用户是否有角色
		List<Signuserrole> listSignuserrole = sportSignUserRoleService.listForEntity("SELECT sur From Signuserrole sur where sur.user.id=?", new Object[] { user.getId() });
		if (listSignuserrole == null || listSignuserrole.size() <= 0) {
			return WebResult.error("您还没有授予角色");
		}
		Signuserrole singUserRole = listSignuserrole.get(0);
		// 根据角色查询当前的节点和下个节点
		ProcessDetial processDetial = baseDao.getByHQL(ProcessDetial.class, "FROM ProcessDetial pd where pd.signroleid = ?", new Object[] { singUserRole.getSignrole().getId() });
		if (null == processDetial) {
			return WebResult.error("角色明细为空");
		}
		ProcessDetial processDetialNext = sportProcessDetialService.getById(processDetial.getNextprocessdetialid());
		Applicationflow applicationflow = new Applicationflow();
		// 当前节点
		applicationflow.setCurrent_processstepid(processDetialNext.getId());
		// 下个节点
		applicationflow.setNext_processstepid(processDetialNext.getNextprocessdetialid());
		// 表单类型     页面写死传过来    
//		<!-- 表单类型 -->
//		<input type="hidden" name="at_id" value="11" />
		String at_id = request.getParameter("at_id");
		applicationflow.setAt_id(Integer.valueOf(at_id));
		// 区域id
		// String area_id = request.getParameter("area_id");
		// applicationflow.setArea_id(Integer.valueOf(area_id));

		Userarea userArea = sportUserAreaService.getByHql("From Userarea ua where ua.user.id=" + user.getId());
		if (ObjectUtil.isNotNull(userArea)) {
			applicationflow.setArea_id(userArea.getArea().getId());
		}
		// add by james 增加区域名称
		applicationflow.setCommunity_name(request.getParameter("area_str"));
		// 备注
		String remark = request.getParameter("remark");
		applicationflow.setRemark(remark);
		// 创建人
		applicationflow.setCreate_userid(user.getId());
		applicationflow.setStatus(Constant.PA_APPLICATIONFLOW_0);
		String create_time_str = request.getParameter("create_time_str");
		if (ObjectUtil.isNotNull(create_time_str)) {
			create_time_str = DateUtil.dateToStr(new Date(), 12);
			applicationflow.setCreate_time(create_time_str);
		} else {
			applicationflow.setCreate_time(create_time_str);
		}

		// 订单号
		try {
			applicationflow.setForm_num(sequenceFactoryBean.getObject());
			applicationflow.setProcessid(processDetialNext.getProcessid());
			Integer afId = (Integer) baseDao.save(applicationflow);
			// 插入新增流水记录（日志）
			ApplicationflowLog applicationflowLog = new ApplicationflowLog();
			applicationflowLog.setAf_id(afId);
			applicationflowLog.setProcessstep_id(processDetial.getId());
			applicationflowLog.setAudit_time(DateUtil.dateToStr(new Date(), 12));
			applicationflowLog.setAudit_userid(user.getId());
			applicationflowLog.setAuditstatus(Constant.PA_APPLICATIONFLOW_0);
			applicationflowLog.setReason("项目申请创建");
			baseDao.save(applicationflowLog);
			String completed_date_str = request.getParameter("completed_date_str");
			String start_date_str = request.getParameter("start_date_str");
			if (!StringUtil.isEmpty(start_date_str)) {
				applicationflowDetail.setStart_date(start_date_str);
			}
			if (!StringUtil.isEmail(completed_date_str)) {
				applicationflowDetail.setCompleted_date(completed_date_str);
			}
			applicationflowDetail.setAf_id(afId);
			applicationflowDetail.setCreate_time(create_time_str);
			applicationflowDetail.setCreate_userid(user.getId());
			applicationflowDetail.setStatus("0");
			applicationflowDetailService.save(applicationflowDetail);
			//
			Applicationtype applicationtype = this.getById(applicationflow.getAt_id());

			if(null != file){
				if (!file.isEmpty()) {
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
					applicationflowAttach.setType(Constant.ATTCH_TYPE_0);
					applicationflowAttach.setName(newFileName);
					applicationflowAttach.setPath(filePath);
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
			sMSUtils.sendSMSGroup(getCurrentUser(), Constant.SMS_TEMP_62695, new String[] { applicationflow.getForm_num(), applicationtype.getName(), Constant.WEB_URL });

			return WebResult.success();
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("系统异常");
		}

	}

	public JSONObject updateProjectQX(HttpServletRequest request, JSQCForm jSQCForm, User user) {
		// 判断用户是否有角色
		List<Signuserrole> listSignuserrole = sportSignUserRoleService.listForEntity("SELECT sur From Signuserrole sur where sur.user.id=?", new Object[] { user.getId() });
		if (listSignuserrole == null || listSignuserrole.size() <= 0) {
			return WebResult.error("您还没有授予角色");
		}
		Signuserrole singUserRole = listSignuserrole.get(0);
		// 根据角色查询当前的节点和下个节点
		ProcessDetial processDetial = baseDao.getByHQL(ProcessDetial.class, "FROM ProcessDetial pd where pd.signroleid = ?", new Object[] { singUserRole.getSignrole().getId() });
		if (null == processDetial) {
			return WebResult.error("角色明细为空");
		}
		ProcessDetial processDetialNext = sportProcessDetialService.getById(processDetial.getNextprocessdetialid());
		String flowId = request.getParameter("flowId");
		Applicationflow applicationflow = applicationflowService.getById(Integer.valueOf(flowId));
		// 当前节点
		applicationflow.setCurrent_processstepid(processDetialNext.getId());
		// 下个节点
		applicationflow.setNext_processstepid(processDetialNext.getNextprocessdetialid());

		String remark = request.getParameter("remark");
		applicationflow.setRemark(remark);
		applicationflowService.update(applicationflow);

		try {
			// 插入新增流水记录（日志）
			ApplicationflowLog applicationflowLog = new ApplicationflowLog();
			applicationflowLog.setAf_id(applicationflow.getId());
			applicationflowLog.setProcessstep_id(processDetial.getId());
			applicationflowLog.setAudit_userid(user.getId());
			applicationflowLog.setAuditstatus(Constant.PA_APPLICATIONFLOW_0);
			applicationflowLog.setAudit_time(DateUtil.dateToStr(new Date(), 12));
			applicationflowLog.setReason("器械项目申请再创建");
			baseDao.save(applicationflowLog);
			applicationflowDetailService.executeHQL("DELET FROM ApplicationflowDetail WHERE af_id=" + applicationflow.getId());

			for (ApplicationflowDetail applicationflowDetail : jSQCForm.getDetails()) {
				applicationflowDetail.setAf_id(applicationflow.getId());
				applicationflowDetailService.save(applicationflowDetail);
			}
			sMSUtils.sendSMSGroup(getCurrentUser(), Constant.SMS_TEMP_62695, new String[] { applicationflow.getForm_num(), "社区百姓健身房器械配置表", Constant.WEB_URL });

			return WebResult.success();
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("系统异常");
		}

	}

	/**
	 * 添加申请单器械
	 * 
	 * @param request
	 * @param areaIds
	 * @return
	 */
	public JSONObject addProjectQX(HttpServletRequest request, JSQCForm jSQCForm, User user) {
		// 判断用户是否有角色
		List<Signuserrole> listSignuserrole = sportSignUserRoleService.listForEntity("SELECT sur From Signuserrole sur where sur.user.id=?", new Object[] { user.getId() });
		if (listSignuserrole == null || listSignuserrole.size() <= 0) {
			return WebResult.error("您还没有授予角色");
		}
		Signuserrole singUserRole = listSignuserrole.get(0);
		// 根据角色查询当前的节点和下个节点
		ProcessDetial processDetial = baseDao.getByHQL(ProcessDetial.class, "FROM ProcessDetial pd where pd.signroleid = ?", new Object[] { singUserRole.getSignrole().getId() });
		if (null == processDetial) {
			return WebResult.error("角色明细为空");
		}
		ProcessDetial processDetialNext = sportProcessDetialService.getById(processDetial.getNextprocessdetialid());
		Applicationflow applicationflow = new Applicationflow();
		// 当前节点
		applicationflow.setCurrent_processstepid(processDetialNext.getId());
		// 下个节点
		applicationflow.setNext_processstepid(processDetialNext.getNextprocessdetialid());
		// 创建时间
		applicationflow.setCreate_time(request.getParameter("create_time_str"));
		applicationflow.setCommunity_name(request.getParameter("community_name"));
		applicationflow.setApplication_unit(request.getParameter("Application_unit"));
		// 表单类型
		String at_id = request.getParameter("at_id");
		applicationflow.setAt_id(Integer.valueOf(at_id));
		// 区域id
		Userarea userArea = sportUserAreaService.getByHql("From Userarea ua where ua.user.id=" + user.getId());
		if (ObjectUtil.isNotNull(userArea)) {
			applicationflow.setArea_id(userArea.getArea().getId());
		}
		// 备注
		String remark = request.getParameter("remark");
		applicationflow.setRemark(remark);
		// 创建人
		applicationflow.setCreate_userid(user.getId());
		applicationflow.setStatus(Constant.PA_APPLICATIONFLOW_0);
		try {
			applicationflow.setForm_num(sequenceFactoryBean.getObject());
			Integer afId = (Integer) baseDao.save(applicationflow);
			// 插入新增流水记录（日志）
			ApplicationflowLog applicationflowLog = new ApplicationflowLog();
			applicationflowLog.setAf_id(afId);
			applicationflowLog.setProcessstep_id(processDetial.getId());
			applicationflowLog.setAudit_userid(user.getId());
			applicationflowLog.setAuditstatus(Constant.PA_APPLICATIONFLOW_0);
			applicationflowLog.setAudit_time(DateUtil.dateToStr(new Date(), 12));
			applicationflowLog.setReason("器械项目申请创建");
			baseDao.save(applicationflowLog);
			for (ApplicationflowDetail applicationflowDetail : jSQCForm.getDetails()) {
				applicationflowDetail.setAf_id(afId);
				applicationflowDetail.setObj8(request.getParameter("obj8"));
				applicationflowDetail.setObj9(request.getParameter("obj9"));
				applicationflowDetail.setObj10(request.getParameter("obj10"));
				applicationflowDetailService.save(applicationflowDetail);
			}
			sMSUtils.sendSMSGroup(getCurrentUser(), Constant.SMS_TEMP_62695, new String[] { applicationflow.getForm_num(), "社区百姓健身房器械配置表", Constant.WEB_URL });

			return WebResult.success();
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("系统异常");
		}
	}

}
