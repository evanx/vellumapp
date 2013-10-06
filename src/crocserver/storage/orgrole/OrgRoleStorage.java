/*
 * Source https://code.google.com/p/vellum by @evanxsummers
 * 
 */
package crocserver.storage.orgrole;

import crocserver.storage.adminuser.AdminUser;
import crocserver.storage.adminuser.AdminUserRole;
import crocserver.storage.common.CrocStorage;
import crocserver.storage.org.Org;
import java.util.ArrayList;
import java.util.List;
import vellum.entity.LongIdEntityMapStorage;
import vellum.storage.StorageException;
import vellum.storage.StorageExceptionType;

/**
 *
 * @author evan.summers
 */
public class OrgRoleStorage extends LongIdEntityMapStorage<Long, OrgRole> {

    CrocStorage storage;

    public OrgRoleStorage(CrocStorage storage) {
        this.storage = storage;
    }

    public List<OrgRole> getOrgRoleList(AdminUser user) {
        List<OrgRole> list = new ArrayList();
        for (OrgRole entity : super.getExtentList()) {
            if (entity.getUser().equals(user)) {
                list.add(entity);
            }
        }
        return list;
    }

    public List<OrgRole> getOrgRoleList(AdminUser user, Org org) {
        List<OrgRole> list = new ArrayList();
        for (OrgRole entity : super.getExtentList()) {
            if (entity.getUser().equals(user) && entity.getOrg().equals(org)) {
                list.add(entity);
            }
        }
        return list;
    }
    
    public Org getOrg(AdminUser user, String certName) throws StorageException {
        List<OrgRole> list = getOrgRoleList(user);
        if (list.isEmpty()) {
            throw new StorageException(StorageExceptionType.NOT_FOUND, user.getUserName(), certName);
        }
        return list.get(0).getOrg();
    }    

    public boolean verifyRole(String userName, Long orgId, AdminUserRole role) throws StorageException {
        if (true) {
            return true;
        }
        throw new StorageException(StorageExceptionType.NOT_FOUND, userName, orgId, role);
    }
    
    public boolean verifyRole(AdminUser user, Org org, AdminUserRole role) throws StorageException {
        if (true) {
            return true;
        }
        throw new StorageException(StorageExceptionType.NOT_FOUND, user.getUserName(),
                org.getOrgName(), role);
    }
}
