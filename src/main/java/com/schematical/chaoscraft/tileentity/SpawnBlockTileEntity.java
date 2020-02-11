package com.schematical.chaoscraft.tileentity;
import java.util.Optional;
import java.util.Random;
import javax.annotation.Nullable;

import com.schematical.chaoscraft.ChaosCraft;
import com.schematical.chaoscraft.blocks.ChaosBlocks;
import net.minecraft.block.CampfireBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IClearable;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CampfireCookingRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;


public class SpawnBlockTileEntity  extends TileEntity implements ITickableTileEntity {

    private String spawnPointId = "default";

    public SpawnBlockTileEntity() {
            super(ChaosTileEntity.SPAWN_TILE.get());
        }

    @Override
    public void tick() {
        if(!ChaosBlocks.spawnBlocks.contains(this.getPos())) {
            ChaosBlocks.spawnBlocks.add(this.getPos());
            //ChaosCraft.LOGGER.info("SpawnBlock at " + this.getPos().getX() + ", " + this.getPos().getY() + ", " + this.getPos().getZ());
        }
    }


    @Override
    public void read(CompoundNBT compound) {
        super.read(compound);
        if (compound.contains("spawnPointId", 11)) {
           spawnPointId = compound.getString("spawnPointId");
        }

    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        super.write(compound);
        compound.putString("spawnPointId", this.spawnPointId);
        return compound;
    }



    /**
     * Retrieves packet to send to the client whenever this Tile Entity is resynced via World.notifyBlockUpdate. For
     * modded TE's, this packet comes back to you clientside in {@link #onDataPacket}
     */
    @Nullable
    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        return new SUpdateTileEntityPacket(this.pos, 13, this.getUpdateTag());
    }

    /**
     * Get an NBT compound to sync to the client with SPacketChunkData, used for initial loading of the chunk or when
     * many blocks change at once. This compound comes back to you clientside in
     */
    @Override
    public CompoundNBT getUpdateTag() {
        return this.write(new CompoundNBT());
    }


    public String getSpawnPointId() {
        return spawnPointId;
    }
    public void setSpawnPointId(String spawnPointId){
        setSpawnPointId(spawnPointId, false);
    }
    public void setSpawnPointId(String spawnPointId, boolean setSiblings) {
        this.spawnPointId = spawnPointId;
        this.markForUpdate();
        if(setSiblings){
            for(int x = -1; x <= 1; x += 2){
                for(int z = -1; z <= 1; z += 2) {
                   /* BlockPos blockPos = new BlockPos(
                    this.getPos().getX() + x,
                       this.getPos().getY(),
                    this.getPos().getZ() + z
                    );*/
                    BlockPos blockPos = this.getPos().add(
                        new Vec3i(
                            x,
                            0,
                            z
                        )
                    );
                    TileEntity tileentity = getWorld().getTileEntity(blockPos);
                    if(
                        tileentity != null &&
                        tileentity instanceof SpawnBlockTileEntity
                    ){
                        SpawnBlockTileEntity spawnBlockTileEntity = (SpawnBlockTileEntity) tileentity;
                        if(!spawnBlockTileEntity.getSpawnPointId().equals(spawnPointId)){
                            spawnBlockTileEntity.setSpawnPointId(spawnPointId, setSiblings);
                        }
                    }
                }
            }
        }
    }
    private void markForUpdate() {
        this.markDirty();
        this.getWorld().notifyBlockUpdate(this.getPos(), this.getBlockState(), this.getBlockState(), 3);
    }
}
