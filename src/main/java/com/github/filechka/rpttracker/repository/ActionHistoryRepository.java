package com.github.filechka.rpttracker.repository;

import com.github.filechka.rpttracker.domain.ActionHistory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.util.*;

@Repository
public class ActionHistoryRepository implements CommonRepository<ActionHistory> {

    private static final String SQL_ACTION_HISTORY_INSERT = "insert into actions_history (id, action_id, created)" +
            "values (:id, :action_id, :created)";
    private static final String SQL_QUERY_FIND_ALL_ACTION_HISTORY = "select id, action_id, created from actions_history";
    private static final String SQL_QUERY_FIND_ACTION_HISTORY_BY_ID = SQL_QUERY_FIND_ALL_ACTION_HISTORY + " where id = :id";
    private static final String SQL_QUERY_FIND_ACTION_HISTORY_BY_ACTION_ID = SQL_QUERY_FIND_ALL_ACTION_HISTORY + " where action_id = :action_id";
    private static final String SQL_UPDATE_ACTION_HISTORY_BY_ID = "update actions_history set action_id = :action_id, created = :created where id = :id";
    private static final String SQL_DELETE_ACTION_HISTORY_BY_ID = "delete from actions_history where id = :id";
    private final NamedParameterJdbcTemplate jdbcTemplate;

    public ActionHistoryRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private RowMapper<ActionHistory> actionHistoryRowMapper = (ResultSet rs, int rowNum) -> {
        ActionHistory actionHistory = new ActionHistory();
        actionHistory.setId(rs.getString("id"));
        actionHistory.setActionId(rs.getString("action_id"));
        actionHistory.setCreated(rs.getTimestamp("created").toLocalDateTime());
        return actionHistory;
    };

    @Override
    public ActionHistory save(final ActionHistory domain) {
        ActionHistory result = findById(domain.getId());
        if (result != null) {
            result.setId(domain.getId());
            result.setActionId(domain.getActionId());
            result.setCreated(domain.getCreated());
            return upsert(result, SQL_UPDATE_ACTION_HISTORY_BY_ID);
        }
        return upsert(domain, SQL_ACTION_HISTORY_INSERT);
    }

    private ActionHistory upsert(final ActionHistory domain, final String sql) {
        Map<String, String> namedParameters = new HashMap<>();
        namedParameters.put("id", domain.getId());
        namedParameters.put("action_id", domain.getActionId());
        namedParameters.put("created", domain.getCreated().toString());
        this.jdbcTemplate.update(sql, namedParameters);
        return findById(domain.getId());
    }

    @Override
    public Iterable<ActionHistory> save(Collection<ActionHistory> domains) {
        domains.forEach(this::save);
        return findAll();

    }

    @Override
    public void delete(String id) {
        Map<String, String> namedParameters = Collections.singletonMap("id", id);
        this.jdbcTemplate.update(SQL_DELETE_ACTION_HISTORY_BY_ID, namedParameters);
    }

    @Override
    public ActionHistory findById(String id) {
        try {
            Map<String, String> namedParameters = Collections.singletonMap("id", id);
            return this.jdbcTemplate.queryForObject(SQL_QUERY_FIND_ACTION_HISTORY_BY_ID, namedParameters, actionHistoryRowMapper);
        } catch (EmptyResultDataAccessException ex) {
            return null;
        }
    }

    public Iterable<ActionHistory> findByActionId(String action_id) {
        try {
            Map<String, String> namedParameters = Collections.singletonMap("action_id", action_id);
            return this.jdbcTemplate.query(SQL_QUERY_FIND_ACTION_HISTORY_BY_ACTION_ID, namedParameters, actionHistoryRowMapper);
        } catch (EmptyResultDataAccessException ex) {
            return new ArrayList<>();
        }
    }

    @Override
    public Iterable<ActionHistory> findAll() {
        return this.jdbcTemplate.query(SQL_QUERY_FIND_ALL_ACTION_HISTORY, actionHistoryRowMapper);
    }
}
