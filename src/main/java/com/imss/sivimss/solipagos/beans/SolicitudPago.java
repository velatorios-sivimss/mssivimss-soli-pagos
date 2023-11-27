package com.imss.sivimss.solipagos.beans;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.DatatypeConverter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.imss.sivimss.solipagos.util.AppConstantes;
import com.imss.sivimss.solipagos.util.QueryHelper;
import com.imss.sivimss.solipagos.util.Utilidades;
import com.imss.sivimss.solipagos.model.request.BusquedaDto;
import com.imss.sivimss.solipagos.model.request.DatosFormatoDto;
import com.imss.sivimss.solipagos.model.request.SolicitudFoliosDto;
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

	private static final Logger log = LoggerFactory.getLogger(SolicitudPago.class);
	private Integer id;
	private Integer idUsuarioAlta;
	
	private static final Integer NIVEL_DELEGACION = 2;
	private static final Integer NIVEL_VELATORIO = 3;
	
	public DatosRequest listaEjercicios() {
    	DatosRequest request = new DatosRequest();
    	Map<String, Object> parametro = new HashMap<>();
    	StringBuilder query = new StringBuilder("SELECT EXTRACT(YEAR FROM NOW()) AS anio");
    	log.info(query.toString());
    	String encoded = DatatypeConverter.printBase64Binary(query.toString().getBytes(StandardCharsets.UTF_8));
		parametro.put(AppConstantes.QUERY, encoded);
		request.setDatos(parametro);
		return request;
	}
	
	public DatosRequest listaTiposSoli() {
    	DatosRequest request = new DatosRequest();
    	Map<String, Object> parametro = new HashMap<>();
    	StringBuilder query = new StringBuilder("SELECT ID_TIPO_SOLICITUD AS tipoSolicitud, DES_TIPO_SOLICITUD AS desTipoSolicitud ");
    	query.append("FROM SVC_TIPO_SOLICITUD_PAGO ");
    	log.info(query.toString());
    	String encoded = DatatypeConverter.printBase64Binary(query.toString().getBytes(StandardCharsets.UTF_8));
		parametro.put(AppConstantes.QUERY, encoded);
		request.setDatos(parametro);
		return request;
	}
	
	public DatosRequest buscaFolios(String folioSolicitud) {
    	DatosRequest request = new DatosRequest();
    	Map<String, Object> parametro = new HashMap<>();
    	StringBuilder query = new StringBuilder("SELECT NULLIF(CVE_FOLIO_GASTOS,CVE_FOLIO_CONSIGNADOS) AS cveFolio ");
    	query.append("FROM SVT_SOLICITUD_PAGO WHERE CVE_FOLIO_GASTOS LIKE '%" + folioSolicitud + "%' ");
    	query.append("OR CVE_FOLIO_CONSIGNADOS LIKE '%" + folioSolicitud + "%' ");
    	log.info(query.toString());
    	String encoded = DatatypeConverter.printBase64Binary(query.toString().getBytes(StandardCharsets.UTF_8));
		parametro.put(AppConstantes.QUERY, encoded);
		request.setDatos(parametro);
		return request;
	}
	
	public DatosRequest consulta(DatosRequest request, BusquedaDto busqueda, String formatoFecha) {
		StringBuilder query = armaQuery(formatoFecha);
	
    	if (busqueda.getIdOficina().equals(NIVEL_DELEGACION)) {
    		query.append(" AND VEL.ID_DELEGACION = ").append(busqueda.getIdDelegacion());
    	} else if (busqueda.getIdOficina().equals(NIVEL_VELATORIO)) {
    		query.append(" AND SP.ID_VELATORIO = ").append(busqueda.getIdVelatorio());
    	}
    	log.info(query.toString());
		String encoded = DatatypeConverter.printBase64Binary(query.toString().getBytes(StandardCharsets.UTF_8));
		request.getDatos().put(AppConstantes.QUERY, encoded);
    	
    	return request;
	}
	
	public DatosRequest busqueda(DatosRequest request, BusquedaDto busqueda, String formatoFecha) {
		StringBuilder query = armaQuery(formatoFecha);
		
		if (busqueda.getIdOficina().equals(NIVEL_DELEGACION)) {
    		query.append(" AND VEL.ID_DELEGACION = ").append(busqueda.getIdDelegacion());
    	} else if (busqueda.getIdOficina().equals(NIVEL_VELATORIO) || busqueda.getIdVelatorio() != null) {
    		query.append(" AND SP.ID_VELATORIO = ").append(busqueda.getIdVelatorio());
    	}
		
		if (busqueda.getFecInicial() != null) {
    		query.append(" AND SP.FEC_ELABORACION BETWEEN '" + busqueda.getFecInicial() + "' AND '" + busqueda.getFecFinal() + "'\r\n");
    	}
		if (busqueda.getEjercicioFiscal() != null) {
			query.append(" AND SP.NUM_EJERCICIO_FISCAL = " + busqueda.getEjercicioFiscal() + "\r\n");
		}
		if (busqueda.getIdTipoSolicitud() != null) {
			query.append(" AND SP.ID_TIPO_SOLICITUD = " + busqueda.getIdTipoSolicitud() + "\r\n");
		}
		if (busqueda.getFolioSolicitud() != null) {
			query.append("AND \r\n"
					+ "(\r\n"
					+ "SP.CVE_FOLIO_GASTOS = '" + busqueda.getFolioSolicitud());
			query.append("' \r\n"
					+ "OR\r\n"
					+ "SP.CVE_FOLIO_CONSIGNADOS = '");
			query.append(busqueda.getFolioSolicitud());
			query.append("' \r\n"
					+ "OR\r\n"
					+ "SF.CVE_FOLIO = '");
			query.append(busqueda.getFolioSolicitud());
			query.append("' \r\n) \r\n");
		}
		log.info(query.toString());
		String encoded = DatatypeConverter.printBase64Binary(query.toString().getBytes(StandardCharsets.UTF_8));
		request.getDatos().put(AppConstantes.QUERY, encoded);
    	
    	return request;
	}
	
	public DatosRequest detalle(DatosRequest request, String formatoFecha) throws UnsupportedEncodingException {
		StringBuilder query = new StringBuilder("SELECT ID_SOLICITUD_PAGO AS idSolicitud, \r\n"
				+ "PRV.ID_PROVEEDOR AS idProveedor, \r\n"
				+ "SP.NOM_BENEFICIARIO AS beneficiario,  \r\n"
				+ "SP.ID_TIPO_SOLICITUD AS idTipoSolicitud, \r\n"
				+ "SP.ID_VELATORIO AS idVelatorio, \r\n"
				+ "VEL.DES_VELATORIO AS desVelatorio,  \r\n"
				+ "SP.CVE_FOLIO_GASTOS AS cveFolioGastos, \r\n"
				+ "SP.CVE_FOLIO_CONSIGNADOS AS cveFolioConsignados, \r\n"
				+ "SP.NUM_EJERCICIO_FISCAL AS ejercicioFiscal, \r\n"
				+ "SP.ID_UNIDAD_OPERATIVA AS idUnidadOperativa, \r\n"
				+ "SP.ID_TIPO_SOLICITUD AS idTipoSolicitud, \r\n"
				+ "TIP.DES_TIPO_SOLICITUD AS desTipoSolicitud,  \r\n"
				+ "DATE_FORMAT(SP.FEC_ELABORACION,'");
		query.append(formatoFecha);
		query.append("') AS fecElabora, \r\n"
				+ "PRV.REF_PROVEEDOR AS nomBeneficiario,  \r\n"
				+ "SP.ID_ESTATUS_SOLICITUD AS idEstatusSol,\r\n"
				+ "EST.DES_ESTATUS_SOLICITUD AS desEstatusSolicitud, \r\n"
				+ "SP.IMP_TOTAL AS impTotal,  \r\n"
				+ "SFIB.ID_SUBDIRECCION AS idUnidadOpe, \r\n"
				+ "SFIB.NOM_SUBDIRECCION AS nomUnidadOpe,\r\n"
				+ "SFIB.REF_SUBDIRECCION AS refUnidadOpe,\r\n"
				+ "SP.NOM_DESTINATARIO AS nomDestinatario, \r\n"
				+ "NOM_REMITENTE AS nomRemitente,  \r\n"
				+ "SP.NUM_REFERENCIA_DT AS referenciaTD, \r\n"
				+ "DATE_FORMAT(SP.FEC_INICIAL,'");
		query.append(formatoFecha);
		query.append("') AS fechaInicial, \r\n"
				+ "DATE_FORMAT(SP.FEC_FINAL,'");
		query.append(formatoFecha);
		query.append("') AS fechaFinal, \r\n"
				+ "PRV.REF_BANCO AS banco,  \r\n"
				+ "PRV.NUM_CUENTA AS cuenta, \r\n"
				+ "PRV.CVE_BANCARIA AS claveBancaria, \r\n"
				+ "SP.REF_CONCEPTO AS concepto, \r\n"
				+ "CON.CVE_CONTRATO AS numContrato,  \r\n"
				+ "SP.REF_OBSERVACIONES AS observaciones,\r\n"
				+ "SP.ID_SOLICITUD_PAGO AS folioSolicitud,\r\n"
				+ "(\r\n"
				+ "SELECT\r\n"
				+ "GROUP_CONCAT( CVE_FOLIO SEPARATOR ', ' )\r\n"
				+ "FROM \r\n"
				+ "SVT_SOLICITUD_FOLIO\r\n"
				+ "WHERE \r\n"
				+ "ID_SOLICITUD_PAGO = ");
		query.append( this.getId() );
		query.append( " ) AS foliosFactura,\r\n");
		query.append("IFNULL(\r\n"
				+ "SFIB.NOM_RESPONSABLE,\r\n"
				+ "VEL.NOM_RESPO_SANITARIO\r\n"
				+ ") AS nomResponsable \r\n");
		query.append("FROM SVT_SOLICITUD_PAGO SP  \r\n"
				+ "JOIN SVC_TIPO_SOLICITUD_PAGO TIP ON TIP.ID_TIPO_SOLICITUD = SP.ID_TIPO_SOLICITUD  \r\n"
				+ "JOIN SVC_ESTATUS_SOLICITUD_PAGO EST ON EST.ID_ESTATUS_SOLICITUD = SP.ID_ESTATUS_SOLICITUD  \r\n"
				+ "LEFT JOIN SVC_VELATORIO VEL ON VEL.ID_VELATORIO = SP.ID_VELATORIO  \r\n"
				+ "LEFT JOIN SVT_PROVEEDOR PRV ON PRV.ID_PROVEEDOR = SP.ID_PROVEEDOR  \r\n"
				+ "LEFT JOIN SVT_SUBDIRECCION_FIBESO SFIB ON SFIB. ID_SUBDIRECCION = SP.ID_UNIDAD_OPERATIVA  \r\n"
				+ "LEFT JOIN SVT_CONTRATO CON ON CON.ID_PROVEEDOR = PRV.ID_PROVEEDOR  " );
		
		query.append("WHERE ID_SOLICITUD_PAGO = " + this.getId());
		log.info(query.toString());
		String encoded = DatatypeConverter.printBase64Binary(query.toString().getBytes(StandardCharsets.UTF_8));
		request.getDatos().put(AppConstantes.QUERY, encoded);
		return request;
	}
	
	public DatosRequest detFolios() {
		DatosRequest request = new DatosRequest();
    	Map<String, Object> parametro = new HashMap<>();
		StringBuilder query = new StringBuilder("SELECT CVE_FOLIO AS cveFolio FROM SVT_SOLICITUD_FOLIO ");
		query.append("WHERE ID_SOLICITUD_PAGO = " + this.id);
		log.info(query.toString());
		String encoded = DatatypeConverter.printBase64Binary(query.toString().getBytes(StandardCharsets.UTF_8));
		parametro.put(AppConstantes.QUERY, encoded);
		request.setDatos(parametro);
		return request;
	}
	
	public DatosRequest factura(DatosRequest request, String folioFiscal) {
		StringBuilder query = new StringBuilder("SELECT IFNULL(REF_PARTIDA_PRES,'') AS partidaPres, REF_CUENTA_CONTABLE AS cuentaContable, ");
		query.append("IMP_TOTAL_SERV AS importeTotal FROM SVC_FACTURA WHERE CVE_FOLIO_FISCAL = '" + folioFiscal + "' ");
		log.info(query.toString());
		String encoded = DatatypeConverter.printBase64Binary(query.toString().getBytes(StandardCharsets.UTF_8));
		request.getDatos().put(AppConstantes.QUERY, encoded);
		return request;
	}
	
	public DatosRequest listaVelatorios(BusquedaDto busqueda) {
    	DatosRequest request = new DatosRequest();
    	Map<String, Object> parametro = new HashMap<>();
    	StringBuilder query = new StringBuilder("SELECT sv.ID_VELATORIO AS idVelatorio, sv.DES_VELATORIO AS desVelatorio, sv.NOM_RESPO_SANITARIO AS nomResponsable ");
	    query.append("FROM SVC_VELATORIO sv ");
    	log.info(query.toString());
    	String encoded = DatatypeConverter.printBase64Binary(query.toString().getBytes(StandardCharsets.UTF_8));
		parametro.put(AppConstantes.QUERY, encoded);
		request.setDatos(parametro);
    	return request;
	}
	
	public DatosRequest listaUnidadesOpe() {
    	DatosRequest request = new DatosRequest();
    	Map<String, Object> parametro = new HashMap<>();
    	StringBuilder query = new StringBuilder("SELECT ID_SUBDIRECCION AS idSubdireccion, NOM_SUBDIRECCION AS nomSubdireccion, ");
    	query.append("REF_SUBDIRECCION AS referencia, NOM_RESPONSABLE AS nomResponsable ");
    	query.append("FROM SVT_SUBDIRECCION_FIBESO");
    	log.info(query.toString());
    	String encoded = DatatypeConverter.printBase64Binary(query.toString().getBytes(StandardCharsets.UTF_8));
		parametro.put(AppConstantes.QUERY, encoded);
		request.setDatos(parametro);
    	return request;
	}
	
	public DatosRequest listaDatosBanco() {
    	DatosRequest request = new DatosRequest();
    	Map<String, Object> parametro = new HashMap<>();
    	StringBuilder query = new StringBuilder("SELECT PRV.ID_PROVEEDOR AS idProveedor, REF_PROVEEDOR AS nomProveedor, ");
    	query.append("REF_BANCO AS banco, CVE_BANCARIA AS cveBancaria, PRV.NUM_CUENTA AS cuenta , CVE_CONTRATO AS numeroContrato ");
    	query.append("FROM SVT_PROVEEDOR PRV JOIN SVT_CONTRATO CON ON CON.ID_PROVEEDOR = PRV.ID_PROVEEDOR ");
    	log.info(query.toString());
    	String encoded = DatatypeConverter.printBase64Binary(query.toString().getBytes(StandardCharsets.UTF_8));
		parametro.put(AppConstantes.QUERY, encoded);
		request.setDatos(parametro);
    	return request;
	}
	
	public DatosRequest crearSolicitud(SolicitudPagoDto solicPagoDto, String formatoFecha) {
		DatosRequest request = new DatosRequest();
		Map<String, Object> parametro = new HashMap<>();
		final QueryHelper q = new QueryHelper("INSERT INTO SVT_SOLICITUD_PAGO");
		q.agregarParametroValues("ID_TIPO_SOLICITUD", "" + solicPagoDto.getIdTipoSolic());
		q.agregarParametroValues("CVE_FOLIO_GASTOS", setValor(solicPagoDto.getCveFolioGastos()));
		q.agregarParametroValues("CVE_FOLIO_CONSIGNADOS", setValor(solicPagoDto.getCveFolioConsignados()));
		q.agregarParametroValues("ID_UNIDAD_OPERATIVA", "" + solicPagoDto.getIdUnidadOperativa());
		q.agregarParametroValues("NOM_DESTINATARIO ", setValor(solicPagoDto.getNomDestinatario()));
		q.agregarParametroValues("NOM_REMITENTE ", setValor(solicPagoDto.getNomRemitente()));
		q.agregarParametroValues("NUM_REFERENCIA_DT", "" + solicPagoDto.getNumReferencia());
		q.agregarParametroValues("ID_PROVEEDOR", "" + solicPagoDto.getIdProveedor());
		q.agregarParametroValues("NOM_BENEFICIARIO ", setValor(solicPagoDto.getBeneficiario()));
		q.agregarParametroValues("FEC_ELABORACION", "STR_TO_DATE(" + setValor(solicPagoDto.getFechaElabora()) + ",'" + formatoFecha + "')");
		q.agregarParametroValues("FEC_INICIAL", "STR_TO_DATE(" + setValor(solicPagoDto.getFechaInicial()) + ",'" + formatoFecha + "')");
		q.agregarParametroValues("FEC_FINAL", "STR_TO_DATE(" + setValor(solicPagoDto.getFechaFinal()) + ",'" + formatoFecha + "')");
		q.agregarParametroValues("REF_CONCEPTO ", setValor(solicPagoDto.getConcepto()));
		q.agregarParametroValues("REF_OBSERVACIONES ", setValor(solicPagoDto.getObservaciones()));
		q.agregarParametroValues("ID_VELATORIO", "" + solicPagoDto.getIdVelatorio());
		q.agregarParametroValues("NUM_EJERCICIO_FISCAL", "" + solicPagoDto.getEjercicioFiscal());
		q.agregarParametroValues("IMP_TOTAL", "" + solicPagoDto.getImpTotal());
		q.agregarParametroValues("ID_ESTATUS_SOLICITUD", "1");
		q.agregarParametroValues("ID_USUARIO_ALTA", "" + this.idUsuarioAlta);
		
		String query = q.obtenerQueryInsertar();
		log.info(query.toString());
		String encoded = DatatypeConverter.printBase64Binary(query.getBytes(StandardCharsets.UTF_8));
		parametro.put(AppConstantes.QUERY, encoded);
		request.setDatos(parametro);
		
		return request;
	}
	
	public DatosRequest agregarFolios(SolicitudFoliosDto solicitudFoliosDto, Integer idUsuarioAlta) {
		DatosRequest request = new DatosRequest();
		Map<String, Object> parametro = new HashMap<>();
		StringBuilder query = new StringBuilder("");
		for (String cveFolio : solicitudFoliosDto.getCveFolios()) {
			query.append("INSERT INTO SVT_SOLICITUD_FOLIO (ID_SOLICITUD_PAGO, CVE_FOLIO, ID_USUARIO_ALTA, FEC_ALTA) "
					+ " VALUES (" + solicitudFoliosDto.getIdSolicitud() + ", '" + cveFolio +"'," + idUsuarioAlta + ", NOW() );$$");
		}
		log.info(query.toString());
		String encoded = DatatypeConverter.printBase64Binary(query.toString().getBytes(StandardCharsets.UTF_8));
		parametro.put(AppConstantes.QUERY, encoded);
		parametro.put("separador", "$$");
		request.setDatos(parametro);
				
		return request;
	}
		
	public DatosRequest aprobarSolicitud(CambioEstatusResponse cambioEstatus) {
	    DatosRequest request = new DatosRequest();
	    Map<String, Object> parametro = new HashMap<>();
	    String query =" UPDATE SVT_SOLICITUD_PAGO SET ID_ESTATUS_SOLICITUD = 2, ID_USUARIO_MODIFICA = " + cambioEstatus.getIdUsuario() +
	    		", FEC_ACTUALIZACION = CURRENT_TIMESTAMP() WHERE ID_SOLICITUD_PAGO = " + cambioEstatus.getIdSolicitud();
	    log.info(query.toString());
	    String encoded = DatatypeConverter.printBase64Binary(query.getBytes(StandardCharsets.UTF_8));
		parametro.put(AppConstantes.QUERY, encoded);
		request.setDatos(parametro);
		return request;
    }
	
	public DatosRequest cancelarSolicitud(CambioEstatusResponse cambioEstatus) {
	    DatosRequest request = new DatosRequest();
	    Map<String, Object> parametro = new HashMap<>();
	    String query =" UPDATE SVT_SOLICITUD_PAGO SET ID_ESTATUS_SOLICITUD = 0, ID_USUARIO_MODIFICA = " + cambioEstatus.getIdUsuario() +
	    		", FEC_ACTUALIZACION = CURRENT_TIMESTAMP(), REF_MOTIVO_CANCELACION = '" + cambioEstatus.getMotivo() + "' " +
	    	    "WHERE ID_SOLICITUD_PAGO = " + cambioEstatus.getIdSolicitud();
	    log.info(query.toString());
	    String encoded = DatatypeConverter.printBase64Binary(query.getBytes(StandardCharsets.UTF_8));
		parametro.put(AppConstantes.QUERY, encoded);
		request.setDatos(parametro);
		return request;
    }
	
	public DatosRequest rechazarSolicitud(CambioEstatusResponse cambioEstatus) {
	    DatosRequest request = new DatosRequest();
	    Map<String, Object> parametro = new HashMap<>();
	    String query =" UPDATE SVT_SOLICITUD_PAGO SET ID_ESTATUS_SOLICITUD = 3, ID_USUARIO_MODIFICA = " + cambioEstatus.getIdUsuario() +
	    		", FEC_ACTUALIZACION = CURRENT_TIMESTAMP(), REF_MOTIVO_RECHAZO = '" + cambioEstatus.getMotivo() + "' " +
	    	    "WHERE ID_SOLICITUD_PAGO = " + cambioEstatus.getIdSolicitud();
	    log.info(query.toString());
	    String encoded = DatatypeConverter.printBase64Binary(query.getBytes(StandardCharsets.UTF_8));
		parametro.put(AppConstantes.QUERY, encoded);
		request.setDatos(parametro);
		return request;
    }
	
	public DatosRequest datosFormato(DatosRequest request, DatosFormatoDto reporteDto, String formatoFecha) throws UnsupportedEncodingException {
		StringBuilder query = new StringBuilder("");
		if (reporteDto.getIdUnidadOperativa() != null) {
		    query.append("SELECT sfb.NOM_SUBDIRECCION AS unidadAdmOpe, sfb.REF_SUBDIRECCION AS referenciaUnidad, sp.NUM_REFERENCIA_DT AS refDirTec,  ");
		    query.append("prv.REF_PROVEEDOR AS beneficiario, sp.REF_CONCEPTO AS concepto, sp.REF_OBSERVACIONES AS observaciones, con.CVE_CONTRATO AS numContrato,  ");
		    query.append("DATE_FORMAT(sp.FEC_ELABORACION,'" + formatoFecha + "') AS fechaElabora, sp.NOM_REMITENTE AS remitente,  ");
		    query.append("CONCAT('DEL: ',DATE_FORMAT(sp.FEC_INICIAL,'" + formatoFecha + "'),' AL ',DATE_FORMAT(sp.FEC_INICIAL,'" + formatoFecha + "')) AS periodo,   ");
		    query.append("CONCAT('$ ', FORMAT(sp.IMP_TOTAL,2)) AS importe, CONCAT(prv.REF_BANCO,' ',prv.CVE_BANCARIA,' ') AS datosBancarios  ");
		    query.append(",sfb.NOM_RESPONSABLE AS solicitado ");
		    query.append("FROM SVT_SOLICITUD_PAGO sp  ");
		    query.append("JOIN SVT_SUBDIRECCION_FIBESO sfb ON sfb.ID_SUBDIRECCION = sp.ID_UNIDAD_OPERATIVA  ");
		    query.append("LEFT JOIN SVT_PROVEEDOR prv ON prv.ID_PROVEEDOR = sp.ID_PROVEEDOR ");
		    query.append("LEFT JOIN SVT_CONTRATO con ON con.ID_PROVEEDOR = prv.ID_PROVEEDOR ");
		    query.append("WHERE sp.ID_SOLICITUD_PAGO = " + reporteDto.getIdSolicitud());
		} else {
			query.append("SELECT vel.DES_VELATORIO AS unidadAdmOpe, vel.DES_VELATORIO AS referenciaUnidad, sp.NUM_REFERENCIA_DT AS refDirTec,  ");
			query.append("prv.REF_PROVEEDOR AS beneficiario, sp.REF_CONCEPTO AS concepto, sp.REF_OBSERVACIONES AS observaciones, con.CVE_CONTRATO AS numContrato,  ");
		    query.append("DATE_FORMAT(sp.FEC_ELABORACION,'" + formatoFecha + "') AS fechaElabora, sp.NOM_REMITENTE AS remitente,  ");
		    query.append("CONCAT('DEL: ',DATE_FORMAT(sp.FEC_INICIAL,'" + formatoFecha + "'),' AL',DATE_FORMAT(sp.FEC_INICIAL,'" + formatoFecha + "')) AS periodo,   ");
		    query.append("CONCAT('$ ', FORMAT(sp.IMP_TOTAL,2)) AS importe, CONCAT(prv.REF_BANCO,' ',prv.CVE_BANCARIA,' ') AS datosBancarios, vel.NOM_RESPO_SANITARIO AS solicitado  ");
		    query.append("FROM SVT_SOLICITUD_PAGO sp  ");
			query.append("JOIN SVC_VELATORIO vel ON vel.ID_VELATORIO = sp.ID_VELATORIO  ");
			query.append("LEFT JOIN SVT_PROVEEDOR prv ON prv.ID_PROVEEDOR = sp.ID_PROVEEDOR ");
			query.append("LEFT JOIN SVT_CONTRATO con ON con.ID_PROVEEDOR = prv.ID_PROVEEDOR ");
			query.append("WHERE sp.ID_SOLICITUD_PAGO = " + reporteDto.getIdSolicitud());
		}
		log.info(query.toString());
		String encoded = DatatypeConverter.printBase64Binary(query.toString().getBytes(StandardCharsets.UTF_8));
		request.getDatos().put(AppConstantes.QUERY, encoded);
		return request;
	}
	
	public Map<String, Object> generarFormato(DatosFormatoDto reporteDto,String nombrePdfFormato) {
		Map<String, Object> envioDatos = new HashMap<>();
		
		envioDatos.put("unOpAd", reporteDto.getUnidadAdmOpe());
		envioDatos.put("dirTecnica", "Dra. Cristinne Leo Martel");
		envioDatos.put("cargo", reporteDto.getRemitente());
		envioDatos.put("refUnOpAd", reporteDto.getReferenciaUnidad());
		envioDatos.put("refDirTec", reporteDto.getRefDirTec().toString());
		envioDatos.put("beneficiario", reporteDto.getBeneficiario());
		if (reporteDto.getIdTipoSolicitud() == 5 || reporteDto.getIdTipoSolicitud() == 6) {
			envioDatos.put("numContrato", reporteDto.getNumContrato());
		}
		envioDatos.put("concepto", reporteDto.getConcepto());
		envioDatos.put("fecElaboracion", reporteDto.getFechaElabora());
		if (reporteDto.getIdTipoSolicitud() == 3 || reporteDto.getIdTipoSolicitud() == 5) {
			envioDatos.put("periodo", reporteDto.getPeriodo());
		}
		envioDatos.put("importe", reporteDto.getImporte().toString()); 
		envioDatos.put("canLetra", reporteDto.getCantidadLetra());
		envioDatos.put("datBancarios", reporteDto.getDatosBancarios());
		envioDatos.put("observaciones", reporteDto.getObservaciones());
		envioDatos.put("solicitado", reporteDto.getSolicitado());
		envioDatos.put("subAdmin", "Lic. Armando Julio Mosco Neria");
		envioDatos.put("subComer", "Lic. Ana Cecilia Victoria Ochoa");
		envioDatos.put("subFinanzas", "C.P. Cesar Omar Carranza Martínez");
		envioDatos.put("tipoReporte", reporteDto.getTipoReporte());
		envioDatos.put("rutaNombreReporte", nombrePdfFormato);
		
		return envioDatos;
	}
	
	public Map<String, Object> generarReporte(DatosRequest request, BusquedaDto reporteDto,String nombrePdfReportes, String formatoFecha) {
		Map<String, Object> envioDatos = new HashMap<>();
		StringBuilder condicion = new StringBuilder(" ");

		DatosRequest dr= busqueda(request, reporteDto, formatoFecha);
		
		if (reporteDto.getIdOficina().equals(NIVEL_DELEGACION)) {
			condicion.append(" AND VEL.ID_DELEGACION = ").append(reporteDto.getIdDelegacion());
    	} else if (reporteDto.getIdVelatorio() != null) {
			condicion.append(" AND SP.ID_VELATORIO = ").append(reporteDto.getIdVelatorio());
		}
		if (reporteDto.getFecInicial() != null) {
			condicion.append(" AND DATE(SP.FEC_ELABORACION) BETWEEN STR_TO_DATE('" + reporteDto.getFecInicial() + "','" + formatoFecha + "') AND STR_TO_DATE('" + reporteDto.getFecFinal() + "','" + formatoFecha + "')");
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
		envioDatos.put("periodo", Utilidades.periodo(reporteDto.getFecInicial(), reporteDto.getFecFinal()));
		String str = queryDecoded(dr.getDatos());
		envioDatos.put("condicion", str);
		envioDatos.put("tipoReporte", reporteDto.getTipoReporte());
		envioDatos.put("rutaNombreReporte", nombrePdfReportes);
		if (reporteDto.getTipoReporte().equals("xls")) {
			envioDatos.put("IS_IGNORE_PAGINATION", true);
		}
		
		return envioDatos;
	}
	    		
	private StringBuilder armaQuery(String formatoFecha) {
		
		StringBuilder query = new StringBuilder("SELECT\r\n"
				+ "LPAD(SP.ID_SOLICITUD_PAGO,4,0) AS idSolicitud,\r\n"
				+ "VEL.DES_VELATORIO AS desVelatorio,\r\n"
				+ "NULLIF(SP.CVE_FOLIO_GASTOS,SP.CVE_FOLIO_CONSIGNADOS) AS cveFolio,\r\n"
				+ "SF.CVE_FOLIO  AS cveFolios,\r\n"
				+ "SP.ID_UNIDAD_OPERATIVA AS idUnidadOperartiva, \r\n"
				+ "SP.ID_VELATORIO AS idVelatorio, \r\n"
				+ "SP.IMP_TOTAL AS importe,  \r\n"
				+ "SP.NUM_EJERCICIO_FISCAL AS ejercicioFiscal, \r\n"
				+ "DATE_FORMAT(SP.FEC_ELABORACION,'");
		query.append(formatoFecha);
		query.append("') AS fecElaboracion,  \r\n"
				+ "SP.ID_TIPO_SOLICITUD AS idTipoSolicitid, \r\n"
				+ "TIP.DES_TIPO_SOLICITUD AS desTipoSolicitud, \r\n"
				+ "PRV.ID_PROVEEDOR AS idProveedor,  \r\n"
				+ "IFNULL(\r\n"
				+ "IFNULL(\r\n"
				+ "PRV.REF_PROVEEDOR,SP.NOM_BENEFICIARIO),'') AS nomBeneficiario,  \r\n"
				+ "SP.ID_ESTATUS_SOLICITUD AS idEstatus, \r\n"
				+ "EST.DES_ESTATUS_SOLICITUD AS desEstatusSolicitud,  \r\n"
				+ "SP.REF_MOTIVO_CANCELACION AS motCancelacion, \r\n"
				+ "SP.REF_MOTIVO_RECHAZO AS motRechazo \r\n"
				+ "FROM \r\n"
				+ "SVT_SOLICITUD_PAGO SP  \r\n"
				+ "JOIN SVC_TIPO_SOLICITUD_PAGO TIP ON TIP.ID_TIPO_SOLICITUD = SP.ID_TIPO_SOLICITUD  \r\n"
				+ "JOIN SVC_ESTATUS_SOLICITUD_PAGO EST ON EST.ID_ESTATUS_SOLICITUD = SP.ID_ESTATUS_SOLICITUD  \r\n"
				+ "LEFT JOIN SVC_VELATORIO VEL ON VEL.ID_VELATORIO = SP.ID_VELATORIO  \r\n"
				+ "LEFT JOIN SVT_PROVEEDOR PRV ON PRV.ID_PROVEEDOR = SP.ID_PROVEEDOR \r\n"
				+ "LEFT JOIN  SVT_SOLICITUD_FOLIO SF ON SF.ID_SOLICITUD_PAGO = SP.ID_SOLICITUD_PAGO\r\n"
				+ "WHERE 1 = 1 \r\n"
				);
		log.info(query.toString());
		return query;
	}
	
	private String setValor(String valor) {
        if (valor == null || valor.equals("")) {
            return "NULL";
        } else {
            return "'" + valor + "'";
        }
    }
	private String queryDecoded (Map<String, Object> envioDatos ) {
		return new String(DatatypeConverter.parseBase64Binary(envioDatos.get(AppConstantes.QUERY).toString()));
	}
}