/**
 * Copyright © 2016-2020 The Thingsboard Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.thingsboard.server.common.data;

import org.thingsboard.server.common.data.id.CustomerId;
import org.thingsboard.server.common.data.id.EntityId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.id.UUIDBased;

public abstract class TbEntity<I extends UUIDBased> extends BaseData<I> implements HasName, HasTenantId, HasCustomerId {

    public TbEntity() {
        super();
    }

    public TbEntity(I id) {
        super(id);
    }

    public TbEntity(TbEntity<I> data) {
        super(data);
    }

    public String getLabel() {
        return this.getName();
    }

    @Override
    public CustomerId getCustomerId() {
        return new CustomerId(EntityId.NULL_UUID);
    }

    @Override
    public TenantId getTenantId() {
        return new TenantId(EntityId.NULL_UUID);
    }
}
