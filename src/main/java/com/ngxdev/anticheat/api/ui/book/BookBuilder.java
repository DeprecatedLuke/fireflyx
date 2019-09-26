package com.ngxdev.anticheat.api.ui.book;

import com.ngxdev.anticheat.utils.Reflective;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import java.util.List;

@SuppressWarnings("unchecked")
public class BookBuilder {

    private ItemStack book;
    private BookMeta bookMeta;
    private List pages;

    /** constructs that thing i wrote */
    public BookBuilder() {
        book = new ItemStack(Material.WRITTEN_BOOK);
        bookMeta = (BookMeta) book.getItemMeta();
        pages = Reflective.get(bookMeta, "pages");
    }

    public BookBuilder clearPages() {
        pages.clear();
        return this;
    }

    public BookBuilder addPage(NMSChatBuilder builder) {
        pages.add(builder.getRoot());
        return this;
    }

    public BookBuilder setAuthor(String author) {
        bookMeta.setAuthor(author);
        return this;
    }

    public BookBuilder setTitle(String title) {
        bookMeta.setTitle(title);
        return this;
    }

    public ItemStack get() {
        book.setItemMeta(bookMeta);
        return book;
    }

    public BookBuilder send(Player player) {
        send(player, get());
        return this;
    }

    public static void send(Player player, ItemStack book) {
        BookHandler.sendBook(player,book);
    }


}
