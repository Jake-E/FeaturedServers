package io.alwa.featuredservers.mixin;

import io.alwa.featuredservers.FeaturedList;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.minecraft.client.gui.screens.multiplayer.ServerSelectionList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(JoinMultiplayerScreen.class)
public abstract class MixinServerSelectionList {

    @Shadow protected ServerSelectionList serverSelectionList;
    @Shadow private Button selectButton, deleteButton, editButton;

    @SuppressWarnings("OverwriteAuthorRequired")
    @Overwrite
    protected void onSelectedChange() {
        this.selectButton.active = false;
        this.editButton.active = false;
        this.deleteButton.active = false;
        ServerSelectionList.Entry serverselectionlist$entry = this.serverSelectionList.getSelected();
        if (serverselectionlist$entry != null && !(serverselectionlist$entry instanceof ServerSelectionList.LANHeader)) {
            this.selectButton.active = true;
            if (serverselectionlist$entry instanceof ServerSelectionList.OnlineServerEntry) {
                if (FeaturedList.servers.containsKey(((ServerSelectionList.OnlineServerEntry) serverselectionlist$entry).getServerData().ip)) {
                    boolean active = FeaturedList.servers.get(((ServerSelectionList.OnlineServerEntry) serverselectionlist$entry).getServerData().ip).disableButtons;
                    this.editButton.active = !active;
                    this.deleteButton.active = !active;
                    return;
                }
                this.editButton.active = true;
                this.deleteButton.active = true;
            }
        }
    }
}
