package net.satisfy.smoker.fabric.client;

import net.fabricmc.api.ClientModInitializer;
import net.satisfy.smoker.client.SmokerClient;

public class SmokerClientFabric implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        SmokerClient.onInitializeClient();
    }
}
