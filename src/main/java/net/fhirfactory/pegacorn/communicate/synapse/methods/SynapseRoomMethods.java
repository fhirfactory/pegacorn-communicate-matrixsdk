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
package net.fhirfactory.pegacorn.communicate.synapse.methods;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.netty.handler.codec.http.HttpMethod;
import javassist.compiler.ast.Symbol;
import net.fhirfactory.pegacorn.communicate.synapse.methods.common.SynapseAPIResponse;
import net.fhirfactory.pegacorn.communicate.synapse.model.SynapseAdminProxyInterface;
import net.fhirfactory.pegacorn.communicate.synapse.model.SynapseRoom;
import net.fhirfactory.pegacorn.communicate.synapse.model.datatypes.SynapseQuery;
import org.apache.camel.Exchange;
import org.apache.camel.ExchangePattern;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class SynapseRoomMethods {
    private static final Logger LOG = LoggerFactory.getLogger(SynapseRoomMethods.class);

    ObjectMapper jsonMapper;

    @Inject
    SynapseAdminProxyInterface synapseProxy;

    @Produce
    private ProducerTemplate camelRouteInjector;

    public SynapseRoomMethods(){
        jsonMapper = new ObjectMapper();
        JavaTimeModule module = new JavaTimeModule();
        jsonMapper.registerModule(module);
        jsonMapper.configure(JsonParser.Feature.ALLOW_MISSING_VALUES, true);
    }

    public ObjectMapper getJSONMapper() {
        return jsonMapper;
    }

    /**
     * The List Room admin API allows server admins to get a list of rooms on their server. There are various parameters
     * available that allow for filtering and sorting the returned list. This API supports pagination.
     * @param searchTerm
     * @return
     */
    public List<SynapseRoom> getRooms(String searchTerm){
        getLogger().debug(".getRooms(): Entry, searchTerm->{}", searchTerm);

        SynapseQuery query = new SynapseQuery();

        //
        // Create the Query
        query.setHttpPath("/_synapse/admin/v1/rooms?limit=5000");
        query.setHttpMethod(HttpMethod.GET.name());

        SynapseAPIResponse response = (SynapseAPIResponse)camelRouteInjector.sendBody(synapseProxy.getSynapseRoomActionIngresEndpoint(), ExchangePattern.InOut, query);

        getLogger().debug(".getRooms(): response->{}", response);

        List<SynapseRoom> roomSet = new ArrayList<>();
        if(response.getResponseCode() != 200){
            getLogger().debug("getRooms(): Exit, something went wrong, returning empty list");
            return(roomSet);
        }

        //
        // Extract the Rooms
        JSONObject localMessageObject = new JSONObject(response.getResponseContent());
        LOG.trace("getRooms(): Converted to JSONObject --> " + localMessageObject.toString());
        JSONArray localMessageEvents = localMessageObject.getJSONArray("rooms");
        LOG.trace("getRooms(): Converted to JSONArray, number of elements --> " + localMessageEvents.length());
        boolean processingIsSuccessful = true;

        String rawRoomSet = localMessageEvents.toString();
        try {
            roomSet = getJSONMapper().readValue(rawRoomSet, new TypeReference<List<SynapseRoom>>(){});
        } catch (JsonProcessingException e) {
            getLogger().error(".getRooms(): Cannot convert retrieved room set, error->{}", ExceptionUtils.getStackTrace(e));
        }

        getLogger().debug(".getRooms(): Retrieved Room Count->{}", roomSet.size());

        getLogger().debug(".getRooms(): Entry, roomSet->{}", roomSet);
        return(roomSet);
    }

    /**
     * The List Room admin API supporting the searching for rooms owned/created-by a specific userID;
     *
     * @param userID
     * @return
     */
    public SynapseAPIResponse getRoomsForUser(String userID){
        SynapseAPIResponse taskResponse = new SynapseAPIResponse();

        return(taskResponse);
    }

    /**
     * The Room Details admin API allows server admins to get all details of a room.
     *
     * @param roomID
     * @return
     */
    public SynapseRoom getRoomDetail(String roomID){
        getLogger().debug(".getRoomDetail(): Entry, roomID->{}", roomID);
        SynapseQuery query = new SynapseQuery();

        //
        // Create the Query
        query.setHttpPath("/_synapse/admin/v1/rooms/"+roomID);
        query.setHttpMethod(HttpMethod.GET.name());

        SynapseAPIResponse response = (SynapseAPIResponse)camelRouteInjector.sendBody(synapseProxy.getSynapseRoomActionIngresEndpoint(), ExchangePattern.InOut, query);
        getLogger().trace(".getRoomDetail(): response->{}", response);

        SynapseRoom synapseRoom = null;
        if(response.getResponseCode() == 200) {
            try {
                synapseRoom = getJSONMapper().readValue(response.getResponseContent(), SynapseRoom.class);

            } catch (JsonProcessingException e) {
                getLogger().error(".getRooms(): Cannot convert retrieved room, error->{}, stacktrace->{}", ExceptionUtils.getMessage(e), ExceptionUtils.getStackTrace(e));
            }
        }
        getLogger().debug(".getRoomDetail(): Exit, synapseRoom->{}", synapseRoom);
        return(synapseRoom);
    }

    /**
     * The Room Members admin API allows server admins to get a list of all members of a room.
     *
     * @param roomID
     * @return
     */
    public SynapseAPIResponse getRoomMembers(String roomID){
        SynapseAPIResponse taskResponse = new SynapseAPIResponse();



        return(taskResponse);
    }

    public SynapseAPIResponse addRoomMember(String roomID, String memberId){
        getLogger().debug(".addRoomMember(): Entry, roomID->{}, memberId->{}", roomID, memberId);
        SynapseQuery query = new SynapseQuery();
        //
        // Create the Query
        query.setHttpPath("/_synapse/admin/v1/join/"+roomID);
        query.setHttpMethod(HttpMethod.POST.name());

        JSONObject bodyObject = new JSONObject();
        bodyObject.put("user_id", memberId);
        query.setBody(bodyObject.toString(2));

        SynapseAPIResponse response = (SynapseAPIResponse)camelRouteInjector.sendBody(synapseProxy.getSynapseRoomActionIngresEndpoint(), ExchangePattern.InOut, query);

        getLogger().debug(".getRoomDetail(): Exit, response->{}", response);
        return(response);
    }

    /**
     * The Room State admin API allows server admins to get a list of all state events in a room.
     *
     * @param roomID
     * @return
     */
    public SynapseAPIResponse getRoomState(String roomID){
        SynapseAPIResponse taskResponse = new SynapseAPIResponse();

        return(taskResponse);
    }

    /**
     * The Delete Room admin API allows server admins to remove rooms from server and block these rooms.
     *
     * The new room will be created with the user specified by the new_room_user_id parameter as room administrator and
     * will contain a message explaining what happened. Users invited to the new room will have power level -10 by
     * default, and thus be unable to speak.
     *
     * If block is True it prevents new joins to the old room.
     *
     * This API will remove all trace of the old room from your database after removing all local users. If purge is
     * true (the default), all traces of the old room will be removed from your database after removing all local users.
     * If you do not want this to happen, set purge to false. Depending on the amount of history being purged a call to
     * the API may take several minutes or longer.
     *
     * The local server will only have the power to move local user and room aliases to the new room. Users on other
     * servers will be unaffected.
     *
     * @param roomID
     * @return
     */
    public SynapseAPIResponse deleteRoom(String roomID, String reason){
        getLogger().debug(".deleteRoom(): Entry, roomID->{}", roomID);
        SynapseQuery query = new SynapseQuery();
        //
        // Create the Query
        query.setHttpPath("/_synapse/admin/v1/rooms/"+roomID);
        query.setHttpMethod(HttpMethod.DELETE.name());
        JSONObject body = new JSONObject();
        body.put("message", reason);
        body.put("block", true);
        body.put("purge", true);
        String bodyAsString = body.toString(2);
        query.setBody(bodyAsString);

        SynapseAPIResponse response = (SynapseAPIResponse)camelRouteInjector.sendBody(synapseProxy.getSynapseRoomActionIngresEndpoint(), ExchangePattern.InOut, query);

        getLogger().trace(".deleteRoom(): response->{}", response);
        return(response);
    }

    //
    // General Helper Methods
    //

    /**
     * This method searches the Synapse Database for any Rooms that contain the roomName as part of the "name"
     * attribute and returns true if one matches. Be very careful using this method, as many rooms may exist with
     * the same name. If you want to be sure about whether a room exists for a particular user (Practitioner), you
     * should use the roomExists(String roomName, String userID) method.
     *
     * @param roomName
     * @return
     */
    public boolean roomExists(String roomName){
        if(StringUtils.isBlank(roomName)){
            return(false);
        }
        List<SynapseRoom> roomList = getRooms(roomName);
        if(!roomList.isEmpty()){
            for(SynapseRoom currentRoom: roomList){
                String currentRoomName = currentRoom.getName();
                if(StringUtils.isNotBlank(currentRoomName)){
                    if(currentRoomName.contentEquals(roomName)){
                        return(true);
                    }
                }
            }
        }
        return(false);
    }

    /**
     * This method searches the Synapse Database for any Rooms that contain the roomName as part of the "name"
     * attribute and returns true if one matches AND the userID matches.
     *
     * @param roomName
     * @param userID
     * @return
     */
    public boolean roomExists(String roomName, String userID){
        if(StringUtils.isBlank(userID) || StringUtils.isBlank(roomName)) {
            return (false);
        }
        List<SynapseRoom> roomList = getRooms(roomName);
        if(!roomList.isEmpty()){
            for(SynapseRoom currentRoom: roomList){
                String currentRoomName = currentRoom.getName();
                if(roomName.contentEquals(currentRoomName)){
                    String currentRoomCreator = currentRoom.getCreator();
                    if( userID.contentEquals(currentRoomCreator)){
                        return(true);
                    }
                }
            }
        }
        return(false);
    }

    /**
     * This method extracts the (set of) SynapseRoom details (typically) provided in response to
     * a getRooms() method on the Synapse Admin API.
     *
     * @param searchResponseString The JSON Payload response to the getRooms() Synapse Admin API Method.
     * @return a List of SynapseRoom POJOs as converted from the JSON Paylood
     */
    public List<SynapseRoom> extractSearchResponseDetail(String searchResponseString){
        List<SynapseRoom> roomList = new ArrayList<>();
        if(StringUtils.isBlank(searchResponseString)){
            return(roomList);
        }
        JSONObject searchResponse = new JSONObject(searchResponseString);
        JSONArray roomElementSet = searchResponse.getJSONArray("rooms");
        if(roomElementSet.isEmpty()){
            return(roomList);
        }
        try {
            for (Integer counter = 0; counter < roomElementSet.length(); counter += 1) {
                String roomDetailString = roomElementSet.getString(counter);
                SynapseRoom currentRoom = getJSONMapper().readValue(roomDetailString, SynapseRoom.class);
                roomList.add(currentRoom);
            }
        } catch (JsonMappingException e) {
            LOG.error(".extractSearchResponseDetail(): Unable to decode room detail (JSON Mapping Exception), error ->{}",e.getMessage());
        } catch (JsonParseException e) {
            LOG.error(".extractSearchResponseDetail(): Unable to decode room detail (JSON Parsing Exception), error ->{}",e.getMessage());
        } catch (IOException e) {
            LOG.error(".extractSearchResponseDetail(): Unable to decode room detail (IO Exception), error ->{}",e.getMessage());
        }
        return(roomList);
    }

    public List<String> extractMembersFromRoom(String roomID){
        List<String> memberIDList = new ArrayList<>();
        if(StringUtils.isBlank(roomID)){
            return(memberIDList);
        }
        SynapseAPIResponse response = getRoomMembers(roomID);
        if(response.isSuccessful()){
            String responseString = response.getResponseContent();
            if(StringUtils.isNotBlank(responseString)){
                JSONObject responseObject = new JSONObject(responseString);
                JSONArray memberIDListArray = responseObject.getJSONArray("members");

            }
        }
        return(memberIDList);
    }

    //
    // Getters (and Setters)
    //

    protected Logger getLogger(){
        return(LOG);
    }
}
