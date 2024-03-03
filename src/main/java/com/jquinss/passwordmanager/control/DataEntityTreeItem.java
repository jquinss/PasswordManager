package com.jquinss.passwordmanager.control;

import com.jquinss.passwordmanager.data.DataEntity;
import com.jquinss.passwordmanager.data.Folder;
import com.jquinss.passwordmanager.data.PasswordEntity;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.TreeItem;

public class DataEntityTreeItem extends TreeItem<DataEntity> {
    private static final String FOLDER_IMG = "/com/jquinss/passwordmanager/images/folder.png";
    private static final String PASSWORD_ENTITY_IMG = "/com/jquinss/passwordmanager/images/password_entity.png";
    private ContextMenu contextMenu;

    public DataEntityTreeItem(DataEntity dataEntity) {
        setValue(dataEntity);
    }

    public void setContextMenu(ContextMenu contextMenu) {
        this.contextMenu = contextMenu;
    }

    public ContextMenu getContextMenu() {
        return contextMenu;
    }

    public String getImgURL() {
        String imgURL = null;
        DataEntity dataEntity = getValue();

        if (dataEntity instanceof Folder) {
            imgURL = FOLDER_IMG;
        }
        else if (dataEntity instanceof PasswordEntity) {
            imgURL = PASSWORD_ENTITY_IMG;
        }

        return imgURL;
    }
}
