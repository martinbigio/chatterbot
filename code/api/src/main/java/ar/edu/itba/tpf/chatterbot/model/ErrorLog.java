package ar.edu.itba.tpf.chatterbot.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;

/**
 * Un error ocurrido en la aplicación. 
 * 
 * Contiene el mensaje, el stack trace y la fecha y hora.
 */
@Entity
public class ErrorLog extends VersionablePersistentEntity {
    private static final long serialVersionUID = 0L;
    
    private static final int MAX_LENGTH_MESSAGE = 500; 
    private static final int MAX_LENGTH_STACK_TRACE = 1000; 

    @Column(length=MAX_LENGTH_MESSAGE)
    private String message;

    @Column(length=MAX_LENGTH_STACK_TRACE)
    private String stackTrace;
    private Date timestamp;

    ErrorLog() {
    }

    /**
     * Crea un nuevo error log dado un mensaje, un stack trace y un timestamp.
     * 
     * @param message Mensaje del error.
     * @param stackTrace Stack trace completo.
     * @param timestamp Fecha y hora del error.
     */
    public ErrorLog(String message, String stackTrace, Date timestamp) {
        super();

        if (message.length() > MAX_LENGTH_MESSAGE) {
            this.message = message.substring(0, MAX_LENGTH_MESSAGE - 1);
        } else {
        	this.message = message;
        }
        
        if (stackTrace.length() > MAX_LENGTH_STACK_TRACE) {
            this.stackTrace = stackTrace.substring(0, MAX_LENGTH_STACK_TRACE- 1);
        } else {
        	this.stackTrace = stackTrace;
        }

        this.timestamp = timestamp;
    }

    /**
     * Crea un nuevo error log dado un mensaje y un stack trace utilizando la fecha de creación del objeto.
     * 
     * @param message Mensaje del error.
     * @param stackTrace Stack trace completo.
     */
    public ErrorLog(String message, String stackTrace) {
        super();
        
        if (message.length() > MAX_LENGTH_MESSAGE) {
            this.message = message.substring(0, MAX_LENGTH_MESSAGE - 1);
        } else {
        	this.message = message;
        }
        
        if (stackTrace.length() > MAX_LENGTH_STACK_TRACE) {
            this.stackTrace = stackTrace.substring(0, MAX_LENGTH_STACK_TRACE- 1);
        } else {
        	this.stackTrace = stackTrace;
        }

        this.timestamp = new Date();
    }

    /**
     * @return Mensaje del error.
     */
    public String getMessage() {
        return message;
    }

    /**
     * @return Stack trace del error.
     */
    public String getStackTrace() {
        return stackTrace;
    }

    /**
     * @return Fecha y hora del error.
     */
    public Date getTimestamp() {
        return timestamp;
    }

    /**
     * @param message Mensaje de error.
     */
    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((message == null) ? 0 : message.hashCode());
        result = prime * result + ((stackTrace == null) ? 0 : stackTrace.hashCode());
        result = prime * result + ((timestamp == null) ? 0 : timestamp.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        ErrorLog other = (ErrorLog) obj;
        if (message == null) {
            if (other.message != null)
                return false;
        } else if (!message.equals(other.message))
            return false;
        if (stackTrace == null) {
            if (other.stackTrace != null)
                return false;
        } else if (!stackTrace.equals(other.stackTrace))
            return false;
        if (timestamp == null) {
            if (other.timestamp != null)
                return false;
        } else if (!timestamp.equals(other.timestamp))
            return false;
        return true;
    }
    
    
}
