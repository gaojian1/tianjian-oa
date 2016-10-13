package org.zframework.web.service.admin.sport;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.zframework.core.util.ObjectUtil;
import org.zframework.core.web.support.WebResult;
import org.zframework.web.entity.sport.ProcessDetial;
import org.zframework.web.entity.sport.Signuserrole;
import org.zframework.web.entity.system.User;
import org.zframework.web.service.BaseService;
import org.zframework.web.service.admin.system.LogService;
import org.zframework.web.service.admin.system.UserService;

import com.nethsoft.zhxq.core.util.DateUtil;

/**
 * 新增service
 * 
 * @author zhumin
 *
 */
@Service
public class SportSignUserRoleService extends BaseService<Signuserrole> {

	@Autowired
	private LogService logService;
	@Autowired
	private UserService us;
	@Autowired
	private SportSignRoleService sportSignRoleService;
	@Autowired
	private SportProcessDetialService sportProcessDetialService;

	/**
	 * 删除用户信息
	 * 
	 * @param request
	 * @param user
	 * @param userIds
	 * @return
	 */
	public JSONObject deleteSportSignUserRole(HttpServletRequest request, User user, Integer[] roleIds) {
		try {
			this.delete(roleIds);
			logService.recordInfo("删除角色中用户(新)", "成功", user.getLoginName(), request.getRemoteAddr());
		} catch (Exception e) {
			e.printStackTrace();
			logService.recordInfo("删除角色中用户(新)失败", e.getMessage(), user.getLoginName(), request.getRemoteAddr());
			return WebResult.error("系统异常sport-001");
		}
		return WebResult.success();
	}

	/**
	 * 用于添加用户角色
	 * 
	 * @param request
	 * @param user
	 * @param roleid
	 * @param userIds
	 * @param type
	 * @return
	 */
	public JSONObject executeAssignUser(HttpServletRequest request, User user, Integer roleid, Integer[] userIds) {
		try {
			for (Integer userId : userIds) {
				Signuserrole sur = new Signuserrole();
				sur.setUser(us.getById(userId));
				sur.setSignrole(sportSignRoleService.getById(roleid));
				sur.setCreate_time(DateUtil.getDateTime(new Date()));
				sur.setStatus(0);
				sur.setCreate_userid(user.getId());
				this.save(sur);
			}
			logService.recordInfo("用户新增(新)", "成功", user.getLoginName(), request.getRemoteAddr());

		} catch (Exception e) {
			e.printStackTrace();
			logService.recordInfo("用户新增(新)失败", "失败", user.getLoginName(), request.getRemoteAddr());
		}
		return WebResult.success();
	}

	/**
	 * 根据流程查询用户
	 * 
	 * @param user
	 * @param processDetial
	 */
	public <M> List<M> findUserByRoleId(User user) {
		ArrayList<Integer> ids = new ArrayList<Integer>();
		if (ObjectUtil.isNotNull(user)) {
			// 根据用户查询角色,区域
			String sql = "select pauser.area_id,parole.signrole_id from pa_signuserrole parole,pa_userarea pauser where parole.user_id = pauser.user_id and pauser.user_id=" + user.getId();
			List<Map<String, Object>> listMap = baseDao.queryForList(sql);
			if (listMap.size() > 0) {
				Map<String, Object> resultMap = listMap.get(0);
				// 区域id
				int area_id = (Integer) resultMap.get("area_id");
				int signrole_id = (Integer) resultMap.get("signrole_id");
				ProcessDetial pd = sportProcessDetialService.getByHql("FROM  ProcessDetial WHERE signroleid=" + signrole_id);
				ProcessDetial pdNext = sportProcessDetialService.getById(pd.getNextprocessdetialid());
				// 根据下个节点查询用户角色
				//modify by james 增加获取上级区域领导
				String sql2 = "select pauser.user_id from pa_signuserrole parole,pa_userarea pauser where parole.user_id = pauser.user_id and (pauser.area_id=" + area_id + " or exists(select 1 from pa_area p where p.id=" + area_id + " and p.parent_id=pauser.area_id)) and parole.signrole_id=" + pdNext.getSignroleid();
				listMap = baseDao.queryForList(sql2);
				for (Map<String, Object> mapResult : listMap) {
					ids.add((Integer) mapResult.get("user_id"));
				}
				if (ids.size() > 0) {
					return (List<M>) us.getByIds((Integer[])ids.toArray(new Integer[ids.size()]));
				}
			}

		}
		return null;
	}
}
