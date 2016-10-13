package org.zframework.web.controller.sprot;

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
import org.zframework.web.entity.sport.Area;
import org.zframework.web.entity.sport.Userarea;
import org.zframework.web.entity.system.User;
import org.zframework.web.service.admin.sport.SportUserAreaService;
import org.zframework.web.service.admin.sport.area.AreaService;
import org.zframework.web.service.admin.system.LogService;
import org.zframework.web.service.admin.system.UserService;
import org.zframework.core.util.Constant;

/**
 * 用户区域提交
 * 
 * @author zhumin
 *
 */
@Controller
@RequestMapping("/admin/sport/area")
public class SportAreaController extends BaseController<Area> {
	@Autowired
	private LogService logService;
	@Autowired
	private AreaService areaService;
	@Autowired
	private UserService userService;
	@Autowired
	private SportUserAreaService sportUserAreaService;
	@Autowired
	private IBaseDao baseHibernateDaoImpl;
	@Autowired
	protected BaseHibernateDao baseDao;

	/**
	 * 查询用户区域
	 * 
	 * @param model
	 * @return
	 */
	@RequestMapping(method = { RequestMethod.GET })
	public String index(Model model) {
		logService.recordInfo("用户区域", "成功", getCurrentUser().getLoginName(), getRequestAddr());
		return "admin/system/sport/area/index";
	}

	@RequestMapping(value="/getArea",method={RequestMethod.POST})
	@ResponseBody
	public List<JSONObject> getArea(){
		List<JSONObject> lstJson = new ArrayList<JSONObject>();
		List<Area> lstArea = areaService.list();
		for(Area area : lstArea){
			if(area.getArealevel().equals(Constant.PA_AREA_AREALEVEL_3)){
				JSONObject jObj = new JSONObject();
				jObj.element("id", area.getId());
				jObj.element("text", area.getAreaname());
				lstJson.add(jObj);
			}
			
	
		}
		lstJson.get(0).element("selected", true);
		return lstJson;
	}
	
	/**
	 * 资源管理调用的方法
	 * 
	 * */
	@RequestMapping(value = "/toAreaTree", method = { RequestMethod.POST })
	@ResponseBody
	public JSONArray toAreaTree() {
		return areaService.toAreaTree();
	}

	/**
	 * 根据用户
	 * 
	 * @param pageBean
	 * @param name
	 * @param value
	 * @param roleId
	 * @param type
	 * @return
	 */
	@RequestMapping(value = "/findUserByArea", method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> findUserByArea(PageBean pageBean, String name, String value, @RequestParam(value = "areaId", required = false) Integer areaId) {
		Map<String, Object> userMap = new HashMap<String, Object>();
		if (org.zframework.core.util.ObjectUtil.isNull(areaId)) {
			userMap.put("rows", "");
			userMap.put("total", 0);
			return userMap;
		} else {
			ArrayList<Object> param = new ArrayList<Object>();
			StringBuffer hql = new StringBuffer(" FROM User user,Userarea userarea where user.id = userarea.user.id and userarea.area.id = ?");
			param.add(areaId);
			if (!StringUtil.isEmpty(name)) {
				if ("id".equals(name)) {
					if (RegexUtil.isInteger(value)) {
						hql.append("and user.id = ?");
						param.add(Integer.valueOf(value));
					}
				} else {
					hql.append("and user." + name + " like ? ");
					param.add("%" + value + "%");
				}
			}
			userMap.put("rows", areaService.findUserByArea(User.class, hql.toString(), pageBean, param.toArray()));
			userMap.put("total", pageBean.getTotalCount());
		}
		return userMap;
	}

	/**
	 * 查询未绑定区域的用户
	 * 
	 * @param pageBean
	 * @param name
	 * @param value
	 * @return
	 */
	@RequestMapping(value = "/findUserByAreaAll", method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> findUserByAreaAll(PageBean pageBean, String name, String value) {
		Map<String, Object> userMap = new HashMap<String, Object>();
		pageBean.addCriterion(Restrictions.not(Restrictions.eq("loginName", ApplicationCommon.SYSTEM_ADMIN)));
		List<Userarea> listUserarea = sportUserAreaService.list();
		List<Integer> ids = new ArrayList<Integer>();
		for (Userarea userarea : listUserarea) {
			ids.add(userarea.getUser().getId());
		}
		if (!StringUtil.isEmpty(name)) {
			if ("id".equals(name)) {
				if (RegexUtil.isInteger(value))
					pageBean.addCriterion(Restrictions.eq(name, Integer.parseInt(value)));
			} else {
				pageBean.addCriterion(Restrictions.like(name, "%" + value + "%"));
			}
		}
		if (ids.size() > 0)
			pageBean.addCriterion(Restrictions.not(Restrictions.in("id", ids)));

		userMap.put("rows", userService.listByPage(pageBean));
		userMap.put("total", pageBean.getTotalCount());
		return userMap;
	}

	/**
	 * 删除用户区域
	 * 
	 * @param request
	 * @param roleid
	 * @param userIds
	 * @return
	 */
	@RequestMapping(value = "/deleteAreaByIds", method = { RequestMethod.POST })
	@ResponseBody
	public JSONObject deleteAreaByIds(HttpServletRequest request, Integer[] areaIds) {
		if (ObjectUtil.isEmpty(areaIds)) {
			return WebResult.error("用户列表为空!");
		}
		return sportUserAreaService.deleteAreaByIds(request, getCurrentUser(), areaIds);
	}

	/**
	 * 根据用户添加权限
	 * 
	 * @param request
	 * @param areaIds
	 * @param userIds
	 * @return
	 */
	@RequestMapping(value = "/addUserAreaByUser", method = { RequestMethod.POST })
	@ResponseBody
	public JSONObject addUserAreaByUser(HttpServletRequest request, Integer areaId, Integer[] userIds) {
		if (ObjectUtil.isNull(areaId)) {
			return WebResult.error("区域不能为空!");
		}
		if (ObjectUtil.isEmpty(userIds)) {
			return WebResult.error("请选择用户!");
		}
		return sportUserAreaService.addUserAreaByUser(request, getCurrentUser(), userIds, areaId);
	}
}
