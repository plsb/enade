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
import br.enade.dao.SemesterDAO;
import br.enade.model.Course;
import br.enade.model.Semester;

/**
 *
 * @author Pedro Saraiva
 */
@ManagedBean(name = "bsemester")
@ViewScoped
public class SemesterBean implements Serializable {

	private static final long serialVersionUID = 196309108760682508L;
	// private static final Logger logger =
	// LoggerFactory.getLogger(CourseBean.class.getName());

	private Semester semester = new Semester();
	private SemesterDAO dao = new SemesterDAO();
	private List<Semester> lista;
	private List<SelectItem> courseSelect;

	public List<SelectItem> getCourseSelect() {
		if (this.courseSelect == null) {
			this.courseSelect = new ArrayList<SelectItem>();

			CourseDAO dao = new CourseDAO();
			
			List<Course> categorias = new ArrayList<Course>(new HashSet<Course>(dao.listByCoordActive()));
			this.showDataSelect(this.courseSelect, categorias, "");
		}

		return courseSelect;
	}

	private void showDataSelect(List<SelectItem> select, List<Course> lista, String prefixo) {

        SelectItem item = null;
        if (lista != null) {
            for (Course object : lista) {
	                item = new SelectItem(object, object.getDescription());
	                item.setEscape(false);
	
	                select.add(item);

            }
        }
    }

	public Semester getSemester() {
		return semester;
	}

	public void setSemester(Semester semester) {
		this.semester = semester;
	}

	public void save() {
		if (semester.getDescription().equals("") || semester.getCourse() == null) {
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, "Preencha os campos Obrigatórios (*)", ""));
			return;
		}

		semester.setDescription(semester.getDescription().toUpperCase());
		
		if (semester.getId() == null) {

			if (!dao.findSemester("description", semester.getDescription(), semester.getCourse()).isEmpty()) {
				FacesContext.getCurrentInstance().addMessage(null,
						new FacesMessage(FacesMessage.SEVERITY_ERROR, "Descrição de Semestre já Cadastrada", ""));
				return;
			} else if (dao.add(semester)) {
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
						"Semestre " + semester.getDescription() + ", cadastrado com sucesso!", ""));
			}

		} else {
			if (!dao.findSemester("description", semester.getDescription(), semester.getCourse()).isEmpty()) {
				Semester object = dao.findSemester("description", semester.getDescription(), semester.getCourse()).get(0);
				if (!object.getId().equals(semester.getId())) {
					FacesContext.getCurrentInstance().addMessage(null,
							new FacesMessage(FacesMessage.SEVERITY_ERROR, "Descrição de Semestre já Cadastrada", ""));

					return;
				}
			}
			if (dao.update(semester)) {
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
						"Semestre " + semester.getDescription() + ", editado com sucesso!", ""));
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

	public String newSemester() {
		semester = new Semester();
		RequestContext context = RequestContext.getCurrentInstance();
		context.execute("PF('dlg').show()");
		return "";
	}

	public List<Semester> getList() {
		if (lista == null) {
			lista = dao.list();
		}
		return new ArrayList<Semester>(new HashSet<Semester>(lista));
	}

	public String edit() {
		return "";
	}

	public String remove() {
		if (dao.remove(this.semester)) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
					"Semestre " + semester.getDescription() + ", excluído com sucesso!", ""));

		} else {
			FacesContext.getCurrentInstance()
					.addMessage(null,
							new FacesMessage(
									FacesMessage.SEVERITY_ERROR, "Não foi possível excluir o semestre "
											+ semester.getDescription() + ", verifique a existência de relacionamentos",
									""));
		}

		lista = null;
		refresh();
		return null;
	}
}
