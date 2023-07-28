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
	public String cantidadLetra;
}
