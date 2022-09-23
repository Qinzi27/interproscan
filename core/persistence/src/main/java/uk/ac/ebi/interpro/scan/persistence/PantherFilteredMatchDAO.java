package uk.ac.ebi.interpro.scan.persistence;

import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.interpro.scan.model.PantherMatch;
import uk.ac.ebi.interpro.scan.model.Protein;
import uk.ac.ebi.interpro.scan.model.raw.PantherRawMatch;
import uk.ac.ebi.interpro.scan.model.raw.RawProtein;
import uk.ac.ebi.interpro.scan.model.helper.SignatureModelHolder;

import java.util.Collection;
import java.util.Map;

/**
* PANTHER CRUD database operations.
*
* @author Matthew Fraser, EMBL-EBI, InterPro
* @version $Id$
* @since 1.0-SNAPSHOT
*/
public interface PantherFilteredMatchDAO extends FilteredMatchDAO<PantherRawMatch, PantherMatch> {

    /**
     * Persists a set of parsed PANTHER match objects as filtered matches.
     * There is no filtering step required.
     */
    @Transactional
    void persist(Collection<RawProtein<uk.ac.ebi.interpro.scan.model.raw.PantherRawMatch>> filteredProteins,
                 Map<String, SignatureModelHolder> modelIdToSignatureMap,
                 Map<String, Protein> proteinIdToProteinMap);

}
