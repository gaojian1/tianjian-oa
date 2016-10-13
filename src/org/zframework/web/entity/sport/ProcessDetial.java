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
 * 流程定义明细表
 * @author zhumin
 *
 */
@Entity
@Table(name = "pa_processdetial")
public class ProcessDetial implements Serializable {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "seq_pa_processdetial")
	@SequenceGenerator(name = "seq_pa_processdetial", sequenceName = "seq_pa_processdetial")
	@NotNull
	public int id;

	@Column
	public int processid;

	@Column
	public int processnodeid;

	@Column
	public int signroleid;

	@Column
	public int nextprocessdetialid;

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
		this.id = id;
	}

	public int getProcessid() {
		return processid;
	}

	public void setProcessid(int processid) {
		this.processid = processid;
	}

	public int getProcessnodeid() {
		return processnodeid;
	}

	public void setProcessnodeid(int processnodeid) {
		this.processnodeid = processnodeid;
	}

	public int getSignroleid() {
		return signroleid;
	}

	public void setSignroleid(int signroleid) {
		this.signroleid = signroleid;
	}

	public int getNextprocessdetialid() {
		return nextprocessdetialid;
	}

	public void setNextprocessdetialid(int nextprocessdetialid) {
		this.nextprocessdetialid = nextprocessdetialid;
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
