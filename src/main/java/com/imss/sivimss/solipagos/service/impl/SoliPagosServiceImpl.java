package com.imss.sivimss.solipagos.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.imss.sivimss.solipagos.util.LogUtil;
import com.imss.sivimss.solipagos.util.ProviderServiceRestTemplate;
import com.google.gson.Gson;
import com.imss.sivimss.solipagos.model.request.BusquedaDto;
import com.imss.sivimss.solipagos.model.request.DatosFormatoDto;
import com.imss.sivimss.solipagos.model.request.SolicitudPagoDto;
import com.imss.sivimss.solipagos.util.AppConstantes;
import com.imss.sivimss.solipagos.exception.BadRequestException;
import com.imss.sivimss.solipagos.model.request.UsuarioDto;
import com.imss.sivimss.solipagos.model.response.CambioEstatusResponse;
import com.imss.sivimss.solipagos.util.MensajeResponseUtil;
import com.imss.sivimss.solipagos.beans.SolicitudPago;
import com.imss.sivimss.solipagos.service.SoliPagosService;
import com.imss.sivimss.solipagos.util.DatosRequest;
import com.imss.sivimss.solipagos.util.Response;

@Service
public class SoliPagosServiceImpl implements SoliPagosService {
	
	@Value("${endpoints.dominio}")
	private String urlDominio;
	
    private static final String PAGINADO = "/paginado";
	
	private static final String CONSULTA = "/consulta";
	
	private static final String CREAR = "/crear";
	
	private static final String ALTA = "alta";
	
	private static final String ACTUALIZAR = "/actualizar";
	
	private static final String MODIFICACION = "modificacion";
	
	private static final String ERROR_DESCARGA = "64";
	
	@Value("${endpoints.generico-reportes}")
	private String urlReportes;
	
	@Value("${formato_fecha}")
	private String formatoFecha;
	
	private static final String INFONOENCONTRADA = "45";
	
	private static final String FOLIOFISCALNOEXISTE = "143";
	
    private static final String NOMBREPDFREPORTE = "reportes/generales/ReporteSolicitudPagos.jrxml";
    
    private static final String[] NOMBREPDFFORMATOS = {"reportes/generales/FormatoSolicitudBienesServ.jrxml",
    		                                           "reportes/generales/FormatoSolicitudComprobacion.jrxml",
    		                                           "reportes/generales/FormatoSolicitudReembolsoFondoFijo.jrxml",
    		                                           "reportes/generales/FormatoSolicitudPago.jrxml",
    		                                           "reportes/generales/FormatoSolicitudPagoConsignante.jrxml",
    		                                           "reportes/generales/FormatoSolicitudPagoContrato.jrxml"};
	
	@Autowired
	private LogUtil logUtil;
	
	@Autowired
	private ProviderServiceRestTemplate providerRestTemplate;
	
	@Autowired
	private ModelMapper modelMapper;
	
	private static final Logger log = LoggerFactory.getLogger(SoliPagosServiceImpl.class);

	@Override
	public Response<Object> listaEjercicios(DatosRequest request, Authentication authentication) throws IOException {
		SolicitudPago solicitudPago = new SolicitudPago();
		Response<Object> response = null;
		
		try {
		    response = providerRestTemplate.consumirServicio(solicitudPago.listaEjercicios().getDatos(), urlDominio + CONSULTA, authentication);
		    ArrayList<LinkedHashMap> datos1 = (ArrayList) response.getDatos();
		    Integer anioActual = (Integer) datos1.get(0).get("anio");
		    ArrayList<Integer> arrAnios = new ArrayList<Integer>();
		    for (int iter=anioActual;iter>=2022;iter--) {
		        arrAnios.add(iter);
		    }
		    response.setDatos(arrAnios);
		    
		} catch (Exception e) {
			log.error(e.getMessage());
        	logUtil.crearArchivoLog(Level.SEVERE.toString(), this.getClass().getSimpleName(), this.getClass().getPackage().toString(), e.getMessage(), CONSULTA, authentication);
			return null;
        }
		
		return response;
	}

	@Override
	public Response<Object> listaTiposSoli(DatosRequest request, Authentication authentication) throws IOException {
		Gson gson = new Gson();
		SolicitudPago solicitudPago = new SolicitudPago();
		
		String datosJson = String.valueOf(request.getDatos().get(AppConstantes.DATOS));
		BusquedaDto busqueda = gson.fromJson(datosJson, BusquedaDto.class);
		Response<Object> response = null;
		
		try {
		    response = providerRestTemplate.consumirServicio(solicitudPago.listaTiposSoli(busqueda).getDatos(), urlDominio + CONSULTA, authentication);
		} catch (Exception e) {
			log.error(e.getMessage());
        	logUtil.crearArchivoLog(Level.SEVERE.toString(), this.getClass().getSimpleName(), this.getClass().getPackage().toString(), e.getMessage(), CONSULTA, authentication);
			return null;
        }
		
		return response;
	}

	@Override
	public Response<Object> consulta(DatosRequest request, Authentication authentication) throws IOException {
		Gson gson = new Gson();
		SolicitudPago solicitudPago = new SolicitudPago();
		
		String datosJson = String.valueOf(authentication.getPrincipal());
		BusquedaDto busqueda = gson.fromJson(datosJson, BusquedaDto.class);
		Response<Object> response = null;
		
		try {
		    response = providerRestTemplate.consumirServicio(solicitudPago.consulta(request, busqueda, formatoFecha).getDatos(), urlDominio + PAGINADO, authentication);
		} catch (Exception e) {
        	log.error(e.getMessage());
        	logUtil.crearArchivoLog(Level.SEVERE.toString(), this.getClass().getSimpleName(), this.getClass().getPackage().toString(), e.getMessage(), CONSULTA, authentication);
			return null;
        }
		
		return response;
	}

	@Override
	public Response<Object> busqueda(DatosRequest request, Authentication authentication) throws IOException {
		Gson gson = new Gson();
		SolicitudPago solicitudPago = new SolicitudPago();
		
		String datosJson = String.valueOf(authentication.getPrincipal());
		BusquedaDto buscaUser = gson.fromJson(datosJson, BusquedaDto.class);
		
		datosJson = String.valueOf(request.getDatos().get(AppConstantes.DATOS));
		BusquedaDto busqueda = gson.fromJson(datosJson, BusquedaDto.class);
		busqueda.setIdOficina(buscaUser.getIdOficina());
		busqueda.setIdDelegacion(buscaUser.getIdDelegacion());
		Response<Object> response = null;
		
		try {
			response = providerRestTemplate.consumirServicio(solicitudPago.busqueda(request, busqueda, formatoFecha).getDatos(), urlDominio + PAGINADO, authentication);
			ArrayList datos1 = (ArrayList) ((LinkedHashMap) response.getDatos()).get("content");
			if (datos1.isEmpty()) {
				response.setMensaje(INFONOENCONTRADA);
		    }
		} catch (Exception e) {
        	log.error(e.getMessage());
        	logUtil.crearArchivoLog(Level.SEVERE.toString(), this.getClass().getSimpleName(), this.getClass().getPackage().toString(), e.getMessage(), CONSULTA, authentication);
			return null;
        }
		
		return response;
	}

	@Override
	public Response<Object> detalle(DatosRequest request, Authentication authentication) throws IOException {
		Gson gson = new Gson();

		String datosJson = String.valueOf(request.getDatos().get(AppConstantes.DATOS));
		SolicitudPagoDto solicitudPagoDto = gson.fromJson(datosJson, SolicitudPagoDto.class);
		if (solicitudPagoDto.getIdSolicitud() == null ) {
			throw new BadRequestException(HttpStatus.BAD_REQUEST, "Informacion incompleta");
		}
		SolicitudPago solicitudPago = new SolicitudPago();
		solicitudPago.setId(solicitudPagoDto.getIdSolicitud());
		try {
		    return providerRestTemplate.consumirServicio(solicitudPago.detalle(request, formatoFecha).getDatos(), urlDominio + CONSULTA, authentication);
		} catch (Exception e) {
			log.error(e.getMessage());
	       	logUtil.crearArchivoLog(Level.SEVERE.toString(), this.getClass().getSimpleName(), this.getClass().getPackage().toString(), e.getMessage(), CONSULTA, authentication);
			return null;
		}

	}

	public Response<Object> factura(DatosRequest request, Authentication authentication) throws IOException {
		Gson gson = new Gson();

		String datosJson = String.valueOf(request.getDatos().get(AppConstantes.DATOS));
		SolicitudPagoDto solicitudPagoDto = gson.fromJson(datosJson, SolicitudPagoDto.class);
		if (solicitudPagoDto.getCveFolioGastos() == null ) {
			throw new BadRequestException(HttpStatus.BAD_REQUEST, "Informacion incompleta");
		}
		SolicitudPago solicitudPago = new SolicitudPago();
		solicitudPago.setFolioFiscal(solicitudPagoDto.getCveFolioGastos());
		Response<Object> response = null;
		try {
			response = providerRestTemplate.consumirServicio(solicitudPago.factura(request).getDatos(), urlDominio + CONSULTA, authentication);
			ArrayList datos1 = (ArrayList) response.getDatos();
			if (datos1.isEmpty()) {
				response.setMensaje(FOLIOFISCALNOEXISTE);
		    }
			return response;
		} catch (Exception e) {
			log.error(e.getMessage());
	       	logUtil.crearArchivoLog(Level.SEVERE.toString(), this.getClass().getSimpleName(), this.getClass().getPackage().toString(), e.getMessage(), CONSULTA, authentication);
			return null;
		}
	}
	
	@Override
	public Response<Object> listaVelatorios(DatosRequest request, Authentication authentication) throws IOException {
		Gson gson = new Gson();
		SolicitudPago solicitudPago = new SolicitudPago();
		String datosJson = String.valueOf(authentication.getPrincipal());
		BusquedaDto busqueda = gson.fromJson(datosJson, BusquedaDto.class);
		
		try {
			return providerRestTemplate.consumirServicio(solicitudPago.listaVelatorios(busqueda).getDatos(), urlDominio + CONSULTA, authentication);
		} catch (Exception e) {
		    log.error(e.getMessage());
       	    logUtil.crearArchivoLog(Level.SEVERE.toString(), this.getClass().getSimpleName(), this.getClass().getPackage().toString(), e.getMessage(), CONSULTA, authentication);
		    return null;
	    }
		
	}

	@Override
	public Response<Object> listaUnidadesOpe(DatosRequest request, Authentication authentication) throws IOException {
		
		try {
			return providerRestTemplate.consumirServicio(new SolicitudPago().listaUnidadesOpe().getDatos(), urlDominio + CONSULTA, authentication);
		} catch (Exception e) {
		    log.error(e.getMessage());
       	    logUtil.crearArchivoLog(Level.SEVERE.toString(), this.getClass().getSimpleName(), this.getClass().getPackage().toString(), e.getMessage(), CONSULTA, authentication);
		    return null;
	    }
		
	}
	
	@Override
	public Response<Object> listaDatosBanco(DatosRequest request, Authentication authentication) throws IOException {
		
		try {
			return providerRestTemplate.consumirServicio(new SolicitudPago().listaDatosBanco().getDatos(), urlDominio + CONSULTA, authentication);
		} catch (Exception e) {
		    log.error(e.getMessage());
       	    logUtil.crearArchivoLog(Level.SEVERE.toString(), this.getClass().getSimpleName(), this.getClass().getPackage().toString(), e.getMessage(), CONSULTA, authentication);
		    return null;
	    }
		
	}
	
	@Override
	public Response<Object> agregarSoli(DatosRequest request, Authentication authentication) throws IOException {
		Gson gson = new Gson();

		String datosJson = String.valueOf(request.getDatos().get(AppConstantes.DATOS));
		SolicitudPagoDto solicitudDto = gson.fromJson(datosJson, SolicitudPagoDto.class);
		UsuarioDto usuarioDto = gson.fromJson((String) authentication.getPrincipal(), UsuarioDto.class);
		SolicitudPago solicitudPago = new SolicitudPago();
		solicitudPago.setIdUsuarioAlta(usuarioDto.getIdUsuario());
		
		try {
			return providerRestTemplate.consumirServicio(solicitudPago.crearSolicitud(solicitudDto, formatoFecha).getDatos(), urlDominio + CREAR, authentication);
		} catch (Exception e) {
			log.error(e.getMessage());
	       	logUtil.crearArchivoLog(Level.SEVERE.toString(), this.getClass().getSimpleName(), this.getClass().getPackage().toString(), e.getMessage(), ALTA, authentication);
			return null;
	     }
		
	}
	
	@Override
	public Response<Object> generarPdf(DatosRequest request, Authentication authentication) throws IOException {
		Gson gson = new Gson();
		
		String datosJson = String.valueOf(request.getDatos().get(AppConstantes.DATOS));
		DatosFormatoDto reporteDto = gson.fromJson(datosJson, DatosFormatoDto.class);
		if (reporteDto.getIdSolicitud() == null || reporteDto.getIdTipoSolicitud() == null) {
			throw new BadRequestException(HttpStatus.BAD_REQUEST, "Informacion incompleta");
		} else if ((reporteDto.getIdVelatorio() == null && reporteDto.idUnidadOperativa == null) || reporteDto.getIdTipoSolicitud() > 6) {
			throw new BadRequestException(HttpStatus.BAD_REQUEST, "Informacion no valida");
		}
		
		Map<String, Object> envioDatos = new SolicitudPago().generarFormato(reporteDto, NOMBREPDFFORMATOS[reporteDto.getIdTipoSolicitud()-1]);
		Response<Object> response =  providerRestTemplate.consumirServicioReportes(envioDatos, urlReportes, authentication);
		return MensajeResponseUtil.mensajeConsultaResponse(response, ERROR_DESCARGA);
	}

	@Override
	public Response<Object> aprobarSoli(DatosRequest request, Authentication authentication) throws IOException {
        Gson gson = new Gson();
		
		String datosJson = String.valueOf(request.getDatos().get(AppConstantes.DATOS));
		UsuarioDto usuarioDto = gson.fromJson((String) authentication.getPrincipal(), UsuarioDto.class);

		datosJson = String.valueOf(request.getDatos().get(AppConstantes.DATOS));
		CambioEstatusResponse cambioEstatus = gson.fromJson(datosJson, CambioEstatusResponse.class);
		cambioEstatus.setIdUsuario(usuarioDto.getIdUsuario());
		if (cambioEstatus.getIdSolicitud() == null) {
			throw new BadRequestException(HttpStatus.BAD_REQUEST, "Informacion incompleta");
		}
		SolicitudPago solicitudPago = new SolicitudPago();
		
		try {
			return providerRestTemplate.consumirServicio(solicitudPago.aprobarSolicitud(cambioEstatus).getDatos(), urlDominio + ACTUALIZAR, authentication);
		} catch (Exception e) {
			log.error(e.getMessage());
			logUtil.crearArchivoLog(Level.SEVERE.toString(), this.getClass().getSimpleName(), this.getClass().getPackage().toString(), e.getMessage(), MODIFICACION, authentication);
			return null;
		}
	
	}
	
	@Override
	public Response<Object> cancelarSoli(DatosRequest request, Authentication authentication) throws IOException {
        Gson gson = new Gson();
		
		String datosJson = String.valueOf(request.getDatos().get(AppConstantes.DATOS));
		UsuarioDto usuarioDto = gson.fromJson((String) authentication.getPrincipal(), UsuarioDto.class);

		datosJson = String.valueOf(request.getDatos().get(AppConstantes.DATOS));
		CambioEstatusResponse cambioEstatus = gson.fromJson(datosJson, CambioEstatusResponse.class);
		cambioEstatus.setIdUsuario(usuarioDto.getIdUsuario());
		if (cambioEstatus.getIdSolicitud() == null || cambioEstatus.getMotivo() == null) {
			throw new BadRequestException(HttpStatus.BAD_REQUEST, "Informacion incompleta");
		}
		SolicitudPago solicitudPago = new SolicitudPago();
		
		try {
			return providerRestTemplate.consumirServicio(solicitudPago.cancelarSolicitud(cambioEstatus).getDatos(), urlDominio + ACTUALIZAR, authentication);
		} catch (Exception e) {
			log.error(e.getMessage());
			logUtil.crearArchivoLog(Level.SEVERE.toString(), this.getClass().getSimpleName(), this.getClass().getPackage().toString(), e.getMessage(), MODIFICACION, authentication);
			return null;
		}
	
	}
	
	@Override
	public Response<Object> rechazarSoli(DatosRequest request, Authentication authentication) throws IOException {
        Gson gson = new Gson();
		
		String datosJson = String.valueOf(request.getDatos().get(AppConstantes.DATOS));
		UsuarioDto usuarioDto = gson.fromJson((String) authentication.getPrincipal(), UsuarioDto.class);

		datosJson = String.valueOf(request.getDatos().get(AppConstantes.DATOS));
		CambioEstatusResponse cambioEstatus = gson.fromJson(datosJson, CambioEstatusResponse.class);
		cambioEstatus.setIdUsuario(usuarioDto.getIdUsuario());
		if (cambioEstatus.getIdSolicitud() == null || cambioEstatus.getMotivo() == null) {
			throw new BadRequestException(HttpStatus.BAD_REQUEST, "Informacion incompleta");
		}
		SolicitudPago solicitudPago = new SolicitudPago();
		
		try {
			return providerRestTemplate.consumirServicio(solicitudPago.rechazarSolicitud(cambioEstatus).getDatos(), urlDominio + ACTUALIZAR, authentication);
		} catch (Exception e) {
			log.error(e.getMessage());
			logUtil.crearArchivoLog(Level.SEVERE.toString(), this.getClass().getSimpleName(), this.getClass().getPackage().toString(), e.getMessage(), MODIFICACION, authentication);
			return null;
		}
	}

	@Override
	public Response<Object> descargarDocto(DatosRequest request, Authentication authentication) throws IOException {
		Gson gson = new Gson();
		
		String datosJson = String.valueOf(authentication.getPrincipal());
		BusquedaDto buscaUser = gson.fromJson(datosJson, BusquedaDto.class);
		
		datosJson = String.valueOf(request.getDatos().get(AppConstantes.DATOS));
		BusquedaDto reporteDto = gson.fromJson(datosJson, BusquedaDto.class);
		reporteDto.setIdOficina(buscaUser.getIdOficina());
		reporteDto.setIdDelegacion(buscaUser.getIdDelegacion());
		
		Map<String, Object> envioDatos = new SolicitudPago().generarReporte(reporteDto, NOMBREPDFREPORTE, formatoFecha);
		Response<Object> response =  providerRestTemplate.consumirServicioReportes(envioDatos, urlReportes, authentication);
		return MensajeResponseUtil.mensajeConsultaResponse(response, ERROR_DESCARGA);
	}

}
