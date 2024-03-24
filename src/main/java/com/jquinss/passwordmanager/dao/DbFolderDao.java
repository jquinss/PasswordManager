package com.jquinss.passwordmanager.dao;

import com.jquinss.passwordmanager.data.Folder;
import com.jquinss.passwordmanager.data.RootFolder;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DbFolderDao implements FolderDao {
    private final DataSource dataSource;

    public DbFolderDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Optional<Folder> getById(int id) throws SQLException {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = buildGetFolderByIdPreparedStatement(conn, id);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return Optional.of(createFolder(rs));
            }
        }

        return Optional.empty();
    }

    @Override
    public List<Folder> getAllByParentId(int id) throws SQLException {
        List<Folder> folders = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = buildGetAllFoldersByParentIdPreparedStatement(conn, id);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                folders.add(createFolder(rs));
            }
        }

        return folders;
    }

    @Override
    public void add(Folder folder) throws SQLException {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = buildAddFolderPreparedStatement(conn, folder);) {

            conn.setAutoCommit(false);
            ps.executeUpdate();

            Statement statement = conn.createStatement();

            try (ResultSet resultSet = statement.executeQuery("SELECT last_insert_rowid()")) {
                if (resultSet.next()) {
                    folder.setId(resultSet.getInt(1));
                }
                conn.commit();
            }
        }
    }

    @Override
    public void update(Folder folder) throws SQLException {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = buildUpdateFolderPreparedStatement(conn, folder)) {
            ps.executeUpdate();
        }
    }

    @Override
    public void delete(Folder folder) throws SQLException {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = buildDeleteFolderPreparedStatement(conn, folder.getId())) {
            ps.executeUpdate();
        }
    }

    private PreparedStatement buildGetFolderByIdPreparedStatement(Connection conn, int id) throws SQLException {
        String statement = """
                SELECT * FROM folder WHERE folder_id = ?""";
        PreparedStatement ps = conn.prepareStatement(statement);
        ps.setInt(1, id);

        return ps;
    }

    private PreparedStatement buildAddFolderPreparedStatement(Connection conn, Folder folder) throws SQLException {
        String statement = "INSERT INTO folder (parent_folder_id, folder_name, description) VALUES (?, ?, ?)";
        PreparedStatement ps = conn.prepareStatement(statement);
        ps.setInt(1, folder.getParentFolderId());
        ps.setString(2, folder.getName());
        ps.setString(3, folder.getDescription());

        return ps;
    }

    private PreparedStatement buildGetAllFoldersByParentIdPreparedStatement(Connection conn, int parentFolderId) throws SQLException {
        String statement = "SELECT * FROM folder WHERE parent_folder_id = ?";
        PreparedStatement ps = conn.prepareStatement(statement);
        ps.setInt(1, parentFolderId);

        return ps;
    }

    private Folder createFolder(ResultSet resultSet) throws SQLException {
        Folder folder = null;
        int folderId = resultSet.getInt(1);

        if (folderId == 0) {
            folder = new RootFolder(folderId, resultSet.getString(3));
        }
        else {
            folder = new Folder(folderId, resultSet.getString(3));
        }

        folder.setParentFolderId(resultSet.getInt(2));
        folder.setDescription(resultSet.getString(4));

        return folder;
    }

    private PreparedStatement buildUpdateFolderPreparedStatement(Connection conn, Folder folder) throws SQLException {
        String statement = "UPDATE folder SET parent_folder_id = ?, SET folder_name = ?,  SET description = ?";
        PreparedStatement ps = conn.prepareStatement(statement);
        ps.setInt(1, folder.getParentFolderId());
        ps.setString(2, folder.getName());
        ps.setString(3, folder.getDescription());

        return ps;
    }

    private PreparedStatement buildDeleteFolderPreparedStatement(Connection conn, int id) throws SQLException {
        PreparedStatement ps = conn.prepareStatement("DELETE FROM folder WHERE folder_id = ?");
        ps.setInt(1, id);

        return ps;
    }
}
