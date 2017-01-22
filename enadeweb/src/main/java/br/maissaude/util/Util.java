/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.maissaude.util;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JRootPane;
import javax.swing.KeyStroke;

/**
 *
 * @author Pedro Saraiva
 */
public class Util {

    JRootPane meurootpane;

    public static String getVersao() {
        return "1.0";
    }

    /**
     * Realiza a validaÃ§Ã£o do CPF.
     *
     * @param strCPF nÃºmero de CPF a ser validado
     * @return true se o CPF Ã© vÃ¡lido e false se nÃ£o Ã© vÃ¡lido
     */
    public static boolean CPF(String strCpf) {
        if (strCpf.equals("")) {
            return false;
        }

        if (strCpf.equals("00000000000") || strCpf.equals("11111111111")
                || strCpf.equals("22222222222") || strCpf.equals("33333333333")
                || strCpf.equals("44444444444") || strCpf.equals("55555555555")
                || strCpf.equals("66666666666") || strCpf.equals("77777777777")
                || strCpf.equals("88888888888") || strCpf.equals("99999999999")
                || (strCpf.length() != 11)) {
            return (false);
        }

        int d1, d2;
        int digito1, digito2, resto;
        int digitoCPF;
        String nDigResult;

        d1 = d2 = 0;
        digito1 = digito2 = resto = 0;

        for (int nCount = 1; nCount < strCpf.length() - 1; nCount++) {
            digitoCPF = Integer.parseInt(strCpf.substring(nCount - 1, nCount));

            //multiplique a ultima casa por 2 a seguinte por 3 a seguinte por 4 e assim por diante.  
            d1 = d1 + (11 - nCount) * digitoCPF;

            //para o segundo digito repita o procedimento incluindo o primeiro digito calculado no passo anterior.  
            d2 = d2 + (12 - nCount) * digitoCPF;
        }

        //Primeiro resto da divisÃ£o por 11.  
        resto = (d1 % 11);

        //Se o resultado for 0 ou 1 o digito Ã© 0 caso contrÃ¡rio o digito Ã© 11 menos o resultado anterior.  
        if (resto < 2) {
            digito1 = 0;
        } else {
            digito1 = 11 - resto;
        }

        d2 += 2 * digito1;

        //Segundo resto da divisÃ£o por 11.  
        resto = (d2 % 11);

        //Se o resultado for 0 ou 1 o digito Ã© 0 caso contrÃ¡rio o digito Ã© 11 menos o resultado anterior.  
        if (resto < 2) {
            digito2 = 0;
        } else {
            digito2 = 11 - resto;
        }
        //Digito verificador do CPF que estÃ¡ sendo validado.  
        String nDigVerific = strCpf.substring(strCpf.length() - 2, strCpf.length());

        //Concatenando o primeiro resto com o segundo.  
        nDigResult = String.valueOf(digito1) + String.valueOf(digito2);

        //comparar o digito verificador do cpf com o primeiro resto + o segundo resto.  
        return nDigVerific.equals(nDigResult);
    }
    /* @MD5 MÃ©todo de EncriptaÃ§Ã£o da Senha em um HASH Hexadecimal */

    public static String md5(String senha) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            BigInteger hash = new BigInteger(1, md.digest(senha.getBytes()));
            return ((String) hash.toString(16));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static boolean chkVazio(String... campos) {
        for (String chk : campos) {
            if (chk.isEmpty() || chk.equals("--") || chk.equals("  /  /    ")) {
                JOptionPane.showMessageDialog(null, "Preencha todos os campos com '*'!");
                return false;
            }
        }
        return true;
    }

    public static boolean chkCaracteres(String... caracteres) {
        Pattern regex = Pattern.compile("\\W");
        Matcher comparador;

        /* Verifica se algum caractere especial 
         * estÃ¡ contido dentro de um dos campos */
        for (int x = 0; x < caracteres.length; x++) {
            if (((comparador) = regex.matcher(caracteres[x])).find()) {
                JOptionPane.showMessageDialog(null,
                        "NÃ£o Ã© permitido o uso de caracteres especiais!", "Caracteres InvÃ¡lidos",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        }
        return true;
    }

    /**
     * @Flag - decimalFormat()
     * @param opt int VarArg
     * @Values..
     * @return -> 0000000 [ if opt(null) or !opt ]
     * @return -> 0000 [ if opt(1) ]
     */
    public static DecimalFormat decimalFormat(int... opt) {
        return new DecimalFormat(((opt.length <= 0) ? "0000000" : "0000"));
    }

    public boolean dataValida(String data) {
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        df.setLenient(false); // aqui o pulo do gato  
        try {
            df.parse(data);
            // data vÃ¡lida
            return true;
        } catch (ParseException ex) {
            // data invÃ¡lida  
            return false;
        }
    }

    public static int diferencaData(Date dia1, Date dia2) {

        //DateFormat fmt = new SimpleDateFormat("dd/MM/yyyy");
        Date data3 = null;
        Date data4 = null;
        long m1 = 0;
        long m2 = 0;
        try {
            data3 = (Date) dia1;
            data4 = (Date) dia2;
        } catch (Exception e) {
        }
        Calendar data1 = new GregorianCalendar();
        data1.setTime(data3);
        Calendar data2 = new GregorianCalendar();
        data2.setTime(data4);
        m1 = data1.getTimeInMillis();
        m2 = data2.getTimeInMillis();
        return (int) ((m2 - m1) / (24 * 60 * 60 * 1000));
    }

    public static void setAcessibilidade(final JDialog tela) {
        Util utilidades = new Util();
        utilidades.meurootpane = tela.getRootPane();
        utilidades.meurootpane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "ESCAPE");
        utilidades.meurootpane.getRootPane().getActionMap().put("ESCAPE", new AbstractAction("ESCAPE") {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (JOptionPane.showConfirmDialog(null, "Deseja sair do formulÃ¡rio?", "Sair", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                    tela.setVisible(false);
                }
            }
        });
    }

    public static boolean chkSize(List lista, String type) {
        if (lista.isEmpty()) {
            JOptionPane.showMessageDialog(null, "NÃ£o foram encontrados registros para: " + type);
            return false;
        } else {
            return true;
        }
    }

    public static void setIcon(Class classe, JFrame jframe) {
        jframe.setIconImage(Toolkit.getDefaultToolkit().getImage(classe.getResource("/fvsosp/imagens/bicoaguia.png")));
    }

    public static String retornarDiaSemana(Calendar objCalendar) {

        int dia_semana = objCalendar.get(Calendar.DAY_OF_WEEK);
        switch (dia_semana) {
            case 1:
                return "Domingo";
            case 2:
                return "Segunda";
            case 3:
                return "Terça";

            case 4:
                return "Quarta";

            case 5:
                return "Quinta";

            case 6:
                return "Sexta";

            case 7:
                return "Sábado";

        }

        return null;

    }

    public static Calendar DateToCalendar(Date date) {
        Calendar cal = null;
        try {
            DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            date = (Date) formatter.parse(date.toString());
            cal = Calendar.getInstance();
            cal.setTime(date);
        } catch (ParseException e) {
            System.out.println("Exception :" + e);
        }
        return cal;
    }
    

}
