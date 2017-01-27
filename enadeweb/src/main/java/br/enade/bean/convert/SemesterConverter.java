/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.enade.bean.convert;


import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;

import br.enade.dao.SemesterDAO;
import br.enade.model.Semester;


/**
 *
 * @author Pedro Saraiva
 */
@FacesConverter(forClass = Semester.class)
public class SemesterConverter implements Converter {

    @Override
    public Object getAsObject(FacesContext contex, UIComponent componente, String value) {
        if (value != null && value.trim().length() > 0) {
            Long codigo = Long.valueOf(value);
            try {
                SemesterDAO dao = new SemesterDAO();
                return dao.checkExists("id", codigo).get(0);
            } catch (Exception e) {
                throw new ConverterException("Não foi possível encontrar o código " + value + "." + e.getMessage());
            }
        }
        return null;
    }

    @Override
    public String getAsString(FacesContext arg0, UIComponent arg1, Object value) {
        if (value != null) {
        	Semester object = (Semester) value;
            return object.getId().toString();
        }
        return "";
    }

}
