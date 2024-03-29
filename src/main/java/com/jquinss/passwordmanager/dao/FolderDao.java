package com.jquinss.passwordmanager.dao;

import com.jquinss.passwordmanager.data.Folder;
import com.jquinss.passwordmanager.data.RootFolder;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface FolderDao {
    Optional<Folder> getById(int id) throws SQLException;

    List<Folder> getAllByParentId(int id) throws SQLException;

    Optional<RootFolder> getRootByUserId(int id) throws SQLException;

    void add(Folder folder) throws SQLException;

    void addRoot(RootFolder folder) throws SQLException;

    void update(Folder folder) throws SQLException;

    void delete(Folder folder) throws SQLException;

    void delete(List<Folder> folders) throws SQLException;
}
