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
package net.fhirfactory.pegacorn.communicate.matrix.issi.query.beans;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.fhirfactory.pegacorn.communicate.matrix.credentials.MatrixAccessToken;
import net.fhirfactory.pegacorn.communicate.matrix.methods.common.MatrixQuery;
import net.fhirfactory.dricats.constants.petasos.PetasosPropertyConstants;
import net.fhirfactory.pegacorn.petasos.tasking.observations.metrics.agents.ProcessingPlantMetricsAgent;
import net.fhirfactory.pegacorn.petasos.tasking.observations.metrics.agents.ProcessingPlantMetricsAgentAccessor;
import net.fhirfactory.pegacorn.petasos.tasking.observations.metrics.agents.WorkUnitProcessorMetricsAgent;
import org.apache.camel.Exchange;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

@Dependent
public class MatrixQueryProcessingBean {
    private static final Logger LOG = LoggerFactory.getLogger(MatrixQueryProcessingBean.class);

    private ObjectMapper jsonMapper;

    @Inject
    private MatrixAccessToken matrixAccessToken;

    @Inject
    private ProcessingPlantMetricsAgentAccessor plantMetricsAgentAccessor;

    //
    // Constructor(s)
    //

    public MatrixQueryProcessingBean(){
        this.jsonMapper = new ObjectMapper();
    }

    //
    // Business Method
    //

    public String createRequest(MatrixQuery matrixQuery, Exchange camelExchange){
        getLogger().debug(".createRequest(): Entry, matrixQuery->{}", matrixQuery);

        String body = matrixQuery.getBody();

        //
        // Now set the Exchange (HTTP) Parameters
        camelExchange.getIn().setHeader(Exchange.HTTP_METHOD, matrixQuery.getHttpMethod());
        String queryPath = null;
        if(matrixQuery.getHttpPath().contains("?")){
            queryPath = matrixQuery.getHttpPath() + "&throwExceptionOnFailure=false";
        } else {
            queryPath = matrixQuery.getHttpPath() + "?throwExceptionOnFailure=false";
        }
        camelExchange.getIn().setHeader(Exchange.HTTP_PATH, queryPath);
        camelExchange.getIn().setHeader(Exchange.CONTENT_TYPE, "application/json");
        camelExchange.getIn().setHeader("Authorization", "Bearer " + getMatrixAccessToken().getSessionAccessToken());

        //
        // Now some metrics
        try {
            WorkUnitProcessorMetricsAgent wupMetricsAgent = camelExchange.getProperty(PetasosPropertyConstants.WUP_METRICS_AGENT_EXCHANGE_PROPERTY, WorkUnitProcessorMetricsAgent.class);
            wupMetricsAgent.incrementEgressMessageAttemptCount();

            ProcessingPlantMetricsAgent plantMetricsAgent = plantMetricsAgentAccessor.getMetricsAgent();
            plantMetricsAgent.incrementInternalMessageDistributionCount("ITOps.Replica.Server[MatrixAPI]");
        } catch(Exception ex){
            getLogger().warn(".createRequest(): Problem using Metrics Services, errorMessage->{}, stackTrace->{}", ExceptionUtils.getMessage(ex), ExceptionUtils.getStackTrace(ex));
        }

        getLogger().debug(".createRequest(): Exit, body->{}", body);
        return(body);
    }

    //
    // Getters (and Setters)
    //

    protected Logger getLogger(){
        return(LOG);
    }

    protected MatrixAccessToken getMatrixAccessToken(){
        return(matrixAccessToken);
    }

    protected ObjectMapper getJSONMapper(){
        return(jsonMapper);
    }
}
