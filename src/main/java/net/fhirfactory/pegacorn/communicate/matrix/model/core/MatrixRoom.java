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
package net.fhirfactory.pegacorn.communicate.matrix.model.core;

import net.fhirfactory.pegacorn.communicate.synapse.model.SynapseRoom;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MatrixRoom extends SynapseRoom implements Serializable {
    private String roomType;
    private Boolean worldReadable;
    private Boolean guestCanJoin;
    private Long creationDate;
    private List<String> containedRoomIds;
    private List<MatrixRoom> containedRooms;

    //
    // Constructor(s)
    //

    public MatrixRoom(){
        super();
        this.roomType = null;
        this.worldReadable = null;
        this.guestCanJoin = null;
        this.creationDate = null;
        this.containedRoomIds = new ArrayList<>();
        this.containedRooms = new ArrayList<>();
    }

    public MatrixRoom(SynapseRoom ori){
        super(ori);
        this.roomType = null;
        this.worldReadable = null;
        this.guestCanJoin = null;
        this.creationDate = null;
        this.containedRoomIds = new ArrayList<>();
        this.containedRooms = new ArrayList<>();
    }

    public MatrixRoom(MatrixRoom ori){
        super(ori);
        this.roomType = null;
        this.worldReadable = null;
        this.guestCanJoin = null;
        this.containedRoomIds = new ArrayList<>();
        this.containedRooms = new ArrayList<>();

        setRoomType(ori.getRoomType());
        setWorldReadable(ori.getWorldReadable());
        setGuestCanJoin(ori.getGuestCanJoin());
        setCreationDate(ori.getCreationDate());
        containedRoomIds.addAll(ori.getContainedRoomIds());
        containedRooms.addAll(ori.getContainedRooms());
    }

    //
    // Getters and Setters
    //


    public List<MatrixRoom> getContainedRooms() {
        return containedRooms;
    }

    public void setContainedRooms(List<MatrixRoom> containedRooms) {
        this.containedRooms = containedRooms;
    }

    public List<String> getContainedRoomIds() {
        return containedRoomIds;
    }

    public void setContainedRoomIds(List<String> containedRoomIds) {
        this.containedRoomIds = containedRoomIds;
    }

    public String getRoomType() {
        return roomType;
    }

    public void setRoomType(String roomType) {
        this.roomType = roomType;
    }

    public Boolean getWorldReadable() {
        return worldReadable;
    }

    public void setWorldReadable(Boolean worldReadable) {
        this.worldReadable = worldReadable;
    }

    public Boolean getGuestCanJoin() {
        return guestCanJoin;
    }

    public void setGuestCanJoin(Boolean guestCanJoin) {
        this.guestCanJoin = guestCanJoin;
    }

    public Long getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Long creationDate) {
        this.creationDate = creationDate;
    }

    //
    // To String
    //


    @Override
    public String toString() {
        return "MatrixRoom{" +
                "roomType='" + roomType + '\'' +
                ", worldReadable=" + worldReadable +
                ", guestCanJoin=" + guestCanJoin +
                ", creationDate=" + creationDate +
                ", containedRoomIds=" + containedRoomIds +
                ", containedRooms=" + containedRooms +
                ", joinedLocalDevices=" + getJoinedLocalDevices() +
                ", topic='" + getTopic() + '\'' +
                ", avatar='" + getAvatar() + '\'' +
                ", roomID='" + getRoomID() + '\'' +
                ", name='" + getName() + '\'' +
                ", canonicalAlias='" + getCanonicalAlias() + '\'' +
                ", joinedMembers=" + getJoinedMembers() +
                ", joinedLocalMembers=" + getJoinedLocalMembers() +
                ", version='" + getVersion() + '\'' +
                ", creator='" + getCreator() + '\'' +
                ", encryption='" + getEncryption() + '\'' +
                ", federatable=" + isFederatable() +
                ", publicRoom=" + isPublicRoom() +
                ", joinRules='" + getJoinRules() + '\'' +
                ", guestAccess='" + getGuestAccess() + '\'' +
                ", historyVisibility='" + getHistoryVisibility() + '\'' +
                ", stateEventsCount=" + getStateEventsCount() +
                '}';
    }
}
