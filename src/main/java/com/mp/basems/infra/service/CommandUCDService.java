package com.mp.basems.infra.service;

import com.mp.basems.infra.command.CreateCommand;
import com.mp.basems.infra.command.DeleteCommand;
import com.mp.basems.infra.command.UpdateCommand;
import com.mp.basems.infra.dto.CreateRequestObject;
import com.mp.basems.infra.dto.ResponseObject;
import com.mp.basems.infra.dto.UpdateRequestObject;
import com.mp.basems.infra.event.PublisherEvent;
import com.mp.basems.infra.model.MPEntity;

import reactor.core.publisher.Mono;

public class CommandUCDService<M extends MPEntity<Q,UR,?>, Q extends CreateRequestObject, UR extends UpdateRequestObject, S extends ResponseObject<M>, CE extends PublisherEvent<S>, UE extends PublisherEvent<S>, DE extends PublisherEvent<S>> {

    private CreateCommand<M, Q, S, CE> createCommand;
    private UpdateCommand<M, UR, S, UE> updateCommand;
    private DeleteCommand<M, S, DE> deleteCommand;
    
    public CommandUCDService(CreateCommand<M, Q, S, CE> createCommand, UpdateCommand<M, UR, S, UE> updateCommand, DeleteCommand<M, S, DE> deleteCommand) {
        this.createCommand = createCommand;
        this.updateCommand = updateCommand;
        this.deleteCommand = deleteCommand;
    }

    public Mono<M> create(Q request) {
        return this.createCommand.execute(request);
    }
    
    public Mono<M> update(UR request) {
        return this.updateCommand.execute(request);
    }
    
    public Mono<?> delete(String id) {
        return this.deleteCommand.execute(id);
    }
    
}