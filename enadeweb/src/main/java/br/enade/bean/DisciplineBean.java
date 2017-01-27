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
import br.enade.dao.DisciplineDAO;
import br.enade.model.Course;
import br.enade.model.Discipline;

/**
 *
 * @author Pedro Saraiva
 */
@ManagedBean(name = "bdiscipline")
@ViewScoped
public class DisciplineBean implements Serializable {

	private static final long serialVersionUID = 196309108760682508L;
	// private static final Logger logger =
	// LoggerFactory.getLogger(CourseBean.class.getName());

	private Discipline discipline = new Discipline();
	private DisciplineDAO dao = new DisciplineDAO();
	private List<Discipline> lista;
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

	public Discipline getDiscipline() {
		return discipline;
	}

	public void setDiscipline(Discipline discipline) {
		this.discipline = discipline;
	}

	public void save() {
		if (discipline.getDescription().equals("") || discipline.getCourse() == null) {
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, "Preencha os campos Obrigatórios (*)", ""));
			return;
		}

		discipline.setDescription(discipline.getDescription().toUpperCase());
		
		if (discipline.getId() == null) {

			if (!dao.findDiscipline("description", discipline.getDescription(), discipline.getCourse()).isEmpty()) {
				FacesContext.getCurrentInstance().addMessage(null,
						new FacesMessage(FacesMessage.SEVERITY_ERROR, "Descrição de Disciplina já Cadastrada", ""));
				return;
			} else if (dao.add(discipline)) {
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
						"Disciplina " + discipline.getDescription() + ", cadastrado com sucesso!", ""));
			}

		} else {
			if (!dao.findDiscipline("description", discipline.getDescription(), discipline.getCourse()).isEmpty()) {
				Discipline object = dao.findDiscipline("description", discipline.getDescription(), discipline.getCourse()).get(0);
				if (!object.getId().equals(discipline.getId())) {
					FacesContext.getCurrentInstance().addMessage(null,
							new FacesMessage(FacesMessage.SEVERITY_ERROR, "Descrição de Disciplina já Cadastrada", ""));

					return;
				}
			}
			if (dao.update(discipline)) {
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
						"Disciplina " + discipline.getDescription() + ", editada com sucesso!", ""));
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

	public String newDiscipline() {
		discipline = new Discipline();
		RequestContext context = RequestContext.getCurrentInstance();
		context.execute("PF('dlg').show()");
		return "";
	}

	public List<Discipline> getList() {
		if (lista == null) {
			lista = dao.list();
		}
		return new ArrayList<Discipline>(new HashSet<Discipline>(lista));
	}

	public String edit() {
		return "";
	}

	public String remove() {
		if (dao.remove(this.discipline)) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
					"Disciplina " + discipline.getDescription() + ", excluído com sucesso!", ""));

		} else {
			FacesContext.getCurrentInstance()
					.addMessage(null,
							new FacesMessage(
									FacesMessage.SEVERITY_ERROR, "Não foi possível excluir a disciplina "
											+ discipline.getDescription() + ", verifique a existência de relacionamentos",
									""));
		}

		lista = null;
		refresh();
		return null;
	}
}
