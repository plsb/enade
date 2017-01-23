/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.maissaude.util;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 *
 * @author Pedro Saraiva
 */
public class Main {
    
    public static void main(String[] args) {
//        new HibernateUtil().getSessionFactory().openSession();
        String s1 = "p";
        MessageDigest md;
		try {
			md = MessageDigest.getInstance("MD5");
			BigInteger hash = new BigInteger(1, md.digest(s1.getBytes()));
	        String s2 = hash.toString(16);
	        System.out.println(s2);
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
    }
    
}
