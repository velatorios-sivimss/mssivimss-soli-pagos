package com.imss.sivimss.solipagos.model.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class BusquedaDto {

	private Integer idOficina;
	private Integer idNivel;
	private Integer idDelegacion;
	private Integer idVelatorio;
	private Integer idTipoSolicitud;
    private String ejercicioFiscal;
	private String fecInicial;
	private String fecFinal;
	private String folioSolicitud;
	private String tipoReporte;
	
}
