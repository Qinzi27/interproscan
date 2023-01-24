package uk.ac.ebi.interpro.scan.io.match.hmmer.hmmer3;

import uk.ac.ebi.interpro.scan.io.match.hmmer.hmmer3.parsemodel.DomainMatch;
import uk.ac.ebi.interpro.scan.io.match.hmmer.hmmer3.parsemodel.HmmSearchRecord;
import uk.ac.ebi.interpro.scan.io.match.hmmer.hmmer3.parsemodel.SequenceMatch;
import uk.ac.ebi.interpro.scan.model.SignatureLibrary;
import uk.ac.ebi.interpro.scan.model.raw.NCBIfamRawMatch;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Support class to parse HMMER3 output into {@link NCBIfamRawMatch}es.
 *
 * @author Phil Jones
 * @version $Id$
 */
public class NCBIfamParserSupport extends AbstractHmmer3ParserSupport<NCBIfamRawMatch> {

    private static final Pattern MODEL_ACCESSION_LINE_PATTERN
            = Pattern.compile("^Accession:\\s+((TIGR|NF)\\d+)\\.\\d+$");

    /**
     * Returns Pattern object to parse the accession line.
     * As the regular expressions required to parse the 'ID' or 'Accession' lines appear
     * to differ from one member database to another, factored out here.
     *
     * @return Pattern object to parse the accession line.
     */
    @Override
    public Pattern getModelIdentLinePattern() {
        return MODEL_ACCESSION_LINE_PATTERN;
    }

    /**
     * Returns the model accession length, or null if this value is not available.
     *
     * @param modelIdentLinePatternMatcher matcher to the Pattern retrieved by the getSequenceIdentLinePattern method
     * @return the model accession length, or null if this value is not available.
     */
    @Override
    public Integer getModelLength(Matcher modelIdentLinePatternMatcher) {
        return null;
    }

    @Override
    protected NCBIfamRawMatch createMatch(final String signatureLibraryRelease,
                                          final HmmSearchRecord hmmSearchRecord,
                                          final SequenceMatch sequenceMatch,
                                          final DomainMatch domainMatch) {
        return new NCBIfamRawMatch(
                sequenceMatch.getSequenceIdentifier(),
                hmmSearchRecord.getModelAccession(),
                SignatureLibrary.NCBIFAM,
                signatureLibraryRelease,
                domainMatch.getAliFrom(),
                domainMatch.getAliTo(),
                sequenceMatch.getEValue(),
                sequenceMatch.getScore(),
                domainMatch.getHmmfrom(),
                domainMatch.getHmmto(),
                domainMatch.getHmmBounds(),
                domainMatch.getScore(),
                domainMatch.getEnvFrom(),
                domainMatch.getEnvTo(),
                domainMatch.getAcc(),
                sequenceMatch.getBias(),
                domainMatch.getCEvalue(),
                domainMatch.getIEvalue(),
                domainMatch.getBias()
        );
    }

    /**
     * @return true to indicate NCBIfam requires alignments be parsed from the HMMER output.
     */
    @Override
    public boolean parseAlignments() {
        return false;
    }

}
