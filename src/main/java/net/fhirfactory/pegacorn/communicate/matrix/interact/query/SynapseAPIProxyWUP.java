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
package net.fhirfactory.pegacorn.communicate.matrix.interact.query;

import net.fhirfactory.pegacorn.components.capabilities.CapabilityFulfillmentInterface;
import net.fhirfactory.pegacorn.components.capabilities.base.CapabilityUtilisationRequest;
import net.fhirfactory.pegacorn.components.capabilities.base.CapabilityUtilisationResponse;
import net.fhirfactory.pegacorn.components.dataparcel.DataParcelManifest;
import net.fhirfactory.pegacorn.components.interfaces.topology.WorkshopInterface;
import net.fhirfactory.pegacorn.deployment.topology.model.endpoints.base.ExternalSystemIPCEndpoint;
import net.fhirfactory.pegacorn.deployment.topology.model.endpoints.interact.StandardInteractClientTopologyEndpointPort;
import net.fhirfactory.pegacorn.deployment.topology.model.nodes.external.ConnectedExternalSystemTopologyNode;
import net.fhirfactory.pegacorn.petasos.core.moa.wup.MessageBasedWUPEndpoint;
import net.fhirfactory.pegacorn.workshops.InteractWorkshop;
import net.fhirfactory.pegacorn.wups.archetypes.petasosenabled.messageprocessingbased.InteractEgressAPIClientGatewayWUP;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;

@ApplicationScoped
public class SynapseAPIProxyWUP extends InteractEgressAPIClientGatewayWUP implements CapabilityFulfillmentInterface {
    private static final Logger LOG = LoggerFactory.getLogger(SynapseAPIProxyWUP.class);

    private static String WUP_VERSION="1.0.0";
    private static String CAMEL_COMPONENT_TYPE = "netty-http";

    @Inject
    private InteractWorkshop workshop;

    @Override
    protected Logger specifyLogger() {
        return (LOG);
    }

    @Override
    protected List<DataParcelManifest> specifySubscriptionTopics() {
        return null;
    }

    @Override
    protected String specifyWUPInstanceName() {
        return (getClass().getSimpleName());
    }

    @Override
    protected String specifyWUPInstanceVersion() {
        return (WUP_VERSION);
    }

    @Override
    protected WorkshopInterface specifyWorkshop() {
        return (workshop);
    }

    @Override
    protected String specifyEgressTopologyEndpointName() {
        return ("synapse-api");
    }

    @Override
    protected MessageBasedWUPEndpoint specifyEgressEndpoint() {
        MessageBasedWUPEndpoint endpoint = new MessageBasedWUPEndpoint();
        StandardInteractClientTopologyEndpointPort clientTopologyEndpoint = (StandardInteractClientTopologyEndpointPort) getTopologyEndpoint(specifyEgressTopologyEndpointName());
        ConnectedExternalSystemTopologyNode targetSystem = clientTopologyEndpoint.getTargetSystem();
        ExternalSystemIPCEndpoint externalSystemIPCEndpoint = targetSystem.getTargetPorts().get(0);
        int portValue = externalSystemIPCEndpoint.getTargetPortValue();
        String targetInterfaceDNSName = externalSystemIPCEndpoint.getTargetPortDNSName();
        String httpType = null;
        if(externalSystemIPCEndpoint.getEncryptionRequired()){
            httpType = "https";
        } else {
            httpType = "http";
        }
        endpoint.setEndpointSpecification(CAMEL_COMPONENT_TYPE+":"+httpType+"//"+targetInterfaceDNSName+":"+Integer.toString(portValue)+"?requireEndOfData=false");
        endpoint.setEndpointTopologyNode(clientTopologyEndpoint);
        endpoint.setFrameworkEnabled(false);
        return endpoint;
    }

    @Override
    public void configure() throws Exception {

    }

    @Override
    public CapabilityUtilisationResponse executeTask(CapabilityUtilisationRequest request) {
        return null;
    }

    @Override
    protected void registerCapabilities(){
        getProcessingPlant().registerCapabilityFulfillmentService("Synapse-RoomServer-Query", this);
    }
}
