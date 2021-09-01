package net.fhirfactory.pegacorn.communicate.matrix.actions.simpleactions.synapseserver;

import net.fhirfactory.pegacorn.communicate.matrix.model.r061.api.common.MAPIResponse;
import net.fhirfactory.pegacorn.internals.esr.resources.PractitionerESR;

import javax.enterprise.context.ApplicationScoped;
import java.util.Map;

@ApplicationScoped
public class SynapseUserSimpleActions {

    public MAPIResponse getUserAccountDetail(String userID){
        MAPIResponse taskResponse = new MAPIResponse();

        return(taskResponse);
    }

    public MAPIResponse createUserAccount(PractitionerESR practitioner){
        MAPIResponse taskResponse = new MAPIResponse();

        return(taskResponse);
    }

    public MAPIResponse updateUserAccount(PractitionerESR practitioner){
        MAPIResponse taskResponse = new MAPIResponse();

        return(taskResponse);
    }

    public MAPIResponse listALLAccounts(Map<String,String> searchCriteria){
        MAPIResponse taskResponse = new MAPIResponse();

        return(taskResponse);
    }

    public MAPIResponse getUserSessions(String userID){
        MAPIResponse taskResponse = new MAPIResponse();

        return(taskResponse);
    }

    public MAPIResponse deactivateUserAccount(String userID){
        MAPIResponse taskResponse = new MAPIResponse();

        return(taskResponse);
    }

    public MAPIResponse getUserRooms(String userID){
        MAPIResponse taskResponse = new MAPIResponse();

        return(taskResponse);
    }

    public MAPIResponse getUserMedia(String userID){
        MAPIResponse taskResponse = new MAPIResponse();

        return(taskResponse);
    }

    public MAPIResponse getUserDevices(String userID){
        MAPIResponse taskResponse = new MAPIResponse();

        return(taskResponse);
    }
}
