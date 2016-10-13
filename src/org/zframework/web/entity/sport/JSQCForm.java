package org.zframework.web.entity.sport;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 健身Form实体
 * @author mzhu
 *
 * 2016年1月17日 下午8:12:43
 */
public class JSQCForm implements Serializable {
	
	/**
	 * 表单类型
	 */
	private Integer at_id;
	/**
	 * 申请时间
	 */
	private String create_time;
	
	/**
	 * 社区名称
	 */
	private String community_name;
	/**
	 * 申请单位
	 */
	private String Application_unit; 
	/**
	 * 区域
	 */
	private String area_id;
	/**
	 * 明细
	 */
	List<ApplicationflowDetail> details = new ArrayList<ApplicationflowDetail>();
	
	
	public String getCreate_time() {
		return create_time;
	}
	public void setCreate_time(String create_time) {
		this.create_time = create_time;
	}
	public Integer getAt_id() {
		return at_id;
	}
	public void setAt_id(Integer at_id) {
		this.at_id = at_id;
	}
 
	public String getCommunity_name() {
		return community_name;
	}
	public void setCommunity_name(String community_name) {
		this.community_name = community_name;
	}
	public String getApplication_unit() {
		return Application_unit;
	}
	public void setApplication_unit(String application_unit) {
		Application_unit = application_unit;
	}
	public String getArea_id() {
		return area_id;
	}
	public void setArea_id(String area_id) {
		this.area_id = area_id;
	}
	public List<ApplicationflowDetail> getDetails() {
		return details;
	}
	public void setDetails(List<ApplicationflowDetail> details) {
		this.details = details;
	}
	
}
