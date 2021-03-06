/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.enade.dao;

import java.util.ArrayList;
import java.util.List;

import javax.jws.soap.SOAPBinding.Use;

import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.enade.bean.ContextBean;
import br.enade.model.Course;
import br.enade.model.User;
import br.maissaude.util.GenericDAO;
import br.maissaude.util.HibernateUtil;

/**
 *
 * @author Pedro Saraiva
 */
public class UserDAO extends GenericDAO<User>{

    public UserDAO() {
        super(User.class);
    }

	public List<User> listByInstitution() {
		List<User> lista = new ArrayList<User>();
        try {
            this.setSessao(HibernateUtil.getSessionFactory().openSession());
            setTransacao(getSessao().beginTransaction());
            ContextBean cb = new ContextBean();
            lista = this.getSessao().createCriteria(User.class).
                    add(Restrictions.eq("intitution", cb.getUser().getIntitution())).
                    addOrder(Order.asc("name")).
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
