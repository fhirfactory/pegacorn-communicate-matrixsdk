/*
 * The MIT License
 *
 * Copyright 2020 Mark A. Hunter (ACT Health).
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package net.fhirfactory.pegacorn.communicate.matrix.receiver;

import net.fhirfactory.pegacorn.communicate.matrix.model.constants.MatrixClientServiceAPIConstants;
import net.fhirfactory.pegacorn.communicate.matrix.receiver.beans.IncomingMatrixMessageSplitter;
import net.fhirfactory.pegacorn.components.dataparcel.DataParcelManifest;
import net.fhirfactory.pegacorn.components.dataparcel.DataParcelTypeDescriptor;
import net.fhirfactory.pegacorn.components.dataparcel.valuesets.DataParcelDirectionEnum;
import net.fhirfactory.pegacorn.components.dataparcel.valuesets.DataParcelNormalisationStatusEnum;
import net.fhirfactory.pegacorn.components.dataparcel.valuesets.DataParcelValidationStatusEnum;
import net.fhirfactory.pegacorn.components.dataparcel.valuesets.PolicyEnforcementPointApprovalStatusEnum;
import net.fhirfactory.pegacorn.components.interfaces.topology.WorkshopInterface;
import net.fhirfactory.pegacorn.workshops.InteractWorkshop;
import net.fhirfactory.pegacorn.wups.archetypes.petasosenabled.messageprocessingbased.MOAStandardWUP;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Mark A. Hunter
 */
@ApplicationScoped
public class MatrixApplicationServicesEventsSeperatorWUP extends MOAStandardWUP {

    private static final Logger LOG = LoggerFactory.getLogger(MatrixApplicationServicesEventsSeperatorWUP.class);

    public MatrixApplicationServicesEventsSeperatorWUP(){
        super();
    }

    @Inject
    private InteractWorkshop workshop;

    @Override
    public List<DataParcelManifest> specifySubscriptionTopics() {
        LOG.debug(".getSubscribedTopics(): Entry");
        LOG.trace(".getSubscribedTopics(): Creating new TopicToken");
        DataParcelTypeDescriptor payloadTopic = new DataParcelTypeDescriptor();
        payloadTopic.setDataParcelDefiner("Matrix");
        payloadTopic.setDataParcelCategory("ClientServerAPI");
        payloadTopic.setDataParcelSubCategory("General");
        payloadTopic.setDataParcelResource("RawEventSet");
        payloadTopic.setVersion(MatrixClientServiceAPIConstants.MATRIX_CLIENT_SERVICES_API_RELEASE);
        DataParcelManifest manifest = new DataParcelManifest();
        manifest.setContentDescriptor(payloadTopic);
        manifest.setValidationStatus(DataParcelValidationStatusEnum.DATA_PARCEL_CONTENT_VALIDATED_TRUE);
        manifest.setNormalisationStatus(DataParcelNormalisationStatusEnum.DATA_PARCEL_CONTENT_NORMALISATION_FALSE);
        manifest.setDataParcelFlowDirection(DataParcelDirectionEnum.INBOUND_DATA_PARCEL);
        manifest.setEnforcementPointApprovalStatus(PolicyEnforcementPointApprovalStatusEnum.POLICY_ENFORCEMENT_POINT_APPROVAL_NEGATIVE);
        List<DataParcelManifest> manifestList = new ArrayList<>();
        manifestList.add(manifest);
        LOG.debug("getSubscribedTopics(): Exit, manifestList->{}", manifestList);
        return(manifestList);
    }

    @Override
    public String specifyWUPInstanceName() {
        return("MatrixApplicationServicesEventsSeparatorWUP");
    }

    @Override
    public void configure() throws Exception {
        LOG.debug(".configure(): Entry");

        getLogger().info("{}:: ingresFeed() --> {}", getClass().getSimpleName(), ingresFeed());
        getLogger().info("{}:: egressFeed() --> {}", getClass().getSimpleName(), egressFeed());
        
        from(ingresFeed())
                .routeId(getNameSet().getRouteCoreWUP())
                .bean(IncomingMatrixMessageSplitter.class, "splitMessageIntoEvents")
                .to(egressFeed());
    }

    @Override
    protected Logger specifyLogger() {
        return (LOG);
    }

    @Override
    protected String specifyWUPInstanceVersion() {
        return ("1.0.0");
    }

    @Override
    protected WorkshopInterface specifyWorkshop() {
        return (workshop);
    }
}
