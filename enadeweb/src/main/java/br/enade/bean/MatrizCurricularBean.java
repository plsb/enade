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
import br.enade.dao.MatrizCurricularDAO;
import br.enade.dao.SemesterDAO;
import br.enade.model.Course;
import br.enade.model.MatrizCurricular;
import br.enade.model.Semester;

/**
 *
 * @author Pedro Saraiva
 */
@ManagedBean(name = "bmatriz")
@ViewScoped
public class MatrizCurricularBean implements Serializable {

	private static final long serialVersionUID = 196309108760682508L;
	// private static final Logger logger =
	// LoggerFactory.getLogger(CourseBean.class.getName());

	private MatrizCurricular matriz = new MatrizCurricular();
	private MatrizCurricularDAO dao = new MatrizCurricularDAO();
	private List<MatrizCurricular> lista;
	private List<SelectItem> courseSelect, semesterSelect, teacherSelect, disciplineSelect;

	public List<SelectItem> getCourseSelect() {
		if (this.courseSelect == null) {
			this.courseSelect = new ArrayList<SelectItem>();

			CourseDAO dao = new CourseDAO();
			
			List<Course> categorias = new ArrayList<Course>(new HashSet<Course>(dao.listByCoordActive()));
			this.showDataSelectCourse(this.courseSelect, categorias, "");
		}

		return courseSelect;
	}

	private void showDataSelectCourse(List<SelectItem> select, List<Course> lista, String prefixo) {

        SelectItem item = null;
        if (lista != null) {
            for (Course object : lista) {
	                item = new SelectItem(object, object.getDescription());
	                item.setEscape(false);
	
	                select.add(item);

            }
        }
    }
	
	public List<SelectItem> getSemesterSelect() {
//		if (this.semesterSelect == null) {
			this.semesterSelect = new ArrayList<SelectItem>();

			SemesterDAO dao = new SemesterDAO();
			
			List<Semester> categorias = new ArrayList<Semester>(new HashSet<Semester>(dao.listByCourse(matriz.getCourse())));
			this.showDataSelectSemester(this.semesterSelect, categorias, "");
//		}

		return semesterSelect;
	}

	private void showDataSelectSemester(List<SelectItem> select, List<Semester> lista, String prefixo) {

        SelectItem item = null;
        if (lista != null) {
            for (Semester object : lista) {
	                item = new SelectItem(object, object.getDescription());
	                item.setEscape(false);
	
	                select.add(item);

            }
        }
    }
	
	public MatrizCurricular getMatriz() {
		return matriz;
	}

	public void setMatriz(MatrizCurricular matriz) {
		this.matriz = matriz;
	}

	public void save() {
		if (matriz.getDescription().equals("") || matriz.getCourse() == null) {
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, "Preencha os campos Obrigatórios (*)", ""));
			return;
		}

		matriz.setDescription(matriz.getDescription().toUpperCase());
		
		if (matriz.getId() == null) {

			if (!dao.findSemester("description", matriz.getDescription(), matriz.getCourse()).isEmpty()) {
				FacesContext.getCurrentInstance().addMessage(null,
						new FacesMessage(FacesMessage.SEVERITY_ERROR, "Descrição de Matriz já Cadastrada", ""));
				return;
			} else if (dao.add(matriz)) {
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
						"Matriz " + matriz.getDescription() + ", cadastrado com sucesso!", ""));
			}

		} else {
			if (!dao.findSemester("description", matriz.getDescription(), matriz.getCourse()).isEmpty()) {
				MatrizCurricular object = dao.findSemester("description", matriz.getDescription(), matriz.getCourse()).get(0);
				if (!object.getId().equals(matriz.getId())) {
					FacesContext.getCurrentInstance().addMessage(null,
							new FacesMessage(FacesMessage.SEVERITY_ERROR, "Descrição de Matriz já Cadastrada", ""));

					return;
				}
			}
			if (dao.update(matriz)) {
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
						"Matriz " + matriz.getDescription() + ", editada com sucesso!", ""));
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

	public String newMatriz() {
		matriz = new MatrizCurricular();
		RequestContext context = RequestContext.getCurrentInstance();
		context.execute("PF('dlg').show()");
		return "";
	}

	public List<MatrizCurricular> getList() {
		if (lista == null) {
			lista = dao.list();
		}
		return new ArrayList<MatrizCurricular>(new HashSet<MatrizCurricular>(lista));
	}

	public String edit() {
		return "";
	}

	public String remove() {
		if (dao.remove(this.matriz)) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
					"Matriz " + matriz.getDescription() + ", excluída com sucesso!", ""));

		} else {
			FacesContext.getCurrentInstance()
					.addMessage(null,
							new FacesMessage(
									FacesMessage.SEVERITY_ERROR, "Não foi possível excluir a matriz "
											+ matriz.getDescription() + ", verifique a existência de relacionamentos",
									""));
		}

		lista = null;
		refresh();
		return null;
	}
}
