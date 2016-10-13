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
@Table(name="pa_process")
public class Process implements Serializable{

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO,generator="seq_pa_process")
	@SequenceGenerator(name="seq_pa_process",sequenceName="seq_pa_process")
	@NotNull
	public int id;

	@Column
	public String process_name;

	@Column
	public String process_desc;

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

	public String getProcess_name() {
		return process_name;
	}

	public void setProcess_name(String process_name) {
		this.process_name=process_name;
	}

	public String getProcess_desc() {
		return process_desc;
	}

	public void setProcess_desc(String process_desc) {
		this.process_desc=process_desc;
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
