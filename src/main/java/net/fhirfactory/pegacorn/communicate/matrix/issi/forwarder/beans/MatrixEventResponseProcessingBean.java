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
package net.fhirfactory.pegacorn.communicate.matrix.issi.forwarder.beans;

import net.fhirfactory.pegacorn.communicate.matrix.model.r110.api.common.MAPIResponse;
import net.fhirfactory.pegacorn.core.constants.petasos.PetasosPropertyConstants;
import net.fhirfactory.pegacorn.petasos.oam.metrics.agents.ProcessingPlantMetricsAgent;
import net.fhirfactory.pegacorn.petasos.oam.metrics.agents.ProcessingPlantMetricsAgentAccessor;
import net.fhirfactory.pegacorn.petasos.oam.metrics.agents.WorkUnitProcessorMetricsAgent;
import org.apache.camel.Exchange;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

@Dependent
public class MatrixEventResponseProcessingBean {
    private static final Logger LOG = LoggerFactory.getLogger(MatrixEventResponseProcessingBean.class);

    @Inject
    private ProcessingPlantMetricsAgentAccessor plantMetricsAgentAccessor;

    //
    // Constructor(s)
    //

    public MatrixEventResponseProcessingBean(){

    }

    //
    // Business Method(s)
    //

    public MAPIResponse processResponse(String httpResponseBody, Exchange camelExchange){
        getLogger().info(".processResponse(): Entry, httpResponseBody->{}", httpResponseBody);

        MAPIResponse response = new MAPIResponse();

        response.setResponseContent(httpResponseBody);

        int responseCode = (int)camelExchange.getIn().getHeader(Exchange.HTTP_RESPONSE_CODE);
        getLogger().info(".processResponse(): Entry, responseCode->{}", responseCode);
        response.setResponseCode(responseCode);

        //
        // Now some metrics
        try {
            WorkUnitProcessorMetricsAgent wupMetricsAgent = camelExchange.getProperty(PetasosPropertyConstants.WUP_METRICS_AGENT_EXCHANGE_PROPERTY, WorkUnitProcessorMetricsAgent.class);
            ProcessingPlantMetricsAgent plantMetricsAgent = plantMetricsAgentAccessor.getMetricsAgent();

            if (responseCode != 200) {
                wupMetricsAgent.incrementEgressMessageFailureCount();
                plantMetricsAgent.incrementEgressMessageFailureCount();
            } else {
                wupMetricsAgent.incrementEgressMessageSuccessCount();
                plantMetricsAgent.incrementEgressMessageSuccessCount();
            }
        } catch(Exception ex){
            getLogger().warn(".processResponse(): Problem using Metrics Services, errorMessage->{}, stackTrace->{}", ExceptionUtils.getMessage(ex), ExceptionUtils.getStackTrace(ex));
        }

        getLogger().info(".processResponse(): Exit, response->{}", response);
        return(response);
    }

    //
    // Getters (and Setters)
    //

    protected Logger getLogger(){
        return(LOG);
    }

}
