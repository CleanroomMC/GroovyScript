package com.cleanroommc.groovyscript.packmode;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.WorldSavedData;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class PackmodeSaveData extends WorldSavedData {

    public static final String ID = "groovyscript_packmode";

    public static PackmodeSaveData get(World world) {
        if (world instanceof WorldServer) {
            return get(world.getMinecraftServer());
        }
        return null;
    }

    public static PackmodeSaveData get(MinecraftServer server) {
        World world = Objects.requireNonNull(server).getWorld(0); // overworld
        PackmodeSaveData saveData = (PackmodeSaveData) world.loadData(PackmodeSaveData.class, ID);
        if (saveData == null) {
            saveData = new PackmodeSaveData(ID);
            world.setData(ID, saveData);
        }
        // if the world is a dedicated server or a lan server the packmode should be synced with each player
        saveData.dedicatedServer = server.isDedicatedServer() || ((IntegratedServer) server).getPublic();
        return saveData;
    }

    private String packmode;
    private boolean dedicatedServer;

    public PackmodeSaveData(String name) {
        super(name);
        this.packmode = Packmode.getPackmode();
        markDirty();
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        if (nbt.hasKey("packmode")) {
            this.packmode = nbt.getString("packmode");
        }
    }

    @Override
    public @NotNull NBTTagCompound writeToNBT(@NotNull NBTTagCompound compound) {
        compound.setString("packmode", this.packmode);
        return compound;
    }

    public boolean isDedicatedServer() {
        return dedicatedServer;
    }

    public void setPackmode(String packmode) {
        this.packmode = packmode;
        markDirty();
    }

    public String getPackmode() {
        return packmode;
    }

    public boolean hasPackmode() {
        return packmode != null && !packmode.isEmpty();
    }
}
