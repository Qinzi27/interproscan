package uk.ac.ebi.interpro.scan.model.raw;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import javax.persistence.Entity;
import javax.persistence.Column;

import uk.ac.ebi.interpro.scan.model.PersistenceConversion;

/**
 * <a href="http://hmmer.janelia.org/">HMMER</a> raw match.
 *
 * @author  Antony Quinn
 * @author  Manjula Primma
 * @version $Id$
 */
@Entity
abstract class HmmerRawMatch extends RawMatch  {

    @Column(name="EVALUE",nullable = false, updatable = false)
    private double evalue;
    
    @Column(name="SCORE",nullable = false, updatable = false)
    private double score;

    @Column(name="HMM_START")
    private int hmmStart;

    @Column(name="HMM_END")
    private int hmmEnd;

    @Column(name="HMM_BOUNDS", length = 2)
    private String hmmBounds;

    @Column(name="SEQ_SCORE")
    private double locationScore;

    protected HmmerRawMatch() { }
    
    protected HmmerRawMatch(String sequenceIdentifier, String model,
                            String signatureLibraryName, String signatureLibraryRelease,
                            int locationStart, int locationEnd,
                            double evalue, double score,
                            int hmmStart, int hmmEnd, String hmmBounds,
                            double locationScore) {
        super(sequenceIdentifier, model, signatureLibraryName, signatureLibraryRelease, locationStart, locationEnd);
        setEvalue(evalue);
        setScore(score);
        setHmmStart(hmmStart);
        setHmmEnd(hmmEnd);
        setHmmBounds(hmmBounds);
        setLocationScore(locationScore);
    }

    public double getEvalue() {
        return PersistenceConversion.get(evalue);
    }

    private void setEvalue(double evalue) {
        this.evalue = PersistenceConversion.set(evalue);
    }

    public double getScore() {
        return score;
    }

    private void setScore(double score) {
        this.score = score;
    }

    public int getHmmStart() {
        return hmmStart;
    }

    private void setHmmStart(int hmmStart) {
        this.hmmStart = hmmStart;
    }

    public int getHmmEnd() {
        return hmmEnd;
    }

    private void setHmmEnd(int hmmEnd) {
        this.hmmEnd = hmmEnd;
    }

    public String getHmmBounds() {
        return hmmBounds;
    }

    private void setHmmBounds(String hmmBounds) {
        this.hmmBounds = hmmBounds;
    }

    public double getLocationScore() {
        return locationScore;
    }

    private void setLocationScore(double locationScore) {
        this.locationScore = locationScore;
    }

    @Override public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof HmmerRawMatch))
            return false;
        final HmmerRawMatch m = (HmmerRawMatch) o;
        return new EqualsBuilder()
                .appendSuper(super.equals(o))
                .append(evalue, m.evalue)
                .append(score, m.score)
                .append(hmmStart, m.hmmStart)
                .append(hmmEnd, m.hmmEnd)
                .append(hmmBounds, m.hmmBounds)
                .append(locationScore, m.locationScore)
                .isEquals();
    }

    @Override public int hashCode() {
        return new HashCodeBuilder(53, 55)
                .appendSuper(super.hashCode())
                .append(evalue)
                .append(score)
                .append(hmmStart)
                .append(hmmEnd)
                .append(hmmBounds)
                .append(locationScore)
                .toHashCode();
    }

    @Override public String toString()  {
        return ToStringBuilder.reflectionToString(this);
    }

}