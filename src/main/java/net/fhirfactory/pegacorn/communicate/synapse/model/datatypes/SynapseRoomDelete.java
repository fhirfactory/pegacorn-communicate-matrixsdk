package net.fhirfactory.pegacorn.communicate.synapse.model.datatypes;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

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
import java.io.Serializable;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class SynapseRoomDelete implements Serializable {
    private String message;
    private boolean block;
    private boolean purge;

    //
    // Getters and Setters
    //

    @JsonProperty("message")
    public String getMessage() {
        return message;
    }

    @JsonProperty("message")
    public void setMessage(String message) {
        this.message = message;
    }

    @JsonProperty("block")
    public boolean isBlock() {
        return block;
    }

    @JsonProperty("block")
    public void setBlock(boolean block) {
        this.block = block;
    }

    @JsonProperty("purge")
    public boolean isPurge() {
        return purge;
    }

    @JsonProperty("purge")
    public void setPurge(boolean purge) {
        this.purge = purge;
    }

    //
    // To String
    //

    @Override
    public String toString() {
        return "SynapseRoomDelete{" +
                "message='" + message + '\'' +
                ", block=" + block +
                ", purge=" + purge +
                '}';
    }
}
