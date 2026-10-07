package com.redblaze908.blazetweaks;

import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.Mixins;

import javax.annotation.Nullable;
import java.util.Map;

@IFMLLoadingPlugin.MCVersion("1.12.2")
@IFMLLoadingPlugin.Name("BlazeTweaksCore")
public class BlazeCoreMod implements IFMLLoadingPlugin {

    public BlazeCoreMod() {
        System.out.println("[BlazeTweaks] COREMOD LOADED");

        MixinBootstrap.init();
        Mixins.addConfiguration("blazetweaks.mixins.json");

        System.out.println("[BlazeTweaks] Mixin configuration added");
    }

    @Override
    public String[] getASMTransformerClass() {
        return new String[0];
    }

    @Override
    public String getModContainerClass() {
        return null;
    }

    @Nullable
    @Override
    public String getSetupClass() {
        return null;
    }

    @Override
    public void injectData(Map<String, Object> data) {
    }

    @Override
    public String getAccessTransformerClass() {
        return null;
    }
}