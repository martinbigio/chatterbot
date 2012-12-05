package ar.edu.itba.tpf.chatterbot.business;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.jws.WebParam.Mode;
import javax.xml.ws.Holder;

import org.apache.log4j.Logger;

/**
 * Webservice que provee información sobre clientes.
 */
@WebService
public class Clients implements InfoProvider {

    /* Constantes que se utilizan como convenciones con las acciones */
    private static final String LAST_USER_MESSAGE_SESSION_VARNAME = "lastUserMessage";
    private static final String ERROR_SESSION_VARNAME = "error";
    private static final String FATAL_ERROR_SESSION_VARNAME = "fatalError";

    /**
     * Punto de entrada del Webservice.
     * 
     * @param serviceMethod Nombre del método que se desea invocar.
     * @param keys Lista con las claves para consultar el estado del chatterbot.
     * @param values Lista con los valores asociados a las claves para consultar el estado del chatterbot.
     */
    @WebMethod
    public void provide(String serviceMethod, @WebParam(mode = Mode.INOUT)
    Holder<ArrayList<String>> keys, @WebParam(mode = Mode.INOUT)
    Holder<ArrayList<String>> values) {

        /* Obtener el método que se desea invocar */
        Method method = null;
        try {
            method = Clients.class.getDeclaredMethod(serviceMethod, ArrayList.class, ArrayList.class);
            method.invoke(this, keys.value, values.value);
        } catch (Exception e) {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            e.printStackTrace(new PrintStream(outputStream));
            keys.value.add(FATAL_ERROR_SESSION_VARNAME);
            values.value.add(outputStream.toString());
            Logger.getRootLogger().error(outputStream.toString());
        }
    }

    /**
     * Mock de servicio para consultar el saldo de un cliente.
     * 
     * @param keys Lista con las claves que contienen el estado del chatterbot.
     * @param values Lista con los valores asociados a las claves que representan el estado del chatterbot.
     */
    @SuppressWarnings("unused")
    private void getSaldoCuenta(ArrayList<String> keys, ArrayList<String> values) {
        String message = getSessionVariableValue(keys, values, LAST_USER_MESSAGE_SESSION_VARNAME);

        CharSequence inputStr = message;
        String patternStr = "^.*(\\d{8}).*$";

        Pattern pattern = Pattern.compile(patternStr);
        Matcher matcher = pattern.matcher(inputStr);

        /* Reemplazar cada ocurrencia de una variable de sesión dentro de la respuesta */
        if (matcher.find()) {
            keys.add("saldoCuenta");
            values.add("US$100.000");
        } else {
            keys.add(ERROR_SESSION_VARNAME);
            values.add("El DNI ingresado es inválido.");
        }

    }

    @SuppressWarnings("unused")
    private void validarNumeroTarjeta(ArrayList<String> keys, ArrayList<String> values) {
        String message = getSessionVariableValue(keys, values, LAST_USER_MESSAGE_SESSION_VARNAME);
        String errorMessage = "No se encuentra un número de cuenta válido en el mensaje recibido. Recuerde que el formato de su número de cuenta es \\d{10}";

        if (message == null) {
            keys.add(ERROR_SESSION_VARNAME);
            values.add(errorMessage);
            return;
        }

        CharSequence inputStr = message;
        String patternStr = "^.*(\\d{10}).*$";

        Pattern pattern = Pattern.compile(patternStr);
        Matcher matcher = pattern.matcher(inputStr);

        /* Reemplazar cada ocurrencia de una variable de sesión dentro de la respuesta */
        if (matcher.find()) {
            keys.add("numeroTarjetaCredito");
            values.add(matcher.group(1));
        } else {
            keys.add(ERROR_SESSION_VARNAME);
            values.add(errorMessage);
        }

    }

    @SuppressWarnings("unused")
    private void consultarVencimientoTarjeta(ArrayList<String> keys, ArrayList<String> values) {
        keys.add("vencimientoTarjetaCredito");
        values.add("11/2009");

        keys.add("fechaPagoTarjetaCredito");
        values.add("MAÑANA :D");
    }

    /**
     * Mock de servicio para validar un número de cuenta. El formato válido es \d{10}
     * 
     * @param keys Lista con las claves que contienen el estado del chatterbot.
     * @param values Lista con los valores asociados a las claves que representan el estado del chatterbot.
     */
    @SuppressWarnings("unused")
    private void validarNumeroCuenta(ArrayList<String> keys, ArrayList<String> values) {
        String message = getSessionVariableValue(keys, values, LAST_USER_MESSAGE_SESSION_VARNAME);
        String errorMessage = "No se encuentra un número de cuenta válido en el mensaje recibido. Recuerde que el formato de su número de cuenta es \\d{10}";

        if (message == null) {
            keys.add(ERROR_SESSION_VARNAME);
            values.add(errorMessage);
            return;
        }

        CharSequence inputStr = message;
        String patternStr = "^.*(\\d{10}).*$";

        Pattern pattern = Pattern.compile(patternStr);
        Matcher matcher = pattern.matcher(inputStr);

        /* Reemplazar cada ocurrencia de una variable de sesión dentro de la respuesta */
        if (matcher.find()) {
            keys.add("numeroCuenta");
            values.add(matcher.group(1));
        } else {
            keys.add(ERROR_SESSION_VARNAME);
            values.add(errorMessage);
        }
    }

    
    
    @SuppressWarnings("unused")
    private void cotizacionDolar(ArrayList<String> keys, ArrayList<String> values) {
        keys.add("dolar");
        values.add("3.14");
    }

    
    /**
     * Dada una clave y 2 listas que asocian una clave con un valor, retorna el valor correspondiente a la clave
     * indicada. En caso de no encontrar nada, devuelve null.
     * 
     * @param keys Lista de claves.
     * @param values Lista con valores asociados a las claves.
     * @param key Clave que se desea buscar.
     * @return Valor asociado a la clave buscada o null.
     */
    private String getSessionVariableValue(List<String> keys, List<String> values, String key) {
        for (int i = 0; i < keys.size(); i++) {
            if (keys.get(i).equals(key)) {
                return values.get(i);
            }
        }
        return null;
    }
}
