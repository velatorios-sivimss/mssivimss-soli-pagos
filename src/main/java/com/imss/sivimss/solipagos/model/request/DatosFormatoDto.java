package com.imss.sivimss.solipagos.model.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class DatosFormatoDto {

	public Integer idSolicitud;
	public Integer idTipoSolicitud;
	public Integer idVelatorio;
	public Integer idUnidadOperativa;
	public String unidadAdmOpe;
	public String referenciaUnidad;
    public Integer refDirTec;
    public String beneficiario;
    public String remitente;
    public String numContrato;
    public String concepto;
    public String fechaElabora;
    public String periodo;
    public Double importe;
	public String cantidadLetra;
	public String datosBancarios;
	public String observaciones;
	public String tipoReporte;
	
}
