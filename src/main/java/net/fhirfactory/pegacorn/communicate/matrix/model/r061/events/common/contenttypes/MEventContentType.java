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
package net.fhirfactory.pegacorn.communicate.matrix.model.r061.events.common.contenttypes;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import net.fhirfactory.pegacorn.communicate.matrix.model.r061.events.room.contenttypes.MMembershipEnum;

public class MEventContentType {
    private String avatarURL;
    private String displayName;
    private MMembershipEnum membership;
    private boolean direct;
    private MInviteType thirdPartyInvite;
    private MUnsignedDataType unsignedData;

    public MEventContentType(){
        this.avatarURL = null;
        this.displayName = null;
        this.membership = null;
        this.direct = false;
        this.thirdPartyInvite = null;
        this.unsignedData = null;
    }

    @JsonIgnore
    public boolean hasAvatarURL(){
        if(this.avatarURL == null){
            return(false);
        } else {
            return(true);
        }
    }

    @JsonProperty("avatar_url")
    public String getAvatarURL() {
        return avatarURL;
    }

    @JsonProperty("avatar_url")
    public void setAvatarURL(String avatarURL) {
        this.avatarURL = avatarURL;
    }

    @JsonIgnore
    public boolean hasDisplayName(){
        if(this.displayName == null){
            return(false);
        } else {
            return(true);
        }
    }

    @JsonProperty("displayname")
    public String getDisplayName() {
        return displayName;
    }

    @JsonProperty("displayname")
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    @JsonIgnore
    public boolean hasMembership(){
        boolean hasValue = this.membership != null;
        return(hasValue);
    }

    @JsonProperty("membership")
    public MMembershipEnum getMembership() {
        return membership;
    }

    @JsonProperty("membership")
    public void setMembership(MMembershipEnum membership) {
        this.membership = membership;
    }

    @JsonProperty("is_direct")
    public boolean isDirect() {
        return direct;
    }

    @JsonProperty("is_direct")
    public void setDirect(boolean direct) {
        this.direct = direct;
    }

    @JsonIgnore
    public boolean hasThirdPartyInvite(){
        if(this.thirdPartyInvite == null){
            return(false);
        } else {
            return(true);
        }
    }

    @JsonProperty("third_party_invite")
    public MInviteType getThirdPartyInvite() {
        return thirdPartyInvite;
    }

    @JsonProperty("third_party_invite")
    public void setThirdPartyInvite(MInviteType thirdPartyInvite) {
        this.thirdPartyInvite = thirdPartyInvite;
    }

    @JsonIgnore
    public boolean hasUnsignedData(){
        if(this.unsignedData == null){
            return(false);
        } else {
            return(true);
        }
    }

    @JsonProperty("unsigned")
    public MUnsignedDataType getUnsignedData() {
        return unsignedData;
    }

    @JsonProperty("unsigned")
    public void setUnsignedData(MUnsignedDataType unsignedData) {
        this.unsignedData = unsignedData;
    }
}
