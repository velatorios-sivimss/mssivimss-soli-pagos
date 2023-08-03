package com.imss.sivimss.solipagos.model.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class SolicitudFoliosDto {
	
	private Integer idSolicitud;
	private String[] cveFolios;

}
