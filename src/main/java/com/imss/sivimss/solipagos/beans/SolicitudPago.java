package com.imss.sivimss.solipagos.beans;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.DatatypeConverter;

import com.imss.sivimss.solipagos.util.AppConstantes;
import com.imss.sivimss.solipagos.util.QueryHelper;
import com.imss.sivimss.solipagos.model.request.BusquedaDto;
import com.imss.sivimss.solipagos.util.DatosRequest;
import com.imss.sivimss.solipagos.model.request.SolicitudPagoDto;

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
	private Integer idUsuarioAlta;
	
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
    	} else if (busqueda.getIdOficina().equals(NIVEL_VELATORIO) || busqueda.getIdVelatorio() != null) {
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
			query.append(" AND SP.CVE_FOLIO_GASTOS = '" + busqueda.getFolioSolicitud() + "' ");
		}
		
		String encoded = DatatypeConverter.printBase64Binary(query.toString().getBytes("UTF-8"));
		request.getDatos().put(AppConstantes.QUERY, encoded);
    	
    	return request;
	}
	
	public DatosRequest detalle(DatosRequest request, String formatoFecha) throws UnsupportedEncodingException {
		StringBuilder query = new StringBuilder("SELECT VEL.DES_VELATORIO AS desVelatorio, SP.CVE_FOLIO_GASTOS AS cveFolioGastos, SP.CVE_FOLIO_CONSIGNADOS AS cveFolioConsignados, \n");
		query.append("SP.NUM_EJERCICIO_FISCAL AS ejercicioFiscal, SP.ID_UNIDAD_MEDICA AS unidadMedica, SP.ID_DELEGACION as idDelegacion, DATE_FORMAT(SP.FEC_ALTA,'" + formatoFecha + "') AS fecElaboracion, \n");
	    query.append("SP.ID_TIPO_SOLICITUD AS idTipoSolicitid, TIP.DES_TIPO_SOLICITUD AS desTipoSolicitud, \n");
		query.append("CONCAT(PER.NOM_PERSONA,' ',PER.NOM_PRIMER_APELLIDO,' ',PER.NOM_SEGUNDO_APELLIDO) AS nomBeneficiario, \n");
		query.append("SP.ID_ESTATUS_SOLICITUD AS idEstatusSol, EST.DES_ESTATUS_SOLICITUD AS desEstatusSolicitud \n");
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
	
	public DatosRequest crearSolicitud(SolicitudPagoDto solicPagoDto, String formatoFecha) throws UnsupportedEncodingException {
		DatosRequest request = new DatosRequest();
		Map<String, Object> parametro = new HashMap<>();
		final QueryHelper q = new QueryHelper("INSERT INTO SVT_SOLICITUD_PAGO");
		q.agregarParametroValues("ID_TIPO_SOLICITUD", "" + solicPagoDto.getIdTipoSolic());
		q.agregarParametroValues("CVE_FOLIO_GASTOS", setValor(solicPagoDto.getCveFolioGastos()));
		q.agregarParametroValues("CVE_FOLIO_CONSIGNADOS", setValor(solicPagoDto.getCveFolioConsignados()));
		q.agregarParametroValues("ID_UNIDAD_MEDICA", "" + solicPagoDto.getIdUnidadMedica());
		q.agregarParametroValues("ID_DELEGACION", "" + solicPagoDto.getIdDelegacion());
		q.agregarParametroValues("DES_NOMBRE_DESTINATARIO", setValor(solicPagoDto.getNomDestinatario()));
		q.agregarParametroValues("DES_NOMBRE_REMITENTE", setValor(solicPagoDto.getNomRemitente()));
		q.agregarParametroValues("NUM_REFERENCIA_DT", "" + solicPagoDto.getNumReferencia());
		q.agregarParametroValues("ID_CONTRATANTE_BENEFICIARIOS", "" + solicPagoDto.getIdContratBenef());
		q.agregarParametroValues("FEC_INICIAL", "STR_TO_DATE(" + setValor(solicPagoDto.getFechaInicial()) + ",'" + formatoFecha + "')");
		q.agregarParametroValues("FEC_FINAL", "STR_TO_DATE(" + setValor(solicPagoDto.getFechaFinal()) + ",'" + formatoFecha + "')");
		q.agregarParametroValues("DES_CONCEPTO", setValor(solicPagoDto.getConcepto()));
		q.agregarParametroValues("DES_OBSERVACIONES", setValor(solicPagoDto.getObservaciones()));
		q.agregarParametroValues("ID_VELATORIO", "" + solicPagoDto.getIdVelatorio());
		q.agregarParametroValues("NUM_EJERCICIO_FISCAL", "" + solicPagoDto.getEjercicioFiscal());
		q.agregarParametroValues("ID_ESTATUS_SOLICITUD", "" + solicPagoDto.getIdEstatusSol());
		q.agregarParametroValues("ID_USUARIO_ALTA", "" + this.idUsuarioAlta);
		
		String query = q.obtenerQueryInsertar();
		String encoded = DatatypeConverter.printBase64Binary(query.getBytes("UTF-8"));
		parametro.put(AppConstantes.QUERY, encoded);
		request.setDatos(parametro);
		
		return request;
	}
		
	
	private StringBuilder armaQuery(String formatoFecha) {
		
		StringBuilder query = new StringBuilder("SELECT VEL.DES_VELATORIO AS desVelatorio, NULLIF(SP.CVE_FOLIO_GASTOS,SP.CVE_FOLIO_CONSIGNADOS) AS cveFolio, \n");
		query.append("SP.NUM_EJERCICIO_FISCAL AS ejercicioFiscal, DATE_FORMAT(SP.FEC_ALTA,'" + formatoFecha + "') AS fecElaboracion, \n");
	    query.append("SP.ID_TIPO_SOLICITUD AS idTipoSolicitid, TIP.DES_TIPO_SOLICITUD AS desTipoSolicitud, \n");
		query.append("CONCAT(PER.NOM_PERSONA,' ',PER.NOM_PRIMER_APELLIDO,' ',PER.NOM_SEGUNDO_APELLIDO) AS nomBeneficiario, \n");
		query.append("SP.ID_ESTATUS_SOLICITUD AS idEstatus, EST.DES_ESTATUS_SOLICITUD AS desEstatusSolicitud \n");
		query.append("FROM SVT_SOLICITUD_PAGO SP \n");
		query.append("JOIN SVC_VELATORIO VEL ON VEL.ID_VELATORIO = SP.ID_VELATORIO \n");
		query.append("JOIN SVC_TIPO_SOLICITUD_PAGO TIP ON TIP.ID_TIPO_SOLICITUD = SP.ID_TIPO_SOLICITUD \n");
		query.append("JOIN SVT_CONTRATANTE_BENEFICIARIOS BEN ON BEN.ID_CONTRATANTE_BENEFICIARIOS = SP.ID_CONTRATANTE_BENEFICIARIOS \n");
		query.append("JOIN SVC_PERSONA PER ON PER.ID_PERSONA = BEN.ID_PERSONA \n");
		query.append("JOIN SVC_ESTATUS_SOLICITUD_PAGO EST ON EST.ID_ESTATUS_SOLICITUD = SP.ID_ESTATUS_SOLICITUD \n");
		query.append("WHERE 1 = 1 ");
		
		return query;
	}
	
	private String setValor(String valor) {
        if (valor == null || valor.equals("")) {
            return "NULL";
        } else {
            return "'" + valor + "'";
        }
    }
	
}