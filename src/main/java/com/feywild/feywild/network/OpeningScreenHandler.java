package com.feywild.feywild.network;

import com.feywild.feywild.screens.OpeningScreen;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class OpeningScreenHandler {
    public static void handle(OpeningScreenSerializer.Message msg, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> Minecraft.getInstance().setScreen(new OpeningScreen(msg.size)));
        context.get().setPacketHandled(true);
    }
}
