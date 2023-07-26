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
import com.imss.sivimss.solipagos.model.response.CambioEstatusResponse;

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
	private String folioFiscal;
	
	private static final Integer NIVEL_DELEGACION = 2;
	private static final Integer NIVEL_VELATORIO = 3;
	
	public DatosRequest listaEjercicios() throws UnsupportedEncodingException {
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
		query.append("SP.NUM_EJERCICIO_FISCAL AS ejercicioFiscal, SP.ID_UNIDAD_OPERATIVA AS unidadOperativa, DATE_FORMAT(SP.FEC_ALTA,'" + formatoFecha + "') AS fecElaboracion, \n");
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
	
	public DatosRequest factura(DatosRequest request) throws UnsupportedEncodingException {
		StringBuilder query = new StringBuilder("SELECT PARTIDA_PRES AS partidaPres, CUENTA_CONTABLE AS cuentaContable, ");
		query.append("IMP_TOTAL AS importeTotal FROM SVC_FACTURA WHERE CVE_FOLIO_FISCAL = '" + this.folioFiscal + "' ");
		
		String encoded = DatatypeConverter.printBase64Binary(query.toString().getBytes("UTF-8"));
		request.getDatos().put(AppConstantes.QUERY, encoded);
		return request;
	}
	
	public DatosRequest listaVelatorios(BusquedaDto busqueda) throws UnsupportedEncodingException {
    	DatosRequest request = new DatosRequest();
    	Map<String, Object> parametro = new HashMap<>();
    	StringBuilder query = new StringBuilder("SELECT ID_VELATORIO AS idVelatorio, DES_VELATORIO AS desVelatorio, NOM_RESPO_SANITARIO AS nomResponsable ");
	    query.append("FROM SVC_VELATORIO ");
    	if (busqueda.getIdOficina().equals(NIVEL_DELEGACION)) {
    		query.append(" WHERE ID_DELEGACION = ").append(busqueda.getIdDelegacion());
    	} else if (busqueda.getIdOficina().equals(NIVEL_VELATORIO)) {
    		query.append(" WHERE ID_VELATORIO = ").append(busqueda.getIdVelatorio());
    	}
    	
    	String encoded = DatatypeConverter.printBase64Binary(query.toString().getBytes("UTF-8"));
		parametro.put(AppConstantes.QUERY, encoded);
		request.setDatos(parametro);
    	return request;
	}
	
	public DatosRequest listaUnidadesOpe() throws UnsupportedEncodingException {
    	DatosRequest request = new DatosRequest();
    	Map<String, Object> parametro = new HashMap<>();
    	StringBuilder query = new StringBuilder("SELECT ID_SUBDIRECCION AS idSubdireccion, NOM_SUBDIRECCION AS nomSubdireccion, ");
    	query.append("DES_REFERENCIA AS referencia, NOM_RESPONSABLE AS nomResponsable ");
    	query.append("FROM SVT_SUBDIRECCION_FIBESO");
    	
    	String encoded = DatatypeConverter.printBase64Binary(query.toString().getBytes("UTF-8"));
		parametro.put(AppConstantes.QUERY, encoded);
		request.setDatos(parametro);
    	return request;
	}
	
	public DatosRequest listaDatosBanco() throws UnsupportedEncodingException {
    	DatosRequest request = new DatosRequest();
    	Map<String, Object> parametro = new HashMap<>();
    	StringBuilder query = new StringBuilder("SELECT DES_BANCO AS banco, CVE_BANCARIA AS cveBancaria, '' AS cuenta ");
    	query.append("FROM SVT_PROVEEDOR");
    	
    	String encoded = DatatypeConverter.printBase64Binary(query.toString().getBytes("UTF-8"));
		parametro.put(AppConstantes.QUERY, encoded);
		request.setDatos(parametro);
    	return request;
	}
	
	public DatosRequest crearSolicitud(SolicitudPagoDto solicPagoDto, String formatoFecha) throws UnsupportedEncodingException {
		DatosRequest request = new DatosRequest();
		Map<String, Object> parametro = new HashMap<>();
		final QueryHelper q = new QueryHelper("INSERT INTO SVT_SOLICITUD_PAGO");
		q.agregarParametroValues("ID_TIPO_SOLICITUD", "" + solicPagoDto.getIdTipoSolic());
		q.agregarParametroValues("CVE_FOLIO_GASTOS", setValor(solicPagoDto.getCveFolioGastos()));
		q.agregarParametroValues("CVE_FOLIO_CONSIGNADOS", setValor(solicPagoDto.getCveFolioConsignados()));
		q.agregarParametroValues("ID_UNIDAD_OPERATIVA", "" + solicPagoDto.getIdUnidadOperativa());
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
		q.agregarParametroValues("ID_ESTATUS_SOLICITUD", "1");
		q.agregarParametroValues("ID_USUARIO_ALTA", "" + this.idUsuarioAlta);
		
		String query = q.obtenerQueryInsertar();
		String encoded = DatatypeConverter.printBase64Binary(query.getBytes("UTF-8"));
		parametro.put(AppConstantes.QUERY, encoded);
		request.setDatos(parametro);
		
		return request;
	}
		
	public DatosRequest aprobarSolicitud(CambioEstatusResponse cambioEstatus) throws UnsupportedEncodingException {
	    DatosRequest request = new DatosRequest();
	    Map<String, Object> parametro = new HashMap<>();
	    String query =" UPDATE SVT_SOLICITUD_PAGO SET ID_ESTATUS_SOLICITUD = 2, ID_USUARIO_MODIFICA = " + cambioEstatus.getIdUsuario() +
	    		", FEC_ACTUALIZACION = CURRENT_TIMESTAMP() WHERE ID_SOLICITUD_PAGO = " + cambioEstatus.getIdSolicitud();
	 
	    String encoded = DatatypeConverter.printBase64Binary(query.getBytes("UTF-8"));
		parametro.put(AppConstantes.QUERY, encoded);
		request.setDatos(parametro);
		return request;
    }
	
	public DatosRequest cancelarSolicitud(CambioEstatusResponse cambioEstatus) throws UnsupportedEncodingException {
	    DatosRequest request = new DatosRequest();
	    Map<String, Object> parametro = new HashMap<>();
	    String query =" UPDATE SVT_SOLICITUD_PAGO SET ID_ESTATUS_SOLICITUD = 0, ID_USUARIO_MODIFICA = " + cambioEstatus.getIdUsuario() +
	    		", FEC_ACTUALIZACION = CURRENT_TIMESTAMP(), DES_MOTIVO_CANCELACION = '" + cambioEstatus.getMotivo() + "' " +
	    	    "WHERE ID_SOLICITUD_PAGO = " + cambioEstatus.getIdSolicitud();
	 
	    String encoded = DatatypeConverter.printBase64Binary(query.getBytes("UTF-8"));
		parametro.put(AppConstantes.QUERY, encoded);
		request.setDatos(parametro);
		return request;
    }
	
	public DatosRequest rechazarSolicitud(CambioEstatusResponse cambioEstatus) throws UnsupportedEncodingException {
	    DatosRequest request = new DatosRequest();
	    Map<String, Object> parametro = new HashMap<>();
	    String query =" UPDATE SVT_SOLICITUD_PAGO SET ID_ESTATUS_SOLICITUD = 3, ID_USUARIO_MODIFICA = " + cambioEstatus.getIdUsuario() +
	    		", FEC_ACTUALIZACION = CURRENT_TIMESTAMP(), DES_MOTIVO_RECHAZO = '" + cambioEstatus.getMotivo() + "' " +
	    	    "WHERE ID_SOLICITUD_PAGO = " + cambioEstatus.getIdSolicitud();
	 
	    String encoded = DatatypeConverter.printBase64Binary(query.getBytes("UTF-8"));
		parametro.put(AppConstantes.QUERY, encoded);
		request.setDatos(parametro);
		return request;
    }
	
	public Map<String, Object> generarReporte(BusquedaDto reporteDto,String nombrePdfReportes, String formatoFecha){
		Map<String, Object> envioDatos = new HashMap<>();
		StringBuilder condicion = new StringBuilder(" ");
		if (reporteDto.getIdOficina().equals(NIVEL_DELEGACION)) {
			condicion.append(" AND VEL.ID_DELEGACION = ").append(reporteDto.getIdDelegacion());
    	} else if (reporteDto.getIdVelatorio() != null) {
			condicion.append(" AND SP.ID_VELATORIO = ").append(reporteDto.getIdVelatorio());
		}
		if (reporteDto.getFecInicial() != null) {
			condicion.append(" AND DATE(SP.FEC_ALTA) BETWEEN STR_TO_DATE('" + reporteDto.getFecInicial() + "','" + formatoFecha + "') AND STR_TO_DATE('" + reporteDto.getFecFinal() + "','" + formatoFecha + "')");
    	}
		if (reporteDto.getEjercicioFiscal() != null) {
			condicion.append(" AND SP.NUM_EJERCICIO_FISCAL = " + reporteDto.getEjercicioFiscal());
		}
		if (reporteDto.getIdTipoSolicitud() != null) {
			condicion.append(" AND SP.ID_TIPO_SOLICITUD = " + reporteDto.getIdTipoSolicitud());
		}
		if (reporteDto.getFolioSolicitud() != null) {
			condicion.append(" AND SP.CVE_FOLIO_GASTOS = '" + reporteDto.getFolioSolicitud() + "' ");
		}
		envioDatos.put("condicion", condicion.toString());
		envioDatos.put("tipoReporte", reporteDto.getTipoReporte());
		envioDatos.put("rutaNombreReporte", nombrePdfReportes);
		
		return envioDatos;
	}
	    		
	private StringBuilder armaQuery(String formatoFecha) {
		
		StringBuilder query = new StringBuilder("SELECT SP.ID_SOLICITUD_PAGO AS idSolicitud, VEL.DES_VELATORIO AS desVelatorio, NULLIF(SP.CVE_FOLIO_GASTOS,SP.CVE_FOLIO_CONSIGNADOS) AS cveFolio, \n");
		query.append("SP.NUM_EJERCICIO_FISCAL AS ejercicioFiscal, DATE_FORMAT(SP.FEC_ALTA,'" + formatoFecha + "') AS fecElaboracion, \n");
	    query.append("SP.ID_TIPO_SOLICITUD AS idTipoSolicitid, TIP.DES_TIPO_SOLICITUD AS desTipoSolicitud, \n");
		query.append("CONCAT(PER.NOM_PERSONA,' ',PER.NOM_PRIMER_APELLIDO,' ',PER.NOM_SEGUNDO_APELLIDO) AS nomBeneficiario, \n");
		query.append("SP.ID_ESTATUS_SOLICITUD AS idEstatus, EST.DES_ESTATUS_SOLICITUD AS desEstatusSolicitud, \n");
		query.append("SP.DES_MOTIVO_CANCELACION AS motCancelacion, SP.DES_MOTIVO_RECHAZO AS motRechazo ");
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