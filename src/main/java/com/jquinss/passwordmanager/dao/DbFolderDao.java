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
            while (rs.next()) {
                folders.add(createFolder(rs));
            }
        }

        return folders;
    }

    @Override
    public Optional<RootFolder> getRootByUserId(int id) throws SQLException {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = buildGetRootFolderByUserIdPreparedStatement(conn, id);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return Optional.of(createRootFolder(rs));
            }
        }

        return Optional.empty();
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
    public void addRoot(RootFolder folder) throws SQLException {
        add(folder);

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = buildAddRootFolderPreparedStatement(conn, folder);) {
            ps.executeUpdate();
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

    @Override
    public void delete(List<Folder> folders) throws SQLException {
        try (Connection conn = dataSource.getConnection();
            PreparedStatement ps = buildDeleteFoldersPreparedStatement(conn, folders)) {
            conn.setAutoCommit(false);
            ps.executeBatch();
            conn.setAutoCommit(true);
        }
    }

    private PreparedStatement buildGetFolderByIdPreparedStatement(Connection conn, int id) throws SQLException {
        String statement = "SELECT * FROM folder WHERE folder_id = ?";
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

    private PreparedStatement buildAddRootFolderPreparedStatement(Connection conn, RootFolder folder) throws SQLException {
        String statement = "INSERT INTO root_folder (root_folder_id, user_id) VALUES (?, ?)";
        PreparedStatement ps = conn.prepareStatement(statement);
        ps.setInt(1, folder.getId());
        ps.setInt(2, folder.getUserId());

        return ps;
    }

    private PreparedStatement buildGetAllFoldersByParentIdPreparedStatement(Connection conn, int parentFolderId) throws SQLException {
        String statement = "SELECT * FROM folder WHERE parent_folder_id = ?";
        PreparedStatement ps = conn.prepareStatement(statement);
        ps.setInt(1, parentFolderId);

        return ps;
    }

    private PreparedStatement buildGetRootFolderByUserIdPreparedStatement(Connection conn, int userId) throws SQLException {
        String statement = """
                SELECT f.folder_id, f.parent_folder_id, f.folder_name, f.description, rf.user_id FROM root_folder rf 
                INNER JOIN folder f ON rf.root_folder_id = f.folder_id WHERE rf.user_id = ?""";
        PreparedStatement ps = conn.prepareStatement(statement);
        ps.setInt(1, userId);

        return ps;
    }

    private Folder createFolder(ResultSet resultSet) throws SQLException {
        Folder folder = new Folder(resultSet.getInt(1), resultSet.getString(3));
        folder.setParentFolderId(resultSet.getInt(2));
        folder.setDescription(resultSet.getString(4));

        return folder;
    }

    private RootFolder createRootFolder(ResultSet resultSet) throws SQLException {
        RootFolder folder = new RootFolder(resultSet.getInt(1), resultSet.getString(3), resultSet.getInt(5));
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

    private PreparedStatement buildDeleteFoldersPreparedStatement(Connection conn, List<Folder> folders) throws SQLException {
        PreparedStatement ps = conn.prepareStatement("DELETE FROM folder WHERE folder_id = ?");
        for (Folder folder : folders) {
            ps.setInt(1, folder.getId());
            ps.addBatch();
        }
        return ps;
    }
}
