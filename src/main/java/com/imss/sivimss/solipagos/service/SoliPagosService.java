package com.imss.sivimss.solipagos.service;

import java.io.IOException;

import org.springframework.security.core.Authentication;

import com.imss.sivimss.solipagos.util.DatosRequest;
import com.imss.sivimss.solipagos.util.Response;

public interface SoliPagosService {

	Response<Object> listaEjercicios(DatosRequest request, Authentication authentication) throws IOException;
	
	Response<Object> listaTiposSoli(DatosRequest request, Authentication authentication) throws IOException;
	
	Response<Object> consulta(DatosRequest request, Authentication authentication) throws IOException;
	
	Response<Object> busqueda(DatosRequest request, Authentication authentication) throws IOException;
	
	Response<Object> detalle(DatosRequest request, Authentication authentication) throws IOException;
	
	Response<Object> partidas(DatosRequest request, Authentication authentication) throws IOException;
	
	Response<Object> generarSoli(DatosRequest request, Authentication authentication) throws IOException;
	
	Response<Object> aprobarSoli(DatosRequest request, Authentication authentication) throws IOException;
	
	Response<Object> cancelarSoli(DatosRequest request, Authentication authentication) throws IOException;
	
	Response<Object> rechazarSoli(DatosRequest request, Authentication authentication) throws IOException;
	
	Response<Object> descargarDocto(DatosRequest request, Authentication authentication) throws IOException;
	
}
