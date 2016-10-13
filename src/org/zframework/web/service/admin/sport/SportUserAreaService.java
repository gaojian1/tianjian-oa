package org.zframework.web.service.admin.sport;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.zframework.core.web.support.WebResult;
import org.zframework.web.entity.sport.Area;
import org.zframework.web.entity.sport.Userarea;
import org.zframework.web.entity.system.User;
import org.zframework.web.service.BaseService;
import org.zframework.web.service.admin.system.LogService;

@Service
public class SportUserAreaService extends BaseService<Userarea>{

	@Autowired
	private LogService logService;
	
	/**
	 * 删除用区域关系
	 * @param request
	 * @param user
	 * @param roleIds
	 * @return
	 */
	public JSONObject deleteAreaByIds(HttpServletRequest request,User user,Integer[] roleIds){
		try{
			for(int ids:roleIds){
				this.executeSQL("delete from pa_userarea WHERE user_id="+ids);	
			}
			logService.recordInfo("删除用户区域关系(新)","成功",user.getLoginName() , request.getRemoteAddr());
		}catch(Exception e){
			e.printStackTrace();
			logService.recordInfo("删除用户区域关系(新)失败",e.getMessage(),user.getLoginName() , request.getRemoteAddr());
			return WebResult.error("系统异常sport-002");
		}
		return WebResult.success();
	}
	
	
	/**
	 * 删除用区域关系
	 * @param request
	 * @param user
	 * @param roleIds
	 * @return
	 */
	public JSONObject addUserAreaByUser(HttpServletRequest request,User user,Integer[] uesrIds,Integer areaId){
		try{
			Area area = new Area();
			area.setId(areaId);
			for(int userId:uesrIds){
				User userNew = new User();
				userNew.setId(userId);
				Userarea ua = new Userarea();
				ua.setUser(userNew);
				ua.setArea(area);
				this.save(ua);
			}
			logService.recordInfo("新增用户区域关系(新)","成功",user.getLoginName() , request.getRemoteAddr());
		}catch(Exception e){
			e.printStackTrace();
			logService.recordInfo("新增用户区域关系(新)失败",e.getMessage(),user.getLoginName() , request.getRemoteAddr());
			return WebResult.error("系统异常sport-003");
		}
		return WebResult.success();
	}
	
	
}
