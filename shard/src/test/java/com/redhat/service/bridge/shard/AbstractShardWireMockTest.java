package com.redhat.service.bridge.shard;

import java.util.List;
import java.util.concurrent.CountDownLatch;

import javax.inject.Inject;

import org.junit.jupiter.api.BeforeEach;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.http.Request;
import com.github.tomakehurst.wiremock.http.RequestListener;
import com.github.tomakehurst.wiremock.http.RequestMethod;
import com.github.tomakehurst.wiremock.http.Response;
import com.redhat.service.bridge.infra.api.APIConstants;
import com.redhat.service.bridge.infra.dto.BridgeDTO;
import com.redhat.service.bridge.infra.dto.ProcessorDTO;

import io.quarkus.test.common.QuarkusTestResource;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.put;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;

@QuarkusTestResource(restrictToAnnotatedClass = true, value = ManagerMockResource.class)
public abstract class AbstractShardWireMockTest {

    @Inject
    protected ManagerSyncService managerSyncService;

    @Inject
    protected ObjectMapper objectMapper;

    @InjectWireMock
    protected WireMockServer wireMockServer;

    @BeforeEach
    protected void beforeEach() {
        wireMockServer.resetAll();
    }

    protected void stubProcessorsToDeployOrDelete(BridgeDTO bridge, List<ProcessorDTO> processorDTOS) throws JsonProcessingException {
        stubFor(get(urlEqualTo(APIConstants.SHARD_API_BASE_PATH + bridge.getId() + "/processors"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody(objectMapper.writeValueAsString(processorDTOS))));
    }

    protected void stubBridgesToDeployOrDelete(List<BridgeDTO> bridgeDTOs) throws JsonProcessingException {
        stubFor(get(urlEqualTo(APIConstants.SHARD_API_BASE_PATH))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody(objectMapper.writeValueAsString(bridgeDTOs))));
    }

    protected void stubProcessorUpdate(BridgeDTO bridgeDTO) {
        stubFor(put(urlEqualTo(APIConstants.SHARD_API_BASE_PATH + bridgeDTO.getId() + "/processors"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withStatus(200)));
    }

    protected void stubBridgeUpdate() {
        stubFor(put(urlEqualTo(APIConstants.SHARD_API_BASE_PATH))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withStatus(200)));
    }

    protected void addUpdateRequestListener(String expectedPath, CountDownLatch latch) {
        wireMockServer.addMockServiceRequestListener(new RequestListener() {
            @Override
            public void requestReceived(Request request, Response response) {
                if (request.getUrl().equals(expectedPath) && request.getMethod().equals(RequestMethod.PUT)) {
                    latch.countDown();
                }
            }
        });
    }

    protected void addProcessorUpdateRequestListener(BridgeDTO bridge, CountDownLatch latch) {
        addUpdateRequestListener(APIConstants.SHARD_API_BASE_PATH + bridge.getId() + "/processors", latch);
    }

    protected void addBridgeUpdateRequestListener(CountDownLatch latch) {
        addUpdateRequestListener(APIConstants.SHARD_API_BASE_PATH, latch);
    }
}