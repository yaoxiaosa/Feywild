package com.feywild.feywild.network;

import com.feywild.feywild.network.quest.*;
import io.github.noeppi_noeppi.libx.mod.ModX;
import io.github.noeppi_noeppi.libx.network.NetworkX;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.PacketDistributor;

public class FeywildNetwork extends NetworkX {

    public FeywildNetwork(ModX mod) {
        super(mod);
    }

    @Override
    protected void registerPackets() {
        this.register(new OpenLibraryScreenSerializer(), () -> OpenLibraryScreenHandler::handle, NetworkDirection.PLAY_TO_CLIENT);
        this.register(new RequestItemSerializer(), () -> RequestItemHandler::handle, NetworkDirection.PLAY_TO_SERVER);
        this.register(new ParticleSerializer(), () -> ParticleHandler::handle, NetworkDirection.PLAY_TO_CLIENT);
        this.register(new TradesSerializer(), () -> TradesHandler::handle, NetworkDirection.PLAY_TO_CLIENT);
        this.register(new OpeningScreenSerializer(), () -> OpeningScreenHandler::handle, NetworkDirection.PLAY_TO_CLIENT);

        this.register(new OpenQuestSelectionSerializer(), () -> OpenQuestSelectionHandler::handle, NetworkDirection.PLAY_TO_CLIENT);
        this.register(new OpenQuestDisplaySerializer(), () -> OpenQuestDisplayHandler::handle, NetworkDirection.PLAY_TO_CLIENT);
        this.register(new SelectQuestSerializer(), () -> SelectQuestHandler::handle, NetworkDirection.PLAY_TO_SERVER);
        this.register(new ConfirmQuestSerializer(), () -> ConfirmQuestHandler::handle, NetworkDirection.PLAY_TO_SERVER);
    }

    @Override
    protected String getProtocolVersion() {
        return "6";
    }

    public void sendParticles(World world, ParticleSerializer.Type type, BlockPos pos) {
        this.sendParticles(world, type, pos, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 0, 0, 0);
    }

    public void sendParticles(World world, ParticleSerializer.Type type, double x, double y, double z) {
        this.sendParticles(world, type, x, y, z, 0, 0, 0);
    }

    public void sendParticles(World world, ParticleSerializer.Type type, double x, double y, double z, double vx, double vy, double vz) {
        BlockPos chunk = new BlockPos((int) x, (int) y, (int) z);
        this.sendParticles(world, type, chunk, x, y, z, vx, vy, vz);
    }

    private void sendParticles(World world, ParticleSerializer.Type type, BlockPos chunk, double x, double y, double z, double vx, double vy, double vz) {
        if (world instanceof ServerWorld) {
            this.instance.send(PacketDistributor.TRACKING_CHUNK.with(() -> world.getChunkAt(chunk)), new ParticleSerializer.Message(type, x, y, z, vx, vy, vz));
        }
    }
}
