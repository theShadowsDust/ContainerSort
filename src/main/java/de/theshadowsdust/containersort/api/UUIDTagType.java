package de.theshadowsdust.containersort.api;

import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.nio.LongBuffer;
import java.util.UUID;

public final class UUIDTagType implements PersistentDataType<long[], UUID> {

    @Override
    public @NotNull Class<long[]> getPrimitiveType() {
        return long[].class;
    }

    @Override
    public @NotNull Class<UUID> getComplexType() {
        return UUID.class;
    }

    @Override
    public long @NotNull [] toPrimitive(@NotNull UUID complex, @NotNull PersistentDataAdapterContext context) {
        LongBuffer bb = LongBuffer.wrap(new long[2]);
        bb.put(complex.getMostSignificantBits());
        bb.put(complex.getLeastSignificantBits());
        return bb.array();
    }

    @Override
    public @NotNull UUID fromPrimitive(long @NotNull [] primitive, @NotNull PersistentDataAdapterContext context) {
        LongBuffer byteBuffer = LongBuffer.wrap(primitive);
        long firstLong = byteBuffer.get();
        long secondLong = byteBuffer.get();
        return new UUID(firstLong, secondLong);
    }
}
