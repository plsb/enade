/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.enade.model;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 *
 * @author Pedro Saraiva
 */
@Entity
@Table(name = "user")
public class User implements Serializable {
    
	private static final long serialVersionUID = -2268058922183899685L;
	
	@Id
    @GeneratedValue
    private Long id;

    @Column(length = 150, nullable = false)
    private String name;
    
    @Column(length = 50, nullable = false)
    private String login;

    @Column(length = 50, nullable = false)
    private String password;

    @ManyToOne
    private Institution intitution;
    
    private boolean active;

    @ElementCollection(targetClass = String.class, fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_permission",
            uniqueConstraints = {
                @UniqueConstraint(columnNames = {"user", "permission"})},
            joinColumns = @JoinColumn(name = "user"))
    @Column(name = "permission", length = 50)
    private List<String> permission;// = new HashSet<String>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Institution getIntitution() {
		return intitution;
	}

	public void setIntitution(Institution intitution) {
		this.intitution = intitution;
	}

	public boolean isActive() {
        return active;
    }
    
    public String getStringAtivo(){
    	if(isActive()){
    		return "Ativo";
    	} else {
    		return "Inativo";
    	}
    	
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public List<String> getPermission() {
        return permission;
    }

    public void setPermission(List<String> permission) {
        this.permission = permission;
    }
    
    public boolean getRoleAdmin(){
    	if(permission!=null){
    		return permission.contains("ROLE_ADMIN");
    	}
    	return false;
    }
    
    public boolean getRoleCoordAdministrativo(){
    	if(permission!=null){
    		return permission.contains("ROLE_COORD_ADMIN");
    	}
    	return false;
    }
    
    public boolean getRoleProfessor(){
    	if(permission!=null){
    		return permission.contains("ROLE_PROF");
    	}
    	return false;
    }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		User other = (User) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

        

}
