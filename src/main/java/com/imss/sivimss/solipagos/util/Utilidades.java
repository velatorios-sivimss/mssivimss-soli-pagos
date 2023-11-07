package com.imss.sivimss.solipagos.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Utilidades {
	
	private Utilidades() {
	    throw new IllegalStateException("Utilidades class");
	  }
	public static String periodo (String fecIni, String fecFin) {
		String str= "";
		SimpleDateFormat formato1 = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy",new Locale("es", "MX"));
		try {
			if(fecIni != null && fecFin != null) {
				Date dateIni1 = formato1.parse(fecIni);
				String dateIni = formato.format(dateIni1);
				Date dateFin1 = formato1.parse(fecFin);
				String dateFin = formato.format(dateFin1);
				str = str + "del " + dateIni + " al " + dateFin;
			}
			else if(fecIni != null ) {
				Date dateIni1 = formato1.parse(fecIni);
				String dateIni = formato.format(dateIni1);
				str = str + "desde " + dateIni;
			}
			else if(fecFin != null) {
				Date dateFin1 = formato1.parse(fecFin);
				String dateFin = formato.format(dateFin1);
				str = str + "hasta " + dateFin;
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return str.equals("")?"":"Periodo: " + str;
	}
}
