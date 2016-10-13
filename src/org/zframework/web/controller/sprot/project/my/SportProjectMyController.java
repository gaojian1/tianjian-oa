package org.zframework.web.controller.sprot.project.my;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.zframework.core.support.ApplicationCommon;
import org.zframework.core.util.ObjectUtil;
import org.zframework.core.util.RegexUtil;
import org.zframework.core.util.StringUtil;
import org.zframework.core.web.support.WebResult;
import org.zframework.orm.dao.BaseHibernateDao;
import org.zframework.orm.dao.IBaseDao;
import org.zframework.orm.query.PageBean;
import org.zframework.web.controller.BaseController;
import org.zframework.web.entity.sport.Applicationflow;
import org.zframework.web.entity.sport.Applicationtype;
import org.zframework.web.entity.sport.Userarea;
import org.zframework.web.entity.system.Role;
import org.zframework.web.entity.system.Unit;
import org.zframework.web.entity.system.User;
import org.zframework.web.service.admin.sport.SportUserAreaService;
import org.zframework.web.service.admin.sport.project.my.SportProjectMyService;
import org.zframework.web.service.admin.system.LogService;

/**
 * 我的项目申请单管理
 * 
 * @author weng
 *
 */
@Controller
@RequestMapping("/admin/sport/project/my")
public class SportProjectMyController extends BaseController<Applicationtype> {
	@Autowired
	private LogService logService;
	@Autowired
	private SportProjectMyService projectMyService;
	@Autowired
	private SportUserAreaService sportUserAreaService;
	@Autowired
	private IBaseDao baseHibernateDaoImpl;
	@Autowired
	protected BaseHibernateDao baseDao;

	/**
	 * 查询项目申请单管理
	 * 
	 * @param model
	 * @return
	 */
	@RequestMapping(method = { RequestMethod.GET })
	public String index(Model model) {
		logService.recordInfo("项目申请单管理", "成功", getCurrentUser().getLoginName(), getRequestAddr());
		return "admin/system/sport/project/my/index";
	}

	/**
	 * 表单类型调用的方法
	 * 
	 * */
	@RequestMapping(value = "/getAppType", method = { RequestMethod.POST })
	@ResponseBody
	public JSONArray getAppType() {
		return projectMyService.getAppType();
	}

	/**
	 * 根据表单类型查询审批中
	 * 
	 * @param pageBean
	 * @param name
	 * @param value
	 * @param roleId
	 * @param type
	 * @return
	 */
	@RequestMapping(value = "/findFlowByType", method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> findFlowByType(PageBean pageBean, String name, String value, @RequestParam(value = "areaId", required = false) Integer areaId) {
		Map<String, Object> userMap = new HashMap<String, Object>();
//		if(areaId==null||areaId.intValue()==0){
//			areaId = 1;
//		}
//		if (org.zframework.core.util.ObjectUtil.isNull(areaId)) {
//			userMap.put("rows", "");
//			userMap.put("total", 0);
//			return userMap;
//		} else {
			ArrayList<Object> param = new ArrayList<Object>();
			StringBuffer hql = new StringBuffer(" FROM Applicationflow applicationflow  where status in('0','1') and create_userid="+this.getCurrentUser().getId());
			if(areaId!=null&&areaId.intValue()!=0){
				hql.append(" and at_id = ? ");
				param.add(areaId);
			}
			if (!StringUtil.isEmpty(name)) {
				if ("id".equals(name)) {
					if (RegexUtil.isInteger(value)) {
						hql.append("and id = ?");
						param.add(Integer.valueOf(value));
					}
				} else {
					hql.append("and " + name + " like ? ");
					param.add("%" + value + "%");
				}
			}
			userMap.put("rows", projectMyService.findFlowByType(Applicationflow.class, hql.toString(), pageBean, param.toArray()));
			userMap.put("total", pageBean.getTotalCount());
//		}
		return userMap;
	}


	
	
	
	/**
	 * 根据表单类型查询已归档
	 * 
	 * @param pageBean
	 * @param name
	 * @param value
	 * @param roleId
	 * @param type
	 * @return
	 */
	@RequestMapping(value = "/findFlowByTypeOver", method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> findFlowByTypeOver(PageBean pageBean, String name, String value, @RequestParam(value = "areaId", required = false) Integer areaId) {
		Map<String, Object> userMap = new HashMap<String, Object>();
//		if(areaId==null||areaId.intValue()==0){
//			areaId = 1;
//		}

		// 获取当前用户
		User user = getCurrentUser();
		// 根据用户查询区域
		Userarea userarea = sportUserAreaService.getByHql("FROM Userarea ua where ua.user.id ="+ user.getId() );
//		if (org.zframework.core.util.ObjectUtil.isNull(areaId)) {
//			userMap.put("rows", "");
//			userMap.put("total", 0);
//			return userMap;
//		} else {
			ArrayList<Object> param = new ArrayList<Object>();
			StringBuffer hql = new StringBuffer(" FROM Applicationflow applicationflow  where status in('2','3','5') and area_id=? and create_userid=? ");
			param.add(userarea.getArea().getId());
			param.add(user.getId());
			if(areaId!=null&&areaId.intValue()!=0){
				hql.append(" and at_id = ? ");
				param.add(areaId);
			}
			if (!StringUtil.isEmpty(name)) {
				if ("id".equals(name)) {
					if (RegexUtil.isInteger(value)) {
						hql.append("and id = ?");
						param.add(Integer.valueOf(value));
					}
				} else {
					hql.append("and " + name + " like ? ");
					param.add("%" + value + "%");
				}
			}
			userMap.put("rows", projectMyService.findFlowByTypeOver(Applicationflow.class, hql.toString(), pageBean, param.toArray()));
			userMap.put("total", pageBean.getTotalCount());
//		}
		return userMap;
	}
	
	
	@RequestMapping(value="/audit",method={RequestMethod.GET})
	public String audit(Model model,HttpServletRequest request){
		model.addAttribute("action", request.getParameter("action"));
		model.addAttribute("ids", request.getParameter("ids"));
		return "admin/system/sport/project/manage/audit";
	}
	
	
	//同意
	@RequestMapping(value="/doAgree",method={RequestMethod.POST})
	@ResponseBody
	public JSONObject doAgree(HttpServletRequest request){
		String ids = request.getParameter("ids");
		String reason = request.getParameter("reason");
		return projectMyService.updateAgree(request, ids,reason,this.getCurrentUser());
	}
	
	//驳回
	@RequestMapping(value="/doRefuse",method={RequestMethod.POST})
	@ResponseBody
	public JSONObject doRefuse(HttpServletRequest request){
		String ids = request.getParameter("ids");
		String reason = request.getParameter("reason");
		return projectMyService.updateRefuse(request, ids,reason,this.getCurrentUser());
	}
	
	//不同意
	@RequestMapping(value="/doDisagree",method={RequestMethod.POST})
	@ResponseBody
	public JSONObject doDisagree(HttpServletRequest request){
		String ids = request.getParameter("ids");
		String reason = request.getParameter("reason");
		return projectMyService.updateDisagree(request, ids,reason,this.getCurrentUser());
	}

}
