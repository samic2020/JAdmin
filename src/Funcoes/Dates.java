/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Funcoes;

import javafx.scene.control.DatePicker;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 *
 * @author supervisor
 */
public class Dates {
    public static final String DIA = "D";
    public static final String MES = "M";
    public static final String ANO = "A";
    public static final String HOR = "H";

    public static Date DateAdd(String patern, int Valor, Date Data) {
        GregorianCalendar dt = new GregorianCalendar();
        dt.setTime(Data);

        if ("D".equals(patern.trim().toUpperCase())) {
            dt.add(GregorianCalendar.DATE, Valor);
        } else if ("M".equals(patern.trim().toUpperCase())) {
            dt.add(GregorianCalendar.MONTH, Valor);
        } else if ("A".equals(patern.trim().toUpperCase())) {
            dt.add(GregorianCalendar.YEAR, Valor);
        }
        return dt.getTime();
    }

    public static java.sql.Date sqlDateAdd(String patern, int Valor, Date Data) {
        GregorianCalendar dt = new GregorianCalendar();
        dt.setTime(Data);

        if ("D".equals(patern.trim().toUpperCase())) {
            dt.add(GregorianCalendar.DATE, Valor);
        } else if ("M".equals(patern.trim().toUpperCase())) {
            dt.add(GregorianCalendar.MONTH, Valor);
        } else if ("A".equals(patern.trim().toUpperCase())) {
            dt.add(GregorianCalendar.YEAR, Valor);
        }
        return toSqlDate(toDatePicker(dt.getTime()));
    }

    public static long DtDiff(String pattern, String sdata1, String sdata2) {
        LocalDate data1 = LocalDate.parse(sdata1);
        LocalDate data2 = LocalDate.parse(sdata2);
        // Calcula a diferença de dias entre as duas datas
        long diferencaEmDias = ChronoUnit.DAYS.between(data1, data2);
        // Calcula a diferença de meses entre as duas datas
        long diferencaEmMes = ChronoUnit.MONTHS.between(data1, data2);
        // Calcula a diferença de anos entre as duas datas
        long diferencaEmAnos = ChronoUnit.YEARS.between(data1, data2);
        return (pattern.equalsIgnoreCase("d") ? diferencaEmDias : pattern.equalsIgnoreCase("m") ? diferencaEmMes : diferencaEmAnos);
    }

    public static LocalDate FimDeSemana(Date data) {
        LocalDate dtatual  = LocalDate.parse(Dates.DateFormata( "yyyy-MM-dd", data));

        switch (dtatual.getDayOfWeek()) {
            case SATURDAY:
                dtatual = dtatual.plusDays(2);
                break;
            case SUNDAY:
                dtatual = dtatual.plusDays(1);
                break;
            default:
        }
        return dtatual;
    }

    public static int DateDiff(String patern, Date Data1, Date Data2) {
        long a = Data2.getTime();
        long b = Data1.getTime();
//        long mx = Math.max(a,b);
//        long mi = Math.min(a, b);
        
        long i = (a - b);
        
        long r = 0;
        if ("D".equals(patern.trim().toUpperCase())) {
            r = (i / 1000 / 60 / 60 / 24);
        } else if ("M".equals(patern.trim().toUpperCase())) {
            r = (i / 1000 / 60 / 60 / 24 / 30);
        } else if ("A".equals(patern.trim().toUpperCase())) {
            r = (i / 1000 / 60 / 60 / 24 / 365);
        }

//        int d = 0;
//        if (a > b) {
//            d = (int)r;
//        } else {
//            d = -(int)r;
//        }
        return (int)r;
    }

    public static int DiffDate(Date Data1, Date Data2) {
        Date xData1, xData2; int mult = 1;
        if (Data2.getTime() > Data1.getTime()) { 
            xData1 = Data2;
            xData2 = Data1;
            mult = -1;
        } else {
            xData1 = Data1;
            xData2 = Data2;
            mult = 1;
        }
        
        boolean mLoop = true;
        int dias = 0; /////////////////////////////////////04/09/2011 - JOAO
        while (mLoop) {
            int idia = iDay(xData2);
            int imes = iMonth(xData2);
            int iano = iYear(xData2);
            
            int fdia = iDay(xData1);
            int fmes = iMonth(xData1);
            int fano = iYear(xData1);
            
            if (idia == fdia && imes == fmes && iano == fano) {
                mLoop = false;
            } else {
                dias = dias + 1;
                xData2 = Dates.DateAdd(Dates.DIA, 1, xData2);
            }
        }
        
        return (dias * mult);
    }
    
    public static String DateFormata(String patern, Date Data) {
        if (Data == null) return "";
        SimpleDateFormat formatter = new SimpleDateFormat(patern);

        return formatter.format(Data);
    }

    public static String DatetoString(Date Data) { return DateFormata("dd/MM/yyyy", Data); }

    public static Date StringtoDate(String Data, String patern) {
        GregorianCalendar ret = null;
        Date ter = null;
        if (!Data.trim().equalsIgnoreCase("")) {
            int posDia = patern.indexOf("dd");
            int posMes = patern.indexOf("MM");
            int posAno = patern.indexOf("yyyy");

            int vDia = Integer.valueOf(Data.substring(posDia, posDia + 2));
            int vMes = Integer.valueOf(Data.substring(posMes, posMes + 2)) - 1;
            int vAno = Integer.valueOf(Data.substring(posAno, (Data.length() == 10 ? posAno + 4 : posAno + 2)));

            GregorianCalendar dt = new GregorianCalendar();
            dt.set(vAno, vMes, vDia);
            ret = dt;
        }
        
        if (ret != null) { ter = ret.getTime(); }
        return ter;
    }
    
    public static String StringtoString(String Data, String patern, String outpatern) {
        int posDia = patern.indexOf("dd");
        int posMes = patern.indexOf("MM");
        int posAno = patern.indexOf("yyyy");
        
        String vDia = Data.substring(posDia, posDia + 2);
        String vMes = Data.substring(posMes, posMes + 2);
        String vAno = Data.substring(posAno, posAno + 4);

        String newDateFormat = outpatern;
        newDateFormat = newDateFormat.replace("dd", vDia);
        newDateFormat = newDateFormat.replace("MM", vMes);
        newDateFormat = newDateFormat.replace("yyyy", vAno);

        return newDateFormat;
    }    

    public static String Month(Date Data) {
        GregorianCalendar dt = new GregorianCalendar();
        dt.setTime(Data);
        String[] meses = {"Janeiro", "Fevereiro", "Março", "Abril", "Maio", "Junho", "Julho", "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro"};

        return meses[dt.getTime().getMonth()];
    }

    public static String ShortMonth(Date Data) {
        GregorianCalendar dt = new GregorianCalendar();
        dt.setTime(Data);
        String[] meses = {"Janeiro", "Fevereiro", "Março", "Abril", "Maio", "Junho", "Julho", "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro"};

        return meses[dt.getTime().getMonth()].substring(0,3);
    }

    public static int iYear(Date Data) {
        GregorianCalendar dt = new GregorianCalendar();
        dt.setTime(Data);
        return dt.get(GregorianCalendar.YEAR);
    }

    public static int iMonth(Date Data) {
        GregorianCalendar dt = new GregorianCalendar();
        dt.setTime(Data);
        return dt.get(GregorianCalendar.MONTH) + 1;
    }

    public static int iDay(Date Data) {
        GregorianCalendar dt = new GregorianCalendar();
        dt.setTime(Data);
        return dt.get(GregorianCalendar.DATE);
    }

    public static int isSabadoOuDomingo(Date data) {  
        Calendar gc = GregorianCalendar.getInstance();
        gc.setTime(data);
        int diaSemana = gc.get(GregorianCalendar.DAY_OF_WEEK);  
        
        int retorno = 0;
        if (diaSemana == GregorianCalendar.SATURDAY) {
            retorno = 2;
        } else if (diaSemana == GregorianCalendar.SUNDAY) {
            retorno = 1;
        } else retorno = 0;

        return retorno;
    }      
    
    public static String ultDiaMes(Date data) {
        Calendar cal = GregorianCalendar.getInstance();
        cal.setTime( data );

        int dia = cal.getActualMaximum( Calendar.DAY_OF_MONTH );
        //int mes = (cal.get(Calendar.MONDAY)+1);
        //int ano = cal.get(Calendar.YEAR);        
        return FuncoesGlobais.StrZero(String.valueOf(dia), 2);
    }

    public static Date primDataMes(Date value) {
        return new Date(value.getYear(), value.getMonth(), 1);
    }

    public static Date ultDataMes(Date value) {
        return new Date(value.getYear(), value.getMonth(), Integer.valueOf(ultDiaMes(value)));
    }

    public static boolean isDateValid(String date, String pattern) {
        try {
            DateFormat df = new SimpleDateFormat(pattern);
            df.setLenient(false);
            df.parse(date);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }    
    
/**
     * Converte LocalDate para Date
     *
     * @param datePicker
     * @return date
     */
    public static Date toDate(DatePicker datePicker) {
        if(datePicker.getValue() == null){
            return null;
        }
        LocalDate ld = datePicker.getValue();
        Instant instant = ld.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant();
        Date date = Date.from(instant);

        return date;
    }

    public static Date toDate(LocalDate localDate) {
        return Date.from(localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
    }

    /**
     * Converte Data para DatePicker
     *
     * @param date
     * @return DatePicker
     */
    public static DatePicker toDatePicker(Date date) {
        return new DatePicker(toLocalDate(date));
    }

    /**
     * Converte Date para LocalDate
     *
     * @param d
     * @return LocalDate
     */
    public static LocalDate toLocalDate(Date d) {
        Instant instant = Instant.ofEpochMilli(d.getTime());
        LocalDate localDate = LocalDateTime.ofInstant(instant, ZoneId.systemDefault()).toLocalDate();
        return localDate;
    }    
    
    public static java.sql.Date toSqlDate(DatePicker data) {
        java.sql.Date sqlDate = null;
        try {
            Date date = Date.from(data.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant());
            sqlDate = new java.sql.Date(date.getTime());
        } catch (Exception e) {}       
        return sqlDate;
    }

    public static java.sql.Date toSqlDate(Date data) {
        java.sql.Date sqlDate = null;
        try {
            sqlDate = new java.sql.Date(data.getTime());
        } catch (Exception e) {}
        return sqlDate;
    }

    public static Date convertFromSQLDateToJAVADate(
            java.sql.Date sqlDate) {
        Date javaDate = null;
        if (sqlDate != null) {
            javaDate = new Date(sqlDate.getTime());
        }
        return javaDate;
    }

    public static Date String2Date(String datatime) {
        Date retorno = null;

        if (datatime != "") {
            int iAno = 0, iMes = 0, iDia = 0, iHora = 0, iMin = 0, iSeg = 0;
            iAno = Integer.valueOf(datatime.substring(0,4));
            iMes = Integer.valueOf(datatime.substring(5,7));
            iDia = Integer.valueOf(datatime.substring(8,10));
            if (datatime.length() > 10) {
                iHora = Integer.valueOf(datatime.substring(11,13));
                iMin = Integer.valueOf(datatime.substring(14,16));
                iSeg = Integer.valueOf(datatime.substring(17,19));
            }
            retorno = new Date(iAno - 1900, iMes - 1, iDia, iHora, iMin, iSeg);
        }

        return retorno;
    }

    public static int MonthToInteger(String value) {
        int retorno = 0;
        switch (value) {
            case "Janeiro":
                retorno = 1;
                break;
            case "Fevereiro":
                retorno = 2;
                break;
            case "Março":
                retorno = 3;
                break;
            case "Abril":
                retorno = 4;
                break;
            case "Maio":
                retorno = 5;
                break;
            case "Junho":
                retorno = 6;
                break;
            case "Julho":
                retorno = 7;
                break;
            case "Agosto":
                retorno = 8;
                break;
            case "Setembro":
                retorno = 9;
                break;
            case "Outubro":
                retorno = 10;
                break;
            case "Novembro":
                retorno = 11;
                break;
            case "Dezembro":
                retorno = 12;
                break;
        }
        return retorno;
    }
}
