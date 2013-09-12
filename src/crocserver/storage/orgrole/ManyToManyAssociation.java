/*
 * Source https://code.google.com/p/vellum by @evanxsummers
 * 
 */
package crocserver.storage.orgrole;

import vellum.entity.IdEntity;
import vellum.entity.LongIdEntity;

/**
 *
 * @author evan.summers
 */
public class ManyToManyAssociation<L extends IdEntity, R extends IdEntity> implements LongIdEntity {
    Long id;
    L left;
    R right;

    public ManyToManyAssociation(Long id, L left, R right) {
        this.id = id;
        this.left = left;
        this.right = right;
    }
            
    @Override
    public void setId(Long id) {
        this.id = id;
    }
    
    @Override
    public Long getId() {
        return id;
    }

    public L getLeft() {
        return left;
    }

    public R getRight() {
        return right;
    }
       
}
