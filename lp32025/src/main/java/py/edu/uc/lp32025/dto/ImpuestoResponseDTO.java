package py.edu.uc.lp32025.dto;

import java.math.BigDecimal;

public class ImpuestoResponseDTO extends AbstractResponseDTO {
    private BigDecimal salario;
    private BigDecimal impuestoBase;
    private BigDecimal deducciones;
    private BigDecimal impuestoNeto;
    private boolean datosEspecificosValidos;
    private String informacionCompleta;

    public ImpuestoResponseDTO() {
        super(200, null, null);
    }

    public ImpuestoResponseDTO(BigDecimal salario, BigDecimal impuestoBase, BigDecimal deducciones, BigDecimal impuestoNeto,
                               boolean datosEspecificosValidos, String informacionCompleta) {
        super(200, null, null);
        this.salario = salario;
        this.impuestoBase = impuestoBase;
        this.deducciones = deducciones;
        this.impuestoNeto = impuestoNeto;
        this.datosEspecificosValidos = datosEspecificosValidos;
        this.informacionCompleta = informacionCompleta;
    }

    public ImpuestoResponseDTO(int status, String error, String userError) {
        super(status, error, userError);
    }

    public BigDecimal getSalario() {
        return salario;
    }

    public void setSalario(BigDecimal salario) {
        this.salario = salario;
    }

    public BigDecimal getImpuestoBase() {
        return impuestoBase;
    }

    public void setImpuestoBase(BigDecimal impuestoBase) {
        this.impuestoBase = impuestoBase;
    }

    public BigDecimal getDeducciones() {
        return deducciones;
    }

    public void setDeducciones(BigDecimal deducciones) {
        this.deducciones = deducciones;
    }

    public BigDecimal getImpuestoNeto() {
        return impuestoNeto;
    }

    public void setImpuestoNeto(BigDecimal impuestoNeto) {
        this.impuestoNeto = impuestoNeto;
    }

    public boolean isDatosEspecificosValidos() {
        return datosEspecificosValidos;
    }

    public void setDatosEspecificosValidos(boolean datosEspecificosValidos) {
        this.datosEspecificosValidos = datosEspecificosValidos;
    }

    public String getInformacionCompleta() {
        return informacionCompleta;
    }

    public void setInformacionCompleta(String informacionCompleta) {
        this.informacionCompleta = informacionCompleta;
    }
}