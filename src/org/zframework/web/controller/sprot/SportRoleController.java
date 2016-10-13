package org.zframework.web.controller.sprot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

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
import org.zframework.orm.query.PageBean;
import org.zframework.web.controller.BaseController;
import org.zframework.web.entity.sport.Signuserrole;
import org.zframework.web.entity.system.Role;
import org.zframework.web.entity.system.User;
import org.zframework.web.service.admin.sport.SportSignRoleService;
import org.zframework.web.service.admin.sport.SportSignUserRoleService;
import org.zframework.web.service.admin.system.LogService;
import org.zframework.web.service.admin.system.UserService;

/**
 * 用户权限管理
 * 
 * @author zhumin
 *
 */
@Controller
@RequestMapping("/admin/sport/role")
public class SportRoleController extends BaseController<Role> {
 
	@Autowired
	private LogService logService;
	@Autowired
	private SportSignRoleService sportSignRoleService;
	@Autowired
	private SportSignUserRoleService sportSignUserRoleService;
	@Autowired
	private UserService userService;

	/**
	 * 用户管理首页
	 * 
	 * @param model
	 * @return
	 */
	@RequestMapping(method = { RequestMethod.GET })
	public String index(Model model) {
		logService.recordInfo("查询角色", "成功", getCurrentUser().getLoginName(), getRequestAddr());
		return "admin/system/sport/role/index";
	}

	/**
	 * 获取角色列表
	 * 
	 * @param pageBean
	 * @return
	 */
	@RequestMapping(value = "/roleList", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> roleList(PageBean pageBean, String name, String value) {
		return list(pageBean, name, value, sportSignRoleService);
	}

	/**
	 * 更具角色查询用户(已拥有)
	 * 
	 * @param pageBean
	 * @param name
	 * @param value
	 * @param roleId
	 * @return
	 */
	@RequestMapping(value = "/roleUserListForRole", method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> roleUserListForRole(PageBean pageBean, String name, String value, @RequestParam(value = "roleId", required = false) Integer roleId) {
		Map<String, Object> userMap = new HashMap<String, Object>();
		if (ObjectUtil.isNotNull(roleId)) {
			if (!StringUtil.isEmpty(name)) {
				if ("id".equals(name)) {
					if (RegexUtil.isInteger(value))
						pageBean.addCriterion(Restrictions.eq("user." + name, Integer.parseInt(value)));
				} else {
					pageBean.addCriterion(Restrictions.like("user." + name, "%" + value + "%"));
				}
			}
		}
		pageBean.addCriterion(Restrictions.eq("signrole.id", roleId));
		pageBean.addCriterion(Restrictions.not(Restrictions.eq("user.loginName", ApplicationCommon.SYSTEM_ADMIN)));
		List<Signuserrole> signuserroleList = sportSignUserRoleService.listByPage(pageBean);
		if (signuserroleList.size() > 0) {
			userMap.put("rows", signuserroleList);
			userMap.put("total", pageBean.getTotalCount());
		} else {
			userMap.put("rows", "");
			userMap.put("total", pageBean.getTotalCount());
		}
		return userMap;
	}

	/**
	 * 更具角色查询用户(可添加)
	 * 
	 * @param pageBean
	 * @param name
	 * @param value
	 * @param roleId
	 * @return
	 */
	@RequestMapping(value = "/optionalUserListForRole", method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> optionalUserListForRole(PageBean pageBean, String name, String value) {
		Map<String, Object> userMap = new HashMap<String, Object>();
		List<Signuserrole> listSignuserrole = sportSignUserRoleService.list();
		ArrayList<Integer> ids = new ArrayList<Integer>();
		for (Signuserrole signuserrole : listSignuserrole) {
			ids.add(signuserrole.getUser().getId());
		}
		if (!StringUtil.isEmpty(name)) {
			if ("id".equals(name)) {
				if (RegexUtil.isInteger(value))
					pageBean.addCriterion(Restrictions.eq(name, Integer.parseInt(value)));
			} else {
				pageBean.addCriterion(Restrictions.like(name, "%" + value + "%"));
			}
		}

		pageBean.addCriterion(Restrictions.not(Restrictions.eq("loginName", ApplicationCommon.SYSTEM_ADMIN)));
		if(ids.size() >0)
		pageBean.addCriterion(Restrictions.not(Restrictions.in("id", ids)));
		List<User> listUser = userService.listByPage(pageBean);
		if (listUser.size() > 0) {
			userMap.put("rows", listUser);
			userMap.put("total", pageBean.getTotalCount());
		} else {
			userMap.put("rows", "");
			userMap.put("total", pageBean.getTotalCount());
		}
		return userMap;
	}

	/**
	 * 执行从角色中移除用户
	 * 
	 * @param request
	 * @param roleid
	 * @param userIds
	 * @return
	 */
	@RequestMapping(value = "/doAssignUserForDelete", method = { RequestMethod.POST })
	@ResponseBody
	public JSONObject doAssignUserForDelete(HttpServletRequest request, Integer roleid, Integer[] userIds) {
		if (ObjectUtil.isEmpty(userIds)) {
			return WebResult.error("用户列表为空!");
		} else {
			return sportSignUserRoleService.deleteSportSignUserRole(request, getCurrentUser(), userIds);
		}
	}

	/**
	 * 执行添加用户到角色
	 * 
	 * @param request
	 * @param roleid
	 * @param userIds
	 * @return
	 */
	@RequestMapping(value = "/doAssignUserForAddCopy", method = { RequestMethod.POST })
	@ResponseBody
	public JSONObject doAssignUserForAddCopy(HttpServletRequest request, Integer roleid, Integer[] userIds) {
		if (ObjectUtil.isNull(roleid)) {
			return WebResult.error("请选择权限!");
		}
		if (ObjectUtil.isEmpty(userIds)) {
			return WebResult.error("用户列表为空!");
		} else {
			return sportSignUserRoleService.executeAssignUser(request, getCurrentUser(), roleid, userIds);
		}
	}

}
