/*
 * Source https://github.com/evanx by @evanxsummers
 * 
 */
package vellum.entity;

import java.sql.SQLException;
import vellumexp.logr.Logr;
import vellumexp.logr.LogrFactory;

/**
 *
 * @author evan.summers
 */
public abstract class AbstractEntityStorage<I, E> implements EntityStorage<I, E> {
    protected Logr logger = LogrFactory.getLogger(getClass());
    
    @Override
    public abstract E find(I id) throws SQLException;
        
}
