package integration;/*
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

import com.nimbits.client.*;
import com.nimbits.client.exception.*;
import com.nimbits.user.*;
import org.junit.*;
import static org.junit.Assert.assertTrue;

import java.io.*;

/**
 * Created by bsautner
 * User: benjamin
 * Date: 7/30/11
 * Time: 10:14 AM
 */
public class AuthTest {


    @Test
    public void testAuth()   {
        GoogleUser user = UserFactory.createGoogleUser("", "");
        NimbitsClient client = null;

        try {
            client = NimbitsClientFactory.getInstance(user, "http://nimbits1.appspot.com");
            assertTrue(client.isLoggedIn());
        } catch (NimbitsException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }
    @Test
    public void testAuth2() throws IOException, NimbitsException {

        assertTrue(ClientHelper.client().isLoggedIn());

    }
}
