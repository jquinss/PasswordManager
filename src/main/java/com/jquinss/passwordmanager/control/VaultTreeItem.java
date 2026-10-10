package com.jquinss.passwordmanager.control;

import com.jquinss.passwordmanager.data.VaultItem;
import com.jquinss.passwordmanager.data.Folder;
import com.jquinss.passwordmanager.data.PasswordItem;
import com.jquinss.passwordmanager.data.RootFolder;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.TreeItem;
import java.time.LocalDate;

public class VaultTreeItem extends TreeItem<VaultItem> {
    private static final String ROOT_FOLDER_IMG = "/com/jquinss/passwordmanager/images/root_folder.png";
    private static final String FOLDER_IMG = "/com/jquinss/passwordmanager/images/folder.png";
    private static final String PASSWORD_ITEM_IMG = "/com/jquinss/passwordmanager/images/password_item.png";
    private static final String EXPIRED_PASSWORD_ITEM_IMG = "/com/jquinss/passwordmanager/images/expired_password_item.png";
    private ContextMenu contextMenu;

    public VaultTreeItem(VaultItem vaultItem) {
        setValue(vaultItem);
    }

    public void setContextMenu(ContextMenu contextMenu) {
        this.contextMenu = contextMenu;
    }

    public ContextMenu getContextMenu() {
        return contextMenu;
    }

    public String getImgURL() {
        String imgURL = null;
        VaultItem vaultItem = getValue();

        if (vaultItem instanceof RootFolder) {
            imgURL = ROOT_FOLDER_IMG;
        }
        else if (vaultItem instanceof Folder) {
            imgURL = FOLDER_IMG;
        }
        else if (vaultItem instanceof PasswordItem passwordItem) {
            if (passwordItem.isPasswordExpires() && !passwordItem.getExpirationDate().isAfter(LocalDate.now())) {
                imgURL = EXPIRED_PASSWORD_ITEM_IMG;
            }
            else {
                imgURL = PASSWORD_ITEM_IMG;
            }
        }

        return imgURL;
    }
}
