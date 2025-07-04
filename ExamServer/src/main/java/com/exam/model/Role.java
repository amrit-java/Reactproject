package com.exam.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name="ROLES")
public class Role {
	@Id
	private Long roledId;
	private String roleName;
	@OneToMany(cascade = CascadeType.ALL,fetch=FetchType.LAZY,mappedBy = "role")
	private Set<UserRole> userRole= new HashSet<>();
	
	public Role() {
		
	}
		
	public Set<UserRole> getUserRole() {
		return userRole;
	}

	public void setUserRole(Set<UserRole> userRole) {
		this.userRole = userRole;
	}

	public Long getRoledId() {
		return roledId;
	}
	public void setRoledId(Long roledId) {
		this.roledId = roledId;
	}
	public String getRoleName() {
		return roleName;
	}
	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}
	public Role(Long roledId, String roleName) {
		super();
		this.roledId = roledId;
		this.roleName = roleName;
	}
     
}
 