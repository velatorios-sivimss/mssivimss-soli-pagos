package com.imss.sivimss.solipagos.beans;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.DatatypeConverter;

import com.imss.sivimss.solipagos.util.AppConstantes;
import com.imss.sivimss.solipagos.model.request.BusquedaDto;
import com.imss.sivimss.solipagos.util.DatosRequest;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Setter
@Getter
public class SolicitudPago {
	
	private Integer id;
	
	private static final Integer NIVEL_DELEGACION = 2;
	private static final Integer NIVEL_VELATORIO = 3;
	
	public DatosRequest listaEjercicios(BusquedaDto busqueda) throws UnsupportedEncodingException {
    	DatosRequest request = new DatosRequest();
    	Map<String, Object> parametro = new HashMap<>();
    	StringBuilder query = new StringBuilder("SELECT EXTRACT(YEAR FROM NOW()) AS anio");
    	
    	String encoded = DatatypeConverter.printBase64Binary(query.toString().getBytes("UTF-8"));
		parametro.put(AppConstantes.QUERY, encoded);
		request.setDatos(parametro);
		return request;
	}
	
	public DatosRequest listaTiposSoli(BusquedaDto busqueda) throws UnsupportedEncodingException {
    	DatosRequest request = new DatosRequest();
    	Map<String, Object> parametro = new HashMap<>();
    	StringBuilder query = new StringBuilder("SELECT ID_TIPO_SOLICITUD AS tipoSolicitud, DES_TIPO_SOLICITUD AS desTipoSolicitud ");
    	query.append("FROM SVC_TIPO_SOLICITUD_PAGO ");
    	
    	String encoded = DatatypeConverter.printBase64Binary(query.toString().getBytes("UTF-8"));
		parametro.put(AppConstantes.QUERY, encoded);
		request.setDatos(parametro);
		return request;
	}
	
	public DatosRequest consulta(DatosRequest request, BusquedaDto busqueda, String formatoFecha) throws UnsupportedEncodingException {
		StringBuilder query = armaQuery(formatoFecha);
	
    	if (busqueda.getIdOficina().equals(NIVEL_DELEGACION)) {
    		query.append(" AND VEL.ID_DELEGACION = ").append(busqueda.getIdDelegacion());
    	} else if (busqueda.getIdOficina().equals(NIVEL_VELATORIO)) {
    		query.append(" AND SP.ID_VELATORIO = ").append(busqueda.getIdVelatorio());
    	}
    	
		String encoded = DatatypeConverter.printBase64Binary(query.toString().getBytes("UTF-8"));
		request.getDatos().put(AppConstantes.QUERY, encoded);
    	
    	return request;
	}
	
	public DatosRequest busqueda(DatosRequest request, BusquedaDto busqueda, String formatoFecha) throws UnsupportedEncodingException {
		StringBuilder query = armaQuery(formatoFecha);
		
		if (busqueda.getIdOficina().equals(NIVEL_DELEGACION)) {
    		query.append(" AND VEL.ID_DELEGACION = ").append(busqueda.getIdDelegacion());
    	} else if (busqueda.getIdVelatorio().equals(NIVEL_VELATORIO)) {
    		query.append(" AND SP.ID_VELATORIO = ").append(busqueda.getIdVelatorio());
    	}
		
		if (busqueda.getFecInicial() != null) {
    		query.append(" AND DATE(SP.FEC_ALTA) BETWEEN STR_TO_DATE('" + busqueda.getFecInicial() + "','" + formatoFecha + "') AND STR_TO_DATE('" + busqueda.getFecFinal() + "','" + formatoFecha + "')");
    	}
		if (busqueda.getEjercicioFiscal() != null) {
			query.append(" AND SP.NUM_EJERCICIO_FISCAL = " + busqueda.getEjercicioFiscal());
		}
		if (busqueda.getIdTipoSolicitud() != null) {
			query.append(" AND SP.ID_TIPO_SOLICITUD = " + busqueda.getIdTipoSolicitud());
		}
		if (busqueda.getFolioSolicitud() != null) {
			query.append(" AND SP.CVE_FOLIO_GASTOS = ' " + busqueda.getFolioSolicitud());
		}
		
		String encoded = DatatypeConverter.printBase64Binary(query.toString().getBytes("UTF-8"));
		request.getDatos().put(AppConstantes.QUERY, encoded);
    	
    	return request;
	}
	
	public DatosRequest detalleGeneral(DatosRequest request, String formatoFecha) throws UnsupportedEncodingException {
		StringBuilder query = new StringBuilder("SELECT VEL.DES_VELATORIO AS desVelatorio, NULLIF(SP.CVE_FOLIO_GASTOS,SP.CVE_FOLIO_CONSIGNADOS) AS cveFolio, SP.NUM_EJERCICIO_FISCAL AS ejercicioFiscal, \n");
		query.append("SP.ID_UNIDAD_MEDICA AS unidadMedica, SP.ID_DELEGACION as idDelegacion, DATE_FORMAT(SP.FEC_ALTA,'" + formatoFecha + "') AS fecElaboracion, \n");
	    query.append("SP.ID_TIPO_SOLICITUD AS idTipoSolicitid, TIP.DES_TIPO_SOLICITUD AS desTipoSolicitud, \n");
		query.append("CONCAT(PER.NOM_PERSONA,' ',PER.NOM_PRIMER_APELLIDO,' ',PER.NOM_SEGUNDO_APELLIDO) AS nomBeneficiario, \n");
		query.append("SP.ID_ESTATUS_SOLICITUD AS idEstatusSolicitud, EST.DES_ESTATUS_SOLICITUD AS desEstatusSolicitud \n");
		query.append("FROM SVT_SOLICITUD_PAGO SP \n");
		query.append("JOIN SVC_VELATORIO VEL ON VEL.ID_VELATORIO = SP.ID_VELATORIO \n");
		query.append("JOIN SVC_TIPO_SOLICITUD_PAGO TIP ON TIP.ID_TIPO_SOLICITUD = SP.ID_TIPO_SOLICITUD \n");
		query.append("JOIN SVT_CONTRATANTE_BENEFICIARIOS BEN ON BEN.ID_CONTRATANTE_BENEFICIARIOS = SP.ID_CONTRATANTE_BENEFICIARIOS \n");
		query.append("JOIN SVC_PERSONA PER ON PER.ID_PERSONA = BEN.ID_PERSONA \n");
		query.append("JOIN SVC_ESTATUS_SOLICITUD_PAGO EST ON EST.ID_ESTATUS_SOLICITUD = SP.ID_ESTATUS_SOLICITUD \n");
		query.append("WHERE ID_SOLICITUD_PAGO = " + this.getId());
		
		String encoded = DatatypeConverter.printBase64Binary(query.toString().getBytes("UTF-8"));
		request.getDatos().put(AppConstantes.QUERY, encoded);
		return request;
	}
	
	
	private StringBuilder armaQuery(String formatoFecha) {
		
		StringBuilder query = new StringBuilder("SELECT VEL.DES_VELATORIO AS desVelatorio, NULLIF(SP.CVE_FOLIO_GASTOS,SP.CVE_FOLIO_CONSIGNADOS) AS cveFolio, \n");
		query.append("SP.NUM_EJERCICIO_FISCAL AS ejercicioFiscal, DATE_FORMAT(SP.FEC_ALTA,'" + formatoFecha + "') AS fecElaboracion, \n");
	    query.append("SP.ID_TIPO_SOLICITUD AS idTipoSolicitid, TIP.DES_TIPO_SOLICITUD AS desTipoSolicitud, \n");
		query.append("CONCAT(PER.NOM_PERSONA,' ',PER.NOM_PRIMER_APELLIDO,' ',PER.NOM_SEGUNDO_APELLIDO) AS nomBeneficiario, \n");
		query.append("SP.ID_ESTATUS_SOLICITUD AS idEstatusSolicitud, EST.DES_ESTATUS_SOLICITUD AS desEstatusSolicitud \n");
		query.append("FROM SVT_SOLICITUD_PAGO SP \n");
		query.append("JOIN SVC_VELATORIO VEL ON VEL.ID_VELATORIO = SP.ID_VELATORIO \n");
		query.append("JOIN SVC_TIPO_SOLICITUD_PAGO TIP ON TIP.ID_TIPO_SOLICITUD = SP.ID_TIPO_SOLICITUD \n");
		query.append("JOIN SVT_CONTRATANTE_BENEFICIARIOS BEN ON BEN.ID_CONTRATANTE_BENEFICIARIOS = SP.ID_CONTRATANTE_BENEFICIARIOS \n");
		query.append("JOIN SVC_PERSONA PER ON PER.ID_PERSONA = BEN.ID_PERSONA \n");
		query.append("JOIN SVC_ESTATUS_SOLICITUD_PAGO EST ON EST.ID_ESTATUS_SOLICITUD = SP.ID_ESTATUS_SOLICITUD \n");
		query.append("WHERE 1 = 1 ");
		
		return query;
	}
	
}