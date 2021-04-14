package uk.ac.ebi.interpro.scan.management.model.implementations;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.beans.factory.annotation.Required;
import uk.ac.ebi.interpro.scan.business.postprocessing.PostProcessor;
import uk.ac.ebi.interpro.scan.management.model.Step;
import uk.ac.ebi.interpro.scan.management.model.StepInstance;
import uk.ac.ebi.interpro.scan.management.model.implementations.stepInstanceCreation.StepInstanceCreatingStep;
import uk.ac.ebi.interpro.scan.model.Match;
import uk.ac.ebi.interpro.scan.model.Site;
import uk.ac.ebi.interpro.scan.model.raw.RawMatch;
import uk.ac.ebi.interpro.scan.model.raw.RawProtein;
import uk.ac.ebi.interpro.scan.model.raw.RawSite;
import uk.ac.ebi.interpro.scan.persistence.FilteredMatchAndSiteDAO;
import uk.ac.ebi.interpro.scan.persistence.raw.RawMatchDAO;
import uk.ac.ebi.interpro.scan.persistence.raw.RawSiteDAO;
import uk.ac.ebi.interpro.scan.util.Utilities;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Gift Nuka, EMBL-EBI
 * @version $Id$
 * @since 1.0
 */

public class MatchAndSitePostProcessingStep<A extends RawMatch, B extends Match, C extends RawSite, D extends Site> extends Step {

    private static final Logger LOGGER = LogManager.getLogger(MatchAndSitePostProcessingStep.class.getName());

    protected PostProcessor<A> postProcessor;

    protected String signatureLibraryRelease;

    protected RawMatchDAO<A> rawMatchDAO;

    protected RawSiteDAO<C> rawSiteDAO;

    protected FilteredMatchAndSiteDAO<A, B, C, D> filteredMatchAndSiteDAO;

    protected boolean excludeSites;

    public void setPostProcessor(PostProcessor<A> postProcessor) {
        this.postProcessor = postProcessor;
    }

    @Required
    public void setSignatureLibraryRelease(String signatureLibraryRelease) {
        this.signatureLibraryRelease = signatureLibraryRelease;
    }

    @Required
    public void setRawMatchDAO(RawMatchDAO<A> rawMatchDAO) {
        this.rawMatchDAO = rawMatchDAO;
    }

    public void setRawSiteDAO(RawSiteDAO<C> rawSiteDAO) {
        this.rawSiteDAO = rawSiteDAO;
    }

    @Required
    public void setFilteredMatchAndSiteDAO(FilteredMatchAndSiteDAO<A, B, C, D> filteredMatchAndSiteDAO) {
        this.filteredMatchAndSiteDAO = filteredMatchAndSiteDAO;
    }

    @Required
    public void setExcludeSites(boolean excludeSites) {
        this.excludeSites = excludeSites;
    }

    /**
     * This method is called to execute the action that the StepInstance must perform.
     * <p/>
     * If an error occurs that cannot be immediately recovered from, the implementation
     * of this method MUST throw a suitable Exception, as the call
     * to execute is performed within a transaction with the reply to the JMSBroker.
     * <p/>
     * Implementations of this method MAY call delayForNfs() before starting, if, for example,
     * they are operating of file system resources.
     *
     * @param stepInstance           containing the parameters for executing.
     * @param temporaryFileDirectory
     */
    @Override
    public void execute(StepInstance stepInstance, String temporaryFileDirectory) {

        if (checkIfDoSkipRun(stepInstance.getBottomProtein(), stepInstance.getTopProtein())) {
            String key = getKey(stepInstance.getBottomProtein(), stepInstance.getTopProtein());
            Utilities.verboseLog(110, "doSkipRun - step: "  + this.getId() + " - " +  key);
            return;
        }

        // Retrieve raw results for protein range.
        Set<RawProtein<A>> rawProteins = rawMatchDAO.getProteinsByIdRange(
                stepInstance.getBottomProtein(),
                stepInstance.getTopProtein(),
                signatureLibraryRelease
        );


        Map<String, RawProtein<A>> proteinIdToRawProteinMap = new HashMap<>(rawProteins.size());
        if (rawProteins.size() == 0) {
            Long sequenceCout = stepInstance.getTopProtein() - stepInstance.getBottomProtein();
            Utilities.verboseLog(110, "Zero matches found: on " + sequenceCout + " proteins stepinstance:" + stepInstance.toString());

            int waitTimeFactor = 2;
            if (!Utilities.isRunningInSingleSeqMode()) {
                waitTimeFactor = Utilities.getWaitTimeFactorLogE(10 * sequenceCout.intValue()).intValue();
            }
            Utilities.sleep(waitTimeFactor * 1000);
            //try again
            rawProteins = rawMatchDAO.getProteinsByIdRange(
                    stepInstance.getBottomProtein(),
                    stepInstance.getTopProtein(),
                    signatureLibraryRelease
            );
            Utilities.verboseLog(110, "proteins after : " + rawProteins.size());
        }

        int matchCount = 0;
        for (RawProtein<A> rawProtein : rawProteins) {
            proteinIdToRawProteinMap.put(rawProtein.getProteinIdentifier(), rawProtein);
            matchCount += rawProtein.getMatches().size();
        }
        Utilities.verboseLog(30, " Retrieved " + rawProteins.size() + " protein(s) to post-process."
                + " A total of " + matchCount + " raw matches.");

        Map<String, RawProtein<A>> filteredMatches;
        if (postProcessor == null) {
            // No post processing required, raw matches = filtered matches
            filteredMatches = proteinIdToRawProteinMap;
            Utilities.verboseLog(1100, "No post processing required, raw matches = filtered matches");
        } else {
            // Post processing required
            filteredMatches = postProcessor.process(proteinIdToRawProteinMap);
            Utilities.verboseLog(1100, "Post processing done ...");
        }

        matchCount = 0;
        for (RawProtein<A> rawProtein : filteredMatches.values()) {
            matchCount += rawProtein.getMatches().size();
        }
        Utilities.verboseLog(1100, " Filtered  " + filteredMatches.size() + " protein(s) with  filtered matches count: " + matchCount);

        final Map<String, String> parameters = stepInstance.getParameters();
        final boolean excludeSites = Boolean.TRUE.toString().equals(parameters.get(StepInstanceCreatingStep.EXCLUDE_SITES));
        Set<C> rawSites = new HashSet<>();
        if (!excludeSites) { // Check command line argument
            if (!this.excludeSites) { // No command line argument, so check properties file configuration
                rawSites = rawSiteDAO.getSitesByProteinIdRange(
                        stepInstance.getBottomProtein(),
                        stepInstance.getTopProtein(),
                        signatureLibraryRelease
                );
                for (C repRawSite:rawSites) {
                    Utilities.verboseLog(1100, "rep filtered site: " + repRawSite );
                    break;
                }
                Utilities.verboseLog(30, "Filtered rawSites count: " + rawSites.size() );
            }
        }
        Utilities.verboseLog(30, "Filtered matches: " + filteredMatches.values().size() +  "  Filtered sites: " + rawSites.size()  );
        filteredMatchAndSiteDAO.persist(filteredMatches.values(), rawSites);

        matchCount = 0;
        for (final RawProtein rawProtein : filteredMatches.values()) {
            matchCount += rawProtein.getMatches().size();
        }
        Utilities.verboseLog(30,  "  " + filteredMatches.size() + " proteins passed through post processing."
                + " and a total of " + matchCount + " matches PASSED.");
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(filteredMatches.size() + " proteins passed through post processing.");
            LOGGER.debug("A total of " + matchCount + " matches PASSED.");
        }
    }

}
