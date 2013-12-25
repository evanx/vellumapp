/*
 * Source https://github.com/evanx by @evanxsummers

 Licensed to the Apache Software Foundation (ASF) under one
 or more contributor license agreements. See the NOTICE file
 distributed with this work for additional information
 regarding copyright ownership. The ASF licenses this file to
 you under the Apache License, Version 2.0 (the "License").
 You may not use this file except in compliance with the
 License. You may obtain a copy of the License at:

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing,
 software distributed under the License is distributed on an
 "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 KIND, either express or implied.  See the License for the
 specific language governing permissions and limitations
 under the License.  
 */
package chronicexp.entitymap;

import chronic.app.ChronicApp;
import chronic.entity.Cert;
import chronic.entity.Person;
import chronic.entitytype.OrgRoleType;
import chronic.entity.Org;
import chronic.entitykey.OrgKey;
import chronic.entity.OrgRole;
import chronic.entity.Topic;
import chronic.entity.Subscription;
import chronic.entitykey.PersonKey;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vellum.data.TimestampedComparator;
import vellum.storage.EntityService;
import vellum.storage.StorageException;

/**
 *
 * @author evan.summers
 */
public abstract class ChronicDatabase {

    static Logger logger = LoggerFactory.getLogger(ChronicDatabase.class);

    protected ChronicApp app;
    
    public ChronicDatabase(ChronicApp app) {
        this.app = app;
    }

    public static void close(ChronicDatabase db) {
        if (db != null) {
            db.close();
        }
    }
    public abstract void close();

    public abstract EntityService<Person> person();

    public abstract EntityService<Org> org();

    public abstract EntityService<OrgRole> role();

    public abstract EntityService<Topic> topic();

    public abstract EntityService<Subscription> sub();

    public abstract EntityService<Cert> cert();
    
    public Iterable<Person> listPersons(String email) {
        List list = new LinkedList();
        return list;
    }

    public Iterable<OrgRole> listRoles(String email) throws StorageException {
        List list = new LinkedList();
        for (OrgRole orgRole : role().list(new PersonKey(email))) {
            if (orgRole.getEmail().equals(email) || app.getProperties().isAdmin(email)) {
                list.add(orgRole);
            }
        }
        return list;
    }

    public Iterable<String> listOrgDomains(String email, OrgRoleType roleType) 
            throws StorageException {
        List list = new LinkedList();
        for (OrgRole orgRole : role().list(new PersonKey(email))) {
            if (orgRole.getEmail().equals(email)) {
                if (roleType == null || orgRole.getRoleType() == roleType) {
                    list.add(orgRole.getOrgDomain());
                }
            }
        }
        return list;
    }

    public Iterable<OrgRole> listOrgRole(String orgDomain) throws StorageException {
        return role().list(new OrgKey(orgDomain));
    }

    public Iterable<Topic> listTopics(String email) throws StorageException {
        logger.info("listTopics {} {}", email);
        Set<Topic> topics = new TreeSet();
        for (Subscription subscriber : sub().list(email)) {
            logger.info("listTopics subscriber {}", subscriber);
            topics.add(topic().retrieve(subscriber.getTopicId()));
        }
        return topics;
    }

    public Iterable<Topic> listTopics(String email, boolean enabled) 
            throws StorageException {
        logger.info("listTopics {} {}", email);
        Set<Topic> topics = new TreeSet();
        for (Subscription subscriber : sub().list(email)) {
            logger.info("listTopics subscriber {}", subscriber);
            topics.add(topic().retrieve(subscriber.getTopicId()));
        }
        return topics;
    }
    
    public Iterable<String> listSubscriberEmails(Topic topic) throws StorageException {
        Set<String> set = new TreeSet();
        for (Subscription subscriber : sub().list(topic.getId())) {
            set.add(subscriber.getEmail());
        }
        logger.info("listSubscriberEmails {} {}", topic.getTopicLabel(), set);
        set.clear();
        set.add(app.getProperties().getAdminEmails().iterator().next());
        logger.info("listSubscriberEmails {} {}", topic.getTopicLabel(), set);
        return set;
    }

    public Iterable<Subscription> listSubscribers(String email) throws StorageException {
        Set set = new TreeSet();
        set.addAll(sub().list(email));
        logger.info("listSubscriber {}", set);
        if (app.getProperties().isAdmin(email)) {
            set.addAll(sub().list());
        }
        return set;
    }

    public Iterable<Cert> listCerts(String email) throws StorageException {
        Set set = new TreeSet(TimestampedComparator.reverse());
        for (String orgDomain : listOrgDomains(email, OrgRoleType.ADMIN)) {
            logger.info("listCerts {}", orgDomain);
            set.addAll(cert().list(new OrgKey(orgDomain)));
        }
        if (app.getProperties().isAdmin(email)) {
            set.addAll(cert().list());
        }
        return set;
    }
}
