// Copyright © 2012-2021 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.
package io.vlingo.xoom.nativeexample;

import io.vlingo.xoom.nativeexample.ping.Ping;
import io.vlingo.xoom.nativeexample.ping.PingActor;
import io.vlingo.xoom.nativeexample.pong.Pong;
import io.vlingo.xoom.actors.World;
import io.vlingo.xoom.nativeexample.pong.PongActor;

public class Application {
    public static void main(String[] args) throws InterruptedException {
        World world = World.startWithDefaults("ping-pong-native");
        Ping ping = PingActor.instanceOn(world.stage());
        Pong pong = PongActor.instanceOn(world.stage());

        ping.ping(pong);
        Thread.sleep(1000);
        world.terminate();
    }
}
