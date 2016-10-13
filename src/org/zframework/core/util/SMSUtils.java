package org.zframework.core.util;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.zframework.sms.CCPRestSmsSDK;
import org.zframework.web.entity.system.User;
import org.zframework.web.service.admin.sport.SportSignUserRoleService;
import org.zframework.web.service.admin.system.LogService;

/**
 * 短信发送类
 * 
 * @author zhumin
 *
 */
public class SMSUtils {

	private TaskExecutor taskExecutor;// 线程池
	private CCPRestSmsSDK cCPRestSmsSDK;

	@Autowired
	private SportSignUserRoleService sportSignUserRoleService;

	@Autowired
	private LogService logService;
	
	/**
	 * 给指定用户发送信息
	 * @param user
	 * @param templateId
	 * @param datas
	 */
	public void sendSMSUser(User user, String templateId, String[] datas ){
		taskExecutor.execute(new sendSMSUserThread(user,templateId,datas));
	}
	
	
	class sendSMSUserThread implements Runnable{
		User user;
		String templateId;
		String[] datas;

		public sendSMSUserThread(User user, String templateId, String[] datas) {
			this.user = user;
			this.templateId = templateId;
			this.datas = datas;
		}
		@Override
		public void run() {
			if(ObjectUtil.isNotNull(user)){
				if(ObjectUtil.isNotEmpty(user.getMobile()))
					cCPRestSmsSDK.sendTemplateSMS(user.getMobile(), templateId, datas);
			}
		}
		
	}
	
	/**
	 * 给上级人员发送
	 * @param user
	 * @param templateId
	 * @param datas
	 */
	public void sendSMSGroup(User user, String templateId, String[] datas) {
		taskExecutor.execute(new sendSMSGroupThread(user, templateId, datas));
	}

	class sendSMSGroupThread implements Runnable {
		User user;
		String templateId;
		String[] datas;

		public sendSMSGroupThread(User user, String templateId, String[] datas) {
			this.user = user;
			this.templateId = templateId;
			this.datas = datas;
		}

		@Override
		public void run() {
			List<User> listUser = sportSignUserRoleService.findUserByRoleId(user);
			String mobiles = "";
			String to = "";
			if(ObjectUtil.isNotNull(listUser)){
				for (User user : listUser) {
					if (!StringUtil.isEmpty(user.getMobile())) {
						mobiles = mobiles +user.getMobile()+ ",";
					}
				}
				if (mobiles.length() > 0) {
					to = mobiles.substring(0, mobiles.length() - 1);
					cCPRestSmsSDK.sendTemplateSMS(to, templateId, datas);
				}
			}
		

		}
	}

	public CCPRestSmsSDK getcCPRestSmsSDK() {
		return cCPRestSmsSDK;
	}

	public void setcCPRestSmsSDK(CCPRestSmsSDK cCPRestSmsSDK) {
		this.cCPRestSmsSDK = cCPRestSmsSDK;
	}

	public TaskExecutor getTaskExecutor() {
		return taskExecutor;
	}

	public void setTaskExecutor(TaskExecutor taskExecutor) {
		this.taskExecutor = taskExecutor;
	}

}
