package org.zframework.web.entity.sport;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

/**
 * 附件信息表
 * @author mzhu
 *
 * 2016年1月26日 下午11:13:05
 */
@Entity
@Table(name="pa_applicationflow_attach")
public class ApplicationflowAttach implements Serializable{

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO,generator="seq_pa_applicationflow_attach")
	@SequenceGenerator(name="seq_pa_applicationflow_attach",sequenceName="seq_pa_applicationflow_attach")
	@NotNull
	public int id;

	@Column
	public int af_id;

	@Column
	public String name;

	@Column
	public String path;

	@Column
	public String status;

	@Column
	public int create_userid;

	@Column
	public String create_time;

	@Column
	public String type;
	@Column
	public String file_type;
	
	
	public String getFile_type() {
		return file_type;
	}

	public void setFile_type(String file_type) {
		this.file_type = file_type;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getAf_id() {
		return af_id;
	}

	public void setAf_id(int af_id) {
		this.af_id = af_id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
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
