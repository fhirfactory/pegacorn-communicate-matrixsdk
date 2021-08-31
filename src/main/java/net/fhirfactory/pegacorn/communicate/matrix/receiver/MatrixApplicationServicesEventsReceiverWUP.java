/*
 * Copyright (c) 2020 mhunter
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package net.fhirfactory.pegacorn.communicate.matrix.receiver;

import net.fhirfactory.pegacorn.communicate.matrix.model.exceptions.MatrixEventNotFoundException;
import net.fhirfactory.pegacorn.communicate.matrix.receiver.beans.IncomingMatrixEventSet2UoW;
import net.fhirfactory.pegacorn.communicate.matrix.receiver.beans.IncomingMatrixEventSetValidator;
import net.fhirfactory.pegacorn.components.interfaces.topology.WorkshopInterface;
import net.fhirfactory.pegacorn.deployment.topology.model.endpoints.base.IPCServerTopologyEndpoint;
import net.fhirfactory.pegacorn.deployment.topology.model.endpoints.technologies.HTTPProcessingPlantTopologyEndpointPort;
import net.fhirfactory.pegacorn.deployment.topology.model.endpoints.technologies.HTTPServerClusterServiceTopologyEndpointPort;
import net.fhirfactory.pegacorn.internals.esr.transactions.exceptions.ResourceUpdateException;
import net.fhirfactory.pegacorn.petasos.core.moa.wup.MessageBasedWUPEndpoint;
import net.fhirfactory.pegacorn.petasos.wup.helper.IngresActivityBeginRegistration;
import net.fhirfactory.pegacorn.workshops.InteractWorkshop;
import net.fhirfactory.pegacorn.wups.archetypes.petasosenabled.messageprocessingbased.InteractIngresMessagingGatewayWUP;
import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.model.OnExceptionDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class MatrixApplicationServicesEventsReceiverWUP extends InteractIngresMessagingGatewayWUP {
    private static final Logger LOG = LoggerFactory.getLogger(MatrixApplicationServicesEventsReceiverWUP.class);

    private static String INGRES_GATEWAY_COMPONENT = "netty-http";

    @Inject
    private InteractWorkshop workshop;
    
    public MatrixApplicationServicesEventsReceiverWUP(){
        super();
    }
   
    @Override
    public void configure() throws Exception {
        LOG.debug(".configure(): Entry");

        getLogger().info("{}:: ingresFeed() --> {}", getClass().getSimpleName(), ingresFeed());
        getLogger().info("{}:: egressFeed() --> {}", getClass().getSimpleName(), egressFeed());

        //
        // Exceptions
        //
        routeMatrixEventNotFoundException();
        routeGeneralException();

        //
        // Main Route
        //
        fromInteractIngresService(ingresFeed())
                .routeId(getNameSet().getRouteCoreWUP())
                .transform(simple("${bodyAs(String)}"))
                .log(LoggingLevel.TRACE, ": Message received!!!")
                .bean(IncomingMatrixEventSetValidator.class, "validateEventSetMessage")
                .bean(IncomingMatrixEventSet2UoW.class, "encapsulateMatrixMessage(*, Exchange)")
                .bean(IngresActivityBeginRegistration.class, "registerActivityStart(*,  Exchange)")
                .log(LoggingLevel.TRACE, "Message Validated, Forwarding!!!")
                .to(egressFeed());
    }



    @Override
    public String specifyWUPInstanceName() {
        return("MatrixApplicationServicesEventsReceiverWUP");
    }

    @Override
    public String specifyWUPInstanceVersion() {
        return("0.0.1");
    }

    @Override
    protected Logger specifyLogger() {
        return (LOG);
    }

    @Override
    protected WorkshopInterface specifyWorkshop() {
        return (workshop);
    }

    @Override
    protected MessageBasedWUPEndpoint specifyIngresEndpoint() {
        LOG.debug(".specifyIngresTopologyEndpoint(): Entry");
        MessageBasedWUPEndpoint ingresEndpoint = new MessageBasedWUPEndpoint();
        ingresEndpoint.setFrameworkEnabled(false);
        IPCServerTopologyEndpoint endpoint = (HTTPServerClusterServiceTopologyEndpointPort) getTopologyEndpoint(specifyIngresTopologyEndpointName());
        if(endpoint == null){
            LOG.error(".specifyIngresTopologyEndpoint(): Unable to derive endpoint for Matrix Application Services API  Server");
            return(ingresEndpoint);
        }
        LOG.trace(".specifyIngresTopologyEndpoint(): Resolved endpoint->{}", endpoint);
        int portValue = endpoint.getPortValue();
        String interfaceDNSName = endpoint.getHostDNSName();
        String ingresString = buildIngresString(interfaceDNSName, Integer.toString(portValue), getServerPath());
        ingresEndpoint.setEndpointSpecification(ingresString);
        LOG.debug(".specifyIngresTopologyEndpoint(): Exit, ingresEndpoint->{}", ingresEndpoint);
        return(ingresEndpoint);
    }

    private String buildIngresString(String host, String port, String path){
        String ingresString = "netty-http:http://"+host+":"+port+"/"+path+"/transactions/{id}";
        return(ingresString);
    }

    protected String getPathSuffix() {
        String suffix = "?matchOnUriPrefix=true&option.enableCORS=true&option.corsAllowedCredentials=true";
        return (suffix);
    }

    public String getServerPath(){
        getLogger().debug(".getServerPath(): Entry");
        if(getIngresEndpoint().getEndpointTopologyNode() instanceof HTTPServerClusterServiceTopologyEndpointPort) {
            HTTPServerClusterServiceTopologyEndpointPort serviceEndpoint = (HTTPServerClusterServiceTopologyEndpointPort)getIngresEndpoint().getEndpointTopologyNode();
            String serverPath = serviceEndpoint.getBasePath();
            getLogger().debug(".getServerPath(): Exit, (ClusterService) serverPath->{}", serverPath);
            return(serverPath);
        }
        if(getIngresEndpoint().getEndpointTopologyNode() instanceof HTTPProcessingPlantTopologyEndpointPort){
            HTTPProcessingPlantTopologyEndpointPort processingPlantEndpoint = (HTTPProcessingPlantTopologyEndpointPort)getIngresEndpoint().getEndpointTopologyNode();
            String serverPath = processingPlantEndpoint.getBasePath();
            getLogger().debug(".getServerPath(): Exit, (ProcessingPlant) serverPath->{}", serverPath);
            return(serverPath);
        }
        getLogger().error(".getServerPath(): Cannot resolve Matrix Application Services server Base Path!");
        return("");
    }

    private OnExceptionDefinition routeMatrixEventNotFoundException() {
        OnExceptionDefinition exceptionDef = onException(MatrixEventNotFoundException.class)
                .handled(true)
                .log(LoggingLevel.ERROR, "MatrixEventNotFoundException...")
                // use HTTP status 404 when data was not found
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(404))
                .setBody(simple("${exception.message}\n"));

        return(exceptionDef);
    }

    private OnExceptionDefinition routeResourceUpdateException() {
        OnExceptionDefinition exceptionDef = onException(ResourceUpdateException.class)
                .handled(true)
                .log(LoggingLevel.INFO, "ResourceUpdateException...")
                // use HTTP status 404 when data was not found
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(400))
                .setBody(simple("${exception.message}\n"));

        return(exceptionDef);
    }

    private OnExceptionDefinition routeGeneralException() {
        OnExceptionDefinition exceptionDef = onException(Exception.class)
                .handled(true)
                .log(LoggingLevel.ERROR, "${exception.message}\n")
                // use HTTP status 500 when we had a server side error
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(500))
                .setBody(simple("${exception.message}\n"));
        return (exceptionDef);
    }

    @Override
    protected String specifyIngresTopologyEndpointName() {
        return null;
    }

    @Override
    protected String specifyIngresEndpointVersion() {
        return null;
    }
}
