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
import net.fhirfactory.pegacorn.communicate.matrix.model.interfaces.MatrixAdminProxyInterface;
import net.fhirfactory.pegacorn.communicate.matrix.model.r110.api.common.MAPIResponse;
import net.fhirfactory.pegacorn.communicate.matrix.model.r110.api.datatypes.MCreationContentResponse;
import net.fhirfactory.pegacorn.communicate.matrix.model.r110.api.rooms.MRoomCreation;
import net.fhirfactory.pegacorn.communicate.synapse.methods.SynapseRoomMethods;
import net.fhirfactory.pegacorn.communicate.synapse.model.SynapseAdminProxyInterface;
import net.fhirfactory.pegacorn.communicate.synapse.model.SynapseRoom;
import net.fhirfactory.pegacorn.communicate.synapse.model.datatypes.SynapseQuery;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

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
    public SynapseRoom createSpace(String matrixUserID, MRoomCreation roomCreation){
        getLogger().debug(".createSpace(): Entry, matrixUserID->{}, roomCreation->{}", matrixUserID, roomCreation);
        MatrixQuery query = new MatrixQuery();

        //
        // Create the Query
        query.setHttpPath("_matrix/client/r0/createRoom");
        query.setHttpMethod(HttpMethod.POST.name());

        try {
            String roomCreationAsString = jsonMapper.writeValueAsString(roomCreation);
            query.setBody(roomCreationAsString);
        } catch (JsonProcessingException e) {
            getLogger().error(".createSpace(): Error, could convert MRoomCreation to String, stack->{}", ExceptionUtils.getStackTrace(e));
        }

        MAPIResponse mapiResponse = matrixAdminAPI.executeSpaceAction(query);

        MCreationContentResponse response = new MCreationContentResponse();
        if(mapiResponse.getResponseCode() == 200){
            JSONObject responseObject = new JSONObject(mapiResponse.getResponseContent());
            String roomId = responseObject.getString("room_id");
            String roomAlias = responseObject.getString("room_alias");
            response.setRoomId(roomId);
            response.setRoomAlias(roomAlias);
        }

        SynapseRoom synapseRoom = synapseRoomAPI.getRoomDetail(response.getRoomId());

        getLogger().debug(".createSpace(): Response to creation synapseRoom->{}",synapseRoom);
        return(synapseRoom);
    }

    public MAPIResponse addChildToSpace(String spaceID, String childID){
        getLogger().debug(".createSpace(): Entry, spaceID->{}, childID->{}", spaceID, childID);
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
        getLogger().info(".createSpace(): Exit, mapiResponse->{}", body);

        query.setBody(body);

        MAPIResponse mapiResponse = matrixAdminAPI.executeSpaceAction(query);

        getLogger().debug(".createSpace(): Exit, mapiResponse->{}", mapiResponse);
        return(mapiResponse);
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
