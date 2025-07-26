package com.ibm.sanction.engine;

import javax.enterprise.context.ApplicationScoped;

import org.kie.api.runtime.KieSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.sanction.model.DroolsRequest;
import com.ibm.sanction.model.LcEvaluationRequest;
import com.ibm.sanction.model.LcEvaluationResult;

@ApplicationScoped
public class SanctionRuleEngine extends BaseRuleExecutor {

    private static final Logger LOGGER = LoggerFactory.getLogger(SanctionRuleEngine.class);

    public LcEvaluationResult execute(LcEvaluationRequest lcRequest,DroolsRequest droolsRequest) {
        LOGGER.info("Starting rule execution for sanction category");
        LOGGER.info("Input DroolsRequest: {}", droolsRequest);

        LcEvaluationResult result = new LcEvaluationResult();
        KieSession kieSession = createSession("ksession-sanction");

        try {
            LOGGER.debug("Setting global 'response' and inserting DroolsRequest into session");
            kieSession.setGlobal("response", result);
            kieSession.insert(droolsRequest);
            kieSession.insert(lcRequest);
            
            LOGGER.info("Firing rules for 'ksession-sanction'");
            fire(kieSession);
            LOGGER.info("Rule execution completed successfully");

        } catch (Exception e) {
            LOGGER.error("Error during rule execution", e);
        } finally {
            kieSession.dispose(); // Defensive
            LOGGER.debug("KieSession disposed");
        }

        LOGGER.debug("Returning LcEvaluationResult: {}", result);
        return result;
    }
}
