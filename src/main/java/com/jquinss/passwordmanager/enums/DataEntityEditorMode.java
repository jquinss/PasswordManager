package com.jquinss.passwordmanager.enums;

import com.jquinss.passwordmanager.data.DataEntity;

import java.util.Optional;

public enum DataEntityEditorMode {
    CREATE, EDIT, HIDE, VIEW;

    private DataEntity dataEntity;

    public void setDataEntity(DataEntity dataEntity) {
        this.dataEntity = dataEntity;
    }
    public Optional<DataEntity> getDataEntity() {
        return Optional.ofNullable(dataEntity);
    }
}
