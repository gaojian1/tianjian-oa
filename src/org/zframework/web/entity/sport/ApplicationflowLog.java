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
import javax.persistence.Transient;

/**
 * 流水日志表
 * 
 * @author mzhu
 *
 *         2016年1月16日 下午5:31:26
 */
@Entity
@Table(name="pa_applicationflow_log")
public class ApplicationflowLog implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "seq_pa_applicationflow_log")
	@SequenceGenerator(name = "seq_pa_applicationflow_log", sequenceName = "seq_pa_applicationflow_log")
	@NotNull
	public int id;

	@Column
	public int af_id;
	@Column
	public int processstep_id;
	@Column
	public String auditstatus;
	@Column
	public int audit_userid;
	@Column
	public String audit_time;
	@Column
	public String reason;
	@Column
	public int processid;
	@Transient
	public String processStr;
	@Transient
	public String aduitStatusStr;
	@Transient
	public String auditUserStr;
	@Transient
	public String createUserStr;
	@Transient
	public String sqType;
	@Transient
	public  String formNumber;
	
	
	
	public int getProcessid() {
		return processid;
	}

	public void setProcessid(int processid) {
		this.processid = processid;
	}

	public String getFormNumber() {
		return formNumber;
	}

	public void setFormNumber(String formNumber) {
		this.formNumber = formNumber;
	}

	public String getAduitStatusStr() {
		return aduitStatusStr;
	}

	public void setAduitStatusStr(String aduitStatusStr) {
		this.aduitStatusStr = aduitStatusStr;
	}

	public String getAuditUserStr() {
		return auditUserStr;
	}

	public void setAuditUserStr(String auditUserStr) {
		this.auditUserStr = auditUserStr;
	}

	public String getCreateUserStr() {
		return createUserStr;
	}

	public void setCreateUserStr(String createUserStr) {
		this.createUserStr = createUserStr;
	}

	public String getSqType() {
		return sqType;
	}

	public void setSqType(String sqType) {
		this.sqType = sqType;
	}

	public String getProcessStr() {
		return processStr;
	}

	public void setProcessStr(String processStr) {
		this.processStr = processStr;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getAf_id() {
		return af_id;
	}

	public void setAf_id(int af_id) {
		this.af_id = af_id;
	}

	public int getProcessstep_id() {
		return processstep_id;
	}

	public void setProcessstep_id(int processstep_id) {
		this.processstep_id = processstep_id;
	}

	public String getAuditstatus() {
		return auditstatus;
	}

	public void setAuditstatus(String auditstatus) {
		this.auditstatus = auditstatus;
	}

	public int getAudit_userid() {
		return audit_userid;
	}

	public void setAudit_userid(int audit_userid) {
		this.audit_userid = audit_userid;
	}

	public String getAudit_time() {
		return audit_time;
	}

	public void setAudit_time(String audit_time) {
		this.audit_time = audit_time;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

}
