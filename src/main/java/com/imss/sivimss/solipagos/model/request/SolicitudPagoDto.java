package com.imss.sivimss.solipagos.model.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class SolicitudPagoDto {
	
	private Integer idSolicitud;
	private Integer idTipoSolic;
	private String cveFolioGastos;
	private String cveFolioConsignados;
	private Integer idUnidadMedica;
	private Integer idDelegacion;
	private String nomDestinatario;
	private String nomRemitente;
	private Integer numReferencia;
	private Integer idContratBenef;
	private String fechaInicial;
	private String fechaFinal;
	private String concepto;
	private String observaciones;
	private Integer idVelatorio;
	private Integer ejercicioFiscal;
	private Integer idEstatusSol;

}
