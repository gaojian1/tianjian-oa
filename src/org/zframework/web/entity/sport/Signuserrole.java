package org.zframework.web.entity.sport;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.zframework.web.entity.system.User;

/**
 * 签核权限用户角色关系表
 * 
 * @author mzhu
 *
 *         2016年1月9日 下午9:39:03
 */
@Entity
@Table(name = "pa_signuserrole")
public class Signuserrole implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "seq_pa_signuserrole")
	@SequenceGenerator(name = "seq_pa_signuserrole", sequenceName = "seq_pa_signuserrole")
	@NotNull
	public int id;

	// @Column
	// public int user_id;
	@ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH }, fetch = FetchType.EAGER)
	@JoinColumn(name = "user_id")
	public User user;

	// @Column
	// public int signrole_id;

	@OneToOne
	// JPA注释： 一对一 关系
	@JoinColumn(name = "signrole_id")
	public Signrole signrole;

	@Column
	public int status;

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

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Signrole getSignrole() {
		return signrole;
	}

	public void setSignrole(Signrole signrole) {
		this.signrole = signrole;
	}

	// public int getSignrole_id() {
	// return signrole_id;
	// }
	//
	// public void setSignrole_id(int signrole_id) {
	// this.signrole_id = signrole_id;
	// }

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
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