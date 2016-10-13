package org.zframework.web.entity.sport;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.SequenceGenerator;

@Entity
@Table(name="pa_applicationtype")
public class Applicationtype implements Serializable{

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO,generator="seq_pa_applicationtype")
	@SequenceGenerator(name="seq_pa_applicationtype",sequenceName="seq_pa_applicationtype")
	@NotNull
	public int id;

	@Column
	public String name;

	@Column
	public String desc;

	@Column
	public String remark;

	@Column
	public String status;

	@Column
	public int create_userid;

	@Column
	public String create_time;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id=id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name=name;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc=desc;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark=remark;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status=status;
	}

	public int getCreate_userid() {
		return create_userid;
	}

	public void setCreate_userid(int create_userid) {
		this.create_userid=create_userid;
	}

	public String getCreate_time() {
		return create_time;
	}

	public void setCreate_time(String create_time) {
		this.create_time=create_time;
	}


}
