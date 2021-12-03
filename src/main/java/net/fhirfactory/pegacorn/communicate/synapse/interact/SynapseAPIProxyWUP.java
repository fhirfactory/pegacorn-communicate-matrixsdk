/*
 * Copyright (c) 2021 Mark A. Hunter
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
package net.fhirfactory.pegacorn.communicate.synapse.interact;

import net.fhirfactory.pegacorn.communicate.synapse.credentials.SynapseAdminAccessToken;
import net.fhirfactory.pegacorn.communicate.synapse.methods.common.SynapseAPIResponse;
import net.fhirfactory.pegacorn.communicate.synapse.model.SynapseAdminProxyInterface;
import net.fhirfactory.pegacorn.core.interfaces.topology.WorkshopInterface;
import net.fhirfactory.pegacorn.core.model.capabilities.CapabilityFulfillmentInterface;
import net.fhirfactory.pegacorn.core.model.capabilities.base.CapabilityUtilisationRequest;
import net.fhirfactory.pegacorn.core.model.capabilities.base.CapabilityUtilisationResponse;
import net.fhirfactory.pegacorn.core.model.dataparcel.DataParcelManifest;
import net.fhirfactory.pegacorn.core.model.topology.endpoints.adapters.HTTPClientAdapter;
import net.fhirfactory.pegacorn.core.model.topology.endpoints.interact.matrix.InteractMatrixClientEndpoint;
import net.fhirfactory.pegacorn.core.model.topology.nodes.external.ConnectedExternalSystemTopologyNode;
import net.fhirfactory.pegacorn.petasos.core.moa.wup.MessageBasedWUPEndpoint;
import net.fhirfactory.pegacorn.util.PegacornEnvironmentProperties;
import net.fhirfactory.pegacorn.workshops.InteractWorkshop;
import net.fhirfactory.pegacorn.wups.archetypes.petasosenabled.messageprocessingbased.InteractEgressAPIClientGatewayWUP;
import org.apache.camel.model.rest.RestBindingMode;
import org.apache.commons.lang3.StringUtils;

import javax.inject.Inject;
import java.time.Instant;
import java.util.List;

public abstract class SynapseAPIProxyWUP extends InteractEgressAPIClientGatewayWUP implements CapabilityFulfillmentInterface, SynapseAdminProxyInterface {

    private static String CAMEL_COMPONENT_TYPE = "netty-http";
    private String hostName;
    private int portValue;
    private String httpScheme;
    private String webServicePath;

    @Inject
    private InteractWorkshop workshop;

    @Inject
    private SynapseAdminAccessToken synapseAccessToken;

    @Inject
    private PegacornEnvironmentProperties environmentProperties;

    @Override
    protected List<DataParcelManifest> specifySubscriptionTopics() {
        return null;
    }

    @Override
    protected WorkshopInterface specifyWorkshop() {
        return (workshop);
    }


    @Override
    protected MessageBasedWUPEndpoint specifyEgressEndpoint() {
        MessageBasedWUPEndpoint endpoint = new MessageBasedWUPEndpoint();
        InteractMatrixClientEndpoint clientTopologyEndpoint = (InteractMatrixClientEndpoint) getTopologyEndpoint(specifyEgressTopologyEndpointName());
        ConnectedExternalSystemTopologyNode targetSystem = clientTopologyEndpoint.getTargetSystem();
        HTTPClientAdapter httpClient = (HTTPClientAdapter) targetSystem.getTargetPorts().get(0);
        this.portValue = httpClient.getPortNumber();
        this.hostName = httpClient.getHostName();
        String httpType = null;
        if(httpClient.isEncrypted()){
            httpType = "https";
        } else {
            httpType = "http";
        }
        this.httpScheme = httpType;
        this.webServicePath = httpClient.getContextPath();
        endpoint.setEndpointSpecification(CAMEL_COMPONENT_TYPE+":"+ getHttpScheme() + "//"+getHostName() + ":" + getPortValue() + getWebServicePath());
        endpoint.setEndpointTopologyNode(clientTopologyEndpoint);
        endpoint.setFrameworkEnabled(false);
        return endpoint;
    }

    @Override
    public void configure() throws Exception {

        restConfiguration()
                .producerComponent(CAMEL_COMPONENT_TYPE)
                .scheme(getHttpScheme())
                .host(getHostName())
                .port(getPortValue())
                .bindingMode(RestBindingMode.json)
                .contextPath(getWebServicePath());

        from("direct:synapse-api-user-query")
                .to("rest:get:v2/users");

        from("direct:synapse-api-room-query")
                .to("rest:get:v1/rooms");

    }

    @Override
    public CapabilityUtilisationResponse executeTask(CapabilityUtilisationRequest request) {
        getLogger().debug(".executeTask(): Entry, request->{}", request);
        String capability = request.getRequiredCapabilityName();
        CapabilityUtilisationResponse response = new CapabilityUtilisationResponse();
        response.setInstantCompleted(Instant.now());
        response.setAssociatedRequestID(request.getRequestID());
        switch(capability){
            case "Synapse-Room-Query": {
                SynapseAPIResponse result = executeRoomQuery(request.getRequestStringContent());
                response.setSuccessful(result.isSuccessful());
                if(result.isSuccessful()) {
                    response.setResponseContent(result.getResponseContent());
                } else {
                    response.setResponseContent(result.getErrorContent());
                }
                break;
            }
            case "Synapse-User-Query": {
                SynapseAPIResponse result = executeUserQuery(request.getRequestStringContent());
                response.setSuccessful(result.isSuccessful());
                if(result.isSuccessful()) {
                    response.setResponseContent(result.getResponseContent());
                } else {
                    response.setResponseContent(result.getErrorContent());
                }
                break;
            }
            default:{
                response.setSuccessful(false);
            }
        }
        getLogger().debug(".executeTask(): Exit, response->{}", response);
        return(response);
    }

    @Override
    protected void registerCapabilities(){
        getProcessingPlant().registerCapabilityFulfillmentService("Synapse-Room-Query", this);
        getProcessingPlant().registerCapabilityFulfillmentService("Synapse-User-Query", this);
    }

    @Override
    public SynapseAPIResponse executeRoomQuery(String query) {
        return null;
    }

    @Override
    public SynapseAPIResponse executeUserQuery(String query) {
        return null;
    }

    @Override
    public void executePostInitialisationActivities(){
        // Get the AccessToken and assign it
        String synapseAdminUserName = environmentProperties.getMandatoryProperty(SynapseAdminAccessToken.SYNAPSE_ADMIN_USER_NAME_PROPERTY);
        String synapseAdminUserPassword = environmentProperties.getMandatoryProperty(SynapseAdminAccessToken.SYNAPSE_ADMIN_USER_PASSWORD_PROPERTY);
        String synapseAccessToken  = environmentProperties.getMandatoryProperty(SynapseAdminAccessToken.SYNAPSE_ACCESS_TOKEN_PROPERTY);

        if(StringUtils.isBlank(synapseAdminUserName) || StringUtils.isBlank(synapseAdminUserPassword) || StringUtils.isBlank(synapseAccessToken)){
            throw(new RuntimeException("SynapseAdminUserName or SynapseAdminUserPassword is blank"));
        }

        getSynapseAccessToken().setRemoteAccessToken(synapseAccessToken);
        getSynapseAccessToken().setUserName(synapseAdminUserName);
        getSynapseAccessToken().setUserPassword(synapseAdminUserPassword);
    }

    //
    // Getters (and Setters)
    //

    protected SynapseAdminAccessToken getSynapseAccessToken(){
        return(this.synapseAccessToken);
    }

    protected int getPortValue() {
        return (this.portValue);
    }

    protected String getHttpScheme() {
        return(this.httpScheme);
    }

    protected String getHostName(){
        return(this.hostName);
    }

    protected String getWebServicePath(){
        return(this.webServicePath);
    }

    @Override
    public InteractWorkshop getWorkshop() {
        return workshop;
    }
}
