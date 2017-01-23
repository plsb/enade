/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.enade.bean;

import java.io.Serializable;
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
import br.enade.dao.CourseDAO;
import br.enade.dao.UserDAO;
import br.enade.model.Course;
import br.enade.model.User;

/**
 *
 * @author Pedro Saraiva
 */
@ManagedBean(name = "bcourse")
@ViewScoped
public class CourseBean implements Serializable {

	private static final long serialVersionUID = 196309108760682508L;
	// private static final Logger logger =
	// LoggerFactory.getLogger(CourseBean.class.getName());

	private Course course = new Course();
	private CourseDAO dao = new CourseDAO();
	private List<Course> lista;
	private List<SelectItem> userSelect;

	public List<SelectItem> getUserSelect() {
		if (this.userSelect == null) {
			this.userSelect = new ArrayList<SelectItem>();

			UserDAO dao = new UserDAO();

			List<User> categorias = dao.listByInstitution();
			this.showDataSelect(this.userSelect, categorias, "");
		}

		return userSelect;
	}

	private void showDataSelect(List<SelectItem> select, List<User> lista, String prefixo) {

        SelectItem item = null;
        if (lista != null) {
            for (User object : lista) {
            	if(object.getRoleCoordAdministrativo()){
	                item = new SelectItem(object, object.getName());
	                item.setEscape(false);
	
	                select.add(item);
            	}

                //this.montaDadosSelect(select, usuario.getNome(), prefixo + "&nbsp;&nbsp;");
            }
        }
    }

	public Course getCourse() {
		return course;
	}

	public void setCourse(Course course) {
		this.course = course;
	}

	public void save() {
		if (course.getDescription().equals("") || course.getCoordAdmin() == null) {
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, "Preencha os campos Obrigatórios (*)", ""));
			return;
		}
		ContextBean cb = new ContextBean();

		course.setDescription(course.getDescription().toUpperCase());
		course.setInstitution(cb.getUser().getIntitution());
		if (course.getId() == null) {

			if (!dao.findCourse("description", course.getDescription()).isEmpty()) {
				FacesContext.getCurrentInstance().addMessage(null,
						new FacesMessage(FacesMessage.SEVERITY_ERROR, "Descrição de Curso já Cadastrada", ""));
				return;
			} else if (dao.add(course)) {
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
						"Curso " + course.getDescription() + ", cadastrado com sucesso!", ""));
			}

		} else {
			if (!dao.findCourse("description", course.getDescription()).isEmpty()) {
				Course object = dao.findCourse("description", course.getDescription()).get(0);
				if (!object.getId().equals(course.getId())) {
					FacesContext.getCurrentInstance().addMessage(null,
							new FacesMessage(FacesMessage.SEVERITY_ERROR, "Descrição de Curso já Cadastrada", ""));

					return;
				}
			}
			if (dao.update(course)) {
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
						"Curso " + course.getDescription() + ", editado com sucesso!", ""));
			}

		}

		lista = null;
		refresh();
		RequestContext context = RequestContext.getCurrentInstance();
		context.execute("PF('dlg').hide()");
		return;
	}

	public void refresh() {
		FacesContext context = FacesContext.getCurrentInstance();
		Application application = context.getApplication();
		javax.faces.application.ViewHandler viewHandler = application.getViewHandler();
		UIViewRoot viewRoot = viewHandler.createView(context, context.getViewRoot().getViewId());
		context.setViewRoot(viewRoot);
		context.renderResponse();
	}

	public String newCourse() {
		course = new Course();
		RequestContext context = RequestContext.getCurrentInstance();
		context.execute("PF('dlg').show()");
		return "";
	}

	public List<Course> getList() {
		if (lista == null) {
			lista = dao.list();
		}
		return new ArrayList<Course>(new HashSet<Course>(lista));
	}

	public String edit() {
		return "";
	}

	public String remove() {
		if (dao.remove(this.course)) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
					"Curso " + course.getDescription() + ", excluído com sucesso!", ""));

		} else {
			FacesContext.getCurrentInstance()
					.addMessage(null,
							new FacesMessage(
									FacesMessage.SEVERITY_ERROR, "Não foi possível excluir o curso "
											+ course.getDescription() + ", verifique a existência de relacionamentos",
									""));
		}

		lista = null;
		refresh();
		return null;
	}
}
