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
package net.fhirfactory.pegacorn.communicate.synapse.credentials;

import net.fhirfactory.pegacorn.communicate.common.APIAccessToken;

import javax.enterprise.context.ApplicationScoped;
import java.util.Objects;

@ApplicationScoped
public class SynapseAdminAccessToken extends APIAccessToken  {
    private String userName;
    private String userPassword;

    public static String SYNAPSE_ADMIN_USER_NAME_PROPERTY = "SYNAPSE_ADMIN_USER_NAME";
    public static String SYNAPSE_ADMIN_USER_PASSWORD_PROPERTY = "SYNAPSE_ADMIN_USER_PASSWORD";
    public static String SYNAPSE_ACCESS_TOKEN_PROPERTY = "SYNAPSE_ACCESS_TOKEN";

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SynapseAdminAccessToken)) return false;
        if (!super.equals(o)) return false;
        SynapseAdminAccessToken that = (SynapseAdminAccessToken) o;
        return Objects.equals(getUserName(), that.getUserName()) && Objects.equals(getUserPassword(), that.getUserPassword());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getUserName(), getUserPassword());
    }

    @Override
    public String toString() {
        return "SynapseAdminAccessToken{" +
                "remoteAccessToken='" + getRemoteAccessToken() + '\'' +
                ", localAccessToken='" + getLocalAccessToken() + '\'' +
                ", lastAccessTime=" + getLastAccessTime() +
                ", accessLock=" + getAccessLock() +
                ", userName='" + userName + '\'' +
                ", userPassword='" + userPassword + '\'' +
                '}';
    }
}
