package com.github.filechka.rpttracker.controller;

import com.github.filechka.rpttracker.domain.Action;
import com.github.filechka.rpttracker.domain.ActionHistory;
import com.github.filechka.rpttracker.repository.ActionHistoryRepository;
import com.github.filechka.rpttracker.repository.CommonRepository;
import com.github.filechka.rpttracker.validation.ActionValidationError;
import com.github.filechka.rpttracker.validation.ActionValidationErrorBuilder;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;

@RestController
@RequestMapping("/api")
public class ActionController {
    private CommonRepository<Action> repository;
    private ActionHistoryRepository actionHistoryRepository;

    @Autowired
    public ActionController(CommonRepository<Action> repository, ActionHistoryRepository actionHistoryRepository) {
        this.repository = repository;
        this.actionHistoryRepository = actionHistoryRepository;
    }

    /**
     * Get all actions
     * @return
     */
    @ApiOperation (value = "Get all actions", notes = "Get all actions")
    @GetMapping("/actions")
    public ResponseEntity<Iterable<Action>> getActions() {
        return ResponseEntity.ok(repository.findAll());
    }

    /**
     *  Get action by id
     * @param id
     * @return
     */
    @ApiOperation(value = "Get action data", notes = "Get action data by id")
    @ApiImplicitParam(name = "id", value = "Action ID", required = true, dataType = "String", paramType = "path")
    @GetMapping("/actions/{id}")
    public ResponseEntity<?> getActionById(@PathVariable String id) {
        Action result = repository.findById(id);
        if (result == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(result);
    }

    /**
     *  Get action history by id
     * @param id
     * @return
     */
    @ApiOperation(value = "Get action history data", notes = "Get action history data by action id")
    @ApiImplicitParam(name = "id", value = "Action ID", required = true, dataType = "String", paramType = "path")
    @GetMapping("/actions/{id}/history")
    public ResponseEntity<Iterable<ActionHistory>> getActionHistoryByActionId(@PathVariable String id) {
        return ResponseEntity.ok(actionHistoryRepository.findByActionId(id));
    }

    /**
     *  Add action
     * @param action
     * @return
     */
    @ApiOperation (value = "Create new action", notes = "Create new action")
    @ApiImplicitParam (name = "action", value = "action data: name, description (optional)", required = true, dataType = "Action")
    @PostMapping("/actions")
    public ResponseEntity<?> createAction(@Valid @RequestBody Action action, Errors errors) {
        if (errors.hasErrors()) {
            return ResponseEntity.badRequest().body(ActionValidationErrorBuilder.fromBindingErrors(errors));
        }

        Action result = repository.save(action);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(result.getId()).toUri();
        return ResponseEntity.created(location).body(result);
    }
    
    /**
     * Update action
     * @param action
     * @return
     */
    @ApiOperation (value = "Update action", notes = "Update action by id")
    @ApiImplicitParams({
            @ApiImplicitParam (name = "action", value = "action entity", required = true, dataType = "Action"),
            @ApiImplicitParam (name = "id", value = "Action ID", required = true, dataType = "String", paramType = "path")
    })
    @PutMapping("/actions/{id}")
    public ResponseEntity<?> updateAction(@Valid @RequestBody Action action, @PathVariable String id, Errors errors) {
        if (errors.hasErrors()) {
            return ResponseEntity.badRequest().body(ActionValidationErrorBuilder.fromBindingErrors(errors));
        }
        if (repository.findById(id) == null) return ResponseEntity.notFound().build();
        Action result = repository.save(action);
        return ResponseEntity.ok(result);
    }

    /**
     * Delete action by id
     * @param id
     * @return
     */
    @ApiOperation (value = "Delete action", notes = "Delete action by id")
    @ApiImplicitParam (name = "id", value = "Action ID", required = true, dataType = "String", paramType = "path")
    @DeleteMapping("/actions/{id}")
    public ResponseEntity<Action> deleteAction(@PathVariable String id) {
        if (repository.findById(id) == null) return ResponseEntity.notFound().build();
        repository.delete(id);
        return  ResponseEntity.noContent().build();
    }

    @ExceptionHandler
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ActionValidationError handleException(Exception exception) {
        return new ActionValidationError(exception.getMessage());
    }
}
