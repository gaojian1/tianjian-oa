package org.zframework.web.service.admin.sport.project.my;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Service;
import org.zframework.core.util.TimeUtil;
import org.zframework.core.web.support.WebResult;
import org.zframework.orm.query.Condition;
import org.zframework.orm.query.PageBean;
import org.zframework.web.entity.sport.Applicationflow;
import org.zframework.web.entity.sport.Applicationflow_log;
import org.zframework.web.entity.sport.Applicationtype;
import org.zframework.web.entity.sport.ProcessDetial;
import org.zframework.web.entity.sport.Processnode;
import org.zframework.web.entity.system.Unit;
import org.zframework.web.entity.system.User;
import org.zframework.web.service.BaseService;
/**
 * 项目申请单管理查询service
 * @author weng
 *
 */
@Service
public class SportProjectMyService extends BaseService<Applicationtype> {
	/**
	 * 显示表单类型
	 * @return
	 */
	public JSONArray getAppType() {
		JSONArray jsonTree=new JSONArray();
		List<Applicationtype> listArea = this.list("from Applicationtype apptype where status is null or status=0 ");
		for(Applicationtype root:listArea){
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
	 * @param clazz
	 * @param hql
	 * @param pageBean
	 * @param params
	 * @return
	 */
	public List<JSONObject> findFlowByType(@SuppressWarnings("rawtypes") Class clazz, String hql, PageBean pageBean, Object...params){
		pageBean.setTotalCount(baseDao.count(Long.class, "select count(*)" +hql.toString(), params));
		List<Applicationflow> data = baseDao.listForEntity(clazz, "select applicationflow "+hql.toString()+" order by applicationflow.id desc", pageBean, params);
		
		List<JSONObject> jResList = new ArrayList<JSONObject>();
		
		for(Applicationflow flow:data){
			JSONObject jRes = JSONObject.fromObject(flow);//转换json对象
			
			int atid = flow.getAt_id();
			Applicationtype type = baseDao.get(Applicationtype.class, atid);
			if(type!=null){
				jRes.element("atname",type.getName());
				type = null;
			}
			
			int current_processstepid = flow.getCurrent_processstepid();
			Processnode currentNode = baseDao.get(Processnode.class, current_processstepid);
			if(currentNode!=null){
				jRes.element("currentnode",currentNode.getName());
				currentNode = null;
			}
			
			int nextprocessstepid = flow.getNext_processstepid();
			Processnode nextNode = baseDao.get(Processnode.class, nextprocessstepid);
			if(nextNode!=null){
				jRes.element("nextnode",nextNode.getName());
				nextNode = null;
			}
			
			int create_userid = flow.getCreate_userid();
			User user = baseDao.get(User.class, create_userid);
			if(user!=null){
				jRes.element("create_username",user.getRealName());
				user = null;
			}
			
			int afid = flow.getId();
			//modify by james 2016.1.25 修改最后审批人显示问题
//			Criterion condition = Restrictions.eq("af_id",afid);
//			Applicationflow_log flowlog = baseDao.getBy(Applicationflow_log.class, condition);
			String sql2="select fl.* from pa_applicationflow_log fl where af_id= "+afid+" order by audit_time desc";

			Applicationflow_log flowlog = baseDao.getByNativeSQL(Applicationflow_log.class, sql2.toString());
			if(flowlog!=null){
				jRes.element("audittime",flowlog.getAudit_time());
				int auditid = flowlog.getAudit_userid();
				user = baseDao.get(User.class, auditid);
				if(user!=null){
					jRes.element("auditname",user.getRealName());
					user = null;
				}
			}
			
			jResList.add(jRes);
		}
		
		return jResList;
	}
	
	
	public List<JSONObject> findFlowByTypeOver(@SuppressWarnings("rawtypes") Class clazz, String hql, PageBean pageBean, Object...params){
		pageBean.setTotalCount(baseDao.count(Long.class, "select count(*)" +hql.toString(), params));
		List<Applicationflow> data = baseDao.listForEntity(clazz, "select applicationflow "+hql.toString()+" order by applicationflow.id desc", pageBean, params);
		
		List<JSONObject> jResList = new ArrayList<JSONObject>();
		
		for(Applicationflow flow:data){
			JSONObject jRes = JSONObject.fromObject(flow);//转换json对象
			
			int atid = flow.getAt_id();
			Applicationtype type = baseDao.get(Applicationtype.class, atid);
			if(type!=null){
				jRes.element("atname",type.getName());
				type = null;
			}
			
			int create_userid = flow.getCreate_userid();
			User user = baseDao.get(User.class, create_userid);
			if(user!=null){
				jRes.element("create_username",user.getRealName());
				user = null;
			}
			
//			String status = flow.getStatus().equals("2")?"不通过":"已完成";
			String status ="";
			if( flow.getStatus().equals("2")){
				status = "不通过";
			}else if(flow.getStatus().equals("4")){
				status = "竣工审核";
			}
			else if( flow.getStatus().equals("5")){
				status = "已竣工";
			}
			jRes.element("status",status);
			
			int afid = flow.getId();
			//modify by james 2016.1.25 修改最后审批人显示问题
//			Criterion condition = Restrictions.eq("af_id",afid);
//			Applicationflow_log flowlog = baseDao.getBy(Applicationflow_log.class, condition);
			String sql2="select fl.* from pa_applicationflow_log fl where af_id= "+afid+" order by audit_time desc";

			Applicationflow_log flowlog = baseDao.getByNativeSQL(Applicationflow_log.class, sql2.toString());
//			Criterion condition = Restrictions.eq("af_id",afid);
//			Applicationflow_log flowlog = baseDao.getBy(Applicationflow_log.class, condition);
			if(flowlog!=null){
				jRes.element("audittime",flowlog.getAudit_time());
				int auditid = flowlog.getAudit_userid();
				user = baseDao.get(User.class, auditid);
				if(user!=null){
					jRes.element("auditname",user.getRealName());
					user = null;
				}
			}
			
			jResList.add(jRes);
		}
		
		return jResList;
	}

	
	

	//审批通过
	public JSONObject updateAgree(HttpServletRequest request,String ids,String reason,User user) {
		if(ids!=null&&ids.length()>0){
			String[] flowid = ids.split(",");
			for(String id:flowid){
				Applicationflow flow = baseDao.get(Applicationflow.class, Integer.parseInt(id));
				if(flow!=null){
					int currentprocessstepid = flow.getCurrent_processstepid();
					int nextprocessstepid = flow.getNext_processstepid();
					if(nextprocessstepid>0){
						ProcessDetial nextNode = baseDao.get(ProcessDetial.class, nextprocessstepid);
						if(nextNode!=null){
							flow.setCurrent_processstepid(nextprocessstepid);
							flow.setNext_processstepid(nextNode.getNextprocessdetialid());
						}
					}else{
						//更新状态为已归档
						flow.setStatus("3");
					}
					baseDao.saveOrUpdate(flow);
					flow = null;
					
					//记录审批历史
					Applicationflow_log log = new Applicationflow_log();
					log.setAf_id(Integer.parseInt(id));
					log.setAudit_time(TimeUtil.dateToString(TimeUtil.getCurrentSqlTime(), "yyyy-MM-dd HH:mm:ss"));
					log.setAudit_userid(user.getId());
					log.setAuditstatus("0");//0 通过；1 不通过；2 驳回
					log.setProcessstep_id(currentprocessstepid);
					log.setReason(reason);
					baseDao.save(log);
					log = null;
				}
			}
			flowid = null;
		}
		return WebResult.success();
	}
	
	
	//驳回
	public JSONObject updateRefuse(HttpServletRequest request, String ids,String reason,User user) {
		
		if(ids!=null&&ids.length()>0){
			String[] flowid = ids.split(",");
			for(String id:flowid){
				Applicationflow flow = baseDao.get(Applicationflow.class, Integer.parseInt(id));
				if(flow!=null){
					int currentprocessstepid = flow.getCurrent_processstepid();
					if(currentprocessstepid>0){
						Condition condition = new Condition();
						condition.eq("nextprocessdetialid", currentprocessstepid);
						ProcessDetial preNode = baseDao.getBy(ProcessDetial.class, condition);
						if(preNode!=null){
							flow.setCurrent_processstepid(preNode.getId());
							flow.setNext_processstepid(currentprocessstepid);
						}
					}
					baseDao.saveOrUpdate(flow);
					flow = null;
					
					//记录审批历史
					Applicationflow_log log = new Applicationflow_log();
					log.setAf_id(Integer.parseInt(id));
					log.setAudit_time(TimeUtil.dateToString(TimeUtil.getCurrentSqlTime(), "yyyy-MM-dd HH:mm:ss"));
					log.setAudit_userid(user.getId());
					log.setAuditstatus("2");//0 通过；1 不通过；2 驳回
					log.setProcessstep_id(currentprocessstepid);
					log.setReason(reason);
					baseDao.save(log);
					
					
					log = null;
				}
			}
			flowid = null;
		}
		
		return WebResult.success();
	}

	
	//不通过
	public JSONObject updateDisagree(HttpServletRequest request,String ids,String reason,User user) {
		if(ids!=null&&ids.length()>0){
			String[] flowid = ids.split(",");
			for(String id:flowid){
				Applicationflow flow = baseDao.get(Applicationflow.class, Integer.parseInt(id));
				if(flow!=null){
					int currentprocessstepid = flow.getCurrent_processstepid();
					flow.setStatus("2");
					baseDao.saveOrUpdate(flow);
					flow = null;
					
					//记录审批历史
					Applicationflow_log log = new Applicationflow_log();
					log.setAf_id(Integer.parseInt(id));
					log.setAudit_time(TimeUtil.dateToString(TimeUtil.getCurrentSqlTime(), "yyyy-MM-dd HH:mm:ss"));
					log.setAudit_userid(user.getId());
					log.setAuditstatus("1");//0 通过；1 不通过；2 驳回
					log.setProcessstep_id(currentprocessstepid);
					log.setReason(reason);
					baseDao.save(log);
					log = null;
				}
			}
			flowid = null;
		}
		
		return WebResult.success();
	}
	
	
}
