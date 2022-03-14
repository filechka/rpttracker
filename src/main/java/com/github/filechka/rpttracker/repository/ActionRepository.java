package com.github.filechka.rpttracker.repository;

import com.github.filechka.rpttracker.domain.Action;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Repository
public class ActionRepository implements CommonRepository<Action> {

    private static final String SQL_ACTION_INSERT = "insert into actions (id, name, description)" +
            "values (:id, :name, :description)";
    private static final String SQL_QUERY_FIND_ALL_ACTIONS = "select id, name, description from actions";
    private static final String SQL_QUERY_FIND_ACTIONS_BY_ID = SQL_QUERY_FIND_ALL_ACTIONS + " where id = :id";
    private static final String SQL_UPDATE_ACTIONS_BY_ID = "update actions set name = :name, description = :description where id = :id";
    private static final String SQL_DELETE_ACTIONS_BY_ID = "delete from actions where id = :id";
    private final NamedParameterJdbcTemplate jdbcTemplate;

    public ActionRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private RowMapper<Action> actionRowMapper = (ResultSet rs, int rowNum) -> {
        Action action = new Action();
        action.setId(rs.getString("id"));
        action.setName(rs.getString("name"));
        action.setDescription(rs.getString("description"));
        return action;
    };

    @Override
    public Action save(final Action domain) {
        Action result = findById(domain.getId());
        if (result != null) {
            result.setName(domain.getName());
            result.setDescription(domain.getDescription());
            return upsert(result, SQL_UPDATE_ACTIONS_BY_ID);
        }
        return upsert(domain, SQL_ACTION_INSERT);
    }

    private Action upsert(final Action domain, final String sql) {
        Map<String, String> namedParameters = new HashMap<>();
        namedParameters.put("id", domain.getId());
        namedParameters.put("name", domain.getName());
        namedParameters.put("description", domain.getDescription());
        this.jdbcTemplate.update(sql, namedParameters);
        return findById(domain.getId());
    }

    @Override
    public Iterable<Action> save(Collection<Action> domains) {
        domains.forEach(this::save);
        return findAll();

    }

    @Override
    public void delete(String id) {
        Map<String, String> namedParameters = Collections.singletonMap("id", id);
        this.jdbcTemplate.update(SQL_DELETE_ACTIONS_BY_ID, namedParameters);
    }

    @Override
    public Action findById(String id) {
        try {
            Map<String, String> namedParameters = Collections.singletonMap("id", id);
            return this.jdbcTemplate.queryForObject(SQL_QUERY_FIND_ACTIONS_BY_ID, namedParameters, actionRowMapper);
        } catch (EmptyResultDataAccessException ex) {
            return null;
        }
    }

    @Override
    public Iterable<Action> findAll() {
        return this.jdbcTemplate.query(SQL_QUERY_FIND_ALL_ACTIONS, actionRowMapper);
    }
}
