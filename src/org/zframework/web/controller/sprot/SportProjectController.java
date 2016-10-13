package org.zframework.web.controller.sprot;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.zframework.core.util.Constant;
import org.zframework.core.util.ObjectUtil;
import org.zframework.core.util.StringUtil;
import org.zframework.orm.query.PageBean;
import org.zframework.sms.utils.DateUtil;
import org.zframework.web.controller.BaseController;
import org.zframework.web.controller.ConfigSport;
import org.zframework.web.entity.sport.Applicationflow;
import org.zframework.web.entity.sport.ApplicationflowAttach;
import org.zframework.web.entity.sport.ApplicationflowDetail;
import org.zframework.web.entity.sport.ApplicationflowLog;
import org.zframework.web.entity.sport.Applicationtype;
import org.zframework.web.entity.sport.JSQCForm;
import org.zframework.web.entity.sport.Processnode;
import org.zframework.web.entity.sport.Userarea;
import org.zframework.web.entity.system.User;
import org.zframework.web.service.admin.sport.ApplicationflowLogService;
import org.zframework.web.service.admin.sport.SportProcessnodeService;
import org.zframework.web.service.admin.sport.SportUserAreaService;
import org.zframework.web.service.admin.sport.area.AreaService;
import org.zframework.web.service.admin.sport.project.appliction.ApplicationTypeService;
import org.zframework.web.service.admin.sport.project.appliction.ApplicationflowAttachService;
import org.zframework.web.service.admin.sport.project.appliction.ApplicationflowDetailService;
import org.zframework.web.service.admin.sport.project.appliction.ApplicationflowService;
import org.zframework.web.service.admin.system.UserService;

/**
 * 项目申请
 * 
 * @author zhumin
 *
 */
@Controller
@RequestMapping("/admin/sport/project")
public class SportProjectController extends BaseController<Applicationflow> {

	@Autowired
	private ApplicationTypeService applicationTypeService;

	@Autowired
	ApplicationflowDetailService applicationflowDetailService;
	@Autowired
	ApplicationflowService applicationflowService;
	@Autowired
	ApplicationflowLogService applicationflowLogService;
	@Autowired
	SportProcessnodeService sportProcessnodeService;
	@Autowired
	UserService userService;
	@Autowired
	private SportUserAreaService sportUserAreaService;
	@Autowired
	private AreaService sportAreaService;
	@Autowired
	private ApplicationflowAttachService applicationflowAttachService;
	@Autowired
	private ConfigSport configSport;

	/**
	 * 文件下载
	 * 
	 * @param response
	 * @param id
	 */
	@RequestMapping(value = "/download/{id}", method = { RequestMethod.GET })
	public void downloadFile(HttpServletResponse response, @PathVariable Integer id) {
		List<Criterion> attrList = new ArrayList<Criterion>();
		attrList.add(Restrictions.eq("af_id", id));
		attrList.add(Restrictions.eq("type", Constant.ATTCH_TYPE_0));
		List<ApplicationflowAttach> applicationflowAttachList = applicationflowAttachService.list((Criterion[]) attrList.toArray(new Criterion[attrList.size()]));
		String fileName = "";
		if (applicationflowAttachList.size() > 0) {
			ApplicationflowAttach applicationflowAttach = applicationflowAttachList.get(0);
			fileName = applicationflowAttach.getName();

		}
		String filePath = "";
		try {
			javax.servlet.ServletOutputStream out = response.getOutputStream();
			if (!StringUtil.isBlank(fileName)) {
				filePath = "http://" + configSport.getUrl() + "/image/upload/" + id + "/" + fileName;
				URL url = new URL(filePath);
				URLConnection conn = url.openConnection();
				InputStream is = conn.getInputStream();

				response.reset(); // 必要地清除response中的缓存信息
				response.setHeader("Content-Disposition", "attachment; filename=" + new String(fileName.getBytes()));

				byte[] content = new byte[is.available()];
				int length = 0;
				while ((length = is.read(content)) != -1) {
					out.write(content, 0, length);
				}
				out.write(content);
				out.flush();
				out.close();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 更加查询数据
	 * 
	 * @param model
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/toShow/{id}", method = { RequestMethod.GET })
	public String toShow(Model model, @PathVariable Integer id) {
		// Applicationflow applicationflow = applicationflowService.getById(id);
		//
		// // add by james 日期转换 和 加入区域显示
		// applicationflow.setCreate_time(DateUtil.dateToStr(
		// DateUtil.StrToDate(applicationflow.getCreate_time()), 11));
		// // 根据用户查询区域
		// // Area areasobj =
		// // sportAreaService.getByHql("FROM Area ua where ua.id ="
		// // + applicationflow.getArea_id());
		// // String areastr = areasobj.getAreaname();
		// String areastr = applicationflow.getCommunity_name();
		// model.addAttribute("areastr", areastr);
		//
		// model.addAttribute("applicationflow", applicationflow);
		// List<Criterion> criterions = new ArrayList<Criterion>();
		// criterions.add(Restrictions.eq("af_id", id));
		// List<ApplicationflowDetail> list = applicationflowDetailService
		// .list((Criterion[]) criterions.toArray(new Criterion[criterions
		// .size()]));
		//
		// List<Criterion> attrList = new ArrayList<Criterion>();
		// attrList.add(Restrictions.eq("af_id", id));
		// List<ApplicationflowAttach> applicationflowAttachList =
		// applicationflowAttachService
		// .list((Criterion[]) attrList.toArray(new Criterion[attrList
		// .size()]));
		// if (applicationflowAttachList.size() > 0)
		// model.addAttribute("show", "open");
		//
		// if (applicationflow.getAt_id() == 5) {
		// model.addAttribute("applicationflowDetails", list);
		// } else {
		// if (list != null && list.size() > 0)
		// model.addAttribute("applicationflowDetail", list.get(0));
		// }

		// model.addAttribute("dowlnButton", "")
		Applicationflow applicationflow = toShowOrEdit(model, id);
		String url = "";
		switch (applicationflow.getAt_id()) {
		case 1:
			url = "admin/system/sport/project/apply/show/szfssxmsmzqc";
			break;
		case 2:
			url = "admin/system/sport/project/apply/show/cjxqgym";
			break;
		case 3:
			url = "admin/system/sport/project/apply/show/lygym";
			break;
		case 4:
			url = "admin/system/sport/project/apply/show/sqbxgym";
			break;
		case 5:
			url = "admin/system/sport/project/apply/show/qx";
			break;
		case 6:
			// 社区公共运动场
			url = "admin/system/sport/project/apply/show/sqggplayground";
			break;
		case 7:
			// 新建社区健身苑点
			url = "admin/system/sport/project/apply/show/sqjsplayground";
			break;
		case 8:
			// 市民运动场（足球场）建设申报表
			url = "admin/system/sport/project/apply/show/smplayground";
			break;
		case 9:
			// 百姓健身步道建设申报表
			url = "admin/system/sport/project/apply/show/bxjsbd";
			break;
		case 10:
			// 市政府实事项目 市民运动场 多功能运动场改建工程
			url = "admin/system/sport/project/apply/show/dgnplayground";
			break;
		case 11:
			//2017年度 上海市 村居委健身房 建设 申报（初审）表
			url = "admin/system/sport/project/apply/show/cjwjsfjssbcsb";
			break;
		case 12:
			//2017年度老年健身房建设申报表
			url = "admin/system/sport/project/apply/show/oldpeoplejsfjssbb";
			break;
		case 13:
			//市民益智健身苑点 健身器材配置价目表
			url = "admin/system/sport/project/apply/show/smyzjsydjsqcpzb";
			break;
		default:
			break;
		}
		return url;

	}

	/**
	 * 修改表单申请信息
	 * 
	 * @param model
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/toEdit/{id}", method = { RequestMethod.GET })
	public String toEdit(Model model, @PathVariable Integer id) {
		Applicationflow applicationflow = toShowOrEdit(model, id);
		String url = "";
		switch (applicationflow.getAt_id()) {
		case 1:
			url = "admin/system/sport/project/apply/edit/bxswing";
			break;
		case 2:
			url = "admin/system/sport/project/apply/edit/cjxqgym";
			break;
		case 3:
			url = "admin/system/sport/project/apply/edit/lygym";
			break;
		case 4:
			url = "admin/system/sport/project/apply/edit/sqbxgym";
			break;
		case 5:
			url = "admin/system/sport/project/apply/edit/qx";
			break;
		case 6:
			// 社区公共运动场
			url = "admin/system/sport/project/apply/edit/sqggplayground";
			break;
		case 7:
			// 新建社区健身苑点
			url = "admin/system/sport/project/apply/edit/sqjsplayground";
			break;
		case 8:
			// 市民运动场（足球场）建设申报表
			url = "admin/system/sport/project/apply/edit/smplayground";
			break;
		case 9:
			// 百姓健身步道建设申报表
			url = "admin/system/sport/project/apply/edit/bxjsbd";
			break;
		case 10:
			// 市政府实事项目 市民运动场 多功能运动场改建工程
			url = "admin/system/sport/project/apply/edit/dgnplayground";
			break;
		default:
			break;
		}
		return url;

	}

	public Applicationflow toShowOrEdit(Model model, Integer id) {
		Applicationflow applicationflow = applicationflowService.getById(id);

		// add by james 日期转换 和 加入区域显示
		applicationflow.setCreate_time(DateUtil.dateToStr(DateUtil.StrToDate(applicationflow.getCreate_time()), 11));
		// 根据用户查询区域
		// Area areasobj =
		// sportAreaService.getByHql("FROM Area ua where ua.id ="
		// + applicationflow.getArea_id());
		// String areastr = areasobj.getAreaname();
		String areastr = applicationflow.getCommunity_name();
		model.addAttribute("areastr", areastr);

		model.addAttribute("applicationflow", applicationflow);
		List<Criterion> criterions = new ArrayList<Criterion>();
		criterions.add(Restrictions.eq("af_id", id));
		List<ApplicationflowDetail> list = applicationflowDetailService.list((Criterion[]) criterions.toArray(new Criterion[criterions.size()]));

		List<Criterion> attrList = new ArrayList<Criterion>();
		attrList.add(Restrictions.eq("af_id", id));
		List<ApplicationflowAttach> applicationflowAttachList = applicationflowAttachService.list((Criterion[]) attrList.toArray(new Criterion[attrList.size()]));
		if (applicationflowAttachList.size() > 0)
			model.addAttribute("show", "open");

		if (applicationflow.getAt_id() == 5) {
			model.addAttribute("applicationflowDetails", list);
		} else if(applicationflow.getAt_id() == 13) {
			model.addAttribute("applicationflowDetails", list);
		}
		else {
			if (list != null && list.size() > 0)
				model.addAttribute("applicationflowDetail", list.get(0));
		}
		return applicationflow;
	}

	/**
	 * 根据ID查询流水
	 * 
	 * @return
	 */
	@RequestMapping(value = "/showFlow/{id}", method = { RequestMethod.GET })
	public String showFlow(Model model, @PathVariable Integer id) {
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
		return "admin/system/sport/project/show/flow";
	}

	/**
	 * 项目类型
	 * 
	 * @param model
	 * @return
	 */
	@RequestMapping(method = { RequestMethod.GET })
	public String index(Model model) {
		return "admin/system/sport/project/apply/index";
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
	@RequestMapping(value = "/list", method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> list(PageBean pageBean) {
		Map<String, Object> userMap = new HashMap<String, Object>();
		pageBean.addCriterion(Restrictions.eq("status", "0"));
		List<Applicationtype> listApplicationtype = applicationTypeService.listByPage(pageBean);
		userMap.put("rows", listApplicationtype);
		userMap.put("total", pageBean.getTotalCount());
		return userMap;
	}

	/**
	 * 调整页面
	 * 
	 * @return
	 */
	@RequestMapping(value = "/forward/{id}", method = { RequestMethod.GET })
	public String forward(Model model, @PathVariable Integer id) {
		String url = "";

		String nowdate = DateUtil.dateToStr(new Date(), 11);
		model.addAttribute("nowdate", nowdate);
		// 获取当前用户
		User user = getCurrentUser();
		// 根据用户查询区域
		Userarea userarea = sportUserAreaService.getByHql("FROM Userarea ua where ua.user.id =" + user.getId());
		String areastr = userarea.getArea().getAreaname();
		model.addAttribute("areastr", areastr);

		switch (id) {
		case 1:
			//2017年度市政府实事项目市民足球场建设申报（初审）表
			url = "admin/system/sport/project/apply/szfssxmsmzqc";
			break;
		case 2:
			url = "admin/system/sport/project/apply/cjxqgym";
			break;
		case 3:
			url = "admin/system/sport/project/apply/lygym";
			break;
		case 4:
			url = "admin/system/sport/project/apply/sqbxgym";
			break;
		case 5:
			url = "admin/system/sport/project/apply/qx";
			break;
		case 6:
			// 社区公共运动场
			url = "admin/system/sport/project/apply/sqggplayground";
			break;
		case 7:
			// 新建社区健身苑点
			url = "admin/system/sport/project/apply/sqjsplayground";
			break;
		case 8:
			// 市民运动场（足球场）建设申报表
			url = "admin/system/sport/project/apply/smplayground";
			break;
		case 9:
			// 百姓健身步道建设申报表
			url = "admin/system/sport/project/apply/bxjsbd";
			break;
		case 10:
			// 市政府实事项目 市民运动场 多功能运动场改建工程
			url = "admin/system/sport/project/apply/dgnplayground";
			break;
			
		case 11:
			//2017年度 上海市 村居委健身房 建设 申报（初审）表
			url = "admin/system/sport/project/apply/cjwjsfjssbcsb";
			break;
			
		case 12:
			//2017年度老年健身房建设申报表
			url = "admin/system/sport/project/apply/oldpeoplejsfjssbb";
			break;
		case 13:
			//市民益智健身苑点 健身器材配置价目表
			url = "admin/system/sport/project/apply/smyzjsydjsqcpzb";
			break;
			//添加新的表单
			
		default:
			break;
		}
		return url;
	}

	/**
	 * 添加申请单
	 * 
	 * @param request
	 * @param areaIds
	 * @return
	 */
	@RequestMapping(value = "/addProject", method = { RequestMethod.POST })
	@ResponseBody
	public JSONObject addProject(HttpServletRequest request, @ModelAttribute("applicationflowDetail") ApplicationflowDetail applicationflowDetail, @RequestParam(value = "file", required = false) MultipartFile file) {
		return applicationTypeService.addProject(request, applicationflowDetail, getCurrentUser(), file);
	}

	
	
	@RequestMapping(value = "/updateProject", method = { RequestMethod.POST })
	@ResponseBody
	public JSONObject updateProject(HttpServletRequest request, @ModelAttribute("applicationflowDetail") ApplicationflowDetail applicationflowDetail, @RequestParam(value = "file", required = false) MultipartFile file) {
		return applicationTypeService.updateProject(request, applicationflowDetail, getCurrentUser(), file);
	}
	
	
	/**
	 * 添加申请单器械
	 * 
	 * @param request
	 * @param areaIds
	 * @return
	 */
	@RequestMapping(value = "/addProjectQX", method = { RequestMethod.POST })
	@ResponseBody
	public JSONObject addProjectQX(HttpServletRequest request, @ModelAttribute("details") JSQCForm details) {
		List<ApplicationflowDetail> listApplicationflowDetail = details.getDetails();
		List<ApplicationflowDetail> data = new ArrayList<ApplicationflowDetail>();
		for (int i = 0; i < listApplicationflowDetail.size(); i++) {
			if (!"nullnull".equals(listApplicationflowDetail.get(i).getStr())) {
				data.add(listApplicationflowDetail.get(i));
			}
		}
		details.setDetails(data);
		return applicationTypeService.addProjectQX(request, details, getCurrentUser());
	}
	
	/**
	 * 修改器械
	 * 
	 * @param request
	 * @param areaIds
	 * @return
	 */
	@RequestMapping(value = "/updateProjectQX", method = { RequestMethod.POST })
	@ResponseBody
	public JSONObject updateProjectQX(HttpServletRequest request, @ModelAttribute("details") JSQCForm details) {
		List<ApplicationflowDetail> listApplicationflowDetail = details.getDetails();
		List<ApplicationflowDetail> data = new ArrayList<ApplicationflowDetail>();
		for (int i = 0; i < listApplicationflowDetail.size(); i++) {
			if (!"nullnull".equals(listApplicationflowDetail.get(i).getStr())) {
				data.add(listApplicationflowDetail.get(i));
			}
		}
		details.setDetails(data);
		return applicationTypeService.updateProjectQX(request, details, getCurrentUser());
	}

}
