package io.alwa.featuredservers.mixin;

import io.alwa.featuredservers.FeaturedServers;
import net.minecraft.client.gui.screen.MultiplayerScreen;
import net.minecraft.client.gui.screen.ServerSelectionList;
import net.minecraft.client.gui.widget.button.Button;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(MultiplayerScreen.class)
public abstract class MixinMultiplayerScreen {

    @Shadow protected ServerSelectionList serverSelectionList;
    @Shadow private Button selectButton, deleteButton, editButton;

    @SuppressWarnings("OverwriteAuthorRequired")
    @Overwrite
    protected void onSelectedChange() {
        this.selectButton.active = false;
        this.editButton.active = false;
        this.deleteButton.active = false;
        ServerSelectionList.Entry serverselectionlist$entry = this.serverSelectionList.getSelected();
        if (serverselectionlist$entry != null && !(serverselectionlist$entry instanceof ServerSelectionList.LanScanEntry)) {
            this.selectButton.active = true;
            if (serverselectionlist$entry instanceof ServerSelectionList.NormalEntry &&
                    !FeaturedServers.servers.containsKey(((ServerSelectionList.NormalEntry) serverselectionlist$entry).getServerData().ip)) {
                this.editButton.active = true;
                this.deleteButton.active = true;
            }
        }
    }
}
