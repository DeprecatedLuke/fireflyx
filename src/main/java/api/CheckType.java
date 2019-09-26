/*
 * Copyright (c) 2018 NGXDEV.COM. Licensed under MIT.
 */

package api;

import com.ngxdev.anticheat.utils.DynamicInit;
import lombok.Getter;
import org.atteo.classindex.IndexAnnotated;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Set;

@IndexAnnotated
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface CheckType {
    String id();

    String name();

    Type type();

    int timeout() default 20 * 5;

    int maxVl() default 1;

    State state() default State.RELEASE;

    boolean alert() default true;

    boolean cancel() default false;

    boolean ban() default false;

    boolean support19() default true;

    enum Type {
        COMBAT(Material.DIAMOND_SWORD),
        AUTOCLICKER(Material.STONE_BUTTON),
        KILLAURA(Material.NETHER_STAR),
        MOVEMENT(Material.DIAMOND_BOOTS),
        CONNECTION(Material.DIODE),
        WORLD(Material.STONE),
        BADPACKET(Material.REDSTONE_COMPARATOR),
        EXPLOIT(Material.CHEST),
        INVENTORY(Material.WORKBENCH),
        GAMEPLAY(Material.FISHING_ROD),
        DEBUG(Material.WEB);

        public ItemStack icon;


        Type(Material icon) {
            this.icon = new ItemStack(icon);
        }

        Type(Material icon, int data) {
            this.icon = new ItemStack(icon, 1, (short) data);
        }
    }

    @Getter
    enum State {
        EXPERIMENTAL("*"),
        BETA("^"),
        RELEASE("");

        private String tag;

        State(String tag) {
            this.tag = tag;
        }
    }

    class Dynamic {
        public static Set<Class<?>> get() throws Throwable {
           return DynamicInit.getClasses("com.ngxdev", CheckType.class);
        }
    }
}
