package sandbox;

public interface TextContent {

    String get();

    /**
     * Retrieves a subset of the content.
     * @param start the start
     * @param end the end
     * @return a subset of the content
     */
    String get(int start, int end);

    /**
     * Inserts a sequence of characters into the content.
     *
     * @param index the index
     * @param text the text string
     */
    void insert(int index, String text);

    /**
     * Removes a sequence of characters from the content.
     * @param start the start
     * @param end the end
     */
    void delete(int start, int end);

    /**
     * Returns the number of characters represented by the content.
     * @return the number of characters
     */
    int length();

}
