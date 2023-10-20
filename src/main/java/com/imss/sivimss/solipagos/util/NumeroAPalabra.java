package com.imss.sivimss.solipagos.util;

import java.util.regex.Pattern;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class NumeroAPalabra {
	private static final String[] UNIDADES = { "", "un ", "dos ", "tres ", "cuatro ", "cinco ", "seis ", "siete ", "ocho ", "nueve " };
	private static final String[] DECENAS = { "", "dieci", "veinti", "treinta ", "cuarenta ", "cincuenta ", "sesenta ", "setenta ", "ochenta ", "noventa " };
	private static final String[] CENTENAS = { "", "ciento ", "doscientos ", "trescientos ", "cuatrocientos ", "quinientos ", "seiscientos ", "setecientos ", "ochocientos ", "novecientos " };
	
	 public String convertirAPalabras(String s, Boolean mayusculas) {
	        StringBuilder resultado = new StringBuilder();
	        //si el numero utiliza (.) en lugar de (,) -> se reemplaza
	        s = s.replace(".", ",");
	        //si el numero no tiene parte decimal, se le agrega ,00
	        if(s.indexOf(",")==-1){
	            s = s + ",00";
	        }
	        //se valida formato de entrada -> 0,00 y 999 999 999,00
	        if (Pattern.matches("\\d{1,9},\\d{1,2}", s)) {
	            
	            String Num[] = s.split(",");    
	            s=Num[0];        
	        }
	        long parteEntera = Long.parseLong(s); 
	        int unidades      = (int)((parteEntera % 1000));
	        int miles         = (int)((parteEntera / 1000) % 1000);
	        int millones      = (int)((parteEntera / 1000000) % 1000);
	        int milMillones      = (int)((parteEntera / 1000000000) % 1000);
	        
	        resultado.append((parteEntera == 0)? "cero ":"");
	        resultado.append((milMillones > 0) ? convertirALetra(milMillones).toString() + "mil ":"");
	        resultado.append((millones > 0) ? convertirALetra(millones).toString():"");        
	        resultado.append((milMillones == 0 && millones == 1)? "millón " : "");
	        resultado.append((milMillones > 0 || millones > 0)? "millones ":"");
	        resultado.append((miles > 0) ?convertirALetra(miles).toString() + "mil ":"");
	        resultado.append((unidades > 0) ? convertirALetra(unidades).toString():"");
	        return  Boolean.TRUE.equals((mayusculas)) ? resultado.toString().toUpperCase() : resultado.toString().toLowerCase();
	    }

	    private StringBuilder convertirALetra(int n) {
	        StringBuilder result = new StringBuilder();
	        int centenas = n / 100;
	        int decenas  = (n % 100) / 10;
	        int unidades = (n % 10);
	        
	        result.append((n == 100) ? "cien ":CENTENAS[centenas]);
	        
	        result.append( (decenas == 1 && unidades == 0) ?"diez " : "");
	        result.append( (decenas == 1 && unidades == 1) ?"once " : "");
	        result.append( (decenas == 1 && unidades == 2) ?"doce " : "");
	        result.append( (decenas == 1 && unidades == 3) ?"trece " : "");
	        result.append( (decenas == 1 && unidades == 4) ?"catorce " : "");
	        result.append( (decenas == 1 && unidades == 5) ?"quince ":"");

	        result.append( (decenas == 2 && unidades == 0)?"veinte " : "");        
	        result.append( (decenas >= 2 && unidades >= 0) ? DECENAS[decenas]  : "");

	        result.append( (decenas > 2 && unidades > 0) ? "y " :"");
	        result.append((decenas == 1 && unidades > 5) ? DECENAS[decenas] : "");
	        result.append((decenas == 1 && unidades <= 5) ? "" : UNIDADES[unidades]);
	        
	        return result;
	    }
}
