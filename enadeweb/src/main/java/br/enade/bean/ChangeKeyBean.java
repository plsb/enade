/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.enade.bean;


import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import br.enade.dao.UserDAO;
import br.enade.model.User;
import br.maissaude.util.Util;

/**
 *
 * @author Pedro Saraiva
 */
@ManagedBean(name = "bchangeKey")
@ViewScoped
public class ChangeKeyBean {

    private String senhaAnterior, novaSenha, confirmarSenha;

	public String getSenhaAnterior() {
		return senhaAnterior;
	}

	public void setSenhaAnterior(String senhaAnterior) {
		this.senhaAnterior = senhaAnterior;
	}

	public String getNovaSenha() {
		return novaSenha;
	}

	public void setNovaSenha(String novaSenha) {
		this.novaSenha = novaSenha;
	}

	public String getConfirmarSenha() {
		return confirmarSenha;
	}

	public void setConfirmarSenha(String confirmarSenha) {
		this.confirmarSenha = confirmarSenha;
	}
    
	public String save(){
		if(senhaAnterior.equals("")||confirmarSenha.equals("")||novaSenha.equals("")){
			FacesContext.getCurrentInstance().
			addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
									"Preencha os campos Obrigatórios (*)",""));
			return "";
		}
		ContextBean cb = new ContextBean();
		User user = cb.getUser();
		String senhaAnteriorCriptografada = Util.md5(senhaAnterior);
		if(!senhaAnteriorCriptografada.equals(user.getPassword())){
			FacesContext.getCurrentInstance().
			addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
									"Senha Anterior Incorreta!",""));
			return "";
		}
		if(!novaSenha.equals(confirmarSenha)){
			FacesContext.getCurrentInstance().
				addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
									"Senha não foi confirmada corretamente!",""));
			return "";
		}
		if(novaSenha.length()<6){
			FacesContext.getCurrentInstance().
			addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
								"Senha deve possuir mais que 6 caracteres",""));
			return "";
		}
		UserDAO uDAO = new UserDAO();
		user.setPassword(Util.md5(novaSenha));
		uDAO.update(user);
		
		FacesContext.getCurrentInstance().
			addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
							"Senha alterada com sucesso!",""));
		novaSenha = null;
		senhaAnterior = null;
		confirmarSenha = null;
		return "";
	}
}
