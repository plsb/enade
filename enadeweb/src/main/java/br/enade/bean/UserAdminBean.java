/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.enade.bean;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import javax.faces.application.Application;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import org.primefaces.context.RequestContext;
import br.enade.dao.UserDAO;
import br.enade.model.User;
import br.maissaude.util.Util;

/**
 *
 * @author Pedro Saraiva
 */
@ManagedBean(name = "buseradmin")
@ViewScoped
public class UserAdminBean {

    private User user = new User();
    private String confirmPassword;
    private List<User> list;
    
    public String assignsPermission(User u, String permission) {
        this.user = u;
        List<String> permissions = this.user.getPermission();
        if (permissions.contains(permission)) {
            permissions.remove(permission);
        } else {
            permissions.add(permission);
        }
        return null;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public String newUser() {
        this.user = new User();
        this.user.setActive(true);
        RequestContext context = RequestContext.getCurrentInstance();
		context.execute("PF('dlg').show()");
        return "";
    }

    public String edit() {
        return "";
    }

    public String save() {
        if(user.getLogin().equals("") || 
        		user.getName().equals("") || user.getPassword().equals("")
        		|| confirmPassword.equals("")){
        	FacesContext.getCurrentInstance().
			addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
									"Preencha os campos Obrigatórios (*)",""));
			return "";
        }
        ContextBean cb = new ContextBean();
        
        user.setName(user.getName().toUpperCase());
        user.setIntitution(cb.getUser().getIntitution());
        String senha = this.user.getPassword();
        if (!senha.equals(this.confirmPassword)) {
        	FacesContext.getCurrentInstance().
			addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN,
							"A senha não foi confirmada corretamente",""));
         
            return null;

        }
        user.setPassword(Util.md5(user.getPassword()));
        UserDAO uDAO = new UserDAO();
        if (user.getId() == null) {
        	user.setActive(true);
            
        	if(!uDAO.checkExists("login", user.getLogin()).isEmpty()){
        		FacesContext.getCurrentInstance().
    			addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
    					"Login já Cadastrado",""));

				return "";
        	} else if(uDAO.add(user)){
        		FacesContext.getCurrentInstance().
    			addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
    					"Usuário "
								+ user.getName()
								+ ", cadastrado com sucesso!",""));
        	}
        } else {
        	if(!uDAO.checkExists("login", user.getLogin()).isEmpty()){
        		User userCad = uDAO.checkExists("login", user.getLogin()).get(0);
        		if(!userCad.getId().equals(user.getId())){
        			FacesContext.getCurrentInstance().
        			addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
        					"Login já Cadastrado",""));
					return "";
        		}
        	}
        	if(uDAO.update(this.user)){
        		FacesContext.getCurrentInstance().
    			addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
    					"Usuário "
								+ user.getName()
								+ ", editado com sucesso!",""));
        		
        	}
           
        }
        list = null;
		RequestContext context = RequestContext.getCurrentInstance();
		context.execute("PF('dlg').hide()");
		refresh();
		return "";
    }

    public String remove() {
        UserDAO uDAO = new UserDAO();
        if(uDAO.remove(this.user)){
        	FacesContext.getCurrentInstance().
			addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
					"Usuário " + user.getName()
					+ ", excluído com sucesso!",""));
			
		} else {
			FacesContext.getCurrentInstance().
			addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"Não foi possível excluir o usuário " + user.getName()
					+ ", verifique a existência de relacionamentos ",""));
		}
        list=null;
		refresh();
        return null;
    }

    public String active() {
        if (this.user.isActive()) {
            this.user.setActive(false);
        } else {
            this.user.setActive(true);
        }

        UserDAO uDAO = new UserDAO();
        uDAO.add(this.user);
        return null;
    }

    public List<User> getList() {
        if (this.list == null) {
            UserDAO uDAO = new UserDAO();
            this.list = uDAO.listByInstitution();
        }
        return new ArrayList<User>(new HashSet<User>(list));
    }
    
    public void refresh() {  
        FacesContext context = FacesContext.getCurrentInstance();  
        Application application = context.getApplication();  
        javax.faces.application.ViewHandler viewHandler = application.getViewHandler();  
        UIViewRoot viewRoot = viewHandler.createView(context, context.getViewRoot().getViewId());  
        context.setViewRoot(viewRoot);  
        context.renderResponse();  
    }
    
    public String habilitaDesabilita(String permissao){
    	String stringPermissao = "";
    	switch (permissao) {
			case "ROLE_ADMIN_MASTER":
				stringPermissao = "Administrador Master";
				break;
			case "ROLE_ADMIN":
				stringPermissao = "Administrador";
				break;
			case "ROLE_PROF":
				stringPermissao = "Professor";
				break;
			case "ROLE_COORD_ADMIN":
				stringPermissao = "Corrdenação Administrativa";
				break;

		default:
			break;
		}
    	if(user.getPermission()==null){
    		user.setPermission(new ArrayList<String>());
    	}
    	if(user.getPermission().contains(permissao)){
    		user.getPermission().remove(permissao);
    		FacesContext.getCurrentInstance().
				addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN,
							"Desabilitado "+stringPermissao+": "+user.getName(),""));
    	} else {
    		user.getPermission().add(permissao);
    		FacesContext.getCurrentInstance().
				addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN,
						"Habilitado "+stringPermissao+": "+user.getName(),""));
    	}
    	UserDAO uDAO = new UserDAO();
    	uDAO.update(user);
    	refresh();
    	return null;
    }


}
