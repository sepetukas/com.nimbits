/*
 * Copyright 2016 Benjamin Sautner
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.nimbits.server.transaction.entity.dao;


import com.google.common.base.Optional;
import com.nimbits.client.enums.EntityType;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.entity.EntityName;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.schedule.Schedule;
import com.nimbits.client.model.trigger.Trigger;
import com.nimbits.client.model.user.User;
import com.nimbits.client.model.user.UserModel;
import com.nimbits.server.gson.GsonFactory;
import com.nimbits.server.orm.*;
import com.nimbits.server.transaction.entity.EntityHelper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

import javax.jdo.*;
import java.util.*;


@Repository
public class EntityDao {

    private final static Logger logger = LoggerFactory.getLogger(EntityDao.class.getName());
    private static final int MAX_RECURSION = 10;
    private static final int INT = 1024;
    private PersistenceManagerFactory persistenceManagerFactory;


    public EntityDao() {

    }

    @Autowired
    public void setPersistenceManagerFactory(PersistenceManagerFactory persistenceManagerFactory) {
        this.persistenceManagerFactory = persistenceManagerFactory;

    }

    //@Cacheable(cacheNames = "entity")
    public List<Entity> getSubscriptionsToEntity(final User user, final Entity subscribedEntity) {
        PersistenceManager pm = persistenceManagerFactory.getPersistenceManager();
        try {
            final Query q = pm.newQuery(SubscriptionEntity.class);
            q.setFilter("subscribedEntity==p && enabled==e");
            q.declareParameters("String p, Boolean e");
            final Collection<Entity> results = (Collection<Entity>) q.execute(subscribedEntity.getId(), true);
            return EntityHelper.createModels(user, results);
        } finally {
            pm.close();
        }

    }



    //@Cacheable(cacheNames = "entity")
    public Optional<Entity> getEntityByTrigger(final User user, final Entity entity, final EntityType type) {

        PersistenceManager pm = persistenceManagerFactory.getPersistenceManager();
        try {
            final Query q = pm.newQuery(getEntityPersistentClass(type));
            q.setFilter("trigger == k && enabled == true");
            q.declareParameters("String k");
            final List<Entity> results = (List<Entity>) q.execute(entity.getId());
            if (results.isEmpty()) {
                return Optional.absent();
            } else {
                Entity e = EntityHelper.createModel(user, results.get(0));
                return Optional.of(e);
            }

        } catch (Exception ex) {
            logger.error(ex.getMessage());
            return Optional.absent();

        } finally {
            pm.close();
        }
    }

    //@Cacheable(cacheNames = "entity")
    public Map<String, Entity> getEntityMap(final User user, final EntityType type, final int limit) {

        PersistenceManager pm = persistenceManagerFactory.getPersistenceManager();

        try {
            final Query q1;

            q1 = pm.newQuery(getEntityPersistentClass(type));


            q1.setFilter("owner==b && entityType==t");
            q1.declareParameters("String b, Integer t");
            q1.setRange(0, limit);


            final Collection<Entity> result = (Collection<Entity>) q1.execute(user.getId(), type.getCode());

            final Map<String, Entity> retObj = new HashMap<String, Entity>(result.size());
            for (final Entity e : result) {
                final Entity model = EntityHelper.createModel(user, e);
                retObj.put(model.getId(), model);
            }
            return retObj;


        } finally {
            pm.close();
        }


    }

    //@Cacheable(cacheNames = "entity")
    public List<Entity> getChildren(final User user, final List<Entity> parents) {
        PersistenceManager pm = persistenceManagerFactory.getPersistenceManager();
        try {
            final List<Entity> r = new ArrayList<Entity>(INT);

            final List<Entity> result = getEntityChildren(pm, parents);
            r.addAll(result);

            return EntityHelper.createModels(user, r);
        } finally {
            pm.close();
        }

    }

    //@Cacheable(cacheNames = "entity")
    public List<Entity> getEntities(final User user) {
        PersistenceManager pm = persistenceManagerFactory.getPersistenceManager();

        try {
            final Collection<String> ownerKeys = new ArrayList<String>(1);
            ownerKeys.add(user.getId());


            final List<Entity> retObj = new ArrayList<Entity>(INT);


            for (final EntityType type : EntityType.values()) {
                if (type.isTreeGridItem()) {
                    final Query q1;
                    Collection<Entity> result;
                    try {
                        q1 = pm.newQuery(Class.forName(type.getClassName()), ":p.contains(owner)");
                        result = (Collection<Entity>) q1.execute(ownerKeys);
                    } catch (ClassNotFoundException e) {
                        result = Collections.emptyList();
                    }


                    final List<Entity> entities = EntityHelper.createModels(user, result);
                    for (final Entity entity1 : entities) {

                        retObj.add(entity1);


                    }


                }

            }

            return retObj;
        } finally {
            pm.close();
        }


    }

    //@Cacheable(cacheNames = "entity")
    public List<Entity> getEntitiesByType(final User user, final EntityType type) {
        PersistenceManager pm = persistenceManagerFactory.getPersistenceManager();


        try {
            final Collection<String> ownerKeys = new ArrayList<String>(1);
            ownerKeys.add(user.getId());


            final List<Entity> retObj = new ArrayList<Entity>(INT);


            final Query q1;
            Collection<Entity> result;

            q1 = pm.newQuery(getEntityPersistentClass(type), ":p.contains(owner)");
            result = (Collection<Entity>) q1.execute(ownerKeys);


            final List<Entity> entities = EntityHelper.createModels(user, result);
            for (final Entity entity1 : entities) {


                retObj.add(entity1);


            }


            return retObj;
        } finally {
            pm.close();
        }


    }

    //@Cacheable(cacheNames = "entity")
    public Optional<Entity> getEntity(final User user, final String id, final EntityType type) {
        PersistenceManager pm = persistenceManagerFactory.getPersistenceManager();
        Entity entity = null;

        Class cls = getEntityPersistentClass(type);
        try {
            if (!StringUtils.isEmpty(id)) {
                final Entity result = (Entity) pm.getObjectById(cls, id);
                entity = EntityHelper.createModel(user, result);


            }
            return entity == null ? Optional.<Entity>absent() : Optional.of(entity);
        } catch (Exception ex) {
            return Optional.absent();
        } finally {
            pm.close();
        }
    }

    //@Cacheable(cacheNames = "entity")
    public Optional<Entity> findEntity(User user, String id) {
        PersistenceManager pm = persistenceManagerFactory.getPersistenceManager();

        try {
            for (EntityType type : EntityType.values()) {

                Optional<Entity> sample = getEntity(user, id, type);
                if (sample.isPresent()) {
                    return sample;
                }


            }
            return Optional.absent();
        } finally {
            pm.close();
        }
    }

    //@Cacheable(cacheNames = "entity")
    public Optional<Entity> getEntityByName(final User user, final EntityName name, final EntityType type) {
        PersistenceManager pm = persistenceManagerFactory.getPersistenceManager();

        if (!user.getIsAdmin()) {
            try {

                final Query q1 = pm.newQuery(getEntityPersistentClass(type));

                final List<Entity> c;

                q1.setFilter("name==b && owner==o");
                q1.declareParameters("String b, String o");
                q1.setRange(0, 1);
                c = (List<Entity>) q1.execute(name.getValue(), user.getId());

                return getEntityOptional(user, c);
            } finally {
                pm.close();
            }
        } else {
            try {
                final Query q1 = pm.newQuery(getEntityPersistentClass(type));

                final List<Entity> c;

                q1.setFilter("name==b");
                q1.declareParameters("String b");
                q1.setRange(0, 1);
                c = (List<Entity>) q1.execute(name.getValue());

                return getEntityOptional(user, c);
            } finally {
                pm.close();
            }
        }
    }

    //@Cacheable(cacheNames = "entity")
    public String getOwner(String point) {
        PersistenceManager pm = persistenceManagerFactory.getPersistenceManager();


        try {


            final Entity result = (Entity) pm.getObjectById(Class.forName(EntityType.point.getClassName()), point);


            return result.getOwner();
        } catch (Exception ex) {

            return null;
        } finally {
            pm.close();
        }
    }

    //@Cacheable(cacheNames = "entity")
    public List<Schedule> getSchedules() {

        PersistenceManager pm = persistenceManagerFactory.getPersistenceManager();

        try {
//TODO tasks, cursors etc - query only for those ready to run

            final Query q1 = pm.newQuery(ScheduleEntity.class);
            q1.setFilter("enabled==e");
            q1.declareParameters("Boolean e");

            final List<Schedule> result = (List<Schedule>) q1.execute(true);


            return result;

        } catch (Exception e) {
            logger.error(e.getMessage());
            return Collections.emptyList();

        } finally {
            pm.close();
        }


    }


    @CacheEvict(cacheNames = "entity")
    public void deleteEntity(final User user, final Entity entity, final EntityType type) {
        PersistenceManager pm = persistenceManagerFactory.getPersistenceManager();
        Class cls = getEntityPersistentClass(type);
        if (user.getIsAdmin() || entity.isOwner(user)) {
            try {
                final Entity c = (Entity) pm.getObjectById(cls, entity.getId());
                if (c != null) {
                    List<Entity> list = new ArrayList<>(1);
                    list.add(c);
                    final List<Entity> entities = getEntityChildren(pm, list);
                    entities.add(c);

                    pm.deletePersistentAll(entities);

                }
            } catch (Throwable throwable) {
                logger.error(throwable.getMessage());

            } finally {
                pm.close();
            }
        }
    }

    @CacheEvict(cacheNames = "entity")
    public Entity addUpdateEntity(final User user, final Entity entity) {

        PersistenceManager pm = persistenceManagerFactory.getPersistenceManager();

        try {


            if (StringUtils.isEmpty(entity.getId())) {

                return addEntity(user, entity);
            } else {


                final Class cls = getEntityPersistentClass(entity.getEntityType());
                final Entity result = (Entity) pm.getObjectById(cls, entity.getId());

                if (result != null) {
                    final Transaction tx = pm.currentTransaction();
                    tx.begin();

                    result.update(entity);
                    result.validate(user);

                    tx.commit();
                    logger.info("commited update: " + result.toString());
                    return EntityHelper.createModel(user, result);


                } else {
                    throw new RuntimeException("entity not found 0004");
                }

            }


        } catch (JDOObjectNotFoundException e) {

            throw new RuntimeException("entity not found 0005");

        } finally {
            pm.close();
        }


    }


    private Optional<Entity> getEntityOptional(User user, List<Entity> c) {
        if (c.isEmpty()) {
            return Optional.absent();
        } else {

            final Entity result = c.get(0);
            return Optional.of(EntityHelper.createModel(user, result));

        }
    }

    private List<Entity> getEntityChildren(PersistenceManager pm, final List<Entity> parents) {


        final List<Entity> retObj = new ArrayList<Entity>(INT);


        for (EntityType type : EntityType.values()) {
            Query q1;

            for (Entity entity : parents) {


                q1 = pm.newQuery(getEntityPersistentClass(type));


                q1.setFilter("parent==b");
                q1.declareParameters("String b");
                String id = entity.getId();
                if (id != null) {
                    final Object sample = q1.execute(id);
                    if (sample != null) {
                        List<Entity> result = (List<Entity>) sample;
                        if (!result.isEmpty()) {
                            List<Entity> filtered = new ArrayList<>(result.size()); //avoid recursion on the user entity with the parent same as id
                            for (Entity c : result) {
                                if (c.getEntityType() != null && c.getParent() != null) {
                                    if (!c.getEntityType().equals(EntityType.user) && !c.getParent().equals(c.getId())) {
                                        filtered.add(c);
                                    }
                                } else {
                                    logger.warn("unexpected null values : " + GsonFactory.getInstance(true).toJson(c));
                                }
                            }
                            if (!filtered.isEmpty()) {
                                retObj.addAll(filtered);
                                //  for (final Entity e : result) {
                                List<Entity> children = getEntityChildren(pm, filtered);
                                retObj.addAll(children);
                            }
                            //}
                        }
                    }
                }

            }


        }


        return retObj;

    }

    private Entity addEntity(final User user, final Entity entity) {
        PersistenceManager pm = persistenceManagerFactory.getPersistenceManager();


        try {

            if (entity.getEntityType().isUniqueNameFlag()) {
                checkDuplicateEntity(user, Collections.singletonList(entity));

            }
            final Entity commit = EntityHelper.downcastEntity(entity);

            if (StringUtils.isEmpty(commit.getId())) {
                commit.setId(UUID.randomUUID().toString());

            }

            if (StringUtils.isEmpty(commit.getOwner())) {
                commit.setOwner(user.getId());

            }


            if (!commit.getEntityType().equals(EntityType.user)) {
                commit.validate(user);
                if (commit.getEntityType().isTrigger()) {
                    validate(user, (Trigger) commit);
                }

            }


            pm.makePersistent(commit);

            return EntityHelper.createModel(user, commit);

        } catch (Exception ex) {

            logger.error(ex.getMessage());
            throw ex;

        } finally {
            pm.close();
        }
    }

    private void checkDuplicateEntity(final User user, final List<Entity> entityList) {

        PersistenceManager pm = persistenceManagerFactory.getPersistenceManager();

        try {
            for (Entity entity : entityList) {

                if (entity.getEntityType().equals(EntityType.user)) {
                    final Query q1 = pm.newQuery(Class.forName(entity.getEntityType().getClassName()));
                    q1.setFilter("name==b && entityType==t");
                    q1.declareParameters("String b, Integer t");
                    q1.setRange(0, 1);
                    final Collection<Entity> c = (Collection<Entity>) q1.execute(
                            entity.getName().getValue(),
                            entity.getEntityType().getCode());
                    if (!c.isEmpty()) {

                        throw new IllegalArgumentException("A User with the email address " + entity.getName().getValue() +
                                " already exists. Entities of type [" + entity.getEntityType().name() + "] must have a " +
                                "unique name on your account");


                    }
                } else {


                    final Query q1 = pm.newQuery(Class.forName(entity.getEntityType().getClassName()));
                    q1.setFilter("name==b && owner==o && entityType==t");
                    q1.declareParameters("String b, String o, Integer t");
                    q1.setRange(0, 1);
                    final Collection<Entity> c = (Collection<Entity>) q1.execute(
                            entity.getName().getValue(),
                            user.getId(),
                            entity.getEntityType().getCode());
                    if (!c.isEmpty()) {

                        throw new IllegalArgumentException("An Entity with the name " + entity.getName().getValue() +
                                " already exists. Entities of type [" + entity.getEntityType().name() + "] must have a " +
                                "unique name on your account");

                    }
                }
            }

        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException(e);
        } finally {
            pm.close();
        }
    }

    private Class getEntityPersistentClass(EntityType type) {
        try {
            return Class.forName(type.getClassName());
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    private void validate(final User user, final Trigger entity) {

        if (StringUtils.isEmpty(entity.getTarget())) {
            throw new IllegalArgumentException("Missing target");
        }
        if (StringUtils.isEmpty(entity.getTrigger())) {
            throw new IllegalArgumentException("Missing trigger");
        }

        Optional<Entity> optional = getEntity(user, entity.getOwner(), EntityType.user);

        if (optional.isPresent() && entity.isEnabled()) {
            validateAgainstExisting((User) optional.get(), entity);
        }


    }

    private void validateAgainstExisting(User user, Trigger entity) {

        Map<String, String> map = new HashMap<String, String>(INT);
        map.put(entity.getTrigger(), entity.getTarget());

        for (EntityType type : EntityType.values()) {

            Class cls;
            try {
                cls = Class.forName(type.getClassName());
            } catch (ClassNotFoundException e) {
                throw new IllegalArgumentException(e);
            }

            if (cls.getSuperclass().equals(TriggerEntity.class)) {
                Map<String, Entity> all = getEntityMap(
                        user,
                        type,
                        1000);

                Iterable<Entity> entities = new ArrayList<Entity>(all.values());
                for (Entity e : entities) {

                    Trigger c = (Trigger) e;

                    if (!StringUtils.isEmpty(c.getTrigger()) && !StringUtils.isEmpty(c.getTarget())) {
                        map.put(c.getTrigger(), c.getTarget());
                    }
                }
            }
        }

        testRecursion(map, entity);

    }

    private static void testRecursion(Map<String, String> map, Trigger trigger) {

        if (map.containsKey(trigger.getTarget())) {  //then target is a calc
            String currentTrigger = trigger.getTrigger();
            int count = 0;
            while (true) {
                String currentTarget = map.get(currentTrigger);
                if (map.containsKey(currentTarget)) {

                    count++;
                    if (count > MAX_RECURSION) {

                        throw new IllegalArgumentException("The target for this trigger is a trigger for another entity. That's ok, but the" +
                                "target for that calc is also the trigger for another, and so on for over " + MAX_RECURSION + " steps. We " +
                                "stopped checking after that, but it looks like this is an infinite loop." + " enabled=" + trigger.isEnabled());

                    }
                } else {
                    break;
                }
            }

        }

    }


}
