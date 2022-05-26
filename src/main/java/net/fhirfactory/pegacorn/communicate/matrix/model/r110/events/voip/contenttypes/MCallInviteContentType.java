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
package net.fhirfactory.pegacorn.communicate.matrix.model.r110.events.voip.contenttypes;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import net.fhirfactory.pegacorn.communicate.matrix.model.r110.events.common.contenttypes.MVoIPSessionStatusType;

import java.io.Serializable;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class MCallInviteContentType implements Serializable {
    private String callerIdentifier;
    private MVoIPSessionStatusType offer;
    private Integer version;
    private Integer lifetime;

    @JsonProperty("call_id")
    public String getCallerIdentifier() {
        return callerIdentifier;
    }

    @JsonProperty("call_id")
    public void setCallerIdentifier(String callerIdentifier) {
        this.callerIdentifier = callerIdentifier;
    }

    @JsonProperty("offer")
    public MVoIPSessionStatusType getOffer() {
        return offer;
    }

    @JsonProperty("offer")
    public void setOffer(MVoIPSessionStatusType offer) {
        this.offer = offer;
    }

    @JsonProperty("version")
    public Integer getVersion() {
        return version;
    }

    @JsonProperty("version")
    public void setVersion(Integer version) {
        this.version = version;
    }

    @JsonProperty("lifetime")
    public Integer getLifetime() {
        return lifetime;
    }

    @JsonProperty("lifetime")
    public void setLifetime(Integer lifetime) {
        this.lifetime = lifetime;
    }
}
