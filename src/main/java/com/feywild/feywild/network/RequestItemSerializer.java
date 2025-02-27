package com.feywild.feywild.network;

import io.github.noeppi_noeppi.libx.network.PacketSerializer;
import net.minecraft.network.PacketBuffer;

public class RequestItemSerializer implements PacketSerializer<RequestItemSerializer.Message> {

    @Override
    public Class<Message> messageClass() {
        return Message.class;
    }

    @Override
    public void encode(Message msg, PacketBuffer buffer) {
        buffer.writeVarInt(msg.idx);
        buffer.writeEnum(msg.state);
    }

    @Override
    public Message decode(PacketBuffer buffer) {
        return new Message(buffer.readVarInt(), buffer.readEnum(State.class));
    }

    public enum State{
        books,
        scrolls
    }

    public static class Message {
        
        public final int idx;
        public final State state;

        public Message(int idx, State state ) {
            this.idx = idx;
            this.state = state;
        }
    }
}
