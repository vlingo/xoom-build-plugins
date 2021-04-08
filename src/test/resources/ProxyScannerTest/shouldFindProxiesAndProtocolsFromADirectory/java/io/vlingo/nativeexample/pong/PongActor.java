// Copyright © 2012-2021 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.
package io.vlingo.xoom.nativeexample.pong;

import io.vlingo.xoom.actors.Stage;
import io.vlingo.xoom.nativeexample.ping.Ping;
import io.vlingo.xoom.actors.Actor;

public class PongActor extends Actor implements Pong {
    private int times;
    private Pong self;

    public PongActor() {
        this.times = 0;
        this.self = selfAs(Pong.class);
    }

    public static Pong instanceOn(final Stage stage) {
        return stage.actorFor(Pong.class, PongActor.class, PongActor::new);
    }

    public void pong(final Ping ping) {
        System.out.println("Pong");
        if (this.times > 10) {
            this.stop();
        } else {
            this.times++;
            ping.ping(self);
        }
    }
}
