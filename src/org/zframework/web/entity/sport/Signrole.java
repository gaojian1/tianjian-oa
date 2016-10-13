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

/**
 * 签核权限角色表
 *  
 * @author mzhu
 *
 * 2016年1月9日 下午9:23:22
 */
@Entity
@Table(name = "pa_signrole")
public class Signrole implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "seq_pa_signrole")
	@SequenceGenerator(name = "seq_pa_signrole", sequenceName = "seq_pa_signrole")
	@NotNull
	public int id;

	@Column
	public String name;

	@Column
	public String desc;

	@Column
	public String status;

	@Column
	public int create_userid;

	@Column
	public String create_date;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
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

	public String getCreate_date() {
		return create_date;
	}

	public void setCreate_date(String create_date) {
		this.create_date = create_date;
	}

}
