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
package net.fhirfactory.pegacorn.communicate.common;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

public abstract class APIAccessToken implements Serializable {
    private String remoteAccessToken;
    private String localAccessToken;
    private Instant lastAccessTime;
    private Object accessLock;

    public APIAccessToken(){
        this.accessLock = new Object();
        this.lastAccessTime = Instant.now();
    }

    public String getRemoteAccessToken() {
        return remoteAccessToken;
    }

    public void setRemoteAccessToken(String remoteAccessToken) {
        this.remoteAccessToken = remoteAccessToken;
    }

    public String getLocalAccessToken() {
        return localAccessToken;
    }

    public void setLocalAccessToken(String localAccessToken) {
        this.localAccessToken = localAccessToken;
    }

    public Instant getLastAccessTime() {
        return lastAccessTime;
    }

    public void setLastAccessTime(Instant lastAccessTime) {
        this.lastAccessTime = lastAccessTime;
    }

    @JsonIgnore
    public void touchAccessTime(){
        this.lastAccessTime = Instant.now();
    }

    public Object getAccessLock() {
        return accessLock;
    }

    public void setAccessLock(Object accessLock) {
        this.accessLock = accessLock;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof APIAccessToken)) return false;
        APIAccessToken that = (APIAccessToken) o;
        return Objects.equals(getRemoteAccessToken(), that.getRemoteAccessToken()) && Objects.equals(getLocalAccessToken(), that.getLocalAccessToken()) && Objects.equals(getLastAccessTime(), that.getLastAccessTime()) && Objects.equals(getAccessLock(), that.getAccessLock());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getRemoteAccessToken(), getLocalAccessToken(), getLastAccessTime(), getAccessLock());
    }

    @Override
    public String toString() {
        return "APIAccessToken{" +
                "remoteAccessToken='" + remoteAccessToken + '\'' +
                ", localAccessToken='" + localAccessToken + '\'' +
                ", lastAccessTime=" + lastAccessTime +
                ", accessLock=" + accessLock +
                '}';
    }
}
