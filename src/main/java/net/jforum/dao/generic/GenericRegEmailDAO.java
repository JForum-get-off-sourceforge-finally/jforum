package net.jforum.dao.generic;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.SortedMap;
import java.util.TreeMap;

import net.jforum.JForumExecutionContext;
import net.jforum.dao.DataAccessDriver;
import net.jforum.dao.GroupDAO;
import net.jforum.dao.RegEmailDAO;
import net.jforum.entities.Group;
import net.jforum.exceptions.DatabaseException;
import net.jforum.repository.RegEmailRepository;
import net.jforum.util.DbUtils;
import net.jforum.util.preferences.SystemGlobals;

public class GenericRegEmailDAO implements RegEmailDAO {

	@Override public SortedMap<String, Group> selectAll() {
		SortedMap<String, Group> result = new TreeMap<>();
		PreparedStatement p = null;
		ResultSet rs = null;
		try {
			GroupDAO groupDao = DataAccessDriver.getInstance().newGroupDAO();

			p = preparedStatementFromSqlKey("RegEmail.selectAll");

			rs = p.executeQuery();

			while (rs.next()) {
				Group group = groupDao.selectById(rs.getInt("group_id"));
				result.put(rs.getString("email"), group);
			}

			return result;
		} catch (SQLException e) {
			throw new DatabaseException(e);
		} finally {
			DbUtils.close(rs, p);
		}
	}

    @Override public void addRegEmail (String email, Group group) {
        PreparedStatement p = null;
        try {
            p = preparedStatementFromSqlKey("RegEmail.create");
            p.setString(1, email.trim());
            p.setInt(2, group.getId());

            int recordsAdded = p.executeUpdate();

            if (recordsAdded == 1) {
				RegEmailRepository.load();
            }
        } catch (SQLException e) {
            throw new DatabaseException(e);
        } finally {
            DbUtils.close(p);
        }
    }

    @Override public void deleteRegEmail (String email) {
        PreparedStatement p = null;
        try {
            p = preparedStatementFromSqlKey("RegEmail.delete");
            p.setString(1, email);

            int recordsDeleted = p.executeUpdate();

            if (recordsDeleted == 1) {
				RegEmailRepository.load();
            }
        } catch (SQLException e) {
            throw new DatabaseException(e);
        } finally {
            DbUtils.close(p);
        }
    }

    private PreparedStatement preparedStatementFromSqlKey (String sqlKey) throws SQLException {
        String sql = SystemGlobals.getSql(sqlKey);
        return JForumExecutionContext.getConnection().prepareStatement(sql);
    }
}
