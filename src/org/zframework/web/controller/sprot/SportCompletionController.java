package org.zframework.web.controller.sprot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.zframework.core.util.Constant;
import org.zframework.core.util.ObjectUtil;
import org.zframework.core.util.RegexUtil;
import org.zframework.core.util.StringUtil;
import org.zframework.orm.query.PageBean;
import org.zframework.sms.utils.DateUtil;
import org.zframework.web.controller.BaseController;
import org.zframework.web.controller.ConfigSport;
import org.zframework.web.entity.sport.Applicationflow;
import org.zframework.web.entity.sport.ApplicationflowAttach;
import org.zframework.web.entity.sport.ApplicationflowLog;
import org.zframework.web.entity.sport.Applicationtype;
import org.zframework.web.entity.sport.ProcessDetial;
import org.zframework.web.entity.sport.Processnode;
import org.zframework.web.entity.sport.Signuserrole;
import org.zframework.web.entity.sport.Userarea;
import org.zframework.web.entity.system.User;
import org.zframework.web.service.admin.sport.ApplicationflowLogService;
import org.zframework.web.service.admin.sport.SportCompletionService;
import org.zframework.web.service.admin.sport.SportProcessDetialService;
import org.zframework.web.service.admin.sport.SportProcessnodeService;
import org.zframework.web.service.admin.sport.SportUserAreaService;
import org.zframework.web.service.admin.sport.project.appliction.ApplicationTypeService;
import org.zframework.web.service.admin.sport.project.appliction.ApplicationflowAttachService;
import org.zframework.web.service.admin.sport.project.appliction.ApplicationflowService;
import org.zframework.web.service.admin.sport.project.my.SportProjectMyService;
import org.zframework.web.service.admin.system.LogService;
import org.zframework.web.service.admin.system.UserService;

/**
 * 项目竣工管理
 * 
 * @author zhumin
 *
 */
@Controller
@RequestMapping("/admin/sport/completion/apply")
public class SportCompletionController extends BaseController<Applicationflow> {

	@Autowired
	private LogService logService;
	@Autowired
	private SportUserAreaService sportUserAreaService;
	@Autowired
	private SportProjectMyService sportProjectMyService;
	@Autowired
	private SportProcessDetialService sportProcessDetialService; 
	@Autowired
	private ApplicationflowLogService applicationflowLogService;
	@Autowired
	private ApplicationflowService applicationflowService;
	@Autowired
	private SportProcessnodeService sportProcessnodeService;
	@Autowired
	private UserService userService;
	@Autowired
	private ApplicationTypeService applicationTypeService;
	@Autowired
	private SportCompletionService sportCompletionService;
	@Autowired
	private ApplicationflowAttachService applicationflowAttachService;
	@Autowired
	ConfigSport configSport;
	
	/**
	 * 显示明细
	 * @param model
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/info/{id}", method = { RequestMethod.GET })
	public String info(Model model,@PathVariable Integer id) {
		List<ApplicationflowAttach> listApplicationflowAttach = applicationflowAttachService.listForEntity("FROM ApplicationflowAttach WHERE status='有效' and type="+Constant.ATTCH_TYPE_1+" AND af_id="+id+" AND file_type="+Constant.FILE_TYPE_1);
		List<String> pictures = new ArrayList<String>();
		for(ApplicationflowAttach applicationflowAttach:listApplicationflowAttach){
			String pictureUrl = "http://"+configSport.getUrl()+ "/image/upload/" + id + "/" + applicationflowAttach.getName();
			pictures.add(pictureUrl);
		}
		model.addAttribute("pictures", pictures);
		List<ApplicationflowAttach> listFiles = applicationflowAttachService.listForEntity("FROM ApplicationflowAttach WHERE status='有效' and type="+Constant.ATTCH_TYPE_1+" AND af_id="+id+" AND file_type="+Constant.FILE_TYPE_2);
		model.addAttribute("files", listFiles);
		return "admin/system/sport/project/acceptance/info";
	}
	
	
	/**
	 * 项目申请
	 * 
	 * @param model
	 * @return
	 */
	@RequestMapping(method = { RequestMethod.GET })
	public String index(Model model) {
		logService.recordInfo("项目申请管理", "成功", getCurrentUser().getLoginName(), getRequestAddr());
		return "admin/system/sport/project/completion/index";
	}

	/**
	 * 提交竣工申请
	 * @param model
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/forwardApply/{id}", method = { RequestMethod.GET })
	public String forwardApply(Model model, @PathVariable Integer id){
		
		// 根据附件id查询流水
		List<ApplicationflowLog> listApplicationflowLog = applicationflowLogService.list("FROM ApplicationflowLog WHERE af_id = " + id + " order by audit_time asc");
		// 申请表单主表
		Applicationflow applicationflow = applicationflowService.getById(id);
		model.addAttribute("applicationflow", applicationflow);
		for (ApplicationflowLog applicationflowLog : listApplicationflowLog) {
			Processnode processnode = sportProcessnodeService.getById(applicationflowLog.getProcessstep_id());
			if (ObjectUtil.isNotNull(processnode)) {
				applicationflowLog.setProcessStr(processnode.getName());
			}
			applicationflowLog.setAduitStatusStr(StringUtil.auditstatusStr(applicationflowLog.getAuditstatus()));
			User user = userService.getById(applicationflowLog.getAudit_userid());
			if (ObjectUtil.isNotNull(user)) {
				applicationflowLog.setAuditUserStr(user.getLoginName());
			}
			User creatUser = userService.getById(applicationflow.getCreate_userid());
			if (ObjectUtil.isNotNull(creatUser)) {
				applicationflowLog.setCreateUserStr(creatUser.getLoginName());
			}
			Applicationtype applicationtype = applicationTypeService.getById(applicationflow.getAt_id());
			if (ObjectUtil.isNotNull(applicationtype)) {
				applicationflowLog.setSqType(applicationtype.getName());
			}
			applicationflowLog.setProcessStr(applicationflowLog.getProcessStr() + "(" + applicationflowLog.getAduitStatusStr() + ")");
			applicationflowLog.setFormNumber(applicationflow.getForm_num());
			applicationflowLog.setAudit_time(DateUtil.dateToStr(DateUtil.StrToDate(applicationflowLog.getAudit_time()), 12));
		}

		model.addAttribute("modleList", listApplicationflowLog);		
		
		model.addAttribute("flowId", id);
		
		return "admin/system/sport/project/completion/forwardApply";
	}
	
	/**
	 * 获取建设中的数据
	 * 
	 * @param pageBean
	 * @param name
	 * @param value
	 * @return
	 */
	@RequestMapping(value = "/list", method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> list(PageBean pageBean, String name, String value) {
		Map<String, Object> userMap = new HashMap<String, Object>();
		User user = getCurrentUser();
		List<Signuserrole> signuserroles = user.getSignuserroles();
		if (ObjectUtil.isNotNull(signuserroles) && signuserroles.size() >0) {
			ProcessDetial pd = (ProcessDetial)sportProcessDetialService.queryHQL("FROM ProcessDetial WHERE processid= 2  AND signroleid="+signuserroles.get(0).getSignrole().getId());
			Userarea userarea = sportUserAreaService.getByHql("FROM Userarea ua where ua.user.id =" + user.getId());
			if (ObjectUtil.isNotNull(userarea) && ObjectUtil.isNotNull(pd)) {
				ArrayList<Object> param = new ArrayList<Object>();
				StringBuffer hql = new StringBuffer(" FROM Applicationflow applicationflow  where current_processstepid="+pd.getProcessnodeid()+" and next_processstepid="+pd.getNextprocessdetialid()+" and  status in('1','0') and area_id=" + userarea.getArea().getId());
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
				userMap.put("rows", sportProjectMyService.findFlowByType(Applicationflow.class, hql.toString(), pageBean, param.toArray()));
				userMap.put("total", pageBean.getTotalCount());
			}
		}

		return userMap;
	}

	/**
	 * 审批历史记录
	 * @param pageBean
	 * @param name
	 * @param value
	 * @return
	 */
	@RequestMapping(value = "/listHistory", method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> listHistory(PageBean pageBean, String name, String value) {
		Map<String, Object> userMap = new HashMap<String, Object>();
		User user = getCurrentUser();
		Userarea userarea = sportUserAreaService.getByHql("FROM Userarea ua where ua.user.id =" + user.getId());
		if (ObjectUtil.isNotNull(userarea)) {
			ArrayList<Object> param = new ArrayList<Object>();
			StringBuffer hql = new StringBuffer(" FROM Applicationflow applicationflow  where  status in('4','5') and area_id=" + userarea.getArea().getId());
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
			userMap.put("rows", sportProjectMyService.findFlowByType(Applicationflow.class, hql.toString(), pageBean, param.toArray()));
			userMap.put("total", pageBean.getTotalCount());
		}
		
		return userMap;
	}
	
	
	/**
	 * 审批中
	 * 
	 * @return
	 */
	@RequestMapping(value = "/projectApply", method = { RequestMethod.POST })
	@ResponseBody
	public JSONObject projectApply(@RequestParam("picture") CommonsMultipartFile[] files,HttpServletRequest request,@RequestParam(value = "file", required = false) MultipartFile file) {
		return sportCompletionService.updateProjectApply(files, request, file, getCurrentUser());
	}

}
