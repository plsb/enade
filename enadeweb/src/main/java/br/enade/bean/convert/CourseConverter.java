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

import br.enade.dao.CourseDAO;
import br.enade.model.Course;

/**
 *
 * @author Pedro Saraiva
 */
@FacesConverter(forClass = Course.class)
public class CourseConverter implements Converter {

    @Override
    public Object getAsObject(FacesContext contex, UIComponent componente, String value) {
        if (value != null && value.trim().length() > 0) {
            Long codigo = Long.valueOf(value);
            try {
            	CourseDAO dao = new CourseDAO();
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
        	Course object = (Course) value;
            return object.getId().toString();
        }
        return "";
    }

}
