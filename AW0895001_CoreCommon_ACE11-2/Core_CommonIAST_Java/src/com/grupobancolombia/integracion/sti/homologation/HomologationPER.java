package com.grupobancolombia.integracion.sti.homologation;

public class HomologationPER {
	
	/**
	 * codIdioma, Codigo de idioma a homologar
	 */
	private String codIdioma;
	
	/**
	 * codIdioma, Codigo de idioma a homologar
	 */
	private String codProveedorServicio;
	
	/**
	 * codIdioma, Codigo de idioma a homologar
	 */
	private String estadoRespuesta;

	public HomologationPER(String codIdioma, String codProveedorServicio,
			String estadoRespuesta) {
		super();
		this.codIdioma = codIdioma;
		this.codProveedorServicio = codProveedorServicio;
		this.estadoRespuesta = estadoRespuesta;
	}

	public HomologationPER() {
		super();
		this.codIdioma = "";
		this.codProveedorServicio = "";
		this.estadoRespuesta = "";
	}

	public String getCodIdioma() {
		return codIdioma;
	}

	public void setCodIdioma(String codIdioma) {
		this.codIdioma = codIdioma;
	}

	public String getCodProveedorServicio() {
		return codProveedorServicio;
	}

	public void setCodProveedorServicio(String codProveedorServicio) {
		this.codProveedorServicio = codProveedorServicio;
	}

	public String getEstadoRespuesta() {
		return estadoRespuesta;
	}

	public void setEstadoRespuesta(String estadoRespuesta) {
		this.estadoRespuesta = estadoRespuesta;
	}

}
