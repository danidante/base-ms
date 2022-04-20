package com.mp.basems.infra.model;

import java.time.LocalDateTime;
import java.util.UUID;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.annotation.Id;

import com.mp.basems.infra.dto.CreateRequestObject;
import com.mp.basems.infra.dto.UpdateRequestObject;
import com.mp.basems.infra.resource.QueryObject;

import lombok.Getter;
import lombok.Setter;

@Getter
public abstract class MPEntity<CR extends CreateRequestObject, UR extends UpdateRequestObject, QO extends QueryObject<?>> {
    
    @Id
    @NotEmpty
    @NotNull
    protected String id;
    protected LocalDateTime creationDate;
    protected LocalDateTime updatedAt;
    protected String createdBy;
    @Setter
    protected String marketplaceId; // owner id ? // TODO CHANGE
    
    public MPEntity(String marketplaceId) {
        this.marketplaceId = marketplaceId;
    }
    
    public MPEntity(String marketplaceId, CR request) {
        if(StringUtils.isEmpty(marketplaceId)) {
            throw new RuntimeException("Missing marketplace ID");
        }
        this.marketplaceId = marketplaceId;
        LocalDateTime now = LocalDateTime.now();
        this.id = this.id == null ? UUID.randomUUID().toString() : this.id;
        this.creationDate = now;
        this.updatedAt = now;
        this.createModelFromRequest(request);
    }
    
    @SuppressWarnings("unchecked")
    public MPEntity(String marketplaceId, QueryObject<QO> query) {
        this.marketplaceId = marketplaceId;
        this.setModelFromQuery((QO) query);
    }
    
    public MPEntity<CR, UR, QO> updateModel(UR request) {
        this.updatedAt = LocalDateTime.now();
        this.updateModelFromRequestDetail(request);
        return this;
    }
    
    public abstract void updateModelFromRequestDetail(UR request);
    protected abstract MPEntity<CR, UR, QO> createModelFromRequest(CR request);
    public abstract MPEntity<CR, UR, QO> setModelFromQuery(QO queryObject);

}
