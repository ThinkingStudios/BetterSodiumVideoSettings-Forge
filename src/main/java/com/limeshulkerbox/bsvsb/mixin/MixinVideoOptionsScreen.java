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

import static com.limeshulkerbox.bsvsb.BRVSB.TROLoaded;

@Mixin(VideoOptionsScreen.class)
public class MixinVideoOptionsScreen extends Screen {
    @Shadow
    @Final
    @Mutable
    private static Option[] OPTIONS;
    protected MixinVideoOptionsScreen(Text title) {
        super(title);
    }

    @Inject(method = "init", at = @At("HEAD"))
    void mixinInit(CallbackInfo callbackInfo) {
        this.addButton(new ButtonWidget(this.width / 2 + 5, this.height - 27, 150, 20, new TranslatableText("button.bsvsb.video_settings_button"), (button) -> {
            assert this.client != null;
            this.client.openScreen(new SodiumOptionsGUI(this));
        }));
    }
    @Redirect(method = "init", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/widget/ButtonListWidget;addSingleOptionEntry(Lnet/minecraft/client/options/Option;)I", ordinal = 0))
    private int removeGraphicsButton(ButtonListWidget buttonListWidget, Option option) {
        return 0;
    }
    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void removingFullscreenResolution(CallbackInfo ci) {
        OPTIONS = ArrayUtils.removeElement(OPTIONS, Option.FULLSCREEN);
        OPTIONS = ArrayUtils.removeElement(OPTIONS, Option.CLOUDS);
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