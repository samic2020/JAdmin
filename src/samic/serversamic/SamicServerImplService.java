
package samic.serversamic;

import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceClient;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.WebServiceFeature;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.2.9-b130926.1035
 * Generated source version: 2.2
 * 
 */
@WebServiceClient(name = "SamicServerImplService", targetNamespace = "http://SamicServer/", wsdlLocation = "http://192.168.100.211:9876/SamicServer?wsdl")
public class SamicServerImplService
    extends Service
{

    private final static URL SAMICSERVERIMPLSERVICE_WSDL_LOCATION;
    private final static WebServiceException SAMICSERVERIMPLSERVICE_EXCEPTION;
    private final static QName SAMICSERVERIMPLSERVICE_QNAME = new QName("http://SamicServer/", "SamicServerImplService");

    static {
        URL url = null;
        WebServiceException e = null;
        try {
            url = new URL("http://192.168.100.211:9876/SamicServer?wsdl");
        } catch (MalformedURLException ex) {
            e = new WebServiceException(ex);
        }
        SAMICSERVERIMPLSERVICE_WSDL_LOCATION = url;
        SAMICSERVERIMPLSERVICE_EXCEPTION = e;
    }

    public SamicServerImplService() {
        super(__getWsdlLocation(), SAMICSERVERIMPLSERVICE_QNAME);
    }

    public SamicServerImplService(WebServiceFeature... features) {
        super(__getWsdlLocation(), SAMICSERVERIMPLSERVICE_QNAME, features);
    }

    public SamicServerImplService(URL wsdlLocation) {
        super(wsdlLocation, SAMICSERVERIMPLSERVICE_QNAME);
    }

    public SamicServerImplService(URL wsdlLocation, WebServiceFeature... features) {
        super(wsdlLocation, SAMICSERVERIMPLSERVICE_QNAME, features);
    }

    public SamicServerImplService(URL wsdlLocation, QName serviceName) {
        super(wsdlLocation, serviceName);
    }

    public SamicServerImplService(URL wsdlLocation, QName serviceName, WebServiceFeature... features) {
        super(wsdlLocation, serviceName, features);
    }

    /**
     * 
     * @return
     *     returns SamicServer
     */
    @WebEndpoint(name = "SamicServerImplPort")
    public SamicServer getSamicServerImplPort() {
        return super.getPort(new QName("http://SamicServer/", "SamicServerImplPort"), SamicServer.class);
    }

    /**
     * 
     * @param features
     *     A list of {@link javax.xml.ws.WebServiceFeature} to configure on the proxy.  Supported features not in the <code>features</code> parameter will have their default values.
     * @return
     *     returns SamicServer
     */
    @WebEndpoint(name = "SamicServerImplPort")
    public SamicServer getSamicServerImplPort(WebServiceFeature... features) {
        return super.getPort(new QName("http://SamicServer/", "SamicServerImplPort"), SamicServer.class, features);
    }

    private static URL __getWsdlLocation() {
        if (SAMICSERVERIMPLSERVICE_EXCEPTION!= null) {
            throw SAMICSERVERIMPLSERVICE_EXCEPTION;
        }
        return SAMICSERVERIMPLSERVICE_WSDL_LOCATION;
    }

}
