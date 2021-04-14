package uk.ac.ebi.interpro.scan.io.match.writer;

import uk.ac.ebi.interpro.scan.io.TSVWriter;
import uk.ac.ebi.interpro.scan.model.*;
import uk.ac.ebi.interpro.scan.util.Utilities;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;


/**
 * Write matches as output for InterProScan user.
 *
 * @author David Binns, EMBL-EBI, InterPro
 * @author Phil Jones, EMBL-EBI, InterPro
 * @author Maxim Scheremetjew, EMBL-EBI, InterPro
 * @author Gift Nuka, EMBL-EBI, InterPro
 * @version $Id$
 * @since 1.0-SNAPSHOT
 */
public class ProteinSiteMatchesTSVResultWriter extends ProteinMatchesResultWriter {

    private TSVWriter tsvWriter;

    public ProteinSiteMatchesTSVResultWriter(Path path) throws IOException {
        super(path);
        this.tsvWriter = new TSVWriter(super.fileWriter);
    }


    /**
     * Writes out a Protein object to a TSV file
     *
     * @param protein containing matches to be written out
     * @return the number of rows printed (i.e. the number of Locations on Matches).
     * @throws IOException in the event of I/O problem writing out the file.
     */
    public int write(Protein protein) throws IOException {
        int locationCount = 0;
        List<String> proteinAcs = getProteinAccessions(protein, proteinSequence);
        int length = protein.getSequenceLength();
        String md5 = protein.getMd5();
        String date = dmyFormat.format(new Date());

        Set<Match> matches = protein.getMatches();

        for (String proteinAc: proteinAcs) {
//            Utilities.verboseLog(1100, "sequence mapping: " + proteinAc + " -> " + protein.getId() + "  length: " +  protein.getSequenceLength() ) ;

            for (Match match : matches) {
//                Utilities.verboseLog(1100, "print-match: " + match);
                final Signature signature = match.getSignature();
                final String signatureAc = signature.getAccession();
                final SignatureLibrary signatureLibrary = signature.getSignatureLibraryRelease().getLibrary();
                final String analysis = signatureLibrary.getName();
                final String version = signature.getSignatureLibraryRelease().getVersion();

                if ((match instanceof Hmmer3MatchWithSites) || (match instanceof RPSBlastMatch) ) {
                    Set<LocationWithSites> locations = match.getLocations();
                    if (locations != null) {
                        locationCount += locations.size();
                        for (LocationWithSites location : locations) {

                            Set<Site> sites = location.getSites();
                            if (sites != null) {
                                for (Site site : sites) {
                                    for (SiteLocation siteLocation : site.getSiteLocations()) {
                                        final List<String> mappingFields = new ArrayList<>();
                                        mappingFields.add(proteinAc);
                                        mappingFields.add(md5);
                                        mappingFields.add(Integer.toString(length));
                                        mappingFields.add(analysis + "-" + version);
                                        mappingFields.add(signatureAc);
                                        mappingFields.add(Integer.toString(location.getStart()));
                                        mappingFields.add(Integer.toString(location.getEnd()));
                                        mappingFields.add(Integer.toString(site.getNumLocations()));
                                        mappingFields.add(siteLocation.getResidue());
                                        mappingFields.add(Integer.toString(siteLocation.getStart()));
                                        mappingFields.add(Integer.toString(siteLocation.getEnd()));
                                        String description = site.getDescription() == null ? "-" : site.getDescription();
                                        if (site instanceof Hmmer3MatchWithSites.Hmmer3LocationWithSites.Hmmer3Site){
                                            Hmmer3MatchWithSites.Hmmer3LocationWithSites.Hmmer3Site hmmer3Site = (Hmmer3MatchWithSites.Hmmer3LocationWithSites.Hmmer3Site) site;
                                            String featureType = hmmer3Site.getLabel();
					    description = featureType + ": " + description;
					}					
					mappingFields.add(description);
                                        /* not necessary for the data warehouse
                                        if (site instanceof Hmmer3MatchWithSites.Hmmer3LocationWithSites.Hmmer3Site){
                                            Hmmer3MatchWithSites.Hmmer3LocationWithSites.Hmmer3Site hmmer3Site = (Hmmer3MatchWithSites.Hmmer3LocationWithSites.Hmmer3Site) site;					    
                                            mappingFields.add(Integer.toString(hmmer3Site.getHmmStart()));
                                            mappingFields.add(Integer.toString(hmmer3Site.getHmmEnd()));
                                            mappingFields.add(Integer.toString(hmmer3Site.getGroup()));
                                        }

                                         */
                                        this.tsvWriter.write(mappingFields);
//                                Utilities.verboseLog(mappingFields.toString());
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return locationCount;
    }
}
