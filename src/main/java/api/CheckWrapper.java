/*
 * Copyright (c) 2018 NGXDEV.COM. Licensed under MIT.
 */

package api;

import com.ngxdev.anticheat.api.check.Check;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(fluent = true)
@NoArgsConstructor
@AllArgsConstructor
public class CheckWrapper {
   public String id;

   public boolean alert;
   public boolean cancel;
   public boolean ban;

   public int alertOffset;
   public int cancelOffset;
   public int banOffset;
   public int expirationOffset;

    public CheckWrapper(Class<? extends Check> check) {
        CheckType type = check.getAnnotation(CheckType.class);

        if (type == null) {
            throw new RuntimeException("Missing CheckType for " + check.getSimpleName());
        }
        this.id = type.id();
        this.alert = type.alert();
        this.cancel = type.cancel();
        this.ban = type.ban();
        this.banOffset = 1;
        this.cancelOffset = 0;
        this.alertOffset = 0;
        this.expirationOffset = 0;
        if (this.alert) this.alert = type.state() != CheckType.State.EXPERIMENTAL;
    }
}
