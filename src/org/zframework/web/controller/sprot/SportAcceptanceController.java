package org.zframework.web.controller.sprot;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.zframework.core.util.Constant;
import org.zframework.core.util.ObjectUtil;
import org.zframework.core.util.RegexUtil;
import org.zframework.core.util.StringUtil;
import org.zframework.orm.query.PageBean;
import org.zframework.web.controller.BaseController;
import org.zframework.web.controller.ConfigSport;
import org.zframework.web.entity.sport.Applicationflow;
import org.zframework.web.entity.sport.ApplicationflowAttach;
import org.zframework.web.entity.sport.Userarea;
import org.zframework.web.entity.system.User;
import org.zframework.web.service.admin.sport.ApplicationflowLogService;
import org.zframework.web.service.admin.sport.SportAcceptanceService;
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
 * 项目验收
 * @author mzhu
 *
 * 2016年2月5日 下午7:27:36
 */
@Controller
@RequestMapping("/admin/sport/acceptance")
public class SportAcceptanceController  extends BaseController<Applicationflow>{

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
	private ApplicationflowAttachService applicationflowAttachService;
	@Autowired
	ConfigSport configSport;
	@Autowired
	private SportAcceptanceService sportAcceptanceService;
	
	
	
	/**
	 * 文件下载
	 * 
	 * @param response
	 * @param id
	 */
	@RequestMapping(value = "/download/{id}", method = { RequestMethod.GET })
	public void downloadFile(HttpServletResponse response, @PathVariable Integer id) {
		ApplicationflowAttach applicationflowAttach = applicationflowAttachService.getById(id);
		String fileName = "";
		if (ObjectUtil.isNotNull(applicationflowAttach)) {
			fileName = applicationflowAttach.getName();
		}
		String filePath = "";
		try {
			javax.servlet.ServletOutputStream out = response.getOutputStream();
			if (!StringUtil.isBlank(fileName)) {
				filePath = "http://" + configSport.getUrl() + "/image/upload/" + applicationflowAttach.getAf_id() + "/" + fileName;
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
	 * 项目验收
	 * 
	 * @param model
	 * @return
	 */
	@RequestMapping(method = { RequestMethod.GET })
	public String index(Model model) {
		logService.recordInfo("项目验收审核管理", "成功", getCurrentUser().getLoginName(), getRequestAddr());
		return "admin/system/sport/project/acceptance/index";
	}

	/**
	 * 显示明细
	 * @param model
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/info/{id}", method = { RequestMethod.GET })
	public String info(Model model,@PathVariable Integer id) {
		List<ApplicationflowAttach> listApplicationflowAttach = applicationflowAttachService.listForEntity("FROM ApplicationflowAttach WHERE  status='有效' and type="+Constant.ATTCH_TYPE_1+" AND af_id="+id+" AND file_type="+Constant.FILE_TYPE_1);
		List<String> pictures = new ArrayList<String>();
		for(ApplicationflowAttach applicationflowAttach:listApplicationflowAttach){
			String pictureUrl = "http://"+configSport.getUrl()+ "/image/upload/" + id + "/" + applicationflowAttach.getName();
			pictures.add(pictureUrl);
		}
		model.addAttribute("pictures", pictures);
		List<ApplicationflowAttach> listFiles = applicationflowAttachService.listForEntity("FROM ApplicationflowAttach WHERE status='有效' and  type="+Constant.ATTCH_TYPE_1+" AND af_id="+id+" AND file_type="+Constant.FILE_TYPE_2);
		model.addAttribute("files", listFiles);
		return "admin/system/sport/project/acceptance/info";
	}
	
	/**
	 * 审核
	 * @param model
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/audit/{id}", method = { RequestMethod.GET })
	public String audit(Model model,@PathVariable Integer id) {
		model.addAttribute("flowId", id);
		return "admin/system/sport/project/acceptance/audit";
	}
	
	
	
	/**
	 * 项目验收数据
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
//			Userarea userarea = sportUserAreaService.getByHql("FROM Userarea ua where ua.user.id =" + user.getId());
//			if (ObjectUtil.isNotNull(userarea)) {
				ArrayList<Object> param = new ArrayList<Object>();
//				StringBuffer hql = new StringBuffer(" FROM Applicationflow applicationflow  where  status in('4') and area_id=" + userarea.getArea().getId());
				StringBuffer hql = new StringBuffer(" FROM Applicationflow applicationflow  where  status in('4') ");
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
//			}

		return userMap;
	}
	
	/**
	 * 历史记录
	 * @param pageBean
	 * @param name
	 * @param value
	 * @return
	 */
	@RequestMapping(value = "/listhistory", method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> listhistory(PageBean pageBean, String name, String value) {
		Map<String, Object> userMap = new HashMap<String, Object>();
		User user = getCurrentUser();
//			Userarea userarea = sportUserAreaService.getByHql("FROM Userarea ua where ua.user.id =" + user.getId());
//			if (ObjectUtil.isNotNull(userarea)) {
				ArrayList<Object> param = new ArrayList<Object>();
//				StringBuffer hql = new StringBuffer(" FROM Applicationflow applicationflow  where  status in('5') and area_id=" + userarea.getArea().getId());
				StringBuffer hql = new StringBuffer(" FROM Applicationflow applicationflow  where  status in('5') ");
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
//			}

		return userMap;
	}

	
	// 同意
	@RequestMapping(value = "/submitApply", method = { RequestMethod.POST })
	@ResponseBody
	public JSONObject submitApply(HttpServletRequest request) {
		String status = request.getParameter("status");
		//同意
		if("0".equals(status)){
			return sportAcceptanceService.updateAgree(request, getCurrentUser());
		}else{
			return sportAcceptanceService.updateDisAgree(request, getCurrentUser());
			
		}
	}
 
	
	
}
