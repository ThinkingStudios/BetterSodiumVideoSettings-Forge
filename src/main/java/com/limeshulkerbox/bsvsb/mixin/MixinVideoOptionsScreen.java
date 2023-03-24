package com.limeshulkerbox.bsvsb.mixin;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.option.VideoOptionsScreen;
import net.minecraft.client.gui.widget.ButtonListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.option.Option;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import org.apache.commons.lang3.ArrayUtils;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.List;

import static com.limeshulkerbox.bsvsb.BSVSBForge.TROLoaded;

@Mixin(VideoOptionsScreen.class)
public abstract class MixinVideoOptionsScreen extends Screen {
    @Shadow
    @Final
    @Mutable
    private static Option[] OPTIONS;
    @Unique
    Constructor<?> SodiumVideoOptionsScreenClassCtor;
    @Unique
    Constructor<?> SodiumOptionsGUIClassCtor;
    @Unique
    Field SodiumOptionsGUIClassPagesField;
    @Unique
    Class<?> SodiumOptionsGUIClass;
    protected MixinVideoOptionsScreen(Text title) {
        super(title);
    }

    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void removeOptions(CallbackInfo ci) {
        OPTIONS = ArrayUtils.removeElement(OPTIONS, Option.FULLSCREEN);
        OPTIONS = ArrayUtils.removeElement(OPTIONS, Option.CLOUDS);
    }

    @Inject(method = "init", at = @At("HEAD"))
    void mixinInit(CallbackInfo callbackInfo) {
        this.addDrawableChild(new ButtonWidget(this.width / 2 + 5, this.height - 27, 150, 20, new TranslatableText("button.bsvsb.video_settings_button"), (button) -> {
            if (TROLoaded) {
                flashyReesesOptionsScreen();
            } else {
                sodiumVideoOptionsScreen();
            }
        }));
    }

    @Unique
    void flashyReesesOptionsScreen() {
        if (SodiumVideoOptionsScreenClassCtor == null) {
            try {
                SodiumVideoOptionsScreenClassCtor = Class.forName("me.flashyreese.mods.reeses_sodium_options.client.gui.SodiumVideoOptionsScreen").getConstructor(Screen.class, List.class);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        try {
            assert this.client != null;
            ensureSodiumOptionsGUI();
            var tmpScreen = SodiumOptionsGUIClassCtor.newInstance(this);
            var pages = SodiumOptionsGUIClassPagesField.get(tmpScreen);
            this.client.setScreen((Screen) SodiumVideoOptionsScreenClassCtor.newInstance(this, pages));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Unique
    void ensureSodiumOptionsGUI() {
        if (SodiumOptionsGUIClass == null) {
            try {
                SodiumOptionsGUIClass = Class.forName("me.jellysquid.mods.sodium.client.gui.SodiumOptionsGUI");
                SodiumOptionsGUIClassCtor = SodiumOptionsGUIClass.getConstructor(Screen.class);
                SodiumOptionsGUIClassPagesField = SodiumOptionsGUIClass.getDeclaredField("pages");
                SodiumOptionsGUIClassPagesField.setAccessible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Unique
    void sodiumVideoOptionsScreen() {
        ensureSodiumOptionsGUI();
        try {
            assert this.client != null;
            this.client.setScreen((Screen) SodiumOptionsGUIClassCtor.newInstance(this));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Redirect(method = "init", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/widget/ButtonListWidget;addSingleOptionEntry(Lnet/minecraft/client/option/Option;)I", ordinal = 0))
    private int removeGraphicsButton(ButtonListWidget buttonListWidget, Option option) {
        return 0;
    }

    @ModifyConstant(method = "init", constant = @Constant(intValue = 100, ordinal = 0))
    private int modifyDonePos(int input) {
        return 155;
    }

    @ModifyConstant(method = "init", constant = @Constant(intValue = 200, ordinal = 0))
    private int modifyDoneWidth(int input) {
        return 150;
    }
}