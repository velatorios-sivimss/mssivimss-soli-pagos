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
	private Integer idUnidadOperativa;
	private String nomDestinatario;
	private String nomRemitente;
	private Integer numReferencia;
	private Integer idProveedor;
	private String fechaElabora;
	private String fechaInicial;
	private String fechaFinal;
	private String concepto;
	private String observaciones;
	private Integer idVelatorio;
	private Integer ejercicioFiscal;
	private Double impTotal;
	private Integer idEstatusSol;

}
