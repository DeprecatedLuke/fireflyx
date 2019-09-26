package api;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
public class ViolationX {
    public UUID player;
    public String type;
    public int vl;
    public long time;
    public String extra;
}
