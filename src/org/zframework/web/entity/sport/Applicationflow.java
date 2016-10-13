package org.zframework.web.entity.sport;

/**
 * 流程表
 * @author zhumin
 *
 */
import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.SequenceGenerator;

/**
 * 流程设计表（主表）
 * @author zhumin
 *
 */
@Entity
@Table(name = "pa_applicationflow")
public class Applicationflow implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "seq_pa_applicationflow")
	@SequenceGenerator(name = "seq_pa_applicationflow", sequenceName = "seq_pa_applicationflow")
	@NotNull
	public int id;

	@Column
	public int at_id;

	@Column
	public String form_num;

	@Column
	private int processid;
	
	@Column
	public int current_processstepid;

	@Column
	public int next_processstepid;

	@Column
	public int area_id;

	@Column
	public String remark;

	@Column
	public String Application_unit;

	@Column
	public String community_name;

	@Column
	public String status;

	@Column
	public int create_userid;

	@Column
	public String create_time;

	public int getProcessid() {
		return processid;
	}

	public void setProcessid(int processid) {
		this.processid = processid;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getAt_id() {
		return at_id;
	}

	public void setAt_id(int at_id) {
		this.at_id = at_id;
	}

	public String getForm_num() {
		return form_num;
	}

	public void setForm_num(String form_num) {
		this.form_num = form_num;
	}

	public int getCurrent_processstepid() {
		return current_processstepid;
	}

	public void setCurrent_processstepid(int current_processstepid) {
		this.current_processstepid = current_processstepid;
	}

	public int getNext_processstepid() {
		return next_processstepid;
	}

	public void setNext_processstepid(int next_processstepid) {
		this.next_processstepid = next_processstepid;
	}

	public int getArea_id() {
		return area_id;
	}

	public void setArea_id(int area_id) {
		this.area_id = area_id;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getApplication_unit() {
		return Application_unit;
	}

	public void setApplication_unit(String Application_unit) {
		this.Application_unit = Application_unit;
	}

	public String getCommunity_name() {
		return community_name;
	}

	public void setCommunity_name(String community_name) {
		this.community_name = community_name;
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

}
