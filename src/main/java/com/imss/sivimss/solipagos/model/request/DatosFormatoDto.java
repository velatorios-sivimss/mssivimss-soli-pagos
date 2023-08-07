package com.imss.sivimss.solipagos.model.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class DatosFormatoDto {

	private Integer idSolicitud;
	private Integer idTipoSolicitud;
	private Integer idVelatorio;
	private Integer idUnidadOperativa;
	private String unidadAdmOpe;
	private String referenciaUnidad;
    private Integer refDirTec;
    private String beneficiario;
    private String remitente;
    private String numContrato;
    private String concepto;
    private String fechaElabora;
    private String periodo;
    private Double importe;
	private String cantidadLetra;
	private String datosBancarios;
	private String observaciones;
	private String tipoReporte;
	
}
