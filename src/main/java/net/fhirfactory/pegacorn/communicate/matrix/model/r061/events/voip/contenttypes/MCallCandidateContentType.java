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
package net.fhirfactory.pegacorn.communicate.matrix.model.r061.events.voip.contenttypes;

import com.fasterxml.jackson.annotation.JsonProperty;
import net.fhirfactory.pegacorn.communicate.matrix.model.r061.events.common.contenttypes.MVoIPCandidateType;

import java.util.List;

public class MCallCandidateContentType {
    private String callerIdentifier;
    private List<MVoIPCandidateType> candidates;
    private Integer version;

    @JsonProperty("call_id")
    public String getCallerIdentifier() {
        return callerIdentifier;
    }

    @JsonProperty("call_id")
    public void setCallerIdentifier(String callerIdentifier) {
        this.callerIdentifier = callerIdentifier;
    }

    @JsonProperty("candidates")
    public List<MVoIPCandidateType> getCandidates() {
        return candidates;
    }

    @JsonProperty("candidates")
    public void setCandidates(List<MVoIPCandidateType> candidates) {
        this.candidates = candidates;
    }

    @JsonProperty("version")
    public Integer getVersion() {
        return version;
    }

    @JsonProperty("version")
    public void setVersion(Integer version) {
        this.version = version;
    }
}
