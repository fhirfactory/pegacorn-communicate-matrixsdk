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
package net.fhirfactory.pegacorn.communicate.matrix.interact.forwarder;

import net.fhirfactory.pegacorn.core.interfaces.topology.WorkshopInterface;
import net.fhirfactory.pegacorn.core.model.capabilities.CapabilityFulfillmentInterface;
import net.fhirfactory.pegacorn.core.model.capabilities.base.CapabilityUtilisationRequest;
import net.fhirfactory.pegacorn.core.model.capabilities.base.CapabilityUtilisationResponse;
import net.fhirfactory.pegacorn.core.model.dataparcel.DataParcelManifest;
import net.fhirfactory.pegacorn.core.model.topology.endpoints.adapters.HTTPClientAdapter;
import net.fhirfactory.pegacorn.core.model.topology.endpoints.interact.matrix.InteractMatrixClientEndpoint;
import net.fhirfactory.pegacorn.core.model.topology.nodes.external.ConnectedExternalSystemTopologyNode;
import net.fhirfactory.pegacorn.petasos.core.moa.wup.MessageBasedWUPEndpoint;
import net.fhirfactory.pegacorn.workshops.InteractWorkshop;
import net.fhirfactory.pegacorn.wups.archetypes.petasosenabled.messageprocessingbased.InteractEgressMessagingGatewayWUP;

import javax.inject.Inject;
import java.util.List;

public abstract class MatrixClientServerEventForwarderWUP extends InteractEgressMessagingGatewayWUP implements CapabilityFulfillmentInterface {

    private String CAMEL_COMPONENT_TYPE="netty-http";

    @Inject
    private InteractWorkshop workshop;

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
        HTTPClientAdapter synapseServerClient = (HTTPClientAdapter)targetSystem.getTargetPorts().get(0);
        int portValue = synapseServerClient.getPortNumber();
        String targetInterfaceDNSName = synapseServerClient.getHostName();
        String httpType = null;
        if(synapseServerClient.isEncrypted()){
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
        getProcessingPlant().registerCapabilityFulfillmentService("Matrix-RoomServer-MessageInjection", this);
    }
}
