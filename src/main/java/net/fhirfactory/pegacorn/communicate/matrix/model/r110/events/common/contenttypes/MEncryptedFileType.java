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
package net.fhirfactory.pegacorn.communicate.matrix.model.r110.events.common.contenttypes;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class MEncryptedFileType implements Serializable {
    private String url;
    private MJSONWebKey key;
    private String uniqueCounterBlock;
    private Map<String, String> hashMap;
    private String version;

    @JsonProperty("url")
    public String getUrl() {
        return url;
    }

    @JsonProperty("url")
    public void setUrl(String url) {
        this.url = url;
    }

    @JsonProperty("key")
    public MJSONWebKey getKey() {
        return key;
    }

    @JsonProperty("key")
    public void setKey(MJSONWebKey key) {
        this.key = key;
    }

    @JsonProperty("iv")
    public String getUniqueCounterBlock() {
        return uniqueCounterBlock;
    }

    @JsonProperty("iv")
    public void setUniqueCounterBlock(String uniqueCounterBlock) {
        this.uniqueCounterBlock = uniqueCounterBlock;
    }

    @JsonProperty("hashes")
    public Map<String, String> getHashMap() {
        return hashMap;
    }

    @JsonProperty("hashes")
    public void setHashMap(Map<String, String> hashMap) {
        this.hashMap = hashMap;
    }

    @JsonProperty("v")
    public String getVersion() {
        return version;
    }

    @JsonProperty("v")
    public void setVersion(String version) {
        this.version = version;
    }
}
