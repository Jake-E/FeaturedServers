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
    @Shadow private Button selectButton;
    @Shadow private Button deleteButton;
    @Shadow private Button editButton;

    @SuppressWarnings("OverwriteAuthorRequired")
    @Overwrite
    protected void onSelectedChange() {
        this.selectButton.active = false;
        this.editButton.active = false;
        this.deleteButton.active = false;
        ServerSelectionList.Entry serverSelectionEntry = this.serverSelectionList.getSelected();
        if (serverSelectionEntry != null && !(serverSelectionEntry instanceof ServerSelectionList.LANHeader)) {
            this.selectButton.active = true;
            if (serverSelectionEntry instanceof ServerSelectionList.OnlineServerEntry entry) {
                if (FeaturedList.servers.containsKey(entry.getServerData().ip)) {
                    boolean active = FeaturedList.servers.get(entry.getServerData().ip).disableButtons;
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
