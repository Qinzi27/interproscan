package uk.ac.ebi.interpro.scan.model;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import java.util.HashSet;
import java.util.Set;

/**
 * Models a match based upon the RPSBlast algorithm against a
 * protein sequence.
 *
 * @author Gift Nuka
 * @version $Id$
 * @since 5.16
 */
@Entity
@Table(name = "rpsblast_match")
@XmlType(name = "RPSBlastMatchType")
public class RPSBlastMatch extends Match<RPSBlastMatch.RPSBlastLocation> {

    protected RPSBlastMatch() {
    }

    public RPSBlastMatch(Signature signature, String signatureModels, Set<RPSBlastLocation> locations) {
        super(signature, signatureModels, locations);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof RPSBlastMatch))
            return false;
        return new EqualsBuilder()
                .appendSuper(super.equals(o))
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(41, 59)
                .appendSuper(super.hashCode())
                .toHashCode();
    }

    public Object clone() throws CloneNotSupportedException {
        final Set<RPSBlastLocation> clonedLocations = new HashSet<>(this.getLocations().size());
        for (RPSBlastLocation location : this.getLocations()) {
            clonedLocations.add((RPSBlastLocation) location.clone());
        }
        return new RPSBlastMatch(this.getSignature(), this.getSignatureModels(), clonedLocations);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    /**
     * Location of RPS-Blast (e.g. CDD)  match on a protein sequence
     *
     * @author Gift Nuka
     */
    @Entity
    @Table(name = "rpsblast_location")
    @XmlType(name = "RPSBlastLocationType", namespace = "https://ftp.ebi.ac.uk/pub/software/unix/iprscan/5/schemas")
    //@XmlType(name = "RPSBlastLocationType", namespace = "https://ftp.ebi.ac.uk/pub/software/unix/iprscan/5/schemas", propOrder = { "start", "end", "score", "evalue"})
    public static class RPSBlastLocation extends LocationWithSites<RPSBlastLocation.RPSBlastSite, LocationFragment> {

        @Column(nullable = false, name = "evalue")
        private double evalue;

        @Column(nullable = false, name = "score")
        private double score;

        protected RPSBlastLocation() {
        }

        public RPSBlastLocation(int start, int end, double score, double evalue, Set<RPSBlastSite> sites) {
            super(new RPSBlastLocationFragment(start, end), sites);
            setScore(score);
            setEvalue(evalue);
        }

        @XmlAttribute(required = true)
        public double getEvalue() {
            return evalue;
        }

        private void setEvalue(double evalue) {
            this.evalue = evalue;
        }

        @XmlAttribute(required = true)
        public double getScore() {
            return score;
        }

        private void setScore(double score) {
            this.score = score;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o)
                return true;
            if (!(o instanceof RPSBlastLocation))
                return false;
            return new EqualsBuilder()
                    .appendSuper(super.equals(o))
                    .isEquals();
        }

        @Override
        public int hashCode() {
            return new HashCodeBuilder(41, 59)
                    .appendSuper(super.hashCode())
                    .toHashCode();
        }

        public Object clone() throws CloneNotSupportedException {
            final Set<RPSBlastSite> clonedSites = new HashSet<>(this.getSites().size());
            for (RPSBlastSite site : this.getSites()) {
                clonedSites.add((RPSBlastSite) site.clone());
            }
            return new RPSBlastLocation(this.getStart(), this.getEnd(), this.getScore(), this.getEvalue(), clonedSites);
        }

        /**
         * Location fragment of a RPSBlast match on a protein sequence
         */
        @Entity
        @Table(name = "rpsblast_location_fragment")
        @XmlType(name = "RPSBlastLocationFragmentType", namespace = "https://ftp.ebi.ac.uk/pub/software/unix/iprscan/5/schemas")
        public static class RPSBlastLocationFragment extends LocationFragment {

            protected RPSBlastLocationFragment() {
            }

            public RPSBlastLocationFragment(int start, int end) {
                super(start, end);
            }

            @Override
            public boolean equals(Object o) {
                if (this == o)
                    return true;
                if (!(o instanceof RPSBlastLocationFragment))
                    return false;
                return new EqualsBuilder()
                        .appendSuper(super.equals(o))
                        .isEquals();
            }

            @Override
            public int hashCode() {
                return new HashCodeBuilder(141, 159)
                        .appendSuper(super.hashCode())
                        .toHashCode();
            }

            public Object clone() throws CloneNotSupportedException {
                return new RPSBlastLocationFragment(this.getStart(), this.getEnd());
            }
        }


        @Entity
        @Table(name = "rpsblast_site")
        @XmlType(name = "RPSBlastSiteType", namespace = "https://ftp.ebi.ac.uk/pub/software/unix/iprscan/5/schemas")
        public static class RPSBlastSite extends Site {

            protected RPSBlastSite() {
            }

            public RPSBlastSite(String description, Set<SiteLocation> siteLocations) {
                super(description, siteLocations);
            }

            @Override
            public boolean equals(Object o) {
                if (this == o)
                    return true;
                if (!(o instanceof RPSBlastSite))
                    return false;
                return new EqualsBuilder()
                        .appendSuper(super.equals(o))
                        .isEquals();
            }

            @Override
            public int hashCode() {
                return new HashCodeBuilder(41, 59)
                        .appendSuper(super.hashCode())
                        .toHashCode();
            }

            public Object clone() throws CloneNotSupportedException {
                final Set<SiteLocation> clonedSiteLocations = new HashSet<>(this.getSiteLocations().size());
                for (SiteLocation sl : this.getSiteLocations()) {
                    clonedSiteLocations.add((SiteLocation) sl.clone());
                }
                return new RPSBlastSite(this.getDescription(), clonedSiteLocations);
            }

        }
    }

}
