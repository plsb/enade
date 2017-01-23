package br.enade.dao;

import java.util.List;

import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import br.enade.bean.ContextBean;
import br.enade.model.Course;
import br.maissaude.util.GenericDAO;
import br.maissaude.util.HibernateUtil;

public class CourseDAO extends GenericDAO<Course> {

	public CourseDAO() {
		super(Course.class);
	}

	public List<Course> findCourse(String campo, Object valor) {
		List<Course> lista = null;
        try {
            this.setSessao(HibernateUtil.getSessionFactory().openSession());
            setTransacao(getSessao().beginTransaction());
            ContextBean cb = new ContextBean();
            lista = this.getSessao().createCriteria(Course.class).
                    add(Restrictions.eq(campo, valor)).
                    add(Restrictions.eq("institution", cb.getUser().getIntitution())).
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
