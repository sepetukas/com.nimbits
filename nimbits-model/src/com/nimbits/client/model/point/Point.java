/*
 * Copyright (c) 2010 Tonic Solutions LLC.
 *
 * http://www.nimbits.com
 *
 *
 * Licensed under the GNU GENERAL PUBLIC LICENSE, Version 3.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.gnu.org/licenses/gpl.html
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the license is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.nimbits.client.model.point;

import com.nimbits.client.enums.*;
import com.nimbits.client.model.entity.EntityName;
import com.nimbits.client.model.intelligence.*;
import com.nimbits.client.model.value.*;

import java.io.*;
import java.util.*;

/**
 * Created by bsautner
 * User: benjamin
 * Date: 4/16/11
 * Time: 12:04 PM
 */
public interface Point extends Serializable {

    Intelligence getIntelligence();

    void setIntelligence(final Intelligence intelligence);

    void setCalculation(final Calculation calculation);

    int getAlarmDelay();

    boolean getAlarmToFacebook();
    @Deprecated
    AlertType getAlertState();


    ClientType getClientType();

    double getCompression();

    Date getCreateDate();

    Date getLastAlarmSent();

    Date getLastChecked();

    String getDescription();

    int getExpire();

    @Deprecated
    String getFormula();

    double getHighAlarm();

    String getHost();

    long getId();

    boolean getIdleAlarmSent();

    int getIdleSeconds();

    boolean getIgnoreIncomingCompressedValues();

    double getLowAlarm();


    EntityName getName();

    @Deprecated
    boolean getReadOnly();

    boolean getSendAlarmIM();

    boolean getSendAlarmTweet();

    boolean getSendIM();

    boolean getSendTweet();

    String getTag();

    @Deprecated
    long getTarget();

    double getTargetValue();

    String getUnit();

    long getUserFK();

    String getUUID();


    Value getValue();


    List<Value> getValues();

    @Deprecated
    long getX();

    @Deprecated
    long getY();

    @Deprecated
    long getZ();

    boolean isHighAlarmOn();

    boolean isIdleAlarmOn();

    boolean isLowAlarmOn();

    boolean isPostToFacebook();
    @Deprecated
    boolean isPublic();

    boolean isSystemPoint();

    void setAlarmDelay(final int alarmDelay);

    void setAlarmToFacebook(final boolean alarmToFacebook);

    void setAlertState(final AlertType alertState);

    void setClientType(final ClientType clientType);

    void setCompression(final double compression);

    void setCreateDate(final Date createDate);

    void setDescription(final String value);

    void setExpire(final int expire);

    // void setFormula(final String formula);

    void setHighAlarm(final double highAlarm);

    void setHighAlarmOn(final boolean highAlarmOn);

    //   void setHighestRecordedValue(final double highestRecordedValue);

    void setHost(final String host);

    void setId(final long id);

    void setIdleAlarmOn(boolean idleAlarmOn);

    void setIdleAlarmSent(final boolean idleAlarmSent);

    void setIdleSeconds(int idleSeconds);

    void setIgnoreIncomingCompressedValues(final boolean ignoreIncomingCompressedValues);

    void setLastChecked(final Date lastChecked);

    void setLowAlarm(final double lowAlarm);

    void setLowAlarmOn(final boolean lowAlarmOn);


    void setName(final EntityName name);

    void setPostToFacebook(final boolean postToFacebook);
    @Deprecated
    void setPublic(final boolean isPublic);

    void setReadOnly(final boolean readOnly);

    void setSendAlarmIM(final boolean sendAlarmIM);

    void setSendAlarmTweet(final boolean sendAlarmTweet);

    void setSendIM(final boolean sendIM);

    void setSendTweet(final boolean sendTweet);

    void setSystemPoint(final boolean isSystemPoint);

    void setTag(final String tag);

    // void setTarget(final long long1);

    void setTargetValue(final double targetValue);

    void setUnit(final String unit);

    void setUserFK(final long userFK);

    void setUuid(final String uuid);

    void setValue(final Value value);

    void setValues(final List<Value> values);

    boolean isAlarmToEmail();

    void setAlarmToEmail(boolean alarmToEmail);

    boolean getSendAlertsAsJson();

    void setSendAlertsAsJson(boolean sendAlertsAsJson);

    Calculation getCalculation();

    EntityType getEntityType();

    void setEntityType(final EntityType entityType);

}
