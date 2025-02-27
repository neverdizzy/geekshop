/*
 * Copyright (c) 2020 GeekShop.
 * All rights reserved.
 */

package io.geekshop.resolver;

import io.geekshop.common.Constant;
import io.geekshop.types.administrator.Administrator;
import io.geekshop.types.history.HistoryEntry;
import graphql.kickstart.execution.context.GraphQLContext;
import graphql.kickstart.tools.GraphQLResolver;
import graphql.schema.DataFetchingEnvironment;
import org.dataloader.DataLoader;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

/**
 * Created on Nov, 2020 by @author bobo
 */
@Component
public class HistoryResolver implements GraphQLResolver<Administrator> {
    public CompletableFuture<Administrator> getAdministrator(HistoryEntry historyEntry, DataFetchingEnvironment dfe) {
        final DataLoader<Long, Administrator> dataLoader = ((GraphQLContext) dfe.getContext())
                .getDataLoaderRegistry()
                .getDataLoader(Constant.DATA_LOADER_NAME_HISTORY_ENTRY_ADMINISTRATOR);

        return dataLoader.load(historyEntry.getAdministratorId());
    }
}
