package org.ttrader.tinkoffService;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.websocket.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.ttrader.secondaryServicesUtil.SecondaryWebsocketClient;
import org.ttrader.util.TTraderUtil;
import org.ttrader.util.TickerPrice;

import javax.json.Json;
import java.net.URI;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
@ClientEndpoint
@Profile("tinkoff-service")
public class TinkoffService extends Endpoint {

    @Value("${ttrader.tinkoff.api_key}")
    private String token;

    private final SecondaryWebsocketClient websocketClient;

    private static final String WS_URL =
        "wss://invest-public-api.tinkoff.ru/ws/" +
        "tinkoff.public.invest.api.contract.v1.MarketDataStreamService/MarketDataStream";
    private final ObjectMapper objectMapper = new ObjectMapper();
    private Session session;

    private static final int unitsToNanos = 1_000_000;

    private static final FigiDescriptor[] figiDescriptors = {
        /*OGKB
        "figi": "BBG000RK52V1", "classCode": "TQBR", "uid": "3d8f2777-4a11-4713-af60-8038d11e66fa",*/
        new FigiDescriptor("BBG000RK52V1", "TQBR"),//0
        /*TGKA
        "figi": "BBG000QFH687", "classCode": "TQBR", "uid": "d74daf58-22c3-4e44-8ada-471e404fb795",*/
        new FigiDescriptor("BBG000QFH687", "TQBR"),//1
        /*TGKB
        "figi": "BBG000Q7GG57", "classCode": "TQBR", "uid": "ba9b6eb4-614c-4be8-bdba-dd86cdfece64",*/
        new FigiDescriptor("BBG000Q7GG57", "TQBR"),//2
        /*TGKBP
        "figi": "BBG000Q7GJ60", "classCode": "TQBR", "uid": "45609688-b63e-42dd-88a0-9d30c423c5e5",*/
        //new FigiDescriptor("BBG000Q7GJ60", "TQBR"),//3
        /*KUZB
        "figi": "BBG003C53JK1", "classCode": "TQBR", "uid": "879b2e35-edfb-42f4-baa0-8a00cf1edaac",*/
        new FigiDescriptor("BBG003C53JK1", "TQBR"),//4
        /*IGSTP
        "figi": "BBG002B2JCL8", "classCode": "TQBR", "uid": "0c275361-5f48-4138-a592-6da7d275075d",*/
        //new FigiDescriptor("BBG002B2JCL8", "TQBR"),//5
        /*BANE
        "figi": "BBG004S68758", "classCode": "TQBR", "uid": "0a55e045-e9a6-42d2-ac55-29674634af2f",
        "figi": "RU0007976957", "classCode": "SPEQ", "uid": "83ae56b3-268b-44cf-8fff-529f974b7daa",*/
        new FigiDescriptor("BBG004S68758", "TQBR"),//6
        /*BANEP
        "figi": "BBG004S686N0", "classCode": "TQBR", "uid": "a5776620-1e2f-47ea-bbd6-06d8e4a236d8",*/
        //new FigiDescriptor("BBG004S686N0", "TQBR"),//7
        /*PIKK
        "figi": "RU000A0JP7J7", "classCode": "SPEQ", "uid": "26461501-bc5a-4dbb-8c86-bdd85f75bbc6",*//*
        PRMD
        "figi": "TCS20A108JF7", "classCode": "PTEQ", "uid": "3a994025-a846-4355-9acc-ea07d4cc94a8",*//*
        BSPBP
        "figi": "BBG00Z197548", "classCode": "TQBR", "uid": "e71d6238-71ea-400c-9451-edc7b3338a2a",*/
        //new FigiDescriptor("BBG00Z197548", "TQBR"),//8
        /*JETL
        "figi": "TCS10A10A0J4", "classCode": "SPBRU", "uid": "34f51a49-919b-4eb1-9ed0-59c26f4cdc1e",*//*
        MRSB
        "figi": "BBG000MFYK81", "classCode": "TQBR", "uid": "1037b19b-46f4-4ebf-bb87-53401f8d2148",*/
        new FigiDescriptor("BBG000MFYK81", "TQBR"),//9
        /*CBOM
        "figi": "BBG009GSYN76", "classCode": "TQBR", "uid": "ebfda284-4291-4337-9dfb-f55610d0a907",*/
        new FigiDescriptor("BBG009GSYN76", "TQBR"),//10
        /*NAUK
        "figi": "BBG002BLR408", "classCode": "TQBR", "uid": "700671c4-624e-43d0-8377-95de3956b111",*/
        new FigiDescriptor("BBG002BLR408", "TQBR"),//11
        /*MGNT
        "figi": "BBG004RVFCY3", "classCode": "TQBR", "uid": "ca845f68-6c43-44bc-b584-330d2a1e5eb7",
        "figi": "TCS00A0JKQU8", "classCode": "SPEQ", "uid": "e004b0af-f00b-4b69-8f18-ea5958cae5fd",*/
        new FigiDescriptor("BBG004RVFCY3", "TQBR"),//12
        /*MGNT@GS
        "figi": "BBG000VFBQG4", "classCode": "SPBXM", "uid": "17b2717d-ad8f-4c73-8481-1e11f76c07f1",*//*
        VGSBP
        "figi": "BBG000FJ6S03", "classCode": "TQBR", "uid": "7b1e51e2-9de2-47d6-bf3d-ab502ee54df8",*/
        //new FigiDescriptor("BBG000FJ6S03", "TQBR"),//13
        /*NLMK
        "figi": "TCS109046452", "classCode": "SPEQ", "uid": "f9caecff-55e1-4e71-ad06-a58b39bdbe69",
        "figi": "BBG004S681B4", "classCode": "TQBR", "uid": "161eb0d0-aaac-4451-b374-f5d0eeb1b508",*/
        //new FigiDescriptor("BBG004S681B4", "TQBR"),//14
        /*NLMK@GS
        "figi": "BBG000PR0PJ6", "classCode": "SPBXM", "uid": "5246eb56-f3ec-415b-812b-f1acd5421d81",*//*
        IVAT
        "figi": "TCS00A108GD8", "classCode": "TQBR", "uid": "1936e51c-d914-4171-ac66-6390a605cb5b",*/
        //new FigiDescriptor("TCS00A108GD8", "TQBR"),//15
        /*MVID
        "figi": "BBG004S68CP5", "classCode": "TQBR", "uid": "cf1c6158-a303-43ac-89eb-9b1db8f96043",
        "figi": "TCS00A0JPGA0", "classCode": "SPEQ", "uid": "78882d6a-c7a4-4ec9-b3ed-863a908cc1b3",*/
        //new FigiDescriptor("BBG004S68CP5", "TQBR"),//16
        /*GAZP
        "figi": "BBG004730RP0", "classCode": "TQBR", "uid": "962e2a95-02a9-4171-abd7-aa198dbe643a",
        "figi": "TCS907661625", "classCode": "041",
        "figi": "TCSS07661625", "classCode": "SPEQ", "uid": "4d50dfc5-a02c-44d1-b380-9d69751a60e8",
        "figi": "TCS007661625", "classCode": "SMAL", "uid": "9100df58-0e92-48ec-b962-4e788e6b61fd",
        "figi": "TCS704730RP0", "classCode": "PTEQ", "uid": "7b8f5a9d-34f7-4fa2-88a3-de83632a9c75",
        "figi": "TCS107661625", "classCode": "BEB", "uid": "0c7b5944-5c97-4d0e-962c-f0c032e19f05",*/
        //new FigiDescriptor("BBG004730RP0", "TQBR"),//17
    };

    private final Map<String, FigiDescriptor> figis;

    private record FigiDescriptor(
        String figi, String ticker
    ){}

    public TinkoffService(SecondaryWebsocketClient websocketClient) {
        this.websocketClient = websocketClient;
        this.figis = Arrays.stream(figiDescriptors).collect(Collectors.toMap(
            figi -> figi.figi, figi -> figi
        ));
    }

    // Автоматическое подключение при старте приложения
    @PostConstruct
    public void init() {
        websocketClient.informAboutTickers(figis.values().stream().map(figi -> figi.ticker).collect(Collectors.toList()));
        connect();
    }

    @PreDestroy
    public void finish() {
        this.unsubscribe(figis.keySet());
    }

    // Подключение к WebSocket
    private void connect() {
        try {
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();

            ClientEndpointConfig.Configurator configurator = new ClientEndpointConfig.Configurator() {
                @Override
                public void beforeRequest(Map<String, List<String>> headers) {
                    headers.put("Authorization", Collections.singletonList("Bearer " + token));
                }
            };

            ClientEndpointConfig config = ClientEndpointConfig.Builder.create()
                .configurator(configurator)
                .build();

            container.connectToServer(this, config, URI.create(WS_URL));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Bean
    public CommandLineRunner CommandLineRunnerBean() {
        return (args) -> {
            System.err.println("running!");
            this.subscribe(figis.keySet());
        };
    }

    @Override
    @OnOpen
    public void onOpen(Session session, EndpointConfig endpointConfig) {
        System.err.println("✅ Connected to Tinkoff WebSocket!");
        session.addMessageHandler(String.class, this::onMessage);
        this.session = session;
    }


    @OnMessage
    public void onMessage(String message) {
        System.err.println("📩 Message from server: " + message);
        try{
            JsonNode node = new ObjectMapper().readTree(message);
            if (!node.has("lastPrice")) {
                return;
            }
            JsonNode node1 = node.get("lastPrice");
            FigiDescriptor figi = figis.get(node1.get("figi").asText());
            JsonNode node2 = node.get("price");
            double price = (double)Integer.parseInt(node2.get("units").asText()) + ((double)node2.get("nanos").asInt())/unitsToNanos;

            String time = node1.get("time").asText();
            Instant instant = Instant.parse(time.split("\\.")[0] + "Z");

            websocketClient.sendMessage(List.of(new TickerPrice(figi.ticker, price, instant.getEpochSecond())));
        }
        catch (Exception e) {
            e.printStackTrace();// todo logging errors
        }
    }

    public void subscribe(Collection<String> symbols) {
        subscribe(symbols, "SUBSCRIPTION_ACTION_SUBSCRIBE");
    }

    public void unsubscribe(Collection<String> symbols) {
        subscribe(symbols, "SUBSCRIPTION_ACTION_UNSUBSCRIBE");
    }

    public void subscribe(Collection<String> symbols, String subscription) {
        if (session != null && session.isOpen()) {
            try {
                session.getAsyncRemote().sendText(
                    Json.createObjectBuilder().add(
                        "subscribeLastPriceRequest", Json.createObjectBuilder()
                            .add("subscriptionAction", subscription)
                            .add("instruments", TTraderUtil.ofObjects(symbols.stream().map(
                                    figi -> Json.createObjectBuilder().add("figi", figi).build()
                                ).toList()).build()
                            )
                    ).build().toString()
                );
                System.err.println("subscription success");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // Закрытие соединения
    @OnClose
    @Override
    public void onClose(Session session, CloseReason reason) {
        System.err.println("❌ Connection Closed: " + reason.getReasonPhrase());
    }

    // Обработка ошибок
    @OnError
    @Override
    public void onError(Session session, Throwable throwable) {
        System.err.println("❌ Error: " + throwable.getMessage());
    }
}
