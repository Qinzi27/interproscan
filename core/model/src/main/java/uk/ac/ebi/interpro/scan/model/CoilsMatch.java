package uk.ac.ebi.interpro.scan.model;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlType;
import java.util.HashSet;
import java.util.Set;

/**
 * Models a match based upon the Coils algorithm against a
 * protein sequence.
 *
 * @author Phil Jones
 * @version $Id$
 * @since 1.0
 */
@Entity
@XmlType(name = "CoilsMatchType")
public class CoilsMatch extends Match<CoilsMatch.CoilsLocation> {

    protected CoilsMatch() {
    }

    public CoilsMatch(Signature signature, String signatureModels, Set<CoilsLocation> locations) {
        super(signature, signatureModels, locations);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof CoilsMatch))
            return false;
        return new EqualsBuilder()
                .appendSuper(super.equals(o))
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(27, 47)
                .appendSuper(super.hashCode())
                .toHashCode();
    }

    public Object clone() throws CloneNotSupportedException {
        final Set<CoilsLocation> clonedLocations = new HashSet<CoilsLocation>(this.getLocations().size());
        for (CoilsLocation location : this.getLocations()) {
            clonedLocations.add((CoilsLocation) location.clone());
        }
        return new CoilsMatch(this.getSignature(), this.getSignatureModels(), clonedLocations);
    }

    /**
     * Location of Coils match on a protein sequence
     *
     * @author Phil Jones
     */
    @Entity
    @Table(name = "coils_location")
    @XmlType(name = "CoilsLocationType", namespace = "https://ftp.ebi.ac.uk/pub/software/unix/iprscan/5/schemas")
    public static class CoilsLocation extends Location {

        protected CoilsLocation() {
        }

        public CoilsLocation(int start, int end) {

            super(new CoilsLocationFragment(start, end));
        }

        @Override
        public boolean equals(Object o) {
            if (this == o)
                return true;
            if (!(o instanceof CoilsLocation))
                return false;
            return new EqualsBuilder()
                    .appendSuper(super.equals(o))
                    .isEquals();
        }

        @Override
        public int hashCode() {
            return new HashCodeBuilder(27, 47)
                    .appendSuper(super.hashCode())
                    .toHashCode();
        }

        public Object clone() throws CloneNotSupportedException {
            return new CoilsLocation(this.getStart(), this.getEnd());
        }

        /**
         * Location fragment of a Coils match on a protein sequence
         */
        @Entity
        @Table(name = "coils_location_fragment")
        @XmlType(name = "CoilsLocationFragmentType", namespace = "https://ftp.ebi.ac.uk/pub/software/unix/iprscan/5/schemas")
        public static class CoilsLocationFragment extends LocationFragment {

            protected CoilsLocationFragment() {
            }

            public CoilsLocationFragment(int start, int end) {
                super(start, end);
            }

            @Override
            public boolean equals(Object o) {
                if (this == o)
                    return true;
                if (!(o instanceof CoilsLocationFragment))
                    return false;
                return new EqualsBuilder()
                        .appendSuper(super.equals(o))
                        .isEquals();
            }

            @Override
            public int hashCode() {
                return new HashCodeBuilder(127, 147)
                        .appendSuper(super.hashCode())
                        .toHashCode();
            }

            public Object clone() throws CloneNotSupportedException {
                return new CoilsLocationFragment(this.getStart(), this.getEnd());
            }
        }

    }

}
