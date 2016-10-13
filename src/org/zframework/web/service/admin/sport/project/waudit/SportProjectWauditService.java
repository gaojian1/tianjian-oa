package org.zframework.web.service.admin.sport.project.waudit;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.zframework.core.util.Constant;
import org.zframework.core.util.SMSUtils;
import org.zframework.core.util.TimeUtil;
import org.zframework.core.web.support.WebResult;
import org.zframework.orm.query.Condition;
import org.zframework.orm.query.PageBean;
import org.zframework.sms.utils.DateUtil;
import org.zframework.web.entity.sport.Applicationflow;
import org.zframework.web.entity.sport.ApplicationflowLog;
import org.zframework.web.entity.sport.Applicationflow_log;
import org.zframework.web.entity.sport.Applicationtype;
import org.zframework.web.entity.sport.ProcessDetial;
import org.zframework.web.entity.sport.Processnode;
import org.zframework.web.entity.sport.Signuserrole;
import org.zframework.web.entity.system.User;
import org.zframework.web.service.BaseService;
import org.zframework.web.service.admin.sport.SportSignUserRoleService;
import org.zframework.web.service.admin.system.UserService;

/**
 * 项目申请单管理查询service
 * 
 * @author weng
 *
 */
@Service
public class SportProjectWauditService extends BaseService<Applicationtype> {

	private Log log = LogFactory.getLog("userInfoLog");

	/**
	 * 显示表单类型
	 * 
	 * @return
	 */
	public JSONArray getAppType() {
		JSONArray jsonTree = new JSONArray();
		List<Applicationtype> listArea = this.list("from Applicationtype apptype where status is null or status=0 ");
		for (Applicationtype root : listArea) {
			JSONObject jRes = new JSONObject();
			jRes.element("id", root.getId());
			jRes.element("text", root.getName());
			jRes.element("iconCls", "icon-unit");
			jsonTree.add(jRes);
		}
		return jsonTree;
	}

	/**
	 * 用于查询用户区域信息
	 * 
	 * @param clazz
	 * @param hql
	 * @param pageBean
	 * @param params
	 * @return
	 */
	public List<JSONObject> findFlowByType(@SuppressWarnings("rawtypes") Class clazz, String hql, PageBean pageBean, Object... params) {
		pageBean.setTotalCount(baseDao.count(Long.class, "select count(*)" + hql.toString(), params));
		List<Applicationflow> data = baseDao.listForEntity(clazz, "select applicationflow " + hql.toString()+" order by applicationflow.id desc", pageBean, params);

		List<JSONObject> jResList = new ArrayList<JSONObject>();

		for (Applicationflow flow : data) {
			JSONObject jRes = JSONObject.fromObject(flow);// 转换json对象

			int atid = flow.getAt_id();
			Applicationtype type = baseDao.get(Applicationtype.class, atid);
			if (type != null) {
				jRes.element("atname", type.getName());
				type = null;
			}

			int current_processstepid = flow.getCurrent_processstepid();
			Processnode currentNode = baseDao.get(Processnode.class, current_processstepid);
			if (currentNode != null) {
				jRes.element("currentnode", currentNode.getName());
				currentNode = null;
			}

			int nextprocessstepid = flow.getNext_processstepid();
			Processnode nextNode = baseDao.get(Processnode.class, nextprocessstepid);
			if (nextNode != null) {
				jRes.element("nextnode", nextNode.getName());
				nextNode = null;
			}

			int create_userid = flow.getCreate_userid();
			User user = baseDao.get(User.class, create_userid);
			if (user != null) {
				jRes.element("create_username", user.getRealName());
				user = null;
			}

			int afid = flow.getId();
//			Criterion condition = Restrictions.eq("af_id", afid);
//			Applicationflow_log flowlog = baseDao.getBy(Applicationflow_log.class, condition);
			//modify by james 2016.1.25 修改最后审批人显示问题
			String sql2="select fl.* from pa_applicationflow_log fl where af_id= "+afid+" order by audit_time desc";

			Applicationflow_log flowlog = baseDao.getByNativeSQL(Applicationflow_log.class, sql2.toString());
			
			if (flowlog != null) {
				jRes.element("audittime", flowlog.getAudit_time());
				int auditid = flowlog.getAudit_userid();
				user = baseDao.get(User.class, auditid);
				if (user != null) {
					jRes.element("auditname", user.getRealName());
					user = null;
				}
			}
			
			//add by james 增加是否编辑
			if(create_userid==getCurrentUser().getId())
			{
				jRes.element("isedit", "Y");
			}
			else{
				jRes.element("isedit", "N");
			}

			jResList.add(jRes);
		}

		return jResList;
	}
	
	/**
	 * 用于查询用戶已审核单据
	 * 
	 * @param clazz
	 * @param sql
	 * @param pageBean
	 * @param params
	 * @return
	 */
	public List<JSONObject> findFlowByTypeAudit(@SuppressWarnings("rawtypes") Class clazz, String hql, PageBean pageBean, Object... params) {
		pageBean.setTotalCount(baseDao.count(Long.class, "select count(*)" + hql.toString(), params));
		List<Applicationflow> data = baseDao.listForEntity(clazz,"select p "+ hql.toString() +" order by p.id desc", pageBean, params);

		List<JSONObject> jResList = new ArrayList<JSONObject>();

		for (Applicationflow flow : data) {
			JSONObject jRes = JSONObject.fromObject(flow);// 转换json对象

			int atid = flow.getAt_id();
			Applicationtype type = baseDao.get(Applicationtype.class, atid);
			if (type != null) {
				jRes.element("atname", type.getName());
				type = null;
			}

			int current_processstepid = flow.getCurrent_processstepid();
			Processnode currentNode = baseDao.get(Processnode.class, current_processstepid);
			if (currentNode != null) {
				jRes.element("currentnode", currentNode.getName());
				currentNode = null;
			}

			int nextprocessstepid = flow.getNext_processstepid();
			Processnode nextNode = baseDao.get(Processnode.class, nextprocessstepid);
			if (nextNode != null) {
				jRes.element("nextnode", nextNode.getName());
				nextNode = null;
			}

			int create_userid = flow.getCreate_userid();
			User user = baseDao.get(User.class, create_userid);
			if (user != null) {
				jRes.element("create_username", user.getRealName());
				user = null;
			}

			int afid = flow.getId();
//			Criterion condition = Restrictions.eq("af_id", afid);
//			Applicationflow_log flowlog = baseDao.getBy(Applicationflow_log.class, condition);
			//modify by james 2016.1.25 修改最后审批人显示问题
			String sql2="select fl.* from pa_applicationflow_log fl where processid=1  and  af_id= "+afid+" order by audit_time desc";

			Applicationflow_log flowlog = baseDao.getByNativeSQL(Applicationflow_log.class, sql2.toString());
			
			if (flowlog != null) {
				jRes.element("audittime", flowlog.getAudit_time());
				int auditid = flowlog.getAudit_userid();
				user = baseDao.get(User.class, auditid);
				if (user != null) {
					jRes.element("auditname", user.getRealName());
					user = null;
				}
			}

			jResList.add(jRes);
		}

		return jResList;
	}

	public List<JSONObject> findFlowByTypeOver(@SuppressWarnings("rawtypes") Class clazz, String hql, PageBean pageBean, Object... params) {
		pageBean.setTotalCount(baseDao.count(Long.class, "select count(*)" + hql.toString(), params));
		List<Applicationflow> data = baseDao.listForEntity(clazz, "select applicationflow " + hql.toString(), pageBean, params);

		List<JSONObject> jResList = new ArrayList<JSONObject>();

		for (Applicationflow flow : data) {
			JSONObject jRes = JSONObject.fromObject(flow);// 转换json对象

			int atid = flow.getAt_id();
			Applicationtype type = baseDao.get(Applicationtype.class, atid);
			if (type != null) {
				jRes.element("atname", type.getName());
				type = null;
			}

			int create_userid = flow.getCreate_userid();
			User user = baseDao.get(User.class, create_userid);
			if (user != null) {
				jRes.element("create_username", user.getRealName());
				user = null;
			}

			int afid = flow.getId();
			Criterion condition = Restrictions.eq("af_id", afid);
			Applicationflow_log flowlog = baseDao.getBy(Applicationflow_log.class, condition);
			if (flowlog != null) {
				jRes.element("audittime", flowlog.getAudit_time());
				int auditid = flowlog.getAudit_userid();
				user = baseDao.get(User.class, auditid);
				if (user != null) {
					jRes.element("auditname", user.getRealName());
					user = null;
				}
			}

			jResList.add(jRes);
		}

		return jResList;
	}

	@Autowired
	SportSignUserRoleService sportSignUserRoleService;
	
	@Autowired
	UserService userService;

	@Autowired
	SMSUtils sMSUtils;
	// 审批通过
	public JSONObject updateAgree(HttpServletRequest request, String ids, String reason, User user) {
		if (ids != null && ids.length() > 0) {
			String[] flowid = ids.split(",");
			
			for (String id : flowid) {
				Applicationflow flow = baseDao.get(Applicationflow.class, Integer.parseInt(id));
				Applicationtype applicationtype = this.getById(flow.getAt_id());
				if (flow != null) {
					List<Signuserrole> listSignuserrole = sportSignUserRoleService.listForEntity("SELECT sur From Signuserrole sur where sur.user.id=?", new Object[] { user.getId() });
					Signuserrole singUserRole = listSignuserrole.get(0);
					// 根据角色查询当前的节点和下个节点
					ProcessDetial processDetial = baseDao.getByHQL(ProcessDetial.class, "FROM ProcessDetial pd where pd.processid=1 and  pd.id = ?", new Object[] { flow.getCurrent_processstepid() });
					//为结束
					if(processDetial.getStatus().equals(Constant.PA_PROCESSDETIAL_STATUS_1)){
						//流水表
						ApplicationflowLog applicationflowLog = new ApplicationflowLog();
						applicationflowLog.setAf_id(flow.getId());
						applicationflowLog.setProcessstep_id(processDetial.getId());
						applicationflowLog.setAudit_time(DateUtil.dateToStr(new Date(), 12));
						applicationflowLog.setAudit_userid(user.getId());
						applicationflowLog.setAuditstatus(Constant.PA_APPLICATIONFLOW_LOG_0);
						applicationflowLog.setReason("项目归档:"+reason);
						baseDao.saveOrUpdate(applicationflowLog);
						flow.setStatus(Constant.PA_APPLICATIONFLOW_3);
						baseDao.saveOrUpdate(flow);
						//创建人
						User creatUser = userService.getById(flow.getCreate_userid());
						sMSUtils.sendSMSUser(creatUser, Constant.SMS_TEMP_62701, new String[]{flow.getForm_num(),applicationtype.getName(),Constant.WEB_URL});
					}else{
						ProcessDetial processDetialNext =  baseDao.getByHQL(ProcessDetial.class,"FROM ProcessDetial  where id ="+processDetial.getNextprocessdetialid());
						
						ApplicationflowLog applicationflowLog = new ApplicationflowLog();
						applicationflowLog.setAf_id(flow.getId());
						applicationflowLog.setProcessstep_id(processDetial.getId());
						applicationflowLog.setAudit_time(DateUtil.dateToStr(new Date(), 12));
						applicationflowLog.setAudit_userid(user.getId());
						applicationflowLog.setAuditstatus(Constant.PA_APPLICATIONFLOW_LOG_0);
						applicationflowLog.setReason(singUserRole.getSignrole().getName()+":审批:"+reason);	
						applicationflowLog.setProcessid(processDetialNext.getProcessid());
						baseDao.saveOrUpdate(applicationflowLog);
						flow.setCurrent_processstepid(processDetialNext.getId());
						flow.setNext_processstepid(processDetialNext.getNextprocessdetialid());
						flow.setProcessid(processDetialNext.getProcessid());
						baseDao.saveOrUpdate(flow);
						
						sMSUtils.sendSMSGroup(user, Constant.SMS_TEMP_62695, new String[]{flow.getForm_num(),applicationtype.getName(),Constant.WEB_URL});
					}
				}
			}
		}
		return WebResult.success();
	}

	
	
	
	
	
	// 驳回
	public JSONObject updateRefuse(HttpServletRequest request, String ids, String reason, User user) {

		if (ids != null && ids.length() > 0) {
			String[] flowid = ids.split(",");
			//add by james 判断是否存在当前单据是创建人的情况
			for (String id : flowid) {
				Applicationflow flow = baseDao.get(Applicationflow.class, Integer.parseInt(id));
				if(user.getId()==flow.getCreate_userid())
				{
					return WebResult.error("不能驳回自己的单据："+flow.getForm_num());
				}
				if(flow.getCurrent_processstepid()==1)
				{
					return WebResult.error("此单据："+flow.getForm_num()+" 当前已经在流程起始处，不允许驳回！");
				}
			}
			for (String id : flowid) {
				Applicationflow flow = baseDao.get(Applicationflow.class, Integer.parseInt(id));
				Applicationtype applicationtype = this.getById(flow.getAt_id());
				if (flow != null) {
					int currentprocessstepid = flow.getCurrent_processstepid();
					if (currentprocessstepid > 0) {
						Condition condition = new Condition();
						condition.eq("nextprocessdetialid", currentprocessstepid);
						ProcessDetial preNode = baseDao.getBy(ProcessDetial.class, condition);
						if (preNode != null) {
							flow.setCurrent_processstepid(preNode.getId());
							flow.setNext_processstepid(currentprocessstepid);
						}
					}
					baseDao.saveOrUpdate(flow);
				

					// 记录审批历史
					Applicationflow_log log = new Applicationflow_log();
					log.setAf_id(Integer.parseInt(id));
					log.setAudit_time(TimeUtil.dateToString(TimeUtil.getCurrentSqlTime(), "yyyy-MM-dd HH:mm:ss"));
					log.setAudit_userid(user.getId());
					log.setAuditstatus("2");// 0 通过；1 不通过；2 驳回
					log.setProcessstep_id(currentprocessstepid);
					log.setReason(reason);
					baseDao.save(log);
					//发送短信
					//创建人
					User creatUser = userService.getById(flow.getCreate_userid());
					sMSUtils.sendSMSUser(creatUser, Constant.SMS_TEMP_62698, new String[]{flow.getForm_num(),applicationtype.getName(),Constant.WEB_URL});
					flow = null;
					log = null;
						
				}
			}
			flowid = null;
		}

		return WebResult.success();
	}

	// 不通过
	public JSONObject updateDisagree(HttpServletRequest request, String ids, String reason, User user) {
		if (ids != null && ids.length() > 0) {
			String[] flowid = ids.split(",");
			for (String id : flowid) {
				Applicationflow flow = baseDao.get(Applicationflow.class, Integer.parseInt(id));
				Applicationtype applicationtype = this.getById(flow.getAt_id());
				if (flow != null) {
					int currentprocessstepid = flow.getCurrent_processstepid();
					flow.setStatus(Constant.PA_APPLICATIONFLOW_2);
					baseDao.saveOrUpdate(flow);

					// 记录审批历史
					Applicationflow_log log = new Applicationflow_log();
					log.setAf_id(Integer.parseInt(id));
					log.setAudit_time(TimeUtil.dateToString(TimeUtil.getCurrentSqlTime(), "yyyy-MM-dd HH:mm:ss"));
					log.setAudit_userid(user.getId());
					log.setAuditstatus("1");// 0 通过；1 不通过；2 驳回
					log.setProcessstep_id(currentprocessstepid);
					log.setReason(reason);
					baseDao.save(log);
					
					User creatUser = userService.getById(flow.getCreate_userid());
					sMSUtils.sendSMSUser(creatUser, Constant.SMS_TEMP_62699, new String[]{flow.getForm_num(),applicationtype.getName(),Constant.WEB_URL});
					
					flow = null;
					log = null;
				}
			}
			flowid = null;
		}

		return WebResult.success();
	}

}
