package com.github.filechka.rpttracker.controller;

import com.github.filechka.rpttracker.domain.ActionHistory;
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
public class ActionHistoryController {
    private CommonRepository<ActionHistory> repository;

    @Autowired
    public ActionHistoryController(CommonRepository<ActionHistory> repository) {
        this.repository = repository;
    }

    /**
     * Get all action history
     * @return
     */
    @ApiOperation (value = "Get all action history", notes = "Get all action history")
    @GetMapping("/action-history")
    public ResponseEntity<Iterable<ActionHistory>> getActionHistory() {
        return ResponseEntity.ok(repository.findAll());
    }

    /**
     *  Get action history by id
     * @param id
     * @return
     */
    @ApiOperation(value = "Get action history data", notes = "Get action history data by id")
    @ApiImplicitParam(name = "id", value = "Action history ID", required = true, dataType = "String", paramType = "path")
    @GetMapping("/action-history/{id}")
    public ResponseEntity<?> getActionHistoryById(@PathVariable String id) {
        ActionHistory result = repository.findById(id);
        if (result == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(result);
    }

    /**
     *  Add action history
     * @param actionHistory
     * @return
     */
    @ApiOperation (value = "Create new action history", notes = "Create new action history")
    @ApiImplicitParam (name = "actionHistory", value = "action history data: action_id", required = true, dataType = "ActionHistory")
    @PostMapping("/action-history")
    public ResponseEntity<?> createActionHistory(@Valid @RequestBody ActionHistory actionHistory, Errors errors) {
        if (errors.hasErrors()) {
            return ResponseEntity.badRequest().body(ActionValidationErrorBuilder.fromBindingErrors(errors));
        }

        ActionHistory result = repository.save(actionHistory);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(result.getId()).toUri();
        return ResponseEntity.created(location).body(result);
    }

    /**
     * Update action history
     * @param actionHistory
     * @return
     */
    @ApiOperation (value = "Update action", notes = "Update action by id")
    @ApiImplicitParams({
            @ApiImplicitParam (name = "actionHistory", value = "action history entity", required = true, dataType = "ActionHistory"),
            @ApiImplicitParam (name = "id", value = "Action history ID", required = true, dataType = "String", paramType = "path")
    })
    @PutMapping("/action-history/{id}")
    public ResponseEntity<?> updateActionHistory(@Valid @RequestBody ActionHistory actionHistory, @PathVariable String id, Errors errors) {
        if (errors.hasErrors()) {
            return ResponseEntity.badRequest().body(ActionValidationErrorBuilder.fromBindingErrors(errors));
        }
        if (repository.findById(id) == null) return ResponseEntity.notFound().build();
        ActionHistory result = repository.save(actionHistory);
        return ResponseEntity.ok(result);
    }

    /**
     * Delete action history by id
     * @param id
     * @return
     */
    @ApiOperation (value = "Delete action history", notes = "Delete action history by id")
    @ApiImplicitParam (name = "id", value = "Action history ID", required = true, dataType = "String", paramType = "path")
    @DeleteMapping("/action-history/{id}")
    public ResponseEntity<ActionHistory> deleteActionHistory(@PathVariable String id) {
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
