package com.ngxdev.anticheat.api.ui.book;

import com.ngxdev.anticheat.utils.Reflective;
import lombok.SneakyThrows;
import org.bukkit.ChatColor;

import java.util.List;


@SuppressWarnings({"unchecked", "ConstantConditions"})
public class NMSChatBuilder {

    public static Class<?> chatInterface = BookHandler.c("net.minecraft.server."+ BookHandler.VERSION+".IChatBaseComponent");
    public static Class<?> chatText = BookHandler.c("net.minecraft.server."+ BookHandler.VERSION+".ChatComponentText");
    public static Class<?> chatMod = BookHandler.c("net.minecraft.server."+ BookHandler.VERSION+".ChatModifier");
    public static Class<?> chatHovor = BookHandler.c("net.minecraft.server."+ BookHandler.VERSION+".ChatHoverable");
    public static Class<?> chatClick = BookHandler.c("net.minecraft.server."+ BookHandler.VERSION+".ChatClickable");
    public static Class<Enum> chatHovorAction = BookHandler.c("net.minecraft.server."+ BookHandler.VERSION+".ChatHoverable$EnumHoverAction");
    public static Class<Enum> chatClickAction = BookHandler.c("net.minecraft.server."+ BookHandler.VERSION+".ChatClickable$EnumClickAction");
    public static Class<Enum> chatColor = BookHandler.c("net.minecraft.server."+ BookHandler.VERSION+".EnumChatFormat");

    private Object root;
    private Object curr;

    private List components;

    @SneakyThrows
    public NMSChatBuilder(String text) {
        this.root = chatText.getConstructor(String.class).newInstance("");
        this.curr = chatText.getConstructor(String.class).newInstance(text);
        components = Reflective.get(root,"a");
        components.add(this.curr);
    }

    public NMSChatBuilder color(ChatColor color) {
        Object o = getChatModifier();
        Reflective.call(o,"setColor",getColor(color));
        return this;
    }

    public NMSChatBuilder bold(boolean value) {
        Object o = getChatModifier();
        Reflective.call(o,"setBold",value);
        return this;
    }

    public NMSChatBuilder italic(boolean value) {
        Object o = getChatModifier();
        Reflective.call(o,"setItalic",value);
        return this;
    }

    public NMSChatBuilder strikethrough(boolean value) {
        Object o = getChatModifier();
        Reflective.call(o,"setStrikethrough",value);
        return this;
    }

    public NMSChatBuilder underline(boolean value) {
        Object o = getChatModifier();
        Reflective.call(o,"setUnderline",value);
        return this;
    }

    public NMSChatBuilder obfuscate(boolean value) {
        Object o = getChatModifier();
        Reflective.call(o,"setRandom",value);
        return this;
    }

    @SneakyThrows
    public NMSChatBuilder hovor(String text) {
        Object chat = chatText.getConstructor(String.class).newInstance(text);
        Object type = Enum.valueOf(chatHovorAction,"SHOW_TEXT");
        Object hovor = chatHovor.getConstructor(chatHovorAction, chatInterface).newInstance(type,chat);
        Reflective.call(getChatModifier(),"setChatHoverable",hovor);
        return this;
    }

    public NMSChatBuilder execute(String text) {
        return onClick("RUN_COMMAND",text);
    }

    public NMSChatBuilder suggest(String text) {
        return onClick("SUGGEST_COMMAND",text);
    }

    public NMSChatBuilder link(String text) {
        return onClick("OPEN_URL",text);
    }

    @SneakyThrows
    private NMSChatBuilder onClick(String type, String value) {
        Object type_ = Enum.valueOf(chatClickAction,type);
        Object click = chatClick.getConstructor(chatClickAction, String.class).newInstance(type_,value);
        Reflective.call(getChatModifier(),"setChatClickable",click);
        return this;
    }

    @SneakyThrows
    public NMSChatBuilder next(String text) {
        this.curr = chatText.getConstructor(String.class).newInstance(text);
        components.add(this.curr);
        return this;
    }

    public Object getRoot() {
        return root;
    }

    private static Object getColor(ChatColor color) {
        return ChatColor.valueOf(chatColor,color.name());
    }

    private Object getChatModifier() {
        return Reflective.call(curr,"getChatModifier");
    }

    // ** ** ** Util methods



}
