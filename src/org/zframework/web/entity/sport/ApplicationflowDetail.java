package org.zframework.web.entity.sport;

/**
 * 流程设计副表
 * @author zhumin
 *
 */
import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import org.zframework.core.util.ObjectUtil;

@Entity
@Table(name = "pa_applicationflow_detail")
public class ApplicationflowDetail implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "seq_pa_applicationflow_detail")
	@SequenceGenerator(name = "seq_pa_applicationflow_detail", sequenceName = "seq_pa_applicationflow_detail")
	@NotNull
	public int id;

	@Column
	public int af_id;
	public int getAf_id() {
		return af_id;
	}

	public void setAf_id(int af_id) {
		this.af_id = af_id;
	}

	@Column
	public String project_name;

	@Column
	public String address;

	@Column
	public String town;

	@Column
	public String contact_name;

	@Column
	public String contact_phone;

	@Column
	public String intent_designunit;

	@Column
	public String intent_constructionunit;

	@Column
	public String project_unit;

	@Column
	public BigDecimal total_area;

	@Column
	public BigDecimal pool_area;

	@Column
	public BigDecimal pool_length;

	@Column
	public BigDecimal pool_width;

	@Column
	public BigDecimal ancillary_area;

	@Column
	public String ancillary_content;

	@Column
	public BigDecimal eqproom_area;

	@Column
	public BigDecimal aerobicroom_area;

	@Column
	public BigDecimal gymroom_area;

	@Column
	public String act_fitneseqp;

	@Column
	public String project_desc;

	@Column
	public int district_facility_qty;

	@Column
	public int place_facility_qty;

	@Column
	public BigDecimal trails_length;

	@Column
	public BigDecimal trails_width;

	@Column
	public String project_desc_after;

	@Column
	public BigDecimal overall_budget;

	@Column
	public BigDecimal county_budget;

	@Column
	public BigDecimal street_budget;

	@Column
	public BigDecimal other_budget;

	@Column
	public BigDecimal village_budget;

	@Column
	public BigDecimal company_budget;

	@Column
	public BigDecimal sport_budget;

	@Column
	public String start_date;

	@Column
	public String completed_date;

	@Column
	public String management_unit;

	@Column
	public BigDecimal operating_charges;

	@Column
	public String intent_company;

	@Column
	public int management_staff_qty;

	@Column
	public String is_welfare;

	@Column
	public String Application_unit;

	@Column
	public String community_name;

	@Column
	public String Vendor;

	@Column
	public String brand;

	@Column
	public String importsordomestic;

	@Column
	public String facility_name;

	@Column
	public String facility_model;

	@Column
	public BigDecimal facility_price;

	@Column
	public BigDecimal facility_qty;

	@Column
	public String intent_enterdate;

	@Column
	public String status;

	@Column
	public int create_userid;

	@Column
	public String create_time;

	@Column
	public String obj1;

	@Column
	public String obj2;

	@Column
	public String obj3;

	@Column
	public String obj4;

	@Column
	public String obj5;

	@Column
	public String obj6;

	@Column
	public String obj7;

	@Column
	public String obj8;

	@Column
	public String obj9;

	@Column
	public String obj10;

	@Transient
	public String totalPrice;
	
	
	public String getTotalPrice() {
		//facility_qty
		//facility_price
		if(ObjectUtil.isNotNull(facility_qty) && ObjectUtil.isNotNull(facility_price)){
			return facility_qty.multiply(facility_price).toString();
		}
		return "0";
	}

	public void setTotalPrice(String totalPrice) {
		this.totalPrice = totalPrice;
	}

	public String getStr(){
		return Vendor+brand+importsordomestic+facility_name+facility_model+facility_price+facility_qty+intent_enterdate;
	}
	
	@Override
	public String toString() {
		return "ApplicationflowDetail [id=" + id + ", af_id=" + af_id + ", project_name=" + project_name + ", address=" + address + ", town=" + town + ", contact_name=" + contact_name + ", contact_phone=" + contact_phone + ", intent_designunit=" + intent_designunit + ", intent_constructionunit=" + intent_constructionunit + ", project_unit=" + project_unit + ", total_area=" + total_area + ", pool_area=" + pool_area + ", pool_length=" + pool_length + ", pool_width=" + pool_width + ", ancillary_area=" + ancillary_area + ", ancillary_content=" + ancillary_content + ", eqproom_area=" + eqproom_area + ", aerobicroom_area=" + aerobicroom_area + ", gymroom_area=" + gymroom_area + ", act_fitneseqp=" + act_fitneseqp + ", project_desc=" + project_desc + ", district_facility_qty="
				+ district_facility_qty + ", place_facility_qty=" + place_facility_qty + ", trails_length=" + trails_length + ", trails_width=" + trails_width + ", project_desc_after=" + project_desc_after + ", overall_budget=" + overall_budget + ", county_budget=" + county_budget + ", street_budget=" + street_budget + ", other_budget=" + other_budget + ", village_budget=" + village_budget + ", company_budget=" + company_budget + ", sport_budget=" + sport_budget + ", start_date=" + start_date + ", completed_date=" + completed_date + ", management_unit=" + management_unit + ", operating_charges=" + operating_charges + ", intent_company=" + intent_company + ", management_staff_qty=" + management_staff_qty + ", is_welfare=" + is_welfare + ", Application_unit=" + Application_unit
				+ ", community_name=" + community_name + ", Vendor=" + Vendor + ", brand=" + brand + ", importsordomestic=" + importsordomestic + ", facility_name=" + facility_name + ", facility_model=" + facility_model + ", facility_price=" + facility_price + ", facility_qty=" + facility_qty + ", intent_enterdate=" + intent_enterdate + ", status=" + status + ", create_userid=" + create_userid + ", create_time=" + create_time + ", obj1=" + obj1 + ", obj2=" + obj2 + ", obj3=" + obj3 + ", obj4=" + obj4 + ", obj5=" + obj5 + ", obj6=" + obj6 + ", obj7=" + obj7 + ", obj8=" + obj8 + ", obj9=" + obj9 + ", obj10=" + obj10 + "]";
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getProject_name() {
		return project_name;
	}

	public void setProject_name(String project_name) {
		this.project_name = project_name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getTown() {
		return town;
	}

	public void setTown(String town) {
		this.town = town;
	}

	public String getContact_name() {
		return contact_name;
	}

	public void setContact_name(String contact_name) {
		this.contact_name = contact_name;
	}

	public String getContact_phone() {
		return contact_phone;
	}

	public void setContact_phone(String contact_phone) {
		this.contact_phone = contact_phone;
	}

	public String getIntent_designunit() {
		return intent_designunit;
	}

	public void setIntent_designunit(String intent_designunit) {
		this.intent_designunit = intent_designunit;
	}

	public String getIntent_constructionunit() {
		return intent_constructionunit;
	}

	public void setIntent_constructionunit(String intent_constructionunit) {
		this.intent_constructionunit = intent_constructionunit;
	}

	public String getProject_unit() {
		return project_unit;
	}

	public void setProject_unit(String project_unit) {
		this.project_unit = project_unit;
	}

	public BigDecimal getTotal_area() {
		return total_area;
	}

	public void setTotal_area(BigDecimal total_area) {
		this.total_area = total_area;
	}

	public BigDecimal getPool_area() {
		return pool_area;
	}

	public void setPool_area(BigDecimal pool_area) {
		this.pool_area = pool_area;
	}

	public BigDecimal getPool_length() {
		return pool_length;
	}

	public void setPool_length(BigDecimal pool_length) {
		this.pool_length = pool_length;
	}

	public BigDecimal getPool_width() {
		return pool_width;
	}

	public void setPool_width(BigDecimal pool_width) {
		this.pool_width = pool_width;
	}

	public BigDecimal getAncillary_area() {
		return ancillary_area;
	}

	public void setAncillary_area(BigDecimal ancillary_area) {
		this.ancillary_area = ancillary_area;
	}

	public String getAncillary_content() {
		return ancillary_content;
	}

	public void setAncillary_content(String ancillary_content) {
		this.ancillary_content = ancillary_content;
	}

	public BigDecimal getEqproom_area() {
		return eqproom_area;
	}

	public void setEqproom_area(BigDecimal eqproom_area) {
		this.eqproom_area = eqproom_area;
	}

	public BigDecimal getAerobicroom_area() {
		return aerobicroom_area;
	}

	public void setAerobicroom_area(BigDecimal aerobicroom_area) {
		this.aerobicroom_area = aerobicroom_area;
	}

	public BigDecimal getGymroom_area() {
		return gymroom_area;
	}

	public void setGymroom_area(BigDecimal gymroom_area) {
		this.gymroom_area = gymroom_area;
	}

	public String getAct_fitneseqp() {
		return act_fitneseqp;
	}

	public void setAct_fitneseqp(String act_fitneseqp) {
		this.act_fitneseqp = act_fitneseqp;
	}

	public String getProject_desc() {
		return project_desc;
	}

	public void setProject_desc(String project_desc) {
		this.project_desc = project_desc;
	}

	public int getDistrict_facility_qty() {
		return district_facility_qty;
	}

	public void setDistrict_facility_qty(int district_facility_qty) {
		this.district_facility_qty = district_facility_qty;
	}

	public int getPlace_facility_qty() {
		return place_facility_qty;
	}

	public void setPlace_facility_qty(int place_facility_qty) {
		this.place_facility_qty = place_facility_qty;
	}

	public BigDecimal getTrails_length() {
		return trails_length;
	}

	public void setTrails_length(BigDecimal trails_length) {
		this.trails_length = trails_length;
	}

	public BigDecimal getTrails_width() {
		return trails_width;
	}

	public void setTrails_width(BigDecimal trails_width) {
		this.trails_width = trails_width;
	}

	public String getProject_desc_after() {
		return project_desc_after;
	}

	public void setProject_desc_after(String project_desc_after) {
		this.project_desc_after = project_desc_after;
	}

	public BigDecimal getOverall_budget() {
		return overall_budget;
	}

	public void setOverall_budget(BigDecimal overall_budget) {
		this.overall_budget = overall_budget;
	}

	public BigDecimal getCounty_budget() {
		return county_budget;
	}

	public void setCounty_budget(BigDecimal county_budget) {
		this.county_budget = county_budget;
	}

	public BigDecimal getStreet_budget() {
		return street_budget;
	}

	public void setStreet_budget(BigDecimal street_budget) {
		this.street_budget = street_budget;
	}

	public BigDecimal getOther_budget() {
		return other_budget;
	}

	public void setOther_budget(BigDecimal other_budget) {
		this.other_budget = other_budget;
	}

	public BigDecimal getVillage_budget() {
		return village_budget;
	}

	public void setVillage_budget(BigDecimal village_budget) {
		this.village_budget = village_budget;
	}

	public BigDecimal getCompany_budget() {
		return company_budget;
	}

	public void setCompany_budget(BigDecimal company_budget) {
		this.company_budget = company_budget;
	}

	public BigDecimal getSport_budget() {
		return sport_budget;
	}

	public void setSport_budget(BigDecimal sport_budget) {
		this.sport_budget = sport_budget;
	}

	public String getStart_date() {
		return start_date;
	}

	public void setStart_date(String start_date) {
		this.start_date = start_date;
	}

	public String getCompleted_date() {
		return completed_date;
	}

	public void setCompleted_date(String completed_date) {
		this.completed_date = completed_date;
	}

	public String getManagement_unit() {
		return management_unit;
	}

	public void setManagement_unit(String management_unit) {
		this.management_unit = management_unit;
	}

	public BigDecimal getOperating_charges() {
		return operating_charges;
	}

	public void setOperating_charges(BigDecimal operating_charges) {
		this.operating_charges = operating_charges;
	}

	public String getIntent_company() {
		return intent_company;
	}

	public void setIntent_company(String intent_company) {
		this.intent_company = intent_company;
	}

	public int getManagement_staff_qty() {
		return management_staff_qty;
	}

	public void setManagement_staff_qty(int management_staff_qty) {
		this.management_staff_qty = management_staff_qty;
	}

	public String getIs_welfare() {
		return is_welfare;
	}

	public void setIs_welfare(String is_welfare) {
		this.is_welfare = is_welfare;
	}

	public String getApplication_unit() {
		return Application_unit;
	}

	public void setApplication_unit(String application_unit) {
		Application_unit = application_unit;
	}

	public String getCommunity_name() {
		return community_name;
	}

	public void setCommunity_name(String community_name) {
		this.community_name = community_name;
	}

	public String getVendor() {
		return Vendor;
	}

	public void setVendor(String vendor) {
		Vendor = vendor;
	}

	public String getBrand() {
		return brand;
	}

	public void setBrand(String brand) {
		this.brand = brand;
	}

	public String getImportsordomestic() {
		return importsordomestic;
	}

	public void setImportsordomestic(String importsordomestic) {
		this.importsordomestic = importsordomestic;
	}

	public String getFacility_name() {
		return facility_name;
	}

	public void setFacility_name(String facility_name) {
		this.facility_name = facility_name;
	}

	public String getFacility_model() {
		return facility_model;
	}

	public void setFacility_model(String facility_model) {
		this.facility_model = facility_model;
	}

	public BigDecimal getFacility_price() {
		return facility_price;
	}

	public void setFacility_price(BigDecimal facility_price) {
		this.facility_price = facility_price;
	}

//	public int getFacility_qty() {
//		return facility_qty;
//	}
//
//	public void setFacility_qty(int facility_qty) {
//		this.facility_qty = facility_qty;
//	}

	public String getIntent_enterdate() {
		return intent_enterdate;
	}

	public BigDecimal getFacility_qty() {
		return facility_qty;
	}

	public void setFacility_qty(BigDecimal facility_qty) {
		this.facility_qty = facility_qty;
	}

	public void setIntent_enterdate(String intent_enterdate) {
		this.intent_enterdate = intent_enterdate;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public int getCreate_userid() {
		return create_userid;
	}

	public void setCreate_userid(int create_userid) {
		this.create_userid = create_userid;
	}

	public String getCreate_time() {
		return create_time;
	}

	public void setCreate_time(String create_time) {
		this.create_time = create_time;
	}

	public String getObj1() {
		return obj1;
	}

	public void setObj1(String obj1) {
		this.obj1 = obj1;
	}

	public String getObj2() {
		return obj2;
	}

	public void setObj2(String obj2) {
		this.obj2 = obj2;
	}

	public String getObj3() {
		return obj3;
	}

	public void setObj3(String obj3) {
		this.obj3 = obj3;
	}

	public String getObj4() {
		return obj4;
	}

	public void setObj4(String obj4) {
		this.obj4 = obj4;
	}

	public String getObj5() {
		return obj5;
	}

	public void setObj5(String obj5) {
		this.obj5 = obj5;
	}

	public String getObj6() {
		return obj6;
	}

	public void setObj6(String obj6) {
		this.obj6 = obj6;
	}

	public String getObj7() {
		return obj7;
	}

	public void setObj7(String obj7) {
		this.obj7 = obj7;
	}

	public String getObj8() {
		return obj8;
	}

	public void setObj8(String obj8) {
		this.obj8 = obj8;
	}

	public String getObj9() {
		return obj9;
	}

	public void setObj9(String obj9) {
		this.obj9 = obj9;
	}

	public String getObj10() {
		return obj10;
	}

	public void setObj10(String obj10) {
		this.obj10 = obj10;
	}
	
	

}