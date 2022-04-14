// Copyright © 2012-2022 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.some.vlingo.app.infra.persistence;

import io.some.vlingo.app.model.AccountState;
import io.vlingo.xoom.actors.Definition;
import io.vlingo.xoom.actors.Protocols;
import io.vlingo.xoom.actors.Stage;
import io.vlingo.xoom.lattice.model.stateful.StatefulTypeRegistry;
import io.vlingo.xoom.lattice.model.stateful.StatefulTypeRegistry.Info;
import io.vlingo.xoom.symbio.EntryAdapterProvider;
import io.vlingo.xoom.symbio.StateAdapterProvider;
import io.vlingo.xoom.symbio.store.dispatch.Dispatcher;
import io.vlingo.xoom.symbio.store.dispatch.DispatcherControl;
import io.vlingo.xoom.symbio.store.state.StateStore;
import io.vlingo.xoom.symbio.store.state.inmemory.InMemoryStateStoreActor;

import java.util.Arrays;

public class CommandModelStoreProvider {
    private static CommandModelStoreProvider instance;

    public final DispatcherControl dispatcherControl;
    public final StateStore store;

    public static CommandModelStoreProvider instance() {
        return instance;
    }

    @SuppressWarnings("rawtypes")
    public static CommandModelStoreProvider using(Stage stage, StatefulTypeRegistry registry, Dispatcher dispatcher) {
        if (instance != null) return instance;

        Protocols storeProtocols =
                stage.actorFor(
                        new Class<?>[]{StateStore.class, DispatcherControl.class},
                        Definition.has(InMemoryStateStoreActor.class, Definition.parameters(Arrays.asList(dispatcher))));

        Protocols.Two<StateStore, DispatcherControl> storeWithControl = Protocols.two(storeProtocols);

        instance = new CommandModelStoreProvider(stage, registry, storeWithControl._1, storeWithControl._2);

        return instance;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public CommandModelStoreProvider(Stage stage, StatefulTypeRegistry registry, StateStore store, DispatcherControl dispatcherControl) {
        this.store = store;
        this.dispatcherControl = dispatcherControl;

        StateAdapterProvider stateAdapterProvider = new StateAdapterProvider(stage.world());
        stateAdapterProvider.registerAdapter(AccountState.class, new AccountStateAdapter());
        new EntryAdapterProvider(stage.world()); // future

        registry.register(new Info(store, AccountState.class, AccountState.class.getSimpleName()));
    }
}
