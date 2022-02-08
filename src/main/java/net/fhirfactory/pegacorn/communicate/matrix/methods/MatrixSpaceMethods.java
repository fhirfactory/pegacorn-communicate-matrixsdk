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
package net.fhirfactory.pegacorn.communicate.matrix.methods;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.netty.handler.codec.http.HttpMethod;
import net.fhirfactory.pegacorn.communicate.matrix.methods.common.MatrixQuery;
import net.fhirfactory.pegacorn.communicate.matrix.model.core.MatrixRoom;
import net.fhirfactory.pegacorn.communicate.matrix.model.interfaces.MatrixAdminProxyInterface;
import net.fhirfactory.pegacorn.communicate.matrix.model.r110.api.common.MAPIResponse;
import net.fhirfactory.pegacorn.communicate.matrix.model.r110.api.datatypes.MCreationContentResponse;
import net.fhirfactory.pegacorn.communicate.matrix.model.r110.api.datatypes.MStateEvent;
import net.fhirfactory.pegacorn.communicate.matrix.model.r110.api.rooms.MRoomCreation;
import net.fhirfactory.pegacorn.communicate.synapse.methods.SynapseRoomMethods;
import net.fhirfactory.pegacorn.communicate.synapse.model.SynapseRoom;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class MatrixSpaceMethods {
    private static final Logger LOG = LoggerFactory.getLogger(MatrixSpaceMethods.class);

    private ObjectMapper jsonMapper;

    @Inject
    private MatrixAdminProxyInterface matrixAdminAPI;

    @Inject
    private SynapseRoomMethods synapseRoomAPI;

    //
    // Constructor(s)
    //

    public MatrixSpaceMethods() {
        jsonMapper = new ObjectMapper();
        JavaTimeModule module = new JavaTimeModule();
        jsonMapper.registerModule(module);
        jsonMapper.configure(JsonParser.Feature.ALLOW_MISSING_VALUES,true);
    }

    //
    // Business Methods
    //

    public MAPIResponse getSpace(String spaceId){
        MAPIResponse taskResponse = new MAPIResponse();

        return(taskResponse);
    }

    /**
     * Create a new space with various configuration options.
     *
     * @param roomCreation
     * @return
     */
    public MatrixRoom createSpace(String matrixUserID, MRoomCreation roomCreation){
        getLogger().info(".createSpace(): Entry, matrixUserID->{}, roomCreation->{}", matrixUserID, roomCreation);
        MatrixQuery query = new MatrixQuery();

        //
        // Create the Query
        query.setHttpPath("_matrix/client/r0/createRoom");
        query.setHttpMethod(HttpMethod.POST.name());

        JSONObject creationRequest = new JSONObject();
        if(StringUtils.isNotEmpty(roomCreation.getVisibility())) {
            creationRequest.put("visibility", roomCreation.getVisibility());
        }
        if(StringUtils.isNotEmpty(roomCreation.getRoomAliasName())){
            creationRequest.put("room_alias_name", roomCreation.getRoomAliasName());
        }
        if(StringUtils.isNotEmpty(roomCreation.getName())){
            creationRequest.put("name", roomCreation.getName());
        }
        if(StringUtils.isNotEmpty(roomCreation.getTopic())){
            creationRequest.put("topic", roomCreation.getTopic());
        }
        if(!roomCreation.getInvite3PID().isEmpty()){
            // TODO fix
        }
        if(StringUtils.isNotEmpty(roomCreation.getRoomVersion())){
            creationRequest.put("room_version", roomCreation.getRoomVersion());
        }
        if(!roomCreation.getInitialState().isEmpty()){
            JSONArray stateEventArray = new JSONArray();
            int counter = 0;
            for(MStateEvent currentStateEvent: roomCreation.getInitialState()){
                JSONObject currentStateEventObject = new JSONObject();
                if(StringUtils.isNotEmpty(currentStateEvent.getType())) {
                    currentStateEventObject.put("type", currentStateEvent.getType());
                }
                if(StringUtils.isNotEmpty(currentStateEvent.getStateKey())){
                    currentStateEventObject.put("state_key", currentStateEvent.getStateKey());
                }
                if(currentStateEvent.getContent() != null){
                    if(currentStateEvent.getContent() instanceof JSONObject){
                        JSONObject contentObject = (JSONObject) currentStateEvent.getContent();
                        currentStateEventObject.put("content", contentObject);
                    }
                }
                stateEventArray.put(counter, currentStateEventObject);
                counter += 1;
            }
            creationRequest.put("initial_state", stateEventArray);
        }
        if(StringUtils.isNotEmpty(roomCreation.getPreset())){
            creationRequest.put("preset", roomCreation.getPreset());
        }
        creationRequest.put("is_direct", roomCreation.isDirect());
        if(roomCreation.getCreationContent() != null){
            JSONObject creationContentObject = new JSONObject();
            creationContentObject.put("m.federate", roomCreation.getCreationContent().isFederate());
            if(StringUtils.isNotEmpty(roomCreation.getCreationContent().getType())){
                creationContentObject.put("type", roomCreation.getCreationContent().getType());
            }
            creationRequest.put("creation_content", creationContentObject);
        }
        if(roomCreation.getPowerLevelContentOverride() != null){
            JSONObject powerlevelOverrideObject = new JSONObject();
            powerlevelOverrideObject.put("state_default", roomCreation.getPowerLevelContentOverride().getStateDefault());
            powerlevelOverrideObject.put("ban", roomCreation.getPowerLevelContentOverride().getBan());
            powerlevelOverrideObject.put("events_default", roomCreation.getPowerLevelContentOverride().getEventDefaultPowerLevel());
            powerlevelOverrideObject.put("invite", roomCreation.getPowerLevelContentOverride().getInvite());
            powerlevelOverrideObject.put("kick", roomCreation.getPowerLevelContentOverride().getKick());
            powerlevelOverrideObject.put("redact", roomCreation.getPowerLevelContentOverride().getRedact());
            powerlevelOverrideObject.put("users_default", roomCreation.getPowerLevelContentOverride().getUserDefaultPowerLevel());
            // TODO fix the various array values
            creationRequest.put("power_level_content_override", powerlevelOverrideObject);
        }

        /*
        try {
            String roomCreationAsString = jsonMapper.writeValueAsString(roomCreation);
            getLogger().info(".createSpace(): roomCreationAsString->{}", roomCreationAsString);
            query.setBody(roomCreationAsString);
        } catch (JsonProcessingException e) {
            getLogger().error(".createSpace(): Error, could convert MRoomCreation to String, stack->{}", ExceptionUtils.getStackTrace(e));
        }

         */

        getLogger().info(".createSpace(): request.body->{}", creationRequest);

        query.setBody(creationRequest.toString());

        MAPIResponse mapiResponse = matrixAdminAPI.executeSpaceAction(query);

        MCreationContentResponse response = new MCreationContentResponse();
        SynapseRoom synapseRoom = null;
        MatrixRoom matrixRoom = null;
        if(mapiResponse.getResponseCode() == 200) {
            JSONObject responseObject = new JSONObject(mapiResponse.getResponseContent());
            String roomId = responseObject.getString("room_id");
            String roomAlias = responseObject.getString("room_alias");
            response.setRoomId(roomId);
            response.setRoomAlias(roomAlias);

            synapseRoom = synapseRoomAPI.getRoomDetail(response.getRoomId());

            if(synapseRoom != null){
                matrixRoom = new MatrixRoom(synapseRoom);
            }
        }



        getLogger().debug(".createSpace(): Response to creation matrixRoom->{}",matrixRoom);
        return(matrixRoom);
    }

    public MAPIResponse addChildToSpace(String spaceID, String childID){
        getLogger().debug(".addChildToSpace(): Entry, spaceID->{}, childID->{}", spaceID, childID);
        MatrixQuery query = new MatrixQuery();

        String path = "_matrix/client/r0/rooms/"+spaceID+"/state/m.space.child/"+childID;
        //
        // Create the Query
        query.setHttpPath(path);
        query.setHttpMethod(HttpMethod.PUT.name());

        JSONObject bodyObject = new JSONObject();
        JSONArray viaObject = new JSONArray();
        // TODO - retrieve server name from external configuration.
        viaObject.put("itops.test.gov.au");
        bodyObject.put("via", viaObject);
        bodyObject.put("suggested", false);
        bodyObject.put("auto_join", true);

        String body = bodyObject.toString(2);
        getLogger().debug(".addChildToSpace(): Exit, mapiResponse->{}", body);

        query.setBody(body);

        MAPIResponse mapiResponse = matrixAdminAPI.executeSpaceAction(query);

        getLogger().debug(".addChildToSpace(): Exit, mapiResponse->{}", mapiResponse);
        return(mapiResponse);
    }

    public List<MatrixRoom> getContainedRoomIDs(MatrixRoom space, Integer depth){
        getLogger().debug(".getContainedRoomIDs(): Entry, space->{}, depth->{}", space, depth);

        if(space == null){
            getLogger().debug(".getContainedRoomIDs(): Exit, Space is null, returning empty list");
            return(new ArrayList<>());
        }
        String spaceId = space.getRoomID();
        if(StringUtils.isEmpty(spaceId) || depth == null){
            getLogger().debug(".getContainedRoomIDs(): Exit, Either spaceID is empty or depth is null, returning empty list");
            return(new ArrayList<>());
        }

        if(depth < 1){
            getLogger().debug(".getContainedRoomIDs(): Exit, depth is less then 1, returning empty list");
            return(new ArrayList<>());
        }

        MatrixQuery query = new MatrixQuery();

        String path = "_matrix/client/v1/rooms/"+spaceId+"/hierarchy?max_depth="+depth.toString();
        //
        // Create the Query
        query.setHttpPath(path);
        query.setHttpMethod(HttpMethod.GET.name());

        MAPIResponse mapiResponse = matrixAdminAPI.executeSpaceAction(query);

        if(mapiResponse.getResponseCode() != 200){
            getLogger().debug(".getContainedRoomIDs(): Exit, bad response, error->{}", mapiResponse.getErrorResponse());
            return(new ArrayList<>());
        }

        JSONObject response = new JSONObject(mapiResponse.getResponseContent());
        JSONArray roomArray = response.getJSONArray("rooms");

        if(roomArray == null){
            getLogger().debug(".getContainedRoomIDs(): Exit, No rooms");
            return(new ArrayList<>());
        }

        List<MatrixRoom> roomList = new ArrayList<>();
        int roomCount = roomArray.length();
        for(int counter= 0; counter < roomCount; counter += 1){
            JSONObject currentRoomObject = roomArray.getJSONObject(counter);
            String roomID = currentRoomObject.getString("room_id");
            String roomName = currentRoomObject.getString("name");
            String roomTopic = currentRoomObject.optString("topic","");
            String roomAlias = currentRoomObject.getString("canonical_alias");
            String roomType = currentRoomObject.optString("room_type");
            Integer roomJoinedMembers = currentRoomObject.optInt("num_joined_members", 0);
            String roomJoinRules = currentRoomObject.optString("join_rules");
            Boolean roomWorldReadable = currentRoomObject.optBoolean("world_readable", false);
            Boolean roomGuestCanJoin = currentRoomObject.optBoolean("guest_can_join", false);
            Long roomCreationTimeStamp = currentRoomObject.optLong("creation_ts", Instant.EPOCH.getEpochSecond());
            JSONArray containedRooms = currentRoomObject.optJSONArray("children_state");

            MatrixRoom currentMatrixRoom = null;

            if (StringUtils.isNotEmpty(roomID) && StringUtils.isNotEmpty(roomName) && StringUtils.isNotEmpty(roomAlias)) {
                if(space.getRoomID().contentEquals(roomID)){
                    currentMatrixRoom = space;
                } else {
                    currentMatrixRoom = new MatrixRoom();
                    currentMatrixRoom.setRoomID(roomID);
                    currentMatrixRoom.setName(roomName);
                    currentMatrixRoom.setCanonicalAlias(roomAlias);
                    if (StringUtils.isNotEmpty(roomType)) {
                        currentMatrixRoom.setRoomType(roomType);
                    }
                    if (StringUtils.isNotEmpty(roomTopic)) {
                        currentMatrixRoom.setTopic(roomTopic);
                    }
                    if (roomJoinedMembers != null) {
                        currentMatrixRoom.setJoinedMembers(roomJoinedMembers);
                    }
                    if (StringUtils.isNotEmpty(roomJoinRules)) {
                        currentMatrixRoom.setJoinRules(roomJoinRules);
                    }
                    if (roomWorldReadable != null) {
                        currentMatrixRoom.setWorldReadable(roomWorldReadable);
                    }
                    if (roomGuestCanJoin != null) {
                        currentMatrixRoom.setGuestCanJoin(roomGuestCanJoin);
                    }
                    if (roomCreationTimeStamp != null) {
                        currentMatrixRoom.setCreationDate(roomCreationTimeStamp);
                    }
                    getLogger().info(".getContainedRoomIDs(): Adding room-->{}, name->{}", roomID, roomName);
                    roomList.add(currentMatrixRoom);
                }
                if (containedRooms != null) {
                    if (!containedRooms.isEmpty()) {
                        getLogger().info(".getContainedRoomIDs(): Has contained rooms!");
                        int containedRoomListSize = containedRooms.length();
                        for (int containedRoomCounter = 0; containedRoomCounter < containedRoomListSize; containedRoomCounter += 1) {
                            JSONObject currentContainedRoomObject = containedRooms.getJSONObject(containedRoomCounter);
                            String currentContainedRoomId = currentContainedRoomObject.getString("state_key");
                            if (!currentContainedRoomId.contentEquals(roomID)) {
                                getLogger().info(".getContainedRoomIDs(): Adding Contained Room: roomId->{}", currentContainedRoomId);
                                currentMatrixRoom.getContainedRoomIds().add(currentContainedRoomId);
                            }
                        }
                    }
                }
            }
        }
        getLogger().debug(".getContainedRoomIDs(): Exit, roomList->{}", roomList);
        return(roomList);
    }

    /**
     * Create a new mapping from room alias to room ID.
     *
     * @param roomID
     * @param roomAlias
     * @return
     */
    public MAPIResponse setRoomAlias(String practitionerMatrixUserID, String roomID, String roomAlias){
        MAPIResponse response = new MAPIResponse();

        return(response);
    }

    /**
     * Requests that the server resolve a room alias to a room ID.
     *
     * The server will use the federation API to resolve the alias if the domain part of the alias does not correspond
     * to the server's own domain.
     *
     * @param roomAlias
     * @return
     */
    public MAPIResponse getRoomFromAlias(String practitionerMatrixUserID, String roomAlias){
        MAPIResponse response = new MAPIResponse();

        return(response);
    }

    //
    // Getters and Setters
    //

    protected Logger getLogger(){
        return(LOG);
    }
}
