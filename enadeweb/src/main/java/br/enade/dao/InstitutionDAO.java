/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.enade.dao;


import java.util.List;

import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.enade.model.Institution;
import br.maissaude.util.GenericDAO;
import br.maissaude.util.HibernateUtil;

/**
 *
 * @author Pedro Saraiva
 */
public class InstitutionDAO extends GenericDAO<Institution> {

    public InstitutionDAO() {
        super(Institution.class);
    }
    
    public List<Institution> findInstitution(String campo, Object valor) {
        List<Institution> lista = null;
        try {
            this.setSessao(HibernateUtil.getSessionFactory().openSession());
            setTransacao(getSessao().beginTransaction());
            lista = this.getSessao().createCriteria(Institution.class).
                    add(Restrictions.eq(campo, valor)).
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
