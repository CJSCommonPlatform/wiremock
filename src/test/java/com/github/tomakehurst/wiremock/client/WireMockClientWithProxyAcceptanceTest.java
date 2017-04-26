/*
 * Copyright (C) 2011 Thomas Akehurst
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.tomakehurst.wiremock.client;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.givenThat;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.common.ProxySettings;
import com.github.tomakehurst.wiremock.core.Options;
import com.github.tomakehurst.wiremock.testsupport.WireMockTestClient;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;


//works against wiremock server which is available on UI validation server (10.124.22.42)
@Ignore
public class WireMockClientWithProxyAcceptanceTest {

    public static final String REMOTE_WIREMOCK_SERVER_HOST = "10.124.22.42";
    public static final int REMOTE_WIREMOCK_SERVER_PORT = 8080;
    public static final ProxySettings PROXY_SETTINGS = new ProxySettings("10.224.23.8", 3128);
    private WireMockTestClient testClient;

    @Before
    public void init() {
        WireMock.configureFor(REMOTE_WIREMOCK_SERVER_HOST, REMOTE_WIREMOCK_SERVER_PORT, PROXY_SETTINGS);
        testClient = new WireMockTestClient(REMOTE_WIREMOCK_SERVER_PORT, REMOTE_WIREMOCK_SERVER_HOST);
    }

    @Test
    public void buildsMappingWithUrlOnlyRequestAndStatusOnlyResponse() {
        WireMock wireMock = new WireMock(REMOTE_WIREMOCK_SERVER_HOST, REMOTE_WIREMOCK_SERVER_PORT, PROXY_SETTINGS);
        wireMock.register(
                get(urlEqualTo("/my/new/resource"))
                        .willReturn(
                                aResponse()
                                        .withStatus(304)));

        assertThat(testClient.get("/my/new/resource").statusCode(), is(304));
    }

    @Test
    public void buildsMappingFromStaticSyntax() {
        givenThat(get(urlEqualTo("/my/new/resource"))
                .willReturn(aResponse()
                        .withStatus(304)));

        assertThat(testClient.get("/my/new/resource").statusCode(), is(304));
    }

    @Test
    public void buildsMappingWithUrlOnyRequestAndResponseWithJsonBodyWithDiacriticSigns() {
        WireMock wireMock = new WireMock(REMOTE_WIREMOCK_SERVER_HOST, REMOTE_WIREMOCK_SERVER_PORT, PROXY_SETTINGS);
        wireMock.register(
                get(urlEqualTo("/my/new/resource"))
                        .willReturn(
                                aResponse()
                                        .withBody("{\"address\":\"Puerto Banús, Málaga\"}")
                                        .withStatus(200)));

        assertThat(testClient.get("/my/new/resource").content(), is("{\"address\":\"Puerto Banús, Málaga\"}"));
    }

}
