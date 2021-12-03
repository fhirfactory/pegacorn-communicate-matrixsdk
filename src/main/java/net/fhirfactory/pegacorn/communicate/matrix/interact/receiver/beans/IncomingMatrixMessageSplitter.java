/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.fhirfactory.pegacorn.communicate.matrix.interact.receiver.beans;

import net.fhirfactory.pegacorn.communicate.matrix.model.r061.events.common.contenttypes.MEventTypeEnum;
import net.fhirfactory.pegacorn.communicate.matrix.model.r061.events.room.message.contenttypes.MRoomMessageTypeEnum;
import net.fhirfactory.pegacorn.core.model.dataparcel.DataParcelManifest;
import net.fhirfactory.pegacorn.core.model.dataparcel.DataParcelTypeDescriptor;
import net.fhirfactory.pegacorn.core.model.dataparcel.valuesets.DataParcelNormalisationStatusEnum;
import net.fhirfactory.pegacorn.core.model.dataparcel.valuesets.DataParcelTypeEnum;
import net.fhirfactory.pegacorn.core.model.dataparcel.valuesets.DataParcelValidationStatusEnum;
import net.fhirfactory.pegacorn.core.model.dataparcel.valuesets.PolicyEnforcementPointApprovalStatusEnum;
import net.fhirfactory.pegacorn.core.model.petasos.uow.UoW;
import net.fhirfactory.pegacorn.core.model.petasos.uow.UoWPayload;
import net.fhirfactory.pegacorn.core.model.petasos.uow.UoWProcessingOutcomeEnum;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;

/**
 *
 * @author Mark A. Hunter (ACT Health)
 */
@ApplicationScoped
public class IncomingMatrixMessageSplitter {

    private static final Logger LOG = LoggerFactory.getLogger(IncomingMatrixMessageSplitter.class);

    public UoW splitMessageIntoEvents(UoW incomingUoW) {
        LOG.info("splitMessageIntoEvents(): Entry: Message to split -->" + incomingUoW);
        if (incomingUoW.getIngresContent().getPayload().isEmpty()) {
            LOG.debug("splitMessageIntoEvents(): Exit: Empty message");
            return (incomingUoW);
        }
        JSONObject localMessageObject = new JSONObject(incomingUoW.getIngresContent().getPayload());
        LOG.trace("splitMessageIntoEvents(): Converted to JSONObject --> " + localMessageObject.toString());
        JSONArray localMessageEvents = localMessageObject.getJSONArray("events");
        LOG.trace("splitMessageIntoEvents(): Converted to JSONArray, number of elements --> " + localMessageEvents.length());
        for (Integer counter = 0; counter < localMessageEvents.length(); counter += 1) {
            JSONObject eventInstance = localMessageEvents.getJSONObject(counter);
            LOG.trace("splitMessageIntoEvents(): Extracted JSONObject --> " + eventInstance.toString());
            if (eventInstance.has("type")) {
                String messageType = eventInstance.getString("type");
                MEventTypeEnum parcelType = MEventTypeEnum.fromString(messageType);
                DataParcelTypeDescriptor eventDescriptor = null;
                if(parcelType.equals(MEventTypeEnum.M_ROOM_MESSAGE)){
                    eventDescriptor = resolveInstantMessageType(eventInstance);
                }
                if(eventDescriptor == null){
                    eventDescriptor = parcelType.getTypeDescriptor();
                }
                UoWPayload newPayload = new UoWPayload();
                DataParcelManifest manifest = new DataParcelManifest();
                manifest.setContentDescriptor(eventDescriptor);
                manifest.setNormalisationStatus(DataParcelNormalisationStatusEnum.DATA_PARCEL_CONTENT_NORMALISATION_FALSE);
                manifest.setValidationStatus(DataParcelValidationStatusEnum.DATA_PARCEL_CONTENT_VALIDATED_TRUE);
                manifest.setDataParcelType(DataParcelTypeEnum.GENERAL_DATA_PARCEL_TYPE);
                manifest.setEnforcementPointApprovalStatus(PolicyEnforcementPointApprovalStatusEnum.POLICY_ENFORCEMENT_POINT_APPROVAL_NEGATIVE);
                manifest.setInterSubsystemDistributable(false);
                newPayload.setPayloadManifest(manifest);
                newPayload.setPayload(eventInstance.toString());
                incomingUoW.getEgressContent().addPayloadElement(newPayload);
                if(LOG.isTraceEnabled()){
                    LOG.trace("splitMessageIntoEvents(): Added another payload to payloadSet, count --> " + incomingUoW.getEgressContent().getPayloadElements().size());
                }
            }            
        }
        incomingUoW.setProcessingOutcome(UoWProcessingOutcomeEnum.UOW_OUTCOME_SUCCESS);
        LOG.info("splitMessageIntoEvents(): Exit: incomingUoW has been updated --> {}", incomingUoW);
        return (incomingUoW);
    }

    private DataParcelTypeDescriptor resolveInstantMessageType(JSONObject message){
        LOG.debug(".resolveInstantMessageType(): Entry, message->{}", message);
        if(message==null){
            LOG.debug(".resolveInstantMessageType(): Exit, message is null/empty");
            return(null);
        }
        JSONObject content = message.getJSONObject("content");
        if(content == null){
            LOG.debug(".resolveInstantMessageType(): Exit, message has not content segment");
            return(null);
        }
        String contentType = content.getString("msgtype");
        if(contentType != null) {
            MRoomMessageTypeEnum messageType = MRoomMessageTypeEnum.fromString(contentType);
            DataParcelTypeDescriptor descriptor = messageType.getMessageTypeDescriptor();
            LOG.debug(".resolveInstantMessageType(): Exit, descriptor->{}", descriptor);
            return(descriptor);
        }
        LOG.debug(".resolveInstantMessageType(): Exit, could not resolve type, returning null");
        return(null);
    }
}
