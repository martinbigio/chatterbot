package ar.edu.itba.tpf.chatterbot.business;

import java.util.ArrayList;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.jws.WebParam.Mode;
import javax.xml.ws.Holder;

@WebService
public interface InfoProvider {
    
    @WebMethod
    public void provide(@WebParam String s, @WebParam(mode=Mode.INOUT) Holder<ArrayList<String>> keys, @WebParam(mode=Mode.INOUT) Holder<ArrayList<String>> values);
}
