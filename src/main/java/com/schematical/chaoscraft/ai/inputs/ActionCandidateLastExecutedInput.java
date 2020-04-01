package com.schematical.chaoscraft.ai.inputs;

import com.schematical.chaoscraft.ai.InputNeuron;
import com.schematical.chaoscraft.ai.action.ActionBuffer;
import com.schematical.chaoscraft.ai.biology.ActionTargetSlot;
import com.schematical.chaoscraft.client.ClientOrgManager;
import com.schematical.chaoscraft.entities.OrgEntity;
import com.schematical.chaoscraft.services.targetnet.ScanManager;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;

/**
 * Created by user1a on 12/8/18.
 */
public class ActionCandidateLastExecutedInput extends InputNeuron {



    @Override
    public float evaluate(){
        ClientOrgManager clientOrgManager = ((OrgEntity)this.getEntity()).getClientOrgManager();
        ScanManager scanManager = clientOrgManager.getScanManager();
        ActionTargetSlot actionTargetSlot = scanManager.getFocusedAction();
        ActionBuffer actionBuffer = clientOrgManager.getActionBuffer();
        String key = actionTargetSlot.getSimpleActionStatsKey();
        ActionBuffer.SimpleActionStats simpleActionStats = actionBuffer.getSimpleActionStats(key);
        long diff = simpleActionStats.lastExecutedWorldTime - getEntity().world.getGameTime();
        setCurrentValue(diff/1000);

        return getCurrentValue();
    }


}
