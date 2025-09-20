package core.common.per.dto;
/**
 * 
 * Class that implements the following IMessageMapping interface
 * 
 * @author Yeiner Palomeque Moreno
 * @version 1.0
 * @since 2017-04-18
 */
public class ResponseSPRDTO {

	private String codigoRespuestaCanal;
	private String descripcionNegocio;
	private String codigoCanonico;
	
	
	public String getCodigoRespuestaCanal() {
		return codigoRespuestaCanal;
	}
	public void setCodigoRespuestaCanal(String codigoRespuestaCanal) {
		this.codigoRespuestaCanal = codigoRespuestaCanal;
	}
	public String getDescripcionNegocio() {
		return descripcionNegocio;
	}
	public void setDescripcionNegocio(String descripcionNegocio) {
		this.descripcionNegocio = descripcionNegocio;
	}
	public String getCodigoCanonico() {
		return codigoCanonico;
	}
	public void setCodigoCanonico(String codigoCanonico) {
		this.codigoCanonico = codigoCanonico;
	}
	
	
}
