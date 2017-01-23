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
import javax.faces.model.SelectItem;

import org.primefaces.context.RequestContext;

import br.enade.dao.InstitutionDAO;
import br.enade.dao.UserDAO;
import br.enade.model.Institution;
import br.enade.model.User;
import br.maissaude.util.Util;

/**
 *
 * @author Pedro Saraiva
 */
@ManagedBean(name = "buseradminmaster")
@ViewScoped
public class UserAdminMasterBean {

    private User user = new User();
    private String confirmPassword;
    private List<User> list;
    private List<SelectItem> institutionSelect;

    public List<SelectItem> getInstitutionSelect() {
        if (this.institutionSelect == null) {
            this.institutionSelect = new ArrayList<SelectItem>();

            InstitutionDAO dInstitution = new InstitutionDAO();
            List<Institution> categorias = dInstitution.list();
            this.showDataSelectInstitution(this.institutionSelect, categorias, "");
        }

        return institutionSelect;
    }

    private void showDataSelectInstitution(List<SelectItem> select, List<Institution> institutions, String prefixo) {

        SelectItem item = null;
        if (institutions != null) {
            for (Institution institution : institutions) {
                item = new SelectItem(institution, institution.getDescription());
                item.setEscape(false);

                select.add(item);

                //this.montaDadosSelect(select, usuario.getNome(), prefixo + "&nbsp;&nbsp;");
            }
        }
    }

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
        if(user.getLogin().equals("") || user.getIntitution() == null ||
        		user.getName().equals("") || user.getPassword().equals("")
        		|| confirmPassword.equals("")){
        	FacesContext.getCurrentInstance().
			addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
									"Preencha os campos Obrigatórios (*)",""));
			return "";
        }
        user.setName(user.getName().toUpperCase());
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
            this.list = uDAO.list();
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
    	if(user.getPermission()==null){
    		user.setPermission(new ArrayList<String>());
    	}
    	if(user.getPermission().contains(permissao)){
    		user.getPermission().remove(permissao);
    		FacesContext.getCurrentInstance().
				addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN,
							"Desabilitado usuário administrador: "+user.getName(),""));
    	} else {
    		user.getPermission().add(permissao);
    		FacesContext.getCurrentInstance().
				addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN,
						"Habilitado usuário administrador: "+user.getName(),""));
    	}
    	UserDAO uDAO = new UserDAO();
    	uDAO.update(user);
    	refresh();
    	return null;
    }


}
