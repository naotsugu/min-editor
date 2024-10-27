package com.mammb.code.editor.fx;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Side;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import java.util.LinkedList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AutoCompleteTextField<S> extends TextField {

    private final ObjectProperty<S> lastSelectedItem = new SimpleObjectProperty<>();

    /**
     * The existing autocomplete entries.
     */
    private final SortedSet<S> entries;

    /**
     * The set of filtered entries:<br>
     * Equal to the search results if search results are not empty, equal to
     * {@link #entries entries} otherwise.
     */
    private ObservableList<S> filteredEntries = FXCollections.observableArrayList();

    /**
     * The popup used to select an entry.
     */
    private ContextMenu entriesPopup;

    /**
     * Indicates whether the Popup should be hidden or displayed. Use this if
     * you want to filter an existing list/set (for example values of a
     * {@link javafx.scene.control.ListView ListView}). Do this by binding
     * {@link #getFilteredEntries() getFilteredEntries()} to the list/set.
     */
    private boolean popupHidden = false;

    /**
     * The CSS style that should be applied on the parts in the popup that match
     * the entered text. <br>
     * Default: "-fx-font-weight: bold; -fx-fill: red;"
     * <p>
     * Note: This style is going to be applied on an
     * {@link javafx.scene.text.Text Text} instance. See the <i>JavaFX CSS
     * Reference Guide</i> for available CSS Propeties.
     */
    private String textOccurenceStyle = "-fx-font-weight: bold; "
        + "-fx-fill: red;";

    /**
     * The maximum Number of entries displayed in the popup.<br>
     * Default: 10
     */
    private int maxEntries = 10;

    /**
     * Construct a new AutoCompleteTextField.
     *
     * @param entrySet
     */
    public AutoCompleteTextField(SortedSet<S> entrySet) {
        super();
        this.entries = (entrySet == null ? new TreeSet<>() : entrySet);
        this.filteredEntries.addAll(entries);

        entriesPopup = new ContextMenu();

        textProperty().addListener((ObservableValue<? extends String> ob, String s, String s2) -> {

            if (getText() == null || getText().isEmpty()) {
                filteredEntries.clear();
                filteredEntries.addAll(entries);
                entriesPopup.hide();
            } else {
                LinkedList<S> searchResult = new LinkedList<>();
                //Check if the entered Text is part of some entry
                String text1 = getText();
                Pattern pattern = Pattern.compile(".*" + text1 + ".*", Pattern.CASE_INSENSITIVE);
                for (S entry : entries) {
                    Matcher matcher = pattern.matcher(entry.toString());
                    if (matcher.matches()) {
                        searchResult.add(entry);
                    }
                }
                if (!entries.isEmpty()) {
                    filteredEntries.clear();
                    filteredEntries.addAll(searchResult);
                    //Only show popup if not in filter mode
                    if (!isPopupHidden()) {
                        populatePopup(searchResult, text1);
                        if (!entriesPopup.isShowing()) {
                            entriesPopup.show(AutoCompleteTextField.this, Side.BOTTOM, 0, 0);
                        }
                    }
                } else {
                    entriesPopup.hide();
                }
            }
        });

        focusedProperty().addListener((ObservableValue<? extends Boolean> ob, Boolean aBoolean, Boolean aBoolean2) -> {
            entriesPopup.hide();
        });

    }

    /**
     * Get the existing set of autocomplete entries.
     *
     * @return The existing autocomplete entries.
     */
    public SortedSet<S> getEntries() {
        return entries;
    }

    /**
     * Populate the entry set with the given search results. Display is limited
     * to 10 entries, for performance.
     *
     * @param searchResult The set of matching strings.
     */
    private void populatePopup(List<S> searchResult, String text) {
        List<CustomMenuItem> menuItems = new LinkedList<>();
        int count = Math.min(searchResult.size(), getMaxEntries());
        for (int i = 0; i < count; i++) {
            final String result = searchResult.get(i).toString();
            final S itemObject = searchResult.get(i);
            int occurence = result.toLowerCase().indexOf(text.toLowerCase());
            if (occurence < 0) {
                continue;
            }
            //Part before occurence (might be empty)
            Text pre = new Text(result.substring(0, occurence));
            //Part of (first) occurence
            Text in = new Text(result.substring(occurence, occurence + text.length()));
            in.setStyle(getTextOccurenceStyle());
            //Part after occurence
            Text post = new Text(result.substring(occurence + text.length()));

            TextFlow entryFlow = new TextFlow(pre, in, post);

            CustomMenuItem item = new CustomMenuItem(entryFlow, true);
            item.setOnAction((ActionEvent actionEvent) ->
            {
                lastSelectedItem.set(itemObject);
                entriesPopup.hide();
            });
            menuItems.add(item);
        }
        entriesPopup.getItems().clear();
        entriesPopup.getItems().addAll(menuItems);

    }

    public S getLastSelectedObject() {
        return lastSelectedItem.get();
    }

    public ContextMenu getEntryMenu() {
        return entriesPopup;
    }

    public String getTextOccurenceStyle() {
        return textOccurenceStyle;
    }

    public void setTextOccurenceStyle(String textOccurenceStyle) {
        this.textOccurenceStyle = textOccurenceStyle;
    }

    public boolean isPopupHidden() {
        return popupHidden;
    }

    public void setPopupHidden(boolean popupHidden) {
        this.popupHidden = popupHidden;
    }

    public ObservableList<S> getFilteredEntries() {
        return filteredEntries;
    }

    public int getMaxEntries() {
        return maxEntries;
    }

    public void setMaxEntries(int maxEntries) {
        this.maxEntries = maxEntries;
    }

}
