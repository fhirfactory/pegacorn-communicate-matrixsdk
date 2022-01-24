/*
 * Copyright (c) 2020 Mark A. Hunter
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

package net.fhirfactory.pegacorn.communicate.matrix.issi.receiver;

import net.fhirfactory.pegacorn.core.model.topology.endpoints.http.HTTPServerTopologyEndpoint;
import net.fhirfactory.pegacorn.core.model.topology.endpoints.issi.matrix.MatrixEventReceiverEndpoint;
import net.fhirfactory.pegacorn.communicate.matrix.issi.receiver.beans.IncomingMatrixEventSet2UoW;
import net.fhirfactory.pegacorn.communicate.matrix.issi.receiver.beans.IncomingMatrixEventSetValidator;
import net.fhirfactory.pegacorn.communicate.matrix.issi.receiver.beans.IncomingMatrixMessageSplitter;
import net.fhirfactory.pegacorn.communicate.matrix.model.exceptions.MatrixEventNotFoundException;
import net.fhirfactory.pegacorn.communicate.matrix.model.exceptions.MatrixUpdateException;
import net.fhirfactory.pegacorn.core.interfaces.topology.WorkshopInterface;
import net.fhirfactory.pegacorn.core.model.topology.endpoints.adapters.HTTPServerAdapter;
import net.fhirfactory.pegacorn.petasos.core.moa.wup.MessageBasedWUPEndpointContainer;
import net.fhirfactory.pegacorn.petasos.wup.helper.IngresActivityBeginRegistration;
import net.fhirfactory.pegacorn.workshops.InteractWorkshop;
import net.fhirfactory.pegacorn.wups.archetypes.petasosenabled.messageprocessingbased.InteractIngresMessagingGatewayWUP;
import org.apache.camel.Exchange;
import org.apache.camel.ExchangePattern;
import org.apache.camel.LoggingLevel;
import org.apache.camel.model.OnExceptionDefinition;
import org.apache.commons.lang3.StringUtils;

import javax.inject.Inject;

public abstract class MatrixApplicationServicesEventsReceiverWUP extends InteractIngresMessagingGatewayWUP {

    private static String INGRES_GATEWAY_COMPONENT = "netty-http";
            

    @Inject
    private InteractWorkshop workshop;
    
    public MatrixApplicationServicesEventsReceiverWUP(){
        super();
    }
   
    @Override
    public void configure() throws Exception {
        getLogger().debug(".configure(): Entry");

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
                .bean(IncomingMatrixEventSetValidator.class, "validateEventSetMessage")
                .bean(IncomingMatrixEventSet2UoW.class, "encapsulateMatrixMessage(*, Exchange)")
                .bean(IngresActivityBeginRegistration.class, "registerActivityStart(*,  Exchange)")
                .to(ExchangePattern.InOnly, getContinueProcessingRoute())
                .transform().simple("{}")
                .end();

        fromWithStandardExceptionHandling(getContinueProcessingRoute())
                .bean(IncomingMatrixMessageSplitter.class, "splitMessageIntoEvents")
                .to(egressFeed());
    }

    private String getContinueProcessingRoute(){
        String routeName = "direct:" + getClass().getSimpleName() + "-ContinueProcessingRoute";
        return(routeName);
    }

    @Override
    protected WorkshopInterface specifyWorkshop() {
        return (workshop);
    }


    @Override
    protected MessageBasedWUPEndpointContainer specifyIngresEndpoint() {
        getLogger().debug(".specifyIngresTopologyEndpoint(): Entry");
        MessageBasedWUPEndpointContainer ingresEndpoint = new MessageBasedWUPEndpointContainer();
        ingresEndpoint.setFrameworkEnabled(false);
        HTTPServerTopologyEndpoint myMatrixServerAPIServicesGW = (HTTPServerTopologyEndpoint)getTopologyEndpoint(specifyIngresTopologyEndpointName());
        if (myMatrixServerAPIServicesGW == null) {
            getLogger().error(".specifyIngresTopologyEndpoint(): Unable to derive endpoint for Matrix Application Services API  Server");
            return (ingresEndpoint);
        }
        // Assign the Associated TopologyNode to the Ingres Endpoint
        ingresEndpoint.setEndpointTopologyNode(myMatrixServerAPIServicesGW);
        getLogger().trace(".specifyIngresTopologyEndpoint(): Resolved myMatrixServerAPIServicesGW->{}", myMatrixServerAPIServicesGW);
        // Building the Endpoint Specification (String)

        HTTPServerAdapter httpServerAdapter = myMatrixServerAPIServicesGW.getHTTPServerAdapter();
        int portValue = httpServerAdapter.getPortNumber();
        String interfaceDNSName = httpServerAdapter.getHostName();
        String contextPath = httpServerAdapter.getContextPath();
        if(StringUtils.isEmpty(contextPath) || contextPath.equals("null")) {
            contextPath = "";
        }
        String ingresString = buildIngresString(interfaceDNSName, Integer.toString(portValue), contextPath);
        ingresEndpoint.setEndpointSpecification(ingresString);
        getLogger().info(".specifyIngresTopologyEndpoint(): Exit, ingresEndpoint->{}", ingresEndpoint);
        return(ingresEndpoint);
    }

    private String buildIngresString(String host, String port, String path){
        String ingresString = "netty-http:http://"+host+":"+port+path+"/transactions/{id}";
        return(ingresString);
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
        OnExceptionDefinition exceptionDef = onException(MatrixUpdateException.class)
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
    protected String specifyIngresEndpointVersion() {
        return "1.0.0s";
    }
}
