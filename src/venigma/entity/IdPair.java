/*
 * Source https://github.com/evanx by @evanxsummers
 * 
 */
package venigma.entity;

/**
 *
 * @author evan.summers
 */
public class IdPair implements Comparable<IdPair> {
    Comparable id;
    Comparable otherId;

    public IdPair(Comparable id, Comparable otherId) {
        this.id = id;
        this.otherId = otherId;
    }
    
    @Override
    public int compareTo(IdPair linkKey) {
        int value = id.compareTo(linkKey.id);
        if (value != 0) {
            return value;
        }
        return otherId.compareTo(linkKey.otherId);
    }
    
    @Override
    public int hashCode() {
        return id.hashCode() ^ otherId.hashCode();
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof IdPair) {
            IdPair linkKey = (IdPair) object;
            return id.equals(linkKey.id) && otherId.equals(linkKey.otherId);
        }
        return false;
    }
    
    @Override
    public String toString() {
        return id.toString() + "-" + otherId.toString();
    }
}
