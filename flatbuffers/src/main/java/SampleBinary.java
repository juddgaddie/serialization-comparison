/*
 * Copyright 2015 Google Inc. All rights reserved.
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

// Run this file with the `java_sample.sh` script.

import com.google.flatbuffers.FlatBufferBuilder;
import com.transficc.MyGame.Sample.*;

import java.nio.ByteBuffer;

import static org.assertj.core.api.Assertions.assertThat;

class SampleBinary {
    // Example how to use FlatBuffers to create and read binary buffers.
    public static void main(String[] args) {
        FlatBufferBuilder builder = new FlatBufferBuilder(0);

        // Create some weapons for our Monster ('Sword' and 'Axe').
        int weaponOneName = builder.createString("Sword");
        short weaponOneDamage = 3;
        int weaponTwoName = builder.createString("Axe");
        short weaponTwoDamage = 5;

        // Use the `createWeapon()` helper function to create the weapons, since we set every field.
        int[] weaps = new int[2];
        weaps[0] = Weapon.createWeapon(builder, weaponOneName, weaponOneDamage);
        weaps[1] = Weapon.createWeapon(builder, weaponTwoName, weaponTwoDamage);

        // Serialize the FlatBuffer data.
        int name = builder.createString("Orc");
        byte[] treasure = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
        int inv = Monster.createInventoryVector(builder, treasure);
        int weapons = Monster.createWeaponsVector(builder, weaps);
        int pos = Vec3.createVec3(builder, 1.0f, 2.0f, 3.0f);

        Monster.startMonster(builder);
        Monster.addPos(builder, pos);
        Monster.addName(builder, name);
        Monster.addColor(builder, Color.Red);
        Monster.addHp(builder, (short) 300);
        Monster.addInventory(builder, inv);
        Monster.addWeapons(builder, weapons);
        Monster.addEquippedType(builder, Equipment.Weapon);
        Monster.addEquipped(builder, weaps[1]);
        int orc = Monster.endMonster(builder);

        builder.finish(orc); // You could also call `Monster.finishMonsterBuffer(builder, orc);`.

        // We now have a FlatBuffer that can be stored on disk or sent over a network.

        // ...Code to store to disk or send over a network goes here...

        // Instead, we are going to access it right away, as if we just received it.

        ByteBuffer buf = builder.dataBuffer();

        // Get access to the root:
        Monster monster = Monster.getRootAsMonster(buf);

        // Note: We did not set the `mana` field explicitly, so we get back the default value.
        assertThat(monster.mana()).isEqualTo((short) 150);
        assertThat(monster.hp()).isEqualTo((short) 300);
        assertThat(monster.name()).isEqualTo("Orc");
        assertThat(monster.color()).isEqualTo(Color.Red);
        assertThat(monster.pos().x()).isEqualTo(1.0f);
        assertThat(monster.pos().y()).isEqualTo(2.0f);
        assertThat(monster.pos().z()).isEqualTo(3.0f);

        // Get and test the `inventory` FlatBuffer `vector`.
        for (int i = 0; i < monster.inventoryLength(); i++) {
            assertThat(monster.inventory(i)).isEqualTo((byte) i);
        }

        // Get and test the `weapons` FlatBuffer `vector` of `table`s.
        String[] expectedWeaponNames = {"Sword", "Axe"};
        int[] expectedWeaponDamages = {3, 5};
        for (int i = 0; i < monster.weaponsLength(); i++) {
            assertThat(monster.weapons(i).name()).isEqualTo(expectedWeaponNames[i]);
            assertThat(monster.weapons(i).damage()).isEqualTo((short)expectedWeaponDamages[i]);
        }

        // Get and test the `equipped` FlatBuffer `union`.
        assertThat(monster.equippedType()).isEqualTo(Equipment.Weapon);
        Weapon equipped = (Weapon) monster.equipped(new Weapon());
        assertThat(equipped.name()).isEqualTo("Axe");
        assertThat(equipped.damage()).isEqualTo((short)5);

        System.out.println("The FlatBuffer was successfully created and verified!");
    }
}
