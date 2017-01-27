package br.enade.dao;

import java.util.Collection;
import java.util.List;

import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.enade.bean.ContextBean;
import br.enade.model.Course;
import br.enade.model.Semester;
import br.maissaude.util.GenericDAO;
import br.maissaude.util.HibernateUtil;

public class SemesterDAO extends GenericDAO<Semester>{

	public SemesterDAO() {
		super(Semester.class);
	}

	public List<Semester> findSemester(String campo, Object valor, Course course) {
		List<Semester> lista = null;
        try {
            this.setSessao(HibernateUtil.getSessionFactory().openSession());
            setTransacao(getSessao().beginTransaction());
            ContextBean cb = new ContextBean();
            lista = this.getSessao().createCriteria(Semester.class).
                    add(Restrictions.eq(campo, valor)).
                    add(Restrictions.eq("course", course)).
                    addOrder(Order.asc("description")).
                    list();

        } catch (Exception e) {
            if (getTransacao().isActive()) {
                getTransacao().rollback();
            }
            System.out.println("Não foi possível listar: " + e.getMessage());
        } finally {
            getSessao().close();
        }
        return lista;
	}

	public List<Semester> listByCourse(Course course) {
		List<Semester> lista = null;
        try {
            this.setSessao(HibernateUtil.getSessionFactory().openSession());
            setTransacao(getSessao().beginTransaction());
            lista = this.getSessao().createCriteria(Semester.class).
                    add(Restrictions.eq("course", course)).
                    addOrder(Order.asc("description")).
                    list();

        } catch (Exception e) {
            if (getTransacao().isActive()) {
                getTransacao().rollback();
            }
            System.out.println("Não foi possível listar: " + e.getMessage());
        } finally {
            getSessao().close();
        }
        return lista;
	}

}
