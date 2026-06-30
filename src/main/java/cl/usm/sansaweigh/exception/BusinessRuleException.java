package cl.usm.sansaweigh.exception;

//para reglas de negocio (horario nocturno, balanza prima), responde 422

public class BusinessRuleException extends RuntimeException {
    public BusinessRuleException(String message) {
        super(message);
    }
}