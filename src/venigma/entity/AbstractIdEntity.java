/*
 * Source https://code.google.com/p/vellum by @evanxsummers
 * 
 */
package venigma.entity;

/**
 *
 * @author evan.summers
 */
public abstract class AbstractIdEntity<I extends Comparable> implements IdEntity<I>, Comparable<IdEntity> {

    public AbstractIdEntity() {
    }
    
    @Override
    public int compareTo(IdEntity other) {
        return getId().compareTo(other.getId());
    }
    
    @Override
    public int hashCode() {
        return getId().hashCode();
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof IdEntity) {
            IdEntity other = (IdEntity) object;
            return getId().equals(other.getId());
        }
        return false;
    }
    
    @Override
    public String toString() {
        return getId().toString();
    }
}
