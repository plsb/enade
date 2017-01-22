/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.enade.dao;

import br.enade.model.User;
import br.maissaude.util.GenericDAO;

/**
 *
 * @author Pedro Saraiva
 */
public class UserDAO extends GenericDAO<User>{

    public UserDAO() {
        super(User.class);
    }
    
    
    
}
