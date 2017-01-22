/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.enade.bean;

import java.io.Serializable;
import java.util.List;

import javax.faces.application.Application;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;

import org.primefaces.context.RequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.enade.dao.InstitutionDAO;
import br.enade.model.Institution;

/**
 *
 * @author Pedro Saraiva
 */
@ManagedBean(name = "binstitution")
@RequestScoped
public class InstitutionBean implements Serializable {

	private static final long serialVersionUID = 196309108760682508L;
	
	private Institution institution = new Institution();
	private InstitutionDAO dInstitution = new InstitutionDAO();
	private List<Institution> lista;
	
	public Institution getInstitution() {
		return institution;
	}

	public void setInstitution(Institution institution) {
		this.institution = institution;
	}

	static final Logger logger = LoggerFactory.getLogger(InstitutionBean.class.getName());
	
	public void save() {
		if (institution.getDescription().equals("") ) {
			FacesContext.getCurrentInstance().
					addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
											"Preencha os campos Obrigatórios (*)",""));
			return ;
		}
		
		institution.setDescription(institution.getDescription().toUpperCase());
		if (institution.getId() == null) {
			institution.setActive(true);
			if(!dInstitution.findInstitution("description", institution.getDescription()).isEmpty()){
				FacesContext.getCurrentInstance().
				addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
										"Descrição de Instituição já Cadastrada",""));
				return ;
			} else  if (dInstitution.add(institution)){
				FacesContext.getCurrentInstance().
				addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
						"Instituição "
								+ institution.getDescription()
								+ ", cadastrada com sucesso!",""));
			}

		} else {
			if(!dInstitution.findInstitution("description", institution.getDescription()).isEmpty()){
				Institution institutionCadas = dInstitution.findInstitution("description", institution.getDescription()).get(0);
				if(!institutionCadas.getId().equals(institution.getId())){
					FacesContext.getCurrentInstance().
					addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
							"Descrição de Instituição já Cadastrada",""));

					return ;
				}	
			} 
			if (dInstitution.update(institution)){
				FacesContext.getCurrentInstance().
					addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
						"Instituição "
								+ institution.getDescription()
								+ ", editada com sucesso!",""));
			}

		}

		lista = null;
		refresh();
		RequestContext context = RequestContext.getCurrentInstance();
		context.execute("PF('dlg').hide()");
		return ;
	}
	
	public void refresh() {  
        FacesContext context = FacesContext.getCurrentInstance();  
        Application application = context.getApplication();  
        javax.faces.application.ViewHandler viewHandler = application.getViewHandler();  
        UIViewRoot viewRoot = viewHandler.createView(context, context.getViewRoot().getViewId());  
        context.setViewRoot(viewRoot);  
        context.renderResponse();  
    }
	
	public String newInstituiton() {
		institution = new Institution();
		institution.setActive(true);
		RequestContext context = RequestContext.getCurrentInstance();
		context.execute("PF('dlg').show()");
		return "";
	}
	
	public List<Institution> getList() {
		if(lista==null){
			lista = dInstitution.list();
		}		
		return lista;
	}

	public String edit() {
		return "";
	}

	public String remove() {
		if (dInstitution.remove(this.institution)){
			FacesContext.getCurrentInstance().
			addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
					"Instituição " + institution.getDescription()
					+ ", excluída com sucesso!",""));
			
		} else {
			FacesContext.getCurrentInstance().
			addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"Não foi possível excluir a instituição " + institution.getDescription()
					+ ", verifique a existência de relacionamentos",""));
		}

		lista=null;
		refresh();
		return null;
	}
}
