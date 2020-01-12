// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.
package io.vlingo.nativeexample.ping;

import io.vlingo.actors.Stage;
import io.vlingo.nativeexample.pong.Pong;
import io.vlingo.actors.Actor;

public class PingActor extends Actor implements Ping {
    private int times;
    private Ping self;

    public PingActor() {
        this.times = 0;
        this.self = selfAs(Ping.class);
    }

    public static Ping instanceOn(final Stage stage) {
        return stage.actorFor(Ping.class, PingActor.class, PingActor::new);
    }

    public void ping(Pong pong) {
        System.out.println("Ping");
        if (this.times > 10) {
            this.stop();
        } else {
            this.times++;
            pong.pong(self);
        }
    }
}
